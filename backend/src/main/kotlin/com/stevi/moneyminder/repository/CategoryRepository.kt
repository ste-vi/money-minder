package com.stevi.moneyminder.repository;

import com.stevi.moneyminder.entity.Category
import com.stevi.moneyminder.entity.CategoryType
import com.stevi.moneyminder.repository.projection.TopExpensesProjection
import java.time.LocalDateTime
import java.util.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface CategoryRepository : JpaRepository<Category, UUID> {

    fun findAllBySpaceIdOrderByPosition(spaceId: UUID): List<Category>

    fun findAllBySpaceIdAndTypeOrderByPosition(spaceId: UUID, type: CategoryType): List<Category>

    @Query(
        """
        select 
            sum(t.amount) as total,
            c as category,
            t.currency as currency
        from Transaction t 
            left join t.category c 
        where t.account.space.id = :spaceId 
            and t.type = com.stevi.moneyminder.entity.TransactionType.EXPENSE
            and t.date >= :dateFrom 
            and t.date <= :dateTo
            and c.id not in (:categoryIdsToExclude)
        group by c, t.currency
        order by sum(t.amount) desc
    """
    )
    fun findTopExpenses(
        @Param("spaceId") spaceId: UUID,
        @Param("dateFrom") dateFrom: LocalDateTime,
        @Param("dateTo") dateTo: LocalDateTime,
        @Param("categoryIdsToExclude") categoryIdsToExclude: Set<UUID>,
    ): List<TopExpensesProjection>
}