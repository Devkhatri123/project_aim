package com.newsApi.newsApi.controller;

import com.newsApi.newsApi.Service.UserService;
import com.newsApi.newsApi.dto.ErrorMessage;
import com.newsApi.newsApi.dto.OtpDTO;
import com.newsApi.newsApi.dto.RegisterUserRequest;
import com.newsApi.newsApi.dto.Response;
import com.newsApi.newsApi.dto.user.ResetPasswordDTO;
import com.newsApi.newsApi.dto.user.SignInDTO;
import com.newsApi.newsApi.exception.OperationNotAllowed;
import com.newsApi.newsApi.exception.userexception.*;
import com.newsApi.newsApi.exception.refreshtoken.RefreshTokenExpired;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
public class UserController {
    private final UserService userService;
    public UserController(final UserService userService){
        this.userService = userService;
    }
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterUserRequest registerUserRequest){
    try{
        userService.registerUser(registerUserRequest);
        Response response = new Response();
        response.setData("User registered successfully. Otp sent to your email");
        response.setStatusCode(201);
        log.info("User registered successfully. Otp sent to your email");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    } catch (MessagingException e){
        Response errorMessage = new Response();
        errorMessage.setData("Internal Server error");
        errorMessage.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        return ResponseEntity.status(HttpStatus.valueOf(errorMessage.getStatusCode())).body(errorMessage);
    } catch (RuntimeException e) {
        log.error("Error in registering user: {}", e.getMessage());
        Response errorMessage = new Response();
        if(e instanceof EmailAlreadyTaken){
            errorMessage.setData(e.getMessage());
            errorMessage.setStatusCode(HttpStatus.BAD_REQUEST.value());
        }else{
        errorMessage.setData("Internal Server error");
        errorMessage.setStatusCode(500);
        }
        return ResponseEntity.status(HttpStatus.valueOf(errorMessage.getStatusCode())).body(errorMessage);

    }
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody SignInDTO signInDTO){
        try{
            Map<String,Object> responseMap = userService.login(signInDTO);
            responseMap.put("status", 200);
            log.info("Login successful!");
            return ResponseEntity.ok().body(responseMap);
        } catch (RuntimeException e){
            ErrorMessage errorMessage = new ErrorMessage();
            if(e instanceof IncorrectPassword || e instanceof UserDoesNotExist){
                errorMessage.setErrorMessage(e.getMessage());
                errorMessage.setStatusCode(HttpStatus.BAD_REQUEST.value());
                log.error("Bad Request: {}", e.getMessage());
            }else {
                errorMessage.setErrorMessage("Internal Server error");
                errorMessage.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
                log.error("Internal Server error", e);
            }
            return ResponseEntity.status(HttpStatus.valueOf(errorMessage.getStatusCode())).body(errorMessage);
        }
    }
    @PostMapping("verifyOtp")
    public ResponseEntity<Response> verifyOTP(@RequestBody OtpDTO otpDTO){
        Response response = new Response();
        try {
            userService.verifyOTP(otpDTO);
            response.setData("Otp verified");
            response.setStatusCode(200);
            log.info("Otp verified");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (RuntimeException e) {
            if(e instanceof TokenExpired || e instanceof UserDoesNotExist || e instanceof IncorrectOtpCode){
                response.setData(e.getMessage());
                response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                log.error("Bad Request at verification: {}", e.getMessage());
            }else {
                response.setData("Internal Server error");
                response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
                log.error("Internal Server error", e);
            }
            return ResponseEntity.status(HttpStatus.valueOf(response.getStatusCode())).body(response);
        }
    }
    @PostMapping("/resendOtp")
    public ResponseEntity<Response> resendOTP(@RequestParam String email, @RequestParam String type){
        Response response = new Response();
        try {
            userService.resendOtp(email,type);
            response.setData("Otp resend successfully");
            response.setStatusCode(200);
            log.info("Otp resend successfully");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (MessagingException e){
            response.setData("Facing internal error in sending email. Please try again");
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.valueOf(response.getStatusCode())).body(response);
        } catch (RuntimeException e) {
            if(e instanceof UserDoesNotExist || e instanceof OperationNotAllowed){
                response.setData(e.getMessage());
                response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                log.error("Bad Request at resending otp: {}", e.getMessage());
            } else {
                response.setData("Internal Server error");
                response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
                log.error("Internal Server error", e);
            }
            return ResponseEntity.status(HttpStatus.valueOf(response.getStatusCode())).body(response);
        }
    }
    @PostMapping("/refresh/{refreshToken}")
    public ResponseEntity<?> verifyRefreshToken(@PathVariable String refreshToken){
          try {
              Map<String, Object> responseMap = userService.verifyRefreshToken(refreshToken);
              responseMap.put("status", 200);
              return ResponseEntity.status(HttpStatus.OK).body(responseMap);
          } catch (RuntimeException e) {
              ErrorMessage errorMessage = new ErrorMessage();
              if(e instanceof RefreshTokenExpired){
                  log.error(e.getMessage());
                  errorMessage.setErrorMessage(e.getMessage());
                  errorMessage.setStatusCode(400);
              }else{
                  log.error("Internal Server error", e);
                  errorMessage.setErrorMessage("Internal Server error");
                  errorMessage.setStatusCode(500);
              }
              return ResponseEntity.status(HttpStatus.valueOf(errorMessage.getStatusCode())).body(errorMessage);
          }
    }
    @PostMapping("/forgetPassword")
    public ResponseEntity<Response> forgetPassword(@RequestParam String email){
        Response response = new Response();
        try {
            userService.forgetPassword(email);
            response.setData("Forget password link to sent to your email.");
            response.setStatusCode(200);
            return ResponseEntity.status(HttpStatusCode.valueOf(200)).body(response);
        } catch (MessagingException e) {
            response.setData("We are facing problem in sending forget password link your email. Please try again");
            response.setStatusCode(500);
            return ResponseEntity.status(500).body(response);
        } catch (RuntimeException e) {
            if(e instanceof UserDoesNotExist){
                response.setData(e.getMessage());
                response.setStatusCode(400);
            }else {
               response.setData("Internal Server error. Please try again");
               response.setStatusCode(500);
            }
            return ResponseEntity.status(HttpStatusCode.valueOf(response.getStatusCode())).body(response);
        }
    }
    @PostMapping("/resetPassword")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordDTO resetPasswordDTO, @RequestParam String resetToken){
       Response response = new Response();
        try {
            userService.resetPassword(resetPasswordDTO, resetToken);
            response.setData("Password changed successfully");
            response.setStatusCode(200);
            return ResponseEntity.status(HttpStatusCode.valueOf(200)).body(response);
       } catch (MessagingException e) {
            response.setData("Internal Server error.");
            response.setStatusCode(500);
            return ResponseEntity.status(500).body(response);
       } catch (RuntimeException e) {
            if(e instanceof TokenExpired || e instanceof IncorrectPassword){
                response.setData(e.getMessage());
                response.setStatusCode(400);
            }else {
                response.setData("Internal Server error. Please try again");
                response.setStatusCode(500);
            }
            return ResponseEntity.status(HttpStatusCode.valueOf(response.getStatusCode())).body(response);
        }
    }
    @GetMapping("/loggedInUser")
    public ResponseEntity<?> getLoggedInUser(){
        Response response = new Response();
        try {
            response.setData(userService.getLoggedInUser());
            response.setStatusCode(200);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (RuntimeException e) {
            response.setData("Please login again");
            response.setStatusCode(500);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
