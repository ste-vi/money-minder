package com.stevi.moneyminder.entity

import com.stevi.moneyminder.model.response.RuleResponse
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import java.util.*

@Entity
@Table(name = "rules")
open class Rule(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    open var id: UUID? = null,

    @OneToOne(cascade = [CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE], optional = false, orphanRemoval = true)
    @JoinColumn(nullable = false, unique = true)
    open var condition: Condition,

    @Column(name = "assign_category_id", nullable = false)
    open var assignCategoryId: UUID,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    open var space: Space,
)

fun Rule.mapToResponse(): RuleResponse {
    return RuleResponse(
        id = this.id ?: UUID.randomUUID(),
        condition = this.condition.mapToResponse(),
        assignCategoryId = this.assignCategoryId,
    )
}
