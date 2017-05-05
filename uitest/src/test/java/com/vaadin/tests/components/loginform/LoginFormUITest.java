package com.vaadin.tests.components.loginform;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.LoginFormElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class LoginFormUITest extends SingleBrowserTest {

    @Test
    public void login() {
        openTestURL();
        getUsername().sendKeys("user123");
        getPassword().sendKeys("pass123");
        getLogin().click();
        Assert.assertEquals("User 'user123', password='pass123' logged in",
                getInfo().getText());
    }

    protected WebElement getInfo() {
        return findElement(By.id("info"));
    }

    protected WebElement getUsername() {
        return findElement(By.id("username"));
    }

    protected WebElement getPassword() {
        return findElement(By.id("password"));
    }

    protected WebElement getLogin() {
        return $(LoginFormElement.class).first().$(ButtonElement.class).first();
    }
}
