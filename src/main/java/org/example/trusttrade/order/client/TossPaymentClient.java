package org.example.trusttrade.order.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.trusttrade.order.dto.ConfirmPaymentRequest;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class TossPaymentClient {


    private final ObjectMapper jacksonObjectMapper;

    public TossPaymentClient(ObjectMapper jacksonObjectMapper) {

        this.jacksonObjectMapper = jacksonObjectMapper;
    }


    //결제 승인 요청
    public HttpResponse requestConfirm(ConfirmPaymentRequest confirmPaymentRequest) {
        String tossOrderId = confirmPaymentRequest.getOrderId();
        int amount = confirmPaymentRequest.getAmount();
        String tossPaymentKey = confirmPaymentRequest.getPaymentKey();

        //JSON 객체 생성
        JsonNode requestObj = jacksonObjectMapper.createObjectNode()
                .put("orderId", tossOrderId)
                .put("amount", String.valueOf(amount))
                .put("paymentKey", tossPaymentKey);

        //JSON 객체 문자열 변환
        try {
            String requestBody = jacksonObjectMapper.writeValueAsString(requestObj);
            System.out.println("Sending request to Toss: " + requestBody);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.tosspayments.com/v1/payments/confirm"))
                    .header("Authorization", getAuthorizations())
                    .header("Content-Type", "application/json")
                    .method("POST", HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());


        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    //결제 취소 요청(db 작업 에러시 사용)
    public HttpResponse requestPaymentCancel(String paymentKey, String cancelReason) throws IOException, InterruptedException {

        String json = String.format("{\"cancelReason\": \"%s\"}", cancelReason.replace("\"", "\\\""));


        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.tosspayments.com/v1/payments/" + paymentKey + "/cancel"))
                .header("Authorization", getAuthorizations())
                .header("Content-Type", "application/json")
                .method("POST", HttpRequest.BodyPublishers.ofString("{\"cancelReason\" : \"" + cancelReason + "\"}"))
                .build();

        return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    }

    //@Value("${TOSS_WIDGET_SECRET_KEY}")
    //private final widgetSecretKey;
    //테스트용 인증키 생성
    private String getAuthorizations() {
        String widgetSecretKey = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6"; //테스트용 인증키
        //String widgetSecretKey = "test_sk_zXLkKEypNArWmo50nX3lmeaxYG5R"; //테스트용 인증키

        if (widgetSecretKey == null || widgetSecretKey.isBlank()) {
            throw new IllegalStateException("TOSS_WIDGET_SECRET_KEY 설정이 비어있습니다.");
        }

        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((widgetSecretKey + ":").getBytes(StandardCharsets.UTF_8));
        String authorizations = "Basic " + new String(encodedBytes);

        return authorizations;
    }
}