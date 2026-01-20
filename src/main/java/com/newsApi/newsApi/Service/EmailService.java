package com.newsApi.newsApi.Service;

import com.newsApi.newsApi.model.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Async("asyncExecutor")
public class EmailService {
    private final JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String sendFrom;

    @Autowired
    public EmailService(final JavaMailSender javaMailSender){
        this.mailSender = javaMailSender;
    }


    public void sendForgetPasswordLink(User user) throws MessagingException {
        sendEmail(user,generateResetPasswordPage(user),"Forget password link");
        log.info("Forget password link email sent successfully");
    }
    public void passwordChangedNotification(User user) throws MessagingException {
        String body = "<!doctype html>\n" +
                "<html lang=\"en-US\">\n" +
                "\n" +
                "<head>\n" +
                "    <meta content=\"text/html; charset=utf-8\" http-equiv=\"Content-Type\" />\n" +
                "    <title>Password Changed Successfully</title>\n" +
                "    <meta name=\"description\" content=\"Password Changed Successfully Email Template.\">\n" +
                "    <style type=\"text/css\">\n" +
                "        a:hover { text-decoration: underline !important; }\n" +
                "    </style>\n" +
                "</head>\n" +
                "\n" +
                "<body marginheight=\"0\" topmargin=\"0\" marginwidth=\"0\"\n" +
                "      style=\"margin: 0px; background-color: #f2f3f8;\" leftmargin=\"0\">\n" +
                "\n" +
                "    <!-- 100% body table -->\n" +
                "    <table cellspacing=\"0\" border=\"0\" cellpadding=\"0\" width=\"100%\" bgcolor=\"#f2f3f8\"\n" +
                "           style=\"font-family: 'Open Sans', sans-serif;\">\n" +
                "        <tr>\n" +
                "            <td>\n" +
                "                <table style=\"background-color: #f2f3f8; max-width:670px; margin:0 auto;\"\n" +
                "                       width=\"100%\" border=\"0\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\">\n" +
                "\n" +
                "                    <tr>\n" +
                "                        <td style=\"height:80px;\">&nbsp;</td>\n" +
                "                    </tr>\n" +
                "\n" +
                "                    <tr>\n" +
                "                        <td style=\"text-align:center;\">\n" +
                "                            <a href=\"https://rakeshmandal.com\" title=\"logo\" target=\"_blank\">\n" +
                "                                <img width=\"60\"\n" +
                "                                     src=\"https://i.ibb.co/hL4XZp2/android-chrome-192x192.png\"\n" +
                "                                     title=\"logo\" alt=\"logo\">\n" +
                "                            </a>\n" +
                "                        </td>\n" +
                "                    </tr>\n" +
                "\n" +
                "                    <tr>\n" +
                "                        <td style=\"height:20px;\">&nbsp;</td>\n" +
                "                    </tr>\n" +
                "\n" +
                "                    <tr>\n" +
                "                        <td>\n" +
                "                            <table width=\"95%\" border=\"0\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\"\n" +
                "                                   style=\"max-width:670px;background:#fff;border-radius:3px;\n" +
                "                                   text-align:center;box-shadow:0 6px 18px 0 rgba(0,0,0,.06);\">\n" +
                "\n" +
                "                                <tr>\n" +
                "                                    <td style=\"height:40px;\">&nbsp;</td>\n" +
                "                                </tr>\n" +
                "\n" +
                "                                <tr>\n" +
                "                                    <td style=\"padding:0 35px;\">\n" +
                "                                        <h1 style=\"color:#1e1e2d;font-weight:500;margin:0;\n" +
                "                                        font-size:32px;font-family:'Rubik',sans-serif;\">\n" +
                "                                            Password Changed Successfully\n" +
                "                                        </h1>\n" +
                "\n" +
                "                                        <span style=\"display:inline-block; margin:29px 0 26px;\n" +
                "                                        border-bottom:1px solid #cecece; width:100px;\"></span>\n" +
                "\n" +
                "                                        <p style=\"color:#455056; font-size:15px;\n" +
                "                                        line-height:24px; margin:0;\">\n" +
                "                                            Your password has been updated successfully.\n" +
                "                                            If you did not make this change, please contact\n" +
                "                                            our support team immediately to secure your account.\n" +
                "                                        </p>\n" +
                "\n" +
                "                                        <a href=\"https://rakeshmandal.com/login\"\n" +
                "                                           style=\"background:#20e277;text-decoration:none !important;\n" +
                "                                           font-weight:500;margin-top:35px;color:#fff;\n" +
                "                                           text-transform:uppercase;font-size:14px;\n" +
                "                                           padding:10px 24px;display:inline-block;\n" +
                "                                           border-radius:50px;\">\n" +
                "                                            Login Now\n" +
                "                                        </a>\n" +
                "                                    </td>\n" +
                "                                </tr>\n" +
                "\n" +
                "                                <tr>\n" +
                "                                    <td style=\"height:40px;\">&nbsp;</td>\n" +
                "                                </tr>\n" +
                "                            </table>\n" +
                "                        </td>\n" +
                "                    </tr>\n" +
                "\n" +
                "                    <tr>\n" +
                "                        <td style=\"height:20px;\">&nbsp;</td>\n" +
                "                    </tr>\n" +
                "\n" +
                "                    <tr>\n" +
                "                        <td style=\"text-align:center;\">\n" +
                "                            <p style=\"font-size:14px;color:rgba(69,80,86,0.74);margin:0;\">\n" +
                "                                &copy; <strong>www.rakeshmandal.com</strong>\n" +
                "                            </p>\n" +
                "                        </td>\n" +
                "                    </tr>\n" +
                "\n" +
                "                    <tr>\n" +
                "                        <td style=\"height:80px;\">&nbsp;</td>\n" +
                "                    </tr>\n" +
                "\n" +
                "                </table>\n" +
                "            </td>\n" +
                "        </tr>\n" +
                "    </table>\n" +
                "    <!-- /100% body table -->\n" +
                "\n" +
                "</body>\n" +
                "</html>\n";
        sendEmail(user,body,"Password changed");
        log.info("Password changed email sent successfully");
    }
    private String generateResetPasswordPage(User user){
        return "\n" +
                "<!doctype html>\n" +
                "<html lang=\"en-US\">\n" +
                "\n" +
                "<head>\n" +
                "    <meta content=\"text/html; charset=utf-8\" http-equiv=\"Content-Type\" />\n" +
                "    <title>Reset Password Email Template</title>\n" +
                "    <meta name=\"description\" content=\"Reset Password Email Template.\">\n" +
                "    <style type=\"text/css\">\n" +
                "        a:hover {text-decoration: underline !important;}\n" +
                "    </style>\n" +
                "</head>\n" +
                "\n" +
                "<body marginheight=\"0\" topmargin=\"0\" marginwidth=\"0\" style=\"margin: 0px; background-color: #f2f3f8;\" leftmargin=\"0\">\n" +
                "    <!--100% body table-->\n" +
                "    <table cellspacing=\"0\" border=\"0\" cellpadding=\"0\" width=\"100%\" bgcolor=\"#f2f3f8\"\n" +
                "        style=\"@import url(https://fonts.googleapis.com/css?family=Rubik:300,400,500,700|Open+Sans:300,400,600,700); font-family: 'Open Sans', sans-serif;\">\n" +
                "        <tr>\n" +
                "            <td>\n" +
                "                <table style=\"background-color: #f2f3f8; max-width:670px;  margin:0 auto;\" width=\"100%\" border=\"0\"\n" +
                "                    align=\"center\" cellpadding=\"0\" cellspacing=\"0\">\n" +
                "                    <tr>\n" +
                "                        <td style=\"height:80px;\">&nbsp;</td>\n" +
                "                    </tr>\n" +
                "                    <tr>\n" +
                "                        <td style=\"text-align:center;\">\n" +
                "                          <a href=\"https://rakeshmandal.com\" title=\"logo\" target=\"_blank\">\n" +
                "                            <img width=\"60\" src=\"https://i.ibb.co/hL4XZp2/android-chrome-192x192.png\" title=\"logo\" alt=\"logo\">\n" +
                "                          </a>\n" +
                "                        </td>\n" +
                "                    </tr>\n" +
                "                    <tr>\n" +
                "                        <td style=\"height:20px;\">&nbsp;</td>\n" +
                "                    </tr>\n" +
                "                    <tr>\n" +
                "                        <td>\n" +
                "                            <table width=\"95%\" border=\"0\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\"\n" +
                "                                style=\"max-width:670px;background:#fff; border-radius:3px; text-align:center;-webkit-box-shadow:0 6px 18px 0 rgba(0,0,0,.06);-moz-box-shadow:0 6px 18px 0 rgba(0,0,0,.06);box-shadow:0 6px 18px 0 rgba(0,0,0,.06);\">\n" +
                "                                <tr>\n" +
                "                                    <td style=\"height:40px;\">&nbsp;</td>\n" +
                "                                </tr>\n" +
                "                                <tr>\n" +
                "                                    <td style=\"padding:0 35px;\">\n" +
                "                                        <h1 style=\"color:#1e1e2d; font-weight:500; margin:0;font-size:32px;font-family:'Rubik',sans-serif;\">You have\n" +
                "                                            requested to reset your password</h1>\n" +
                "                                        <span\n" +
                "                                            style=\"display:inline-block; vertical-align:middle; margin:29px 0 26px; border-bottom:1px solid #cecece; width:100px;\"></span>\n" +
                "                                        <p style=\"color:#455056; font-size:15px;line-height:24px; margin:0;\">\n" +
                "                                            We cannot simply send you your old password. A unique link to reset your\n" +
                "                                            password has been generated for you. To reset your password, click the\n" +
                "                                            following link and follow the instructions.\n" +
                "                                        </p>\n" +
                "                                        <a href=\"javascript:void(0);\"\n" +
                "                                            style=\"background:#20e277;text-decoration:none !important; font-weight:500; margin-top:35px; color:#fff;text-transform:uppercase; font-size:14px;padding:10px 24px;display:inline-block;border-radius:50px;\">Reset\n" +
                "                                            Password</a>\n" +
                "                                    </td>\n" +
                "                                </tr>\n" +
                "                                <tr>\n" +
                "                                    <td style=\"height:40px;\">&nbsp;</td>\n" +
                "                                </tr>\n" +
                "                            </table>\n" +
                "                        </td>\n" +
                "                    <tr>\n" +
                "                        <td style=\"height:20px;\">&nbsp;</td>\n" +
                "                    </tr>\n" +
                "                    <tr>\n" +
                "                        <td style=\"text-align:center;\">\n" +
                "                            <p style=\"font-size:14px; color:rgba(69, 80, 86, 0.7411764705882353); line-height:18px; margin:0 0 0;\">&copy; <strong>www.rakeshmandal.com</strong></p>\n" +
                "                        </td>\n" +
                "                    </tr>\n" +
                "                    <tr>\n" +
                "                        <td style=\"height:80px;\">&nbsp;</td>\n" +
                "                    </tr>\n" +
                "                </table>\n" +
                "            </td>\n" +
                "        </tr>\n" +
                "    </table>\n" +
                "    <!--/100% body table-->\n" +
                "</body>\n" +
                "\n" +
                "</html>";
    }
    public void sendOTPNotification(User user, int otp) throws MessagingException {
        String body = "<body style=\"word-spacing: normal; background-color: #fafafa\">\n" +
                "    <div\n" +
                "      style=\"\n" +
                "        display: none;\n" +
                "        font-size: 1px;\n" +
                "        color: #ffffff;\n" +
                "        line-height: 1px;\n" +
                "        max-height: 0px;\n" +
                "        max-width: 0px;\n" +
                "        opacity: 0;\n" +
                "        overflow: hidden;\n" +
                "      \"\n" +
                "    >\n" +
                "      OTP for email confirmation\n" +
                "    </div>\n" +
                "    <div style=\"background-color: #fafafa\" lang=\"und\" dir=\"auto\">\n" +
                "      <table\n" +
                "        align=\"center\"\n" +
                "        border=\"0\"\n" +
                "        cellpadding=\"0\"\n" +
                "        cellspacing=\"0\"\n" +
                "        role=\"presentation\"\n" +
                "        style=\"width: 100%\"\n" +
                "      >\n" +
                "        <tbody>\n" +
                "          <tr>\n" +
                "            <td>\n" +
                "              <!--[if mso | IE]><table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"\" role=\"presentation\" style=\"width:600px;\" width=\"600\" ><tr><td style=\"line-height:0px;font-size:0px;mso-line-height-rule:exactly;\"><![endif]-->\n" +
                "              <div style=\"margin: 0px auto; max-width: 600px\">\n" +
                "                <table\n" +
                "                  align=\"center\"\n" +
                "                  border=\"0\"\n" +
                "                  cellpadding=\"0\"\n" +
                "                  cellspacing=\"0\"\n" +
                "                  role=\"presentation\"\n" +
                "                  style=\"width: 100%\"\n" +
                "                >\n" +
                "                  <tbody>\n" +
                "                    <tr>\n" +
                "                      <td\n" +
                "                        style=\"\n" +
                "                          direction: ltr;\n" +
                "                          font-size: 0px;\n" +
                "                          padding: 16px;\n" +
                "                          text-align: center;\n" +
                "                        \"\n" +
                "                      >\n" +
                "                        <!--[if mso | IE]><table role=\"presentation\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\"><tr><td class=\"\" width=\"600px\" ><table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"\" role=\"presentation\" style=\"width:568px;\" width=\"568\" bgcolor=\"#ffffff\" ><tr><td style=\"line-height:0px;font-size:0px;mso-line-height-rule:exactly;\"><![endif]-->\n" +
                "                        <div\n" +
                "                          style=\"\n" +
                "                            background: #ffffff;\n" +
                "                            background-color: #ffffff;\n" +
                "                            margin: 0px auto;\n" +
                "                            border-radius: 8px;\n" +
                "                            max-width: 568px;\n" +
                "                          \"\n" +
                "                        >\n" +
                "                          <table\n" +
                "                            align=\"center\"\n" +
                "                            border=\"0\"\n" +
                "                            cellpadding=\"0\"\n" +
                "                            cellspacing=\"0\"\n" +
                "                            role=\"presentation\"\n" +
                "                            style=\"\n" +
                "                              background: #ffffff;\n" +
                "                              background-color: #ffffff;\n" +
                "                              width: 100%;\n" +
                "                              border-radius: 8px;\n" +
                "                            \"\n" +
                "                          >\n" +
                "                            <tbody>\n" +
                "                              <tr>\n" +
                "                                <td\n" +
                "                                  style=\"\n" +
                "                                    direction: ltr;\n" +
                "                                    font-size: 0px;\n" +
                "                                    padding: 16px;\n" +
                "                                    text-align: center;\n" +
                "                                  \"\n" +
                "                                >\n" +
                "                                  <!--[if mso | IE]><table role=\"presentation\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\"><tr><td class=\"\" style=\"vertical-align:top;width:536px;\" ><![endif]-->\n" +
                "                                  <div\n" +
                "                                    class=\"mj-column-per-100 mj-outlook-group-fix\"\n" +
                "                                    style=\"\n" +
                "                                      font-size: 0px;\n" +
                "                                      text-align: left;\n" +
                "                                      direction: ltr;\n" +
                "                                      display: inline-block;\n" +
                "                                      vertical-align: top;\n" +
                "                                      width: 100%;\n" +
                "                                    \"\n" +
                "                                  >\n" +
                "                                    <table\n" +
                "                                      border=\"0\"\n" +
                "                                      cellpadding=\"0\"\n" +
                "                                      cellspacing=\"0\"\n" +
                "                                      role=\"presentation\"\n" +
                "                                      width=\"100%\"\n" +
                "                                    >\n" +
                "                                      <tbody>\n" +
                "                                        <tr>\n" +
                "                                          <td\n" +
                "                                            style=\"\n" +
                "                                              vertical-align: top;\n" +
                "                                              padding: 32px;\n" +
                "                                            \"\n" +
                "                                          >\n" +
                "                                            <table\n" +
                "                                              border=\"0\"\n" +
                "                                              cellpadding=\"0\"\n" +
                "                                              cellspacing=\"0\"\n" +
                "                                              role=\"presentation\"\n" +
                "                                              style=\"\"\n" +
                "                                              width=\"100%\"\n" +
                "                                            >\n" +
                "                                              <tbody>\n" +
                "                                                <tr>\n" +
                "                                                  <td\n" +
                "                                                    align=\"center\"\n" +
                "                                                    style=\"\n" +
                "                                                      font-size: 0px;\n" +
                "                                                      padding: 10px 25px;\n" +
                "                                                      padding-bottom: 16px;\n" +
                "                                                      word-break: break-word;\n" +
                "                                                    \"\n" +
                "                                                  >\n" +
                "                                                    <table\n" +
                "                                                      border=\"0\"\n" +
                "                                                      cellpadding=\"0\"\n" +
                "                                                      cellspacing=\"0\"\n" +
                "                                                      role=\"presentation\"\n" +
                "                                                      style=\"\n" +
                "                                                        border-collapse: collapse;\n" +
                "                                                        border-spacing: 0px;\n" +
                "                                                      \"\n" +
                "                                                    >\n" +
                "                                                      <tbody>\n" +
                "                                                        <tr>\n" +
                "                                                          <td\n" +
                "                                                            style=\"width: 180px\"\n" +
                "                                                          >\n" +
                "                                                            <img\n" +
                "                                                              alt=\"LogoIpsum\"\n" +
                "                                                              src=\"https://i.imghippo.com/files/xZkvY1724649505.png\"\n" +
                "                                                              style=\"\n" +
                "                                                                border: 0;\n" +
                "                                                                display: block;\n" +
                "                                                                outline: none;\n" +
                "                                                                text-decoration: none;\n" +
                "                                                                height: auto;\n" +
                "                                                                width: 100%;\n" +
                "                                                                font-size: 13px;\n" +
                "                                                              \"\n" +
                "                                                              width=\"180\"\n" +
                "                                                              height=\"auto\"\n" +
                "                                                            />\n" +
                "                                                          </td>\n" +
                "                                                        </tr>\n" +
                "                                                      </tbody>\n" +
                "                                                    </table>\n" +
                "                                                  </td>\n" +
                "                                                </tr>\n" +
                "                                                <tr>\n" +
                "                                                  <td\n" +
                "                                                    align=\"center\"\n" +
                "                                                    style=\"\n" +
                "                                                      font-size: 0px;\n" +
                "                                                      padding: 0;\n" +
                "                                                      word-break: break-word;\n" +
                "                                                    \"\n" +
                "                                                  >\n" +
                "                                                    <div\n" +
                "                                                      style=\"\n" +
                "                                                        font-family: Inter,\n" +
                "                                                          Arial;\n" +
                "                                                        font-size: 13px;\n" +
                "                                                        line-height: 1;\n" +
                "                                                        text-align: center;\n" +
                "                                                        color: #000000;\n" +
                "                                                      \"\n" +
                "                                                    >\n" +
                "                                                      <h1\n" +
                "                                                        style=\"margin: 16px 0px\"\n" +
                "                                                      >\n" +
                "                                                        Please confirm your\n" +
                "                                                        email\n" +
                "                                                      </h1>\n" +
                "                                                      <p>\n" +
                "                                                        Use this code to confirm\n" +
                "                                                        your email and complete\n" +
                "                                                        signup.\n" +
                "                                                      </p>\n" +
                "                                                    </div>\n" +
                "                                                  </td>\n" +
                "                                                </tr>\n" +
                "                                              </tbody>\n" +
                "                                            </table>\n" +
                "                                          </td>\n" +
                "                                        </tr>\n" +
                "                                      </tbody>\n" +
                "                                    </table>\n" +
                "                                  </div>\n" +
                "                                  <!--[if mso | IE]></td><td class=\"\" style=\"vertical-align:top;width:250px;\" ><![endif]-->\n" +
                "                                  <div\n" +
                "                                    class=\"mj-column-px-250 mj-outlook-group-fix\"\n" +
                "                                    style=\"\n" +
                "                                      font-size: 0px;\n" +
                "                                      text-align: left;\n" +
                "                                      direction: ltr;\n" +
                "                                      display: inline-block;\n" +
                "                                      vertical-align: top;\n" +
                "                                      width: 100%;\n" +
                "                                    \"\n" +
                "                                  >\n" +
                "                                    <table\n" +
                "                                      border=\"0\"\n" +
                "                                      cellpadding=\"0\"\n" +
                "                                      cellspacing=\"0\"\n" +
                "                                      role=\"presentation\"\n" +
                "                                      width=\"100%\"\n" +
                "                                    >\n" +
                "                                      <tbody>\n" +
                "                                        <tr>\n" +
                "                                          <td\n" +
                "                                            style=\"\n" +
                "                                              background-color: #ebe3ff;\n" +
                "                                              border-radius: 8px;\n" +
                "                                              vertical-align: top;\n" +
                "                                              padding: 16px;\n" +
                "                                            \"\n" +
                "                                          >\n" +
                "                                            <table\n" +
                "                                              border=\"0\"\n" +
                "                                              cellpadding=\"0\"\n" +
                "                                              cellspacing=\"0\"\n" +
                "                                              role=\"presentation\"\n" +
                "                                              style=\"\"\n" +
                "                                              width=\"100%\"\n" +
                "                                            >\n" +
                "                                              <tbody>\n" +
                "                                                <tr>\n" +
                "                                                  <td\n" +
                "                                                    align=\"center\"\n" +
                "                                                    style=\"\n" +
                "                                                      font-size: 0px;\n" +
                "                                                      padding: 0;\n" +
                "                                                      word-break: break-word;\n" +
                "                                                    \"\n" +
                "                                                  >\n" +
                "                                                    <div\n" +
                "                                                      style=\"\n" +
                "                                                        font-family: Inter,\n" +
                "                                                          Arial;\n" +
                "                                                        font-size: 32px;\n" +
                "                                                        font-weight: 700;\n" +
                "                                                        letter-spacing: 16px;\n" +
                "                                                        line-height: 32px;\n" +
                "                                                        text-align: center;\n" +
                "                                                        color: #000000;\n" +
                "                                                      \"\n" +
                "                                                    >\n" +
                "                                                      <p\n" +
                "                                                        style=\"\n" +
                "                                                          font-size: 32px;\n" +
                "                                                          margin: 0px;\n" +
                "                                                          margin-right: -16px;\n" +
                "                                                          padding: 0px;\n" +
                "                                                        \"\n" +
                "                                                      >\n" +
                "                                                        "+otp+"\n" +
                "                                                      </p>\n" +
                "                                                    </div>\n" +
                "                                                  </td>\n" +
                "                                                </tr>\n" +
                "                                              </tbody>\n" +
                "                                            </table>\n" +
                "                                          </td>\n" +
                "                                        </tr>\n" +
                "                                      </tbody>\n" +
                "                                    </table>\n" +
                "                                  </div>\n" +
                "                                  <!--[if mso | IE]></td><td class=\"\" style=\"vertical-align:top;width:536px;\" ><![endif]-->\n" +
                "                                  <div\n" +
                "                                    class=\"mj-column-per-100 mj-outlook-group-fix\"\n" +
                "                                    style=\"\n" +
                "                                      font-size: 0px;\n" +
                "                                      text-align: left;\n" +
                "                                      direction: ltr;\n" +
                "                                      display: inline-block;\n" +
                "                                      vertical-align: top;\n" +
                "                                      width: 100%;\n" +
                "                                    \"\n" +
                "                                  >\n" +
                "                                    <table\n" +
                "                                      border=\"0\"\n" +
                "                                      cellpadding=\"0\"\n" +
                "                                      cellspacing=\"0\"\n" +
                "                                      role=\"presentation\"\n" +
                "                                      width=\"100%\"\n" +
                "                                    >\n" +
                "                                      <tbody>\n" +
                "                                        <tr>\n" +
                "                                          <td\n" +
                "                                            style=\"\n" +
                "                                              vertical-align: top;\n" +
                "                                              padding-top: 16px;\n" +
                "                                            \"\n" +
                "                                          >\n" +
                "                                            <table\n" +
                "                                              border=\"0\"\n" +
                "                                              cellpadding=\"0\"\n" +
                "                                              cellspacing=\"0\"\n" +
                "                                              role=\"presentation\"\n" +
                "                                              style=\"\"\n" +
                "                                              width=\"100%\"\n" +
                "                                            >\n" +
                "                                              <tbody>\n" +
                "                                                <tr>\n" +
                "                                                  <td\n" +
                "                                                    align=\"center\"\n" +
                "                                                    style=\"\n" +
                "                                                      font-size: 0px;\n" +
                "                                                      padding: 10px 25px;\n" +
                "                                                      word-break: break-word;\n" +
                "                                                    \"\n" +
                "                                                  >\n" +
                "                                                    <div\n" +
                "                                                      style=\"\n" +
                "                                                        font-family: Inter,\n" +
                "                                                          Arial;\n" +
                "                                                        font-size: 13px;\n" +
                "                                                        line-height: 1;\n" +
                "                                                        text-align: center;\n" +
                "                                                        color: #555555;\n" +
                "                                                      \"\n" +
                "                                                    >\n" +
                "                                                      <p>\n" +
                "                                                        This code is valid for\n" +
                "                                                        5 minutes.\n" +
                "                                                      </p>\n" +
                "                                                    </div>\n" +
                "                                                  </td>\n" +
                "                                                </tr>\n" +
                "                                              </tbody>\n" +
                "                                            </table>\n" +
                "                                          </td>\n" +
                "                                        </tr>\n" +
                "                                      </tbody>\n" +
                "                                    </table>\n" +
                "                                  </div>\n" +
                "                                  <!--[if mso | IE]></td></tr></table><![endif]-->\n" +
                "                                </td>\n" +
                "                              </tr>\n" +
                "                            </tbody>\n" +
                "                          </table>\n" +
                "                        </div>\n" +
                "                        <!--[if mso | IE]></td></tr></table></td></tr></table><![endif]-->\n" +
                "                      </td>\n" +
                "                    </tr>\n" +
                "                  </tbody>\n" +
                "                </table>\n" +
                "              </div>\n" +
                "              <!--[if mso | IE]></td></tr></table><![endif]-->\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </tbody>\n" +
                "      </table>\n" +
                "    </div>\n" +
                "  </body>";
        sendEmail(user,body, "Account verification");
    }
    private void sendEmail(User user, String page,String subject) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
        helper.setTo(user.getEmail());
        helper.setFrom(sendFrom);
        helper.setSubject(subject);
        helper.setText(page,true);
        mailSender.send(mimeMessage);
    }
}
