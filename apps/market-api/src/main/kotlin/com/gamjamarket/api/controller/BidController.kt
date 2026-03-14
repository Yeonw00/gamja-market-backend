package com.gamjamarket.api.controller

import com.gamjamarket.api.dto.request.BidRequest
import com.gamjamarket.api.dto.response.BidHistoryResponse
import com.gamjamarket.api.dto.response.BidResponse
import com.gamjamarket.api.service.BidService
import com.gamjamarket.utils.response.ApiResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/auctions")
class BidController(
    private val bidService: BidService
) {

    @PostMapping("/{auctionId}/bids")
    fun placeBid(
        @PathVariable auctionId: Long,
        @RequestHeader("X-Bidder-Id") bidderId: UUID,
        @RequestBody request: BidRequest
    ): ResponseEntity<ApiResponse<BidResponse>> {
        val response = bidService.placeBid(
            auctionId = auctionId,
            bidderId = bidderId,
            bidPrice = request.bidPrice
        )

        return ResponseEntity.ok(
            ApiResponse.success(response,"성공적으로 입찰되었습니다.")
        )
    }

    @GetMapping("/{auctionId}/bids")
    fun getBidHistory(
        @PathVariable auctionId: Long,
        @PageableDefault(size = 10, sort = ["bidPrice"], direction = Sort.Direction.DESC) pageable: Pageable
    ): ResponseEntity<ApiResponse<Page<BidHistoryResponse>>> {
        val response = bidService.getBidHistory(auctionId, pageable)

        return ResponseEntity.ok(ApiResponse.success(response))
    }
}