package com.gamjamarket.domain

import com.gamjamarket.domain.common.BaseTimeEntity
import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "items")
class Item (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    val seller: User,

    @OneToOne(mappedBy = "item", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val auction: Auction? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    var category: Category,

    @Column(nullable = false)
    var title: String,

    @Column(columnDefinition = "TEXT", nullable = false)
    var content: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "item_condition", nullable = false)
    var condition: ItemCondition, // S, A, B, C 등급

    @Column(nullable = false)
    var viewCount: Int = 0,

    @OneToMany(
        mappedBy = "item",
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    var images: MutableList<ItemImage> = mutableListOf()
) : BaseTimeEntity() {
    fun updateImages(newImageUrls: List<String>) {
        this.images.clear()
        newImageUrls.forEachIndexed { index, url ->
            this.images.add(
                ItemImage(
                    item = this,
                    imageUrl = url,
                    sortOrder = index,
                    isThumbnail = (index == 0)
                )
            )
        }
    }
}

enum class ItemCondition {
    S, A, B, C
}