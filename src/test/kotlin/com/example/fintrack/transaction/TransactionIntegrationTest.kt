package com.example.fintrack.transaction

import com.example.fintrack.categories.entity.Category
import com.example.fintrack.categories.entity.CategoryType
import com.example.fintrack.categories.repository.CategoryRepository
import com.example.fintrack.transaction.entity.PaymentMethod
import com.example.fintrack.transaction.entity.Transaction
import com.example.fintrack.transaction.entity.TransactionType
import com.example.fintrack.transaction.repository.TransactionRepository
import com.example.fintrack.user.entity.User
import com.example.fintrack.user.repository.UserRepository
import com.example.fintrack.utils.TestJwtHelper
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import java.math.BigDecimal
import java.time.LocalDateTime

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TransactionIntegrationTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var categoryRepository: CategoryRepository

    @Autowired
    lateinit var transactionRepository: TransactionRepository

    @Autowired
    lateinit var jwtHelper: TestJwtHelper

    private lateinit var user: User
    private lateinit var category: Category
    private lateinit var token: String

    @BeforeEach
    fun setup() {
        transactionRepository.deleteAll()
        categoryRepository.deleteAll()
        userRepository.deleteAll()

        user = userRepository.save(
            User(
                email = "test@mail.com",
                password_hash = "password@123",
                name = "test",
                default_currency = "INR",
            )
        )

        category = categoryRepository.save(
            Category(
                name = "Food",
                user = user,
                type = CategoryType.EXPENSE
            )
        )

        token = jwtHelper.generateToken(user.email)
    }

    @Test
    fun `should create transaction successfully`() {

        val request = mapOf(
            "amount" to BigDecimal("500.00"),
            "description" to "Dinner",
            "type" to "EXPENSE",
            "paymentMethod" to "CASH",
            "categoryId" to category.id
        )

        mockMvc.post("/api/v1/transactions") {
            header("Authorization", "Bearer $token")
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }
            .andExpect {
                status { isCreated() }
            }

        val saved = transactionRepository.findAll()

        assert(saved.size == 1)
        assert(saved.first().amount == BigDecimal("500.00"))
        assert(saved.first().type == TransactionType.EXPENSE)
        assert(saved.first().paymentMethod == PaymentMethod.CASH)
        assert(!saved.first().deleted)
    }

    @Test
    fun `should return 401 when token missing`() {

        val request = mapOf(
            "amount" to BigDecimal("100.00"),
            "description" to "Test",
            "type" to "INCOME",
            "paymentMethod" to "CASH",
            "categoryId" to category.id
        )

        mockMvc.post("/api/v1/transactions") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }
            .andExpect {
                status { isUnauthorized() }
            }
    }

    @Test
    fun `should soft delete transaction`() {

        val transaction = transactionRepository.save(
            Transaction(
                amount = BigDecimal("200"),
                description = "Test",
                type = TransactionType.EXPENSE,
                category = category,
                user = user
            )
        )

        mockMvc.delete("/api/v1/transactions/${transaction.id}") {
            header("Authorization", "Bearer $token")
        }
            .andExpect {
                status { isNoContent() }
            }

        val updated = transactionRepository.findById(transaction.id!!).get()

        assert(updated.deleted)
    }

    @Test
    fun `should return correct monthly summary`() {

        // INCOME
        transactionRepository.save(
            Transaction(
                amount = BigDecimal("1000"),
                description = "Salary",
                type = TransactionType.INCOME,
                category = category,
                user = user,
                createdAt = LocalDateTime.of(2026, 2, 5, 10, 0)
            )
        )

        // EXPENSE
        transactionRepository.save(
            Transaction(
                amount = BigDecimal("400"),
                description = "Food",
                type = TransactionType.EXPENSE,
                category = category,
                user = user,
                createdAt = LocalDateTime.of(2026, 2, 10, 12, 0)
            )
        )

        mockMvc.get("/api/v1/transactions/monthly-summary") {
            header("Authorization", "Bearer $token")
            param("year", "2026")
            param("month", "2")
        }
            .andExpect {
                status { isOk() }
                jsonPath("$.income") { value(1000) }
                jsonPath("$.expense") { value(400) }
                jsonPath("$.balance") { value(600) }
            }
    }

    @Test
    fun `should return correct dashboard totals`() {

        transactionRepository.save(
            Transaction(
                amount = BigDecimal("2000"),
                description = "Salary",
                type = TransactionType.INCOME,
                category = category,
                user = user
            )
        )

        transactionRepository.save(
            Transaction(
                amount = BigDecimal("500"),
                description = "Shopping",
                type = TransactionType.EXPENSE,
                category = category,
                user = user
            )
        )

        mockMvc.get("/api/v1/transactions/dashboard") {
            header("Authorization", "Bearer $token")
        }
            .andExpect {
                status { isOk() }
                jsonPath("$.totalIncome") { value(2000) }
                jsonPath("$.totalExpense") { value(500) }
                jsonPath("$.balance") { value(1500) }
            }
    }


    @Test
    fun `should return correct monthly payment method summary`() {

        transactionRepository.save(
            Transaction(
                amount = BigDecimal("300"),
                description = "Food",
                type = TransactionType.EXPENSE,
                paymentMethod = PaymentMethod.CASH,
                category = category,
                user = user,
                createdAt = LocalDateTime.of(2026, 2, 15, 14, 0)
            )
        )

        transactionRepository.save(
            Transaction(
                amount = BigDecimal("200"),
                description = "Other",
                type = TransactionType.EXPENSE,
                paymentMethod = PaymentMethod.UPI,
                category = category,
                user = user,
                createdAt = LocalDateTime.of(2026, 2, 18, 14, 0)
            )
        )

        mockMvc.get("/api/v1/transactions/monthly-payment-summary") {
            header("Authorization", "Bearer $token")
            param("paymentMethod", "CASH")
            param("year", "2026")
            param("month", "2")
        }
            .andExpect {
                status { isOk() }
                jsonPath("$.paymentMethod") { value("CASH") }
                jsonPath("$.amount") { value("300.00") }
            }
    }




}
