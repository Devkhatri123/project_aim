package com.newsApi.newsApi.Service;

import com.newsApi.newsApi.dto.OtpDTO;
import com.newsApi.newsApi.dto.RegisterUserRequest;
import com.newsApi.newsApi.dto.user.LoggedInUserDTO;
import com.newsApi.newsApi.dto.user.ResetPasswordDTO;
import com.newsApi.newsApi.dto.user.SignInDTO;
import com.newsApi.newsApi.exception.OperationNotAllowed;
import com.newsApi.newsApi.exception.userexception.*;
import com.newsApi.newsApi.exception.refreshtoken.RefreshTokenExpired;
import com.newsApi.newsApi.model.*;
import com.newsApi.newsApi.repository.RefreshTokenRepo;
import com.newsApi.newsApi.repository.ResetTokenRepo;
import com.newsApi.newsApi.repository.UserRepo;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Slf4j
@Service
public class UserService {
    private final UserRepo userRepo;
    private final RefreshTokenRepo refreshTokenRepo;
    private final ResetTokenRepo resetTokenRepo;
    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final long REFRESH_TOKEN_VALIDITY = 7 * 24 * 60 * 60 * 1000;

    @Autowired
    public UserService(final UserRepo userRepo, final ModelMapper mapper, final BCryptPasswordEncoder bCryptPasswordEncoder, final JwtService jwtService,
    final RefreshTokenRepo refreshTokenRepo, final EmailService emailService, final ResetTokenRepo resetTokenRepo){
        this.userRepo = userRepo;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.modelMapper = mapper;
        this.jwtService = jwtService;
        this.refreshTokenRepo = refreshTokenRepo;
        this.emailService = emailService;
        this.resetTokenRepo = resetTokenRepo;
    }
    @Transactional
    public void registerUser(RegisterUserRequest registerUserRequest) throws EmailAlreadyTaken, MessagingException {
     if(userRepo.existsByEmail(registerUserRequest.getEmail())){
      throw new EmailAlreadyTaken("Email is already in use");
     }
     User mappedUser = modelMapper.map(registerUserRequest, User.class);
     mappedUser.setRole("GUEST");
     mappedUser.setPassword(bCryptPasswordEncoder.encode(mappedUser.getPassword()));
     mappedUser.setEmailVerified(false);
     int otp = generateOTP();
     mappedUser.setOtpVerification(generateVerification(mappedUser,otp,"EMAIL_VERIFICATION"));
     mappedUser = userRepo.save(mappedUser);
     emailService.sendOTPNotification(mappedUser,otp);
    }
    public int generateOTP() {
      return new Random().nextInt(100000,999999);
    }
    public OtpVerification generateVerification(User user, int otp, String type){
       return OtpVerification.builder()
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .user(user)
                .user_id(user.getUserId())
                .otp(otp)
                .verificationType(type)
                .build();
    }
    public Map<String, Object> login(SignInDTO signInDTO) throws UserDoesNotExist, IncorrectPassword{
     User user = userRepo.findByEmail(signInDTO.getEmail());
     if(user == null){
         throw new UserDoesNotExist("user does not exist against provided email");
     }
     boolean isPassCorrect = bCryptPasswordEncoder.matches(signInDTO.getPassword(), user.getPassword());
     if(isPassCorrect){
       Map<String,Object> responseMap = new HashMap<>();
       String accessToken = jwtService.generateJwt(user); // Jwt Token
       RefreshToken refreshToken = user.getRefreshToken(); // Refresh Token

       if(refreshToken == null){
           refreshToken = generateRefreshToken(user);
       }else {
           refreshToken.setExpiry(Instant.now().plusMillis(REFRESH_TOKEN_VALIDITY));
       }
       refreshTokenRepo.save(refreshToken);
       responseMap.put("accessToken", accessToken);
       responseMap.put("refreshToken", refreshToken.getRefreshToken());
       return responseMap;
     }else {
       throw new IncorrectPassword("Incorrect password");
     }
    }
    private RefreshToken generateRefreshToken(User user){
       return RefreshToken.builder()
                .refreshToken(UUID.randomUUID().toString())
                .expiry(Instant.now().plusMillis(REFRESH_TOKEN_VALIDITY))
                .user(user)
                .user_id(user.getUserId())
                .build();
    }
    @Transactional
    public Map<String,Object> verifyRefreshToken(String refreshToken) throws RefreshTokenExpired{
       RefreshToken savedRefreshToken = refreshTokenRepo.findOneByRefreshToken(refreshToken);

       if(savedRefreshToken == null){
           // Throw token expired exception when token doesn't exist
           throw new RefreshTokenExpired("Refresh token expired, signin again");
       } else if (savedRefreshToken.getExpiry().isBefore(Instant.now())){
           // Delete expired token and throw expired token exception
           log.info("Refresh token expired, and its getting deleted from db");
           savedRefreshToken.getUser().setRefreshToken(null);
           userRepo.save(savedRefreshToken.getUser());
           throw new RefreshTokenExpired("Refresh token expired, signIn again");
       }
       log.info("Refresh token not expired");
       User refreshTokenOwner = savedRefreshToken.getUser();
       String accessToken = jwtService.generateJwt(refreshTokenOwner);
       RefreshToken newRefreshToken = generateRefreshToken(refreshTokenOwner);
       refreshTokenOwner.setRefreshToken(newRefreshToken);
       userRepo.save(refreshTokenOwner);


       Map<String,Object> verifyRefreshTokenResponse = new HashMap<>();
       verifyRefreshTokenResponse.put("accessToken",accessToken);
       verifyRefreshTokenResponse.put("refreshToken", newRefreshToken.getRefreshToken());
       return verifyRefreshTokenResponse;
    }

