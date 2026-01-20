package com.newsApi.newsApi.Service;

import com.newsApi.newsApi.dto.SubscriptionRequest;
import com.newsApi.newsApi.model.Subscription;
import com.newsApi.newsApi.model.User;
import com.newsApi.newsApi.model.UserDetails;
import com.newsApi.newsApi.repository.UserRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import org.springframework.http.HttpHeaders;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.Map;

@Slf4j
@Service
public class SubscriptionService {
    @Value("${spring.safepay.publickey}")
    private String publickey;
    @Value("${spring.safepay.scretkey}")
    private String secretkey;
    @Value("${spring.safepay.env}")
    private String env;
    @Value("${spring.safepay.success_redirect_url}")
    private String successUrl;
    @Value("${spring.safepay.cancel_redirect_url}")
    private String cancelUrl;
    @Value("${spring.safepay.webhook_secret}")
    private String webhooksecret;

    private final UserRepo userRepo;

    @Autowired
    public SubscriptionService(final UserRepo userRepo){
        this.userRepo = userRepo;
    }


    public String createCheckoutURL(SubscriptionRequest subscriptionRequest){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String,Object> paymentSessionDetails = createPaymentSession(subscriptionRequest);
        String trackerToken = paymentSessionDetails.get("token").toString();
        String orderId = userDetails.getUser().getUserId();
        return "https://sandbox.api.getsafepay.com/components?env="+env+"&beacon="+trackerToken+"&order_id=ORDER_"+orderId+"&redirect_url="+successUrl+"&cancel_url="+cancelUrl+"&source=hosted";
    }
    private Map createPaymentSession(SubscriptionRequest subscriptionRequest){
        RestTemplate restTemplate = new RestTemplate();
        Map<String,Object> payload = new HashMap<>();
        payload.put("client", publickey);
        payload.put("amount", subscriptionRequest.getAmount());
        payload.put("currency","PKR");
        payload.put("environment", env);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String,Object>> httpEntity = new HttpEntity<>(payload,headers);
        Map<String,Object> response =  restTemplate.postForObject("https://sandbox.api.getsafepay.com/order/v1/init", httpEntity, Map.class);
        response = (Map<String, Object>) response.get("data");
        return response;
    }
    public void handlePaymentSuccess(Map<Object, Object> payload, String signature) throws NoSuchAlgorithmException, InvalidKeyException {
        payload = (Map<Object, Object>) payload.get("data");
        Map<Object,Object> payload2 = (Map<Object, Object>) payload.get("notification");
        Map<Object,Object> metadataPayload = (Map<Object, Object>) payload2.get("metadata");
        String user_id = metadataPayload.get("order_id").toString().substring(6);
        User subscriptionOwner = userRepo.findById(user_id).get();

        Subscription subscription = Subscription.builder()
                .amount(Float.parseFloat(payload2.get("amount").toString()))
                .currency(payload2.get("currency").toString())
                .state(payload2.get("state").toString())
                .startDateTime(LocalDate.now())
                .expiresOn(LocalDate.now().plusDays(30))
                .isExpired(false)
                .user(subscriptionOwner)
                .user_id(subscriptionOwner.getUserId())
                .build();

          subscriptionOwner.setMySubscription(subscription);
          subscriptionOwner.setRole("SUBSCRIBER");
          userRepo.save(subscriptionOwner);
    }
    private boolean isValidSignature(String tracker, String signature) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac hmacSHA256 = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(webhooksecret.getBytes(StandardCharsets.UTF_8),"HmacSHA256");
        hmacSHA256.init(secretKey);
        byte[] hashBytes = hmacSHA256.doFinal(tracker.getBytes(StandardCharsets.UTF_8));
        String calculatedSignature = HexFormat.of().formatHex(hashBytes);
        return calculatedSignature.equalsIgnoreCase(signature);
    }
    private String generate_TrackerToken(SubscriptionRequest subscriptionRequest){
        RestTemplate restTemplate = new RestTemplate();
        Map<String,Object> payload = new HashMap<>();
        payload.put("merchant_api_key", publickey);
        payload.put("currency","PKR");
        payload.put("intent","CYBERSOURCE");
        payload.put("mode","payment");
        payload.put("amount",subscriptionRequest.getAmount());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(payload,headers);
        Map<String,Object> response = restTemplate.postForObject("https://sandbox.api.getsafepay.com/order/payments/v3/",httpEntity, Map.class);
        response = (Map<String, Object>) response.get("data");
        response = (Map<String, Object>) response.get("tracker");
        return response.get("token").toString();
    }
    private String createAuthenticationToken(){
      RestTemplate restTemplate = new RestTemplate();
      HttpHeaders headers = new HttpHeaders();
      headers.set("X-SFPY-MERCHANT-SECRET", secretkey);
      Map<String,Object> response = restTemplate.postForObject("https://sandbox.api.getsafepay.com/client/passport/v1/token",headers, Map.class);
      return response.get("data").toString();
    }
}
