package com.example.fintrack.budget

import com.example.fintrack.budget.entity.Budget
import com.example.fintrack.budget.repository.BudgetRepository
import com.example.fintrack.categories.entity.Category
import com.example.fintrack.categories.entity.CategoryType
import com.example.fintrack.categories.repository.CategoryRepository
import com.example.fintrack.user.entity.User
import com.example.fintrack.user.repository.UserRepository
import com.example.fintrack.utils.TestJwtHelper
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.transaction.Transactional
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.math.BigDecimal
import kotlin.test.Test

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BudgetIntegrationTest(
    @Autowired val mockMvc: MockMvc,
    @Autowired val objectMapper: ObjectMapper,
    @Autowired val userRepository: UserRepository,
    @Autowired val categoryRepository: CategoryRepository,
    @Autowired val budgetRepository: BudgetRepository,
    @Autowired val jwtHelper: TestJwtHelper
) {
    lateinit var user: User
    lateinit var category: Category
    private lateinit var token: String

    @BeforeEach
    fun setup() {
        budgetRepository.deleteAll()
        categoryRepository.deleteAll()
        userRepository.deleteAll()

        user = userRepository.save(
            User(
                name = "Test User",
                email = "test@example.com",
                password_hash = "hashed",
                default_currency = "INR"
            )
        )

        category = categoryRepository.save(
            Category(
                name = "Food", user = user,
                type = CategoryType.EXPENSE,
            )
        )

        token = jwtHelper.generateToken(user.email)
    }

    @Test
    fun `should create budget successfully`() {

        val request = mapOf(
            "year" to 2026,
            "month" to 2,
            "amount" to BigDecimal("500"),
            "categoryId" to category.id
        )

        mockMvc.post("/api/v1/budgets") {
            header("Authorization", "Bearer $token")
            contentType = (MediaType.APPLICATION_JSON)
            content = (objectMapper.writeValueAsString(request))
        }
            .andExpect {
                status { isCreated() }
            }
    }

    @Test
    fun `should return 409 when duplicate budget exists`() {

        budgetRepository.save(
            Budget(
                year = 2026,
                month = 2,
                amount = BigDecimal("500"),
                category = category,
                user = user
            )
        )

        val request = mapOf(
            "year" to 2026,
            "month" to 2,
            "amount" to BigDecimal("600"),
            "categoryId" to category.id
        )

        mockMvc.post("/api/v1/budgets") {
            header("Authorization", "Bearer $token")
            contentType = (MediaType.APPLICATION_JSON)
            content = (objectMapper.writeValueAsString(request))
        }
            .andExpect {
                status { isConflict() }
            }
    }

    @Test
    fun `should soft delete budget`() {

        val budget = budgetRepository.save(
            Budget(
                year = 2026,
                month = 2,
                amount = BigDecimal("500"),
                category = category,
                user = user
            )
        )

        mockMvc.perform(
            delete("/api/v1/budgets/{id}", budget.id)
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isNoContent)

        val deleted = budgetRepository.findById(budget.id!!).orElseThrow()
        assert(deleted.isDeleted)
    }

    @Test
    fun `should not return deleted budgets`() {

        val budget = budgetRepository.save(
            Budget(
                year = 2026,
                month = 2,
                amount = BigDecimal("500"),
                category = category,
                user = user,
                isDeleted = true
            )
        )

        mockMvc.perform(
            get("/api/v1/budgets")
                .header("Authorization", "Bearer $token")
                .param("year", "2026")
                .param("month", "2")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(0))
    }


}