    public void forgetPassword(String email) throws UserDoesNotExist, MessagingException {
        User existingUser = userRepo.findByEmail(email);
        if(existingUser != null){
          ResetToken resetToken = ResetToken.builder()
                    .token(UUID.randomUUID().toString())
                    .expiresAt(LocalDateTime.now().plusMinutes(5))
                    .user(existingUser)
                    .user_id(existingUser.getUserId())
                    .build();
          existingUser.setResetToken(resetToken);
          emailService.sendForgetPasswordLink(existingUser);
          userRepo.save(existingUser);
        }else {
            throw new UserDoesNotExist("User not found of given email");
        }
    }

    public void resetPassword(ResetPasswordDTO resetPasswordDTO, String resetToken) throws MessagingException, TokenExpired, IncorrectPassword {
     ResetToken savedResetToken = resetTokenRepo.findOneByToken(resetToken);
     if(savedResetToken != null){
         if(!savedResetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
             User resetTokenOwner = userRepo.findByEmail(savedResetToken.getUser().getEmail());
             if (resetPasswordDTO.getNewPassword().equals(resetPasswordDTO.getConfirmPassword())) {
                 resetTokenOwner.setPassword(bCryptPasswordEncoder.encode(resetPasswordDTO.getConfirmPassword()));
                 resetTokenOwner.setResetToken(null);
                 userRepo.save(resetTokenOwner);
                 emailService.passwordChangedNotification(resetTokenOwner);
                 return;
             }else {
                 throw new IncorrectPassword("Password does not match");
             }
         }
     }
     throw new TokenExpired("Reset token doesn't exist");
    }

    public void verifyOTP(OtpDTO otpDTO) throws TokenExpired, UserDoesNotExist, IncorrectOtpCode {
        User user = userRepo.findByEmail(otpDTO.getEmail());
        if(user != null){
          OtpVerification verification = user.getOtpVerification();
          if(user.getOtpVerification() != null) {
              if (!verification.getExpiresAt().isBefore(LocalDateTime.now())) {
                  if (verification.getVerificationType().equals(otpDTO.getVerificationType())) {
                      if (otpDTO.getOtp() == verification.getOtp()) {
                          if(otpDTO.getVerificationType().equals("EMAIL_VERIFICATION")){
                              user.setOtpVerification(null);
                              user.setEmailVerified(true);
                              userRepo.save(user);
                              return;
                          }
                      } else {
                          throw new IncorrectOtpCode("Incorrect otp code");
                      }
                  } else {
                      throw new IncorrectOtpCode("Verification can't be done, resend the code again");
                  }
              }
          }
              throw new TokenExpired("Verification code expired");
        } else {
            throw new UserDoesNotExist("Incorrect email. User not found");
        }
    }

    public void resendOtp(String email, String type) throws UserDoesNotExist, OperationNotAllowed,MessagingException {
        User user = userRepo.findByEmail(email);
        if(user != null) {
            if(user.isEmailVerified() && type.equals("EMAIL_VERIFICATION")){
                throw new OperationNotAllowed("Your email is already verified. You can't resend otp");
            }
            user.setOtpVerification(generateVerification(user,generateOTP(),type));
            userRepo.save(user);
            emailService.sendOTPNotification(user,user.getOtpVerification().getOtp());
        } else {
            throw new UserDoesNotExist("Incorrect email. User not found");
        }
    }

    public LoggedInUserDTO getLoggedInUser() {
      UserDetails userDetails =  (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      User user = userDetails.getUser();
      LoggedInUserDTO loggedInUser = modelMapper.map(user, LoggedInUserDTO.class);
      if(user.getMySubscription() != null && user.getRole().equals("SUBSCRIBER"))
          loggedInUser.setHasSubscription(true);
      else loggedInUser.setHasSubscription(false);
      if(user.isEmailVerified()) loggedInUser.setEmailVerified(true);
      return loggedInUser;
    }
}
