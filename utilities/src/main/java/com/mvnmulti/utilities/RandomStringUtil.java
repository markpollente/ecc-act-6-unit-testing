package com.mvnmulti.utilities;

import java.util.Random;

public class RandomStringUtil {

    public static String generateRandomAscii(int length) {
        Random random = new Random();
        StringBuilder rand = new StringBuilder();
        for (int i = 0; i < length; i++) {
            rand.append((char) (random.nextInt(94) + 33));
        }
        return rand.toString();
    }
}