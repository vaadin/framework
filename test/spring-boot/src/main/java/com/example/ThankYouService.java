package com.example;

import org.springframework.stereotype.Service;

@Service
public class ThankYouService {

    public static final String THANK_YOU_TEXT = "Thank you for clicking.";

    public String getText() {
        return THANK_YOU_TEXT;
    }
}
