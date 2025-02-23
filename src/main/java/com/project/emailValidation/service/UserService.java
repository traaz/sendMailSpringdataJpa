package com.project.emailValidation.service;

import com.project.emailValidation.requests.RegisterRequest;
import com.project.emailValidation.entity.User;
import com.project.emailValidation.repository.UserRepository;
import com.project.emailValidation.requests.VerifyRequest;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class UserService {
        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;
        private final JavaMailSender mailSender;
        private static final Logger logger = LoggerFactory.getLogger(UserService.class);
        public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JavaMailSender mailSender) {
                this.userRepository = userRepository;
                this.passwordEncoder =  passwordEncoder;
                this.mailSender = mailSender;
        }


        public String addUser(RegisterRequest registerRequest){
                if(userRepository.existsByMail(registerRequest.getMail())){
                        return "Kullanici mevcut";
                }else{
                        User user = new User();
                        user.setMail(registerRequest.getMail());
                        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
                        user.setVerificationCode(generateCode());
                        user.setRegisterDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
                        try {
                                sendEmailForCode(registerRequest.getMail(), user.getVerificationCode());
                                userRepository.save(user);
                        }catch (Exception e){
                                throw new RuntimeException("Mail gönderiminde hata alindi " + e.getMessage());
                        }
                        logger.info("Kullanici eklendi: " + user.getMail());
                        return "Kullanici eklendi. Lütfen mail adresinize gelen kodu doğrulayiniz.";
                }
        }
        public String generateCode(){
                return RandomStringUtils.randomNumeric(6);        }


        public void sendEmailForCode(String email, String verificationCode ){
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom("no-reply@gmail.com");
                message.setTo(email);
                message.setSubject("E-posta Doğrulama Kodu");
                message.setText("Lütfen aşağıdaki doğrulama kodunu kullanarak bir dakika içinde hesabınızı doğrulayın:\n" + verificationCode );
                mailSender.send(message);

        }
        public String verifyCode(VerifyRequest verifyRequest){
                User user = userRepository.findByMail(verifyRequest.getMail());
             //   LocalDateTime  userRegisterDate = user.getRegisterDate();
                if(user == null){
                        return "Kullanici yoktur.";
                }
                else if(!(user.getRegisterDate() == null ) && LocalDateTime.now().isAfter(user.getRegisterDate().plusMinutes(1))){ //userregisterdate 1 ekelndikten sonra now'dan sonra mı. sonraysa buraya girer
                        userRepository.delete(user);
                        return "Verilen sürede doğrulama yapmadiniz..Tekrar kayit olunuz..";
                }
                else{
                       if(user.getVerificationCode().equals(verifyRequest.getVerificationCode())){
                               user.setValid(true);
                               user.setVerificationCode("");
                                user.setVerificationDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
                               userRepository.save(user);
                               return "Kullanici doğrulandi";
                       }else{
                               return "Geçersiz doğrulama kodu";
                       }
                }
        }
}
