package com.gamjamarket.domain

import com.gamjamarket.domain.common.BaseTimeEntity
import jakarta.persistence.*

@Entity
@Table(name = "categories")
class Category(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, length = 50)
    var name: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    var parent: Category? = null, // 부모 카테고리 (null이면 최상위 카테고리)

    @OneToMany(mappedBy = "parent", cascade = [CascadeType.ALL])
    val children: MutableList<Category> = mutableListOf(), // 자식 카테고리 리스트

    @Column(nullable = false)
    var depth: Int = 1, // (1: 대분류 2: 중분류)

    @Column(nullable = false)
    var sortOrder: Int = 0 // 출력 순서
) : BaseTimeEntity() {
    // 자식 카테고리를 추가할 때 부모-자식 관계와 depth를 자동으로 설정해주는 편의 메서드
    fun addChildCategory(child: Category) {
        child.parent = this
        child.depth = this.depth + 1
        this.children.add(child)
    }
}