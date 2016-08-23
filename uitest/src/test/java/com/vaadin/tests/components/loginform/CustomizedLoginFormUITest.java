package com.vaadin.tests.components.loginform;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.By;

public class CustomizedLoginFormUITest extends LoginFormUITest {

    private static final String LABELLED_BY = "aria-labelledby";

    @Test
    public void captionsCorrect() {
        openTestURL();
        Assert.assertEquals("Identifiant", getUsernameCaption());
        Assert.assertEquals("Mot de passe", getPasswordCaption());
        Assert.assertEquals("Se connecter", getLoginCaption());
    }

    private String getLoginCaption() {
        return getLogin().getText();
    }

    private String getPasswordCaption() {
        String passwordCaptionId = getPassword().getAttribute(LABELLED_BY);
        return findElement(By.id(passwordCaptionId)).getText();
    }

    private String getUsernameCaption() {
        String usernameCaptionId = getUsername().getAttribute(LABELLED_BY);
        return findElement(By.id(usernameCaptionId)).getText();
    }
}
