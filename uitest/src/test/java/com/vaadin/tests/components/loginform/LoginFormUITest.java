/*
 * Copyright 2000-2016 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.tests.components.loginform;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.customelements.LoginFormElement;
import com.vaadin.testbench.elements.ButtonElement;
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
