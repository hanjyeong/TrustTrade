package org.example.trusttrade.global.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.trusttrade.auction.Auction;
import org.example.trusttrade.item.dto.StoredImage;
import org.example.trusttrade.item.dto.response.ItemResponseDto;
import org.example.trusttrade.item.repository.ItemImageRepository;
import org.example.trusttrade.item.service.ImageService;
import org.example.trusttrade.login.domain.User;
import org.example.trusttrade.item.dto.request.AuctionItemDto;
import org.example.trusttrade.global.repository.AuctionRepository;
import org.example.trusttrade.item.service.ItemService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuctionService {

    private final AuctionRepository auctionRepository;
    private final ItemImageRepository itemImageRepository;
    private final ItemService itemService;
    private final ImageService imageService;

    // 경매 물품 등록
    @Transactional
    public void registerAuctionItem(AuctionItemDto auctionItemDto, User seller, List<MultipartFile> images) {
        List<StoredImage> storedImages = Collections.emptyList();
        try {

            // 이미지 변환
            storedImages = imageService.processImagesAndSetDto(images, auctionItemDto);

            // Auction 객체 생성
            Auction auction = Auction.fromDto(auctionItemDto, seller);
            auctionRepository.save(auction);

        } catch (Exception e) {
            imageService.deleteFiles(storedImages);
            throw new RuntimeException("경매 물품 등록 중 오류 발생", e);
        }

    }

    //경매 조죄 by sellerId
    @Transactional
    public List<Auction> getAuctionsBySeller(UUID sellerId) {
        return auctionRepository.getAuctionsBySellerId(sellerId);
    }

    // 경매 물품 전체 조회
    public List<ItemResponseDto> getAuctionItems() {
        List<Auction> items = auctionRepository.findAll();
        return items.stream()
                .map(ItemResponseDto::fromAuction)
                .collect(Collectors.toList());
    }





}
