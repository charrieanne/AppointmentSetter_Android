package com.example.ltoappointment;

import java.util.Random;

public class RandomString {
    private final String NUMBERS = "0123456789";
    private final char[] ALPHANUMERIC = (NUMBERS+NUMBERS).toCharArray();

    public String generateAlphanumeric(int length){
        StringBuilder result = new StringBuilder();
        for(int i=0; i<length; i++){
            result.append(ALPHANUMERIC[new Random().nextInt(ALPHANUMERIC.length)]);
        }
        return result.toString();
    }
}
