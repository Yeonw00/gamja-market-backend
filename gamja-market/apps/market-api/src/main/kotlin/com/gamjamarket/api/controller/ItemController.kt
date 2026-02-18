package com.gamjamarket.api.controller

import com.gamjamarket.api.dto.request.ItemCreateRequest
import com.gamjamarket.api.dto.response.ItemCreateResponse
import com.gamjamarket.api.dto.response.ItemDetailResponse
import com.gamjamarket.api.dto.response.ItemSummaryResponse
import com.gamjamarket.api.service.ItemService
import com.gamjamarket.api.dto.request.ItemUpdateRequest
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/items")
class ItemController(
    private val itemService: ItemService
) {
    @PostMapping
    fun createItem(
        @RequestHeader("X-Seller-Id")sellerId: UUID,
        @RequestBody request: ItemCreateRequest
    ): ResponseEntity<ItemCreateResponse> {
        val response = itemService.createItem(sellerId, request)

        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    // 페이지 사이즈는 임의로 20으로 둠
    @GetMapping
    fun getItems(
        @PageableDefault(size = 20, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable
    ): ResponseEntity<Page<ItemSummaryResponse>> {
        val response = itemService.getItems(pageable)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{id}")
    fun getItem(@PathVariable id: Long): ResponseEntity<ItemDetailResponse> {
        val response = itemService.getItemDetail(id)
        return ResponseEntity.ok(response)
    }

    @PatchMapping("/{itemId}")
    fun updateItem(
        @PathVariable itemId: Long,
        @RequestHeader("X-Seller-Id")sellerId: UUID,
        @RequestBody request: ItemUpdateRequest
    ): ResponseEntity<ItemDetailResponse> {
        val updateItem = itemService.updateItem(itemId, sellerId, request)

        return ResponseEntity.ok(updateItem)
    }

    @DeleteMapping("/{itemId}")
    fun deleteItem(
        @PathVariable itemId: Long,
        @RequestHeader("X-Seller-Id")sellerId: UUID
    ): ResponseEntity<Unit> {
        itemService.deleteItem(itemId, sellerId)
        return ResponseEntity.noContent().build()
    }
}