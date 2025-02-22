package com.project.emailValidation.requests;

import jakarta.validation.constraints.NotNull;
import lombok.*;



@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    @NotNull(message = "Mail gereklidir.")
    private String mail;
    @NotNull(message = "Şifre gereklidir.")
    private String password;

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
