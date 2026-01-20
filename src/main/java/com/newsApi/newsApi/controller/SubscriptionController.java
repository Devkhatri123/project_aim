package com.newsApi.newsApi.controller;

import com.newsApi.newsApi.Service.SubscriptionService;
import com.newsApi.newsApi.dto.ErrorMessage;
import com.newsApi.newsApi.dto.Response;
import com.newsApi.newsApi.dto.SubscriptionRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

@Slf4j
@RestController
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    @Autowired
    public SubscriptionController(final SubscriptionService subscriptionService){
        this.subscriptionService = subscriptionService;
    }

    @PostMapping("/checkoutURL")
    public ResponseEntity<?> createPaymentSession(@RequestBody SubscriptionRequest subscriptionRequest){
      try{
          String checkoutURL = subscriptionService.createCheckoutURL(subscriptionRequest);
          Response response = new Response();
          response.setData(checkoutURL);
          response.setStatusCode(201);
          return ResponseEntity.status(HttpStatus.CREATED).body(response);
      } catch (RuntimeException e) {
          log.error("Internal Server error: {}", e.getMessage());
          ErrorMessage errorMessage = new ErrorMessage();
          errorMessage.setErrorMessage("Internal Server error");
          errorMessage.setStatusCode(500);
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
      }
    }
    @PostMapping(value="/paymentSuccess")
    public void successPayment(@RequestBody Map<Object,Object> payload,
                                            @RequestHeader("X-SFPY-SIGNATURE") String signature){
        try {
            subscriptionService.handlePaymentSuccess(payload, signature);
            log.info("Payment successful!");
        } catch (InvalidKeyException e) {
            log.error("Invalid key");
        } catch (NoSuchAlgorithmException e) {
            log.error("No such algorithm found");
        } catch (RuntimeException e) {
            log.error("Runtime exception: {}", e.getMessage());
        }

    }
    @PostMapping(value="/paymentError")
    public void paymentError(){
        log.error("Payment failed");
    }
}
