package com.stevi.moneyminder.repository;

import com.stevi.moneyminder.entity.Category
import com.stevi.moneyminder.entity.CategoryType
import com.stevi.moneyminder.model.response.TopExpenseResponse
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
            new com.stevi.moneyminder.model.response.TopExpenseResponse(
                sum(t.amount),
                c.id, 
                c.name
            ) 
        from Transaction t 
        join t.category c 
        where t.fromAccount.space.id = :spaceId 
            and c.type = :categoryType 
            and t.date >= :dateFrom 
            and t.date <= :dateTo 
        group by c.id, c.name 
        order by sum(t.amount) desc
    """
    )
    fun findTopExpenses(
        @Param("spaceId") spaceId: UUID,
        @Param("categoryType") categoryType: CategoryType,
        @Param("dateFrom") dateFrom: LocalDateTime,
        @Param("dateTo") dateTo: LocalDateTime
    ): List<TopExpenseResponse>
}