package org.example.trusttrade.global.service;

import lombok.RequiredArgsConstructor;
import org.example.trusttrade.global.dto.DocumentDto;
import org.example.trusttrade.global.dto.GeoPoint;
import org.example.trusttrade.global.dto.KakaoApiResponseDto;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MapService {

    private final KakaoAddressSearchService kakaoAddressSearchService;

    // 주소를 위도,경도 값으로 변환하는 메서드
    public GeoPoint addressToGeocode(String address){

        KakaoApiResponseDto kakao = kakaoAddressSearchService.requestAddressSearch(address);
        DocumentDto doc = kakao.getDocumentList().getFirst();

        double lat = kakaoAddressSearchService.toDouble(doc.getLatitude());
        double lng = kakaoAddressSearchService.toDouble(doc.getLongitude());

        return new GeoPoint(lat, lng);
    }
}
