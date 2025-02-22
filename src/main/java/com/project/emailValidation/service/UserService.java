package com.project.emailValidation.service;

import com.project.emailValidation.requests.RegisterRequest;
import com.project.emailValidation.entity.User;
import com.project.emailValidation.repository.UserRepository;
import com.project.emailValidation.requests.VerifyRequest;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;
        private final JavaMailSender mailSender;
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
                        userRepository.save(user);
                        sendEmailForCode(registerRequest.getMail(), user.getVerificationCode());
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
                message.setText("Lütfen aşağıdaki doğrulama kodunu kullanarak hesabınızı doğrulayın:\n" + verificationCode );
                try{
                        mailSender.send(message);
                        System.out.println("E-posta gönderildi: " + email); // Log eklendi

                }catch (Exception e){
                        throw new RuntimeException("Mail Gönderiminde Hata " + e.getMessage());
                }



        }
        public String verifyCode(VerifyRequest verifyRequest){
                User user = userRepository.findByMail(verifyRequest.getMail());
                if(user == null){
                        return "Kullanici yoktur.";
                }
                else{
                       if(user.getVerificationCode().equals(verifyRequest.getVerificationCode())){
                               user.setValid(true);
                               user.setVerificationCode("");
                               userRepository.save(user);
                               return "Kullanici doğrulandi";
                       }else{
                               return "Geçersiz doğrulama kodu";
                       }
                }
        }
}
