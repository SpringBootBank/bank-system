package com.itgirls.bank_system.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderUtil {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "12345";
        String hashedPassword = encoder.encode(rawPassword);
        System.out.println("Хеш пароля: " + hashedPassword);
    }
}

