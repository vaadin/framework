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
