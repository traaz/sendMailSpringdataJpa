package com.project.emailValidation.service;

import com.project.emailValidation.entity.User;
import com.project.emailValidation.repository.UserRepository;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserCleanupService {
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserCleanupService.class);

    public UserCleanupService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Scheduled(fixedRate = 180000) // Her 3 dakikada bir çalışacak
    public void cleanUpExpiredUsers() {
        LocalDateTime expirationTime = LocalDateTime.now().minusMinutes(2); //mevcut saatten iki dk öncesine kadar olusturulan tum kayitlari sil
        List<User> expiredUsers = userRepository.findByIsValidFalseAndVerificationCodeNotNullAndRegisterDateBefore(expirationTime);
        for (User user : expiredUsers) {
            userRepository.delete(user);
            logger.info("Silinen kullanıcı: " + user.getMail());
        }
        logger.info("Scheduled çalisti");

    }

}
