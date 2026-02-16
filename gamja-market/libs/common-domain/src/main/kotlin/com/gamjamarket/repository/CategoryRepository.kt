package com.gamjamarket.repository

import com.gamjamarket.domain.Category
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface CategoryRepository : JpaRepository<Category, Long> {
    fun findAllByParentIsNull(): List<Category> // 대분류만 조회
}