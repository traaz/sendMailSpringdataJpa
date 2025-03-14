package com.project.emailValidation.repository;

import com.project.emailValidation.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByMail(String mail);


    User findByMail(String mail);

    //parametre olarak verilen tarihten onceki, valid false, code null olmayan kullaniciler getir.
    List<User> findByIsValidFalseAndVerificationCodeNotNullAndRegisterDateBefore(LocalDateTime registerDate);


}
