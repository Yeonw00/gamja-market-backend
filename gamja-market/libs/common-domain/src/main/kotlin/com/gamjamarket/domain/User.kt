package com.gamjamarket.domain

import com.gamjamarket.domain.common.BaseTimeEntity
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "users")
class User(
    @Id
    @Column(columnDefinition = "UUID")
    val id: UUID = UUID.randomUUID(),

    @Column(nullable = false, unique = true)
    var nickname: String,

    @Column(name = "nickname_normalized")
    var nicknameNormalized: String? = null,

    @Column(name = "nickname_chosung")
    var nicknameChosung: String ?= null,

    @Column(name = "nickname_jamo")
    var nicknameJamo: String ?= null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: UserStatus = UserStatus.ACTIVE,

    @Column(name = "deleted_at")
    var deletedAt: LocalDateTime? = null,
) : BaseTimeEntity()

enum class UserStatus {
    ACTIVE, SUSPENDED, DELETED
}
