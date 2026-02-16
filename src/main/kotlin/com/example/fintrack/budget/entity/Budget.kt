package com.example.fintrack.budget.entity

import com.example.fintrack.categories.entity.Category
import com.example.fintrack.user.entity.User
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(
    name = "budgets",
    uniqueConstraints = [
        UniqueConstraint(
            columnNames = ["user_id", "category_id", "year", "month"]
        )
    ]
)
class Budget(

    @Id
    @GeneratedValue
    val id: UUID? = null,

    @Column(nullable = false)
    var year: Int,

    @Column(nullable = false)
    var month: Int,

    @Column(nullable = false)
    var amount: BigDecimal,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    val category: Category,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(nullable = false)
    var isDeleted: Boolean = false,

    @CreationTimestamp
    var createdAt: LocalDateTime? = null,

    @UpdateTimestamp
    var updatedAt: LocalDateTime? = null
)
