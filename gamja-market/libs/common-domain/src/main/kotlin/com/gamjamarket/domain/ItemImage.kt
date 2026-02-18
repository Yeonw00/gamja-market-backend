package com.gamjamarket.domain

import com.gamjamarket.domain.common.BaseTimeEntity
import jakarta.persistence.*
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction

@Entity
@Table(name = "item_images")
@SQLDelete(sql = "UPDATE item_images SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = false")
class ItemImage(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    val item: Item,

    @Column(nullable = false)
    val imageUrl: String,

    @Column(nullable = false)
    var sortOrder: Int = 0,

    @Column(nullable = false)
    var isThumbnail: Boolean = false,

    @Column(name = "is_deleted")
    var isDeleted: Boolean = false
) : BaseTimeEntity()