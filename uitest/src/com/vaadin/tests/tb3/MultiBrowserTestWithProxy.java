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
package com.vaadin.tests.tb3;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.After;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.vaadin.testbench.parallel.TestCategory;

@TestCategory("push")
public abstract class MultiBrowserTestWithProxy extends MultiBrowserTest {

    private static AtomicInteger availablePort = new AtomicInteger(2000);
    private Session proxySession;
    private Integer proxyPort = null;
    private JSch jsch;
    private static String sshDir = System.getProperty("user.home") + "/.ssh/";
    private String[] publicKeys = new String[] {
            System.getProperty("sshkey.file"), sshDir + "id_rsa",
            sshDir + "id_dsa", sshDir + "id_rsa2" };

    @Override
    public void setup() throws Exception {
        super.setup();
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
        for (int i = 0; i < 10; i++) {
            // Potential workaround for problem with establishing many ssh
            // connections at the same time
            try {
                createProxy(getProxyPort());
                break;
            } catch (JSchException e) {
                try {
                    sleep(500);
                } catch (InterruptedException e1) {
                }
                if (i == 9) {
                    throw new RuntimeException(
                            "All 10 attempts to connect a proxy failed", e);
                }
            }
        }
    }

    private void createProxy(int proxyPort) throws JSchException {
        if (jsch == null) {
            jsch = new JSch();

            String keyFile = null;
            for (String publicKey : publicKeys) {
                if (publicKey != null) {
                    if (new File(publicKey).exists()) {
                        keyFile = publicKey;
                        break;
                    }
                }
            }
            jsch.addIdentity(keyFile);
        }
        proxySession = jsch.getSession("localhost");
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
