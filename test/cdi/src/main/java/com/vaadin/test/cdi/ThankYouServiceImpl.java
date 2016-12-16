package com.vaadin.test.cdi;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ThankYouServiceImpl implements ThankYouService {

    public static final String THANK_YOU_TEXT = "Thank you for clicking!";

    @Override
    public String getText() {
        return THANK_YOU_TEXT;
    }
}
