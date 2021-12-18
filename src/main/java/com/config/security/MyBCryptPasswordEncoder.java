package com.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

@Component
public class MyBCryptPasswordEncoder implements PasswordEncoder {


    @Value("${jwt.password.encoder.secret:secret}")
    private String secret;

    @Value("${jwt.password.encoder.iteration:100}")
    private Integer iteration;

    @Value("${jwt.password.encoder.keylength:100}")
    private Integer keylength;


    @Override
    public String encode(CharSequence cs) {
        try {
            byte[] result =
                    SecretKeyFactory
                            .getInstance("PBKDF2WithHmacSHA512")
                            .generateSecret(new PBEKeySpec(cs.toString().toCharArray(), secret.getBytes(), iteration, keylength))
                            .getEncoded();
            return Base64.getEncoder().encodeToString(result);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public boolean matches(CharSequence cs, String string) {
        return encode(cs).equals(string);
    }

}