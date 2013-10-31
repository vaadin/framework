/*
 * Copyright 2000-2013 Vaadin Ltd.
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
package com.vaadin.tests.tb3;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.After;
import org.junit.Before;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public abstract class MultiBrowserTestWithProxy extends MultiBrowserTest {

    private static AtomicInteger availablePort = new AtomicInteger(2000);
    private Session proxySession;
    private Integer proxyPort = null;

    @Before
    public void setupInitialProxy() throws JSchException {
        connectProxy();
    }

    @After
    public void teardownProxy() {
        disconnectProxy();
    }

    protected Integer getProxyPort() {
        if (proxyPort == null) {
            // Assumes we can use any port >= 2000
            proxyPort = availablePort.addAndGet(1);
        }
        return proxyPort;
    }

    /**
     * Disconnects the proxy if active
     */
    protected void disconnectProxy() {
        if (proxySession == null) {
            return;
        }
        proxySession.disconnect();
        proxySession = null;
    }

    /**
     * Ensure the proxy is active. Does nothing if the proxy is already active.
     */
    protected void connectProxy() throws JSchException {
        if (proxySession != null) {
            return;
        }

        createProxy(getProxyPort());
    }

    private void createProxy(int proxyPort) throws JSchException {
        JSch j = new JSch();
        String keyFile = System.getProperty("sshkey.file");
        if (keyFile == null) {
            keyFile = "~/.ssh/id_rsa";
        }
        j.addIdentity(keyFile);
        proxySession = j.getSession("localhost");
        proxySession.setConfig("StrictHostKeyChecking", "no");
        proxySession.setPortForwardingL("0.0.0.0", proxyPort,
                super.getDeploymentHostname(), super.getDeploymentPort());
        proxySession.connect();
    }

    @Override
    protected String getBaseURL() {
        return "http://" + getDeploymentHostname() + ":" + getProxyPort();
    }

}
