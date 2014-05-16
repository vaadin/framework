/*
 * Copyright 2000-2014 Vaadin Ltd.
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
package com.vaadin.tests.integration;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.vaadin.tests.tb3.PrivateTB3Configuration;

public class WebSpherePortalIntegrationTest extends PrivateTB3Configuration {

    @BeforeClass
    public static void deployPortlet() throws JSchException, SftpException {
        Session session = openSession();

        uploadDemoApplication(session);

        sendCommand(session, "ant -f deploy.xml get-lock startup-and-deploy");

        session.disconnect();
    }

    private static void uploadDemoApplication(Session session)
            throws JSchException, SftpException {
        ChannelSftp sftpChannel = (ChannelSftp) session.openChannel("sftp");
        sftpChannel.connect();

        String applicationPath = System.getProperty("demo.war");
        if (new File(applicationPath).exists()) {
            sftpChannel.put(applicationPath, "demo.war");
            sftpChannel.disconnect();
        } else {
            sftpChannel.disconnect();
            throw new AssertionError("Demo application not found at "
                    + applicationPath);
        }
    }

    private static void sendCommand(Session session, String command)
            throws JSchException {
        ChannelExec channel = (ChannelExec) session.openChannel("exec");
        channel.setCommand(command);
        BufferedReader in;
        try {
            in = new BufferedReader(new InputStreamReader(
                    channel.getInputStream()));

            channel.connect();

            String msg = null;

            while ((msg = in.readLine()) != null) {
                System.out.println(msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        channel.disconnect();
    }

    private static Session openSession() throws JSchException {
        JSch jsch = new JSch();
        Session session = jsch.getSession("integration",
                "websphereportal8.devnet.vaadin.com", 22);
        jsch.addIdentity("~/.ssh/id_dsa");
        session.setConfig("StrictHostKeyChecking", "no");

        session.connect();
        return session;
    }

    @AfterClass
    public static void teardown() throws JSchException {
        Session session = openSession();

        sendCommand(session, "ant -f deploy.xml release-lock");

        session.disconnect();
    }

    @Override
    protected java.lang.String getTestUrl() {
        return "http://websphereportal8.devnet.vaadin.com:10039/wps/portal";
    }

    @Override
    public void setup() throws Exception {
        super.setup();

        openTestURL();

        if (!isLoggedIn()) {
            login();
        }

        waitUntilPortletIsLoaded();
    }

    private void waitUntilPortletIsLoaded() {
        WebDriverWait wait = new WebDriverWait(driver, 15);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By
                .className("v-app")));
    }

    private boolean isLoggedIn() {
        return driver.findElements(By.linkText("Log Out")).size() == 1;
    }

    private void login() {
        driver.findElement(By.linkText("Log In")).click();
        driver.findElement(By.id("userID")).sendKeys("test");
        driver.findElement(By.id("password")).sendKeys("testtest");

        hitButton("login.button.login");
    }

    @Test
    public void portletHasExpectedLayout() throws IOException {
        compareScreen("initial");
    }

    @Test
    public void viewModeIsSetToEdit() throws IOException {
        driver.findElement(By.linkText("Edit")).click();

        assertThat(driver.findElement(By.tagName("input")).isEnabled(),
                is(true));

    }

}
