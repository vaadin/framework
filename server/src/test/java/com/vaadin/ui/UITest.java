package com.vaadin.ui;

import java.util.Properties;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.servlet.ServletConfig;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.server.DefaultDeploymentConfiguration;
import com.vaadin.server.MockServletConfig;
import com.vaadin.server.MockVaadinSession;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinServletService;
import com.vaadin.server.VaadinSession;
import com.vaadin.server.communication.PushConnection;
import com.vaadin.shared.communication.PushMode;

public class UITest {

    @Test
    public void removeFromSessionWithExternalLock() throws Exception {
        // See https://dev.vaadin.com/ticket/18436
        final UI ui = new UI() {

            @Override
            protected void init(VaadinRequest request) {
            }

        };
        final Lock externalLock = new ReentrantLock();

        ServletConfig servletConfig = new MockServletConfig();
        VaadinServlet servlet = new VaadinServlet();
        servlet.init(servletConfig);

        DefaultDeploymentConfiguration deploymentConfiguration = new DefaultDeploymentConfiguration(
                UI.class, new Properties());

        MockVaadinSession session = new MockVaadinSession(
                new VaadinServletService(servlet, deploymentConfiguration));
        session.lock();
        ui.setSession(session);
        ui.getPushConfiguration().setPushMode(PushMode.MANUAL);
        ui.setPushConnection(new PushConnection() {

            private boolean connected = true;

            @Override
            public void push() {
            }

            @Override
            public boolean isConnected() {
                return connected;
            }

            @Override
            public void disconnect() {
                externalLock.lock();
                try {
                    connected = false;
                } finally {
                    externalLock.unlock();
                }

            }
        });
        session.unlock();

        final CountDownLatch websocketReachedCheckpoint = new CountDownLatch(1);
        final CountDownLatch uiDisconnectReachedCheckpoint = new CountDownLatch(
                1);

        final VaadinSession uiSession = ui.getSession();
        final ConcurrentLinkedQueue<Exception> exceptions = new ConcurrentLinkedQueue<Exception>();

        // Simulates the websocket close thread
        Runnable websocketClose = new Runnable() {
            @Override
            public void run() {
                externalLock.lock();
                // Wait for disconnect thread to lock VaadinSession
                websocketReachedCheckpoint.countDown();
                try {
                    uiDisconnectReachedCheckpoint.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    exceptions.add(e);
                    return;
                }
                uiSession.lock();
                externalLock.unlock();
            }
        };

        Runnable disconnectPushFromUI = new Runnable() {
            @Override
            public void run() {
                uiSession.lock();
                // Wait for websocket thread to lock external lock
                uiDisconnectReachedCheckpoint.countDown();
                try {
                    websocketReachedCheckpoint.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    exceptions.add(e);
                    return;
                }

                ui.setSession(null);
                uiSession.unlock();
            }
        };

        Thread websocketThread = new Thread(websocketClose);
        websocketThread.start();
        Thread uiDisconnectThread = new Thread(disconnectPushFromUI);
        uiDisconnectThread.start();

        websocketThread.join(5000);
        uiDisconnectThread.join(5000);

        if (websocketThread.isAlive() || uiDisconnectThread.isAlive()) {
            websocketThread.interrupt();
            uiDisconnectThread.interrupt();
            Assert.fail("Threads are still running");
        }
        if (!exceptions.isEmpty()) {
            for (Exception e : exceptions) {
                e.printStackTrace();
            }
            Assert.fail("There were exceptions in the threads");
        }

        Assert.assertNull(ui.getSession());

        // PushConnection is set to null in another thread. We need to wait for
        // that to happen
        for (int i = 0; i < 10; i++) {
            if (ui.getPushConnection() == null) {
                break;
            }

            Thread.sleep(500);
        }
        Assert.assertNull(ui.getPushConnection());

    }
}
