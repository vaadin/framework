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
            // Assumes we can use any port >= 2000,
            // except for 2049 in Firefox...
            proxyPort = availablePort.addAndGet(1);
            if (proxyPort == 2049) {
                // Restricted in Firefox, see
                // http://www-archive.mozilla.org/projects/netlib/PortBanning.html#portlist
                proxyPort = availablePort.addAndGet(1);
            }
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
