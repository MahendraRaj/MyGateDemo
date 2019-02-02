package com.mygate.mahendran.mygatedemo.controller;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class SimpleOtpGenerator {

    public static String random(int size) {

        StringBuilder generatedToken = new StringBuilder();
        try {
            /*
            1. 128 bit.. takes years to crack the password
            2. uses random OS information(like the time between the two keys pressed,number of keys pressed  etc) to generate password
            which makes it harder to crack
            3. Uses Pseudo-Random Number Generation to generate 6 digit Code */
            SecureRandom number = SecureRandom.getInstance("SHA1PRNG");
            for (int i = 0; i < size; i++) {
                generatedToken.append(number.nextInt(9));
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return generatedToken.toString();
    }
}
