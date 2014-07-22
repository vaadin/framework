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
package com.vaadin.tests.server;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.easymock.EasyMock;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.server.ServiceException;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinServletRequest;
import com.vaadin.server.VaadinServletService;
import com.vaadin.server.VaadinSession;
import com.vaadin.server.communication.ServerRpcHandler.RpcRequest;
import com.vaadin.shared.ApplicationConstants;
import com.vaadin.tests.util.AlwaysLockedVaadinSession;
import com.vaadin.tests.util.MockDeploymentConfiguration;

/**
 * Test the actual csrf token validation by the server.
 * 
 * @since
 * @author Vaadin Ltd
 */
public class CsrfTokenMissingTestServer {

    // Dummy fields just to run the test.
    private VaadinServlet mockServlet;

    // The mock deployment configuration.
    private MockDeploymentConfiguration mockDeploymentConfiguration;

    private VaadinServletService mockService;

    // The mock UI session.
    private VaadinSession mockSession;

    // The mock vaadin request.
    private VaadinServletRequest vaadinRequest;

    /**
     * Initialize the mock servlet and other stuff for our tests.
     */
    @Before
    public void initMockStuff() throws ServiceException {
        mockServlet = new VaadinServlet();
        mockDeploymentConfiguration = new MockDeploymentConfiguration();

        mockService = new VaadinServletService(mockServlet,
                mockDeploymentConfiguration);

        mockSession = new AlwaysLockedVaadinSession(mockService);

        vaadinRequest = new VaadinServletRequest(
                EasyMock.createMock(HttpServletRequest.class), mockService);

    }

    private enum TokenType {
        MISSING, INVALID, VALID
    }

    private TokenType tokenType;

    private String invalidToken;

    public String getInvalidToken() {
        if (invalidToken == null) {
            // Just making sure this will never be in the same format as a valid
            // token.
            invalidToken = UUID.randomUUID().toString().substring(1);
        }
        return invalidToken;
    }

    private String getValidToken() {
        return mockSession.getCsrfToken();
    }

    /*
     * Gets the payload with the default token.
     */
    private String getPayload() {
        switch (tokenType) {
        case MISSING:
            return getPayload(null);

        case INVALID:
            return getPayload(getInvalidToken());

        case VALID:
            return getPayload(getValidToken());
        }

        return null;
    }

    /*
     * Gets the payload with the specified token.
     */
    private String getPayload(String token) {
        return "{"
                + (token != null ? "\"csrfToken\":" + "\"" + token + "\", "
                        : "")
                + "\"rpc\":[[\"0\",\"com.vaadin.shared.ui.ui.UIServerRpc\",\"resize\",[\"449\",\"1155\",\"1155\",\"449\"]],[\"4\",\"com.vaadin.shared.ui.button.ButtonServerRpc\",\"click\",[{\"clientY\":\"53\", \"clientX\":\"79\", \"shiftKey\":false, \"button\":\"LEFT\", \"ctrlKey\":false, \"type\":\"1\", \"metaKey\":false, \"altKey\":false, \"relativeY\":\"17\", \"relativeX\":\"61\"}]]], \"syncId\":1}";
    }

    /*
     * Init the test parameters.
     */
    private void initTest(boolean enableSecurity, TokenType tokenType) {
        mockDeploymentConfiguration.setXsrfProtectionEnabled(enableSecurity);
        this.tokenType = tokenType;
    }

    /*
     * Create the requets.
     */
    private RpcRequest createRequest() {
        try {
            return new RpcRequest(getPayload(), vaadinRequest);
        } catch (JSONException e) {
            LOGGER.log(Level.SEVERE, "", e);

            Assert.assertTrue(false);
            return null;
        }
    }

    /*
     * Gets whether the token from the request is the default one.
     */
    private boolean isDefaultToken(RpcRequest rpcRequest) {
        return ApplicationConstants.CSRF_TOKEN_DEFAULT_VALUE.equals(rpcRequest
                .getCsrfToken());
    }

    /*
     * Gets whether the token from the request is the invalid one.
     */
    private boolean isInvalidToken(RpcRequest rpcRequest) {
        return getInvalidToken().equals(rpcRequest.getCsrfToken());
    }

    /*
     * Gets whether the token from the request is the valid one.
     */
    private boolean isValidToken(RpcRequest rpcRequest) {
        return getValidToken().equals(rpcRequest.getCsrfToken());
    }

    /*
     * Gets whether the token from the request is valid.
     */
    private boolean isRequestValid(RpcRequest rpcRequest) {
        return VaadinService.isCsrfTokenValid(mockSession,
                rpcRequest.getCsrfToken());
    }

    private static Logger LOGGER = Logger
            .getLogger(CsrfTokenMissingTestServer.class.getName());
    static {
        LOGGER.setLevel(Level.ALL);
    }

    @Test
    public void securityOnAndNoToken() {
        initTest(true, TokenType.MISSING);

        RpcRequest rpcRequest = createRequest();

        Assert.assertTrue(isDefaultToken(rpcRequest));
        Assert.assertFalse(isRequestValid(rpcRequest));
    }

    @Test
    public void securityOffAndNoToken() {
        initTest(false, TokenType.MISSING);

        RpcRequest rpcRequest = createRequest();

        Assert.assertTrue(isDefaultToken(rpcRequest));
        Assert.assertTrue(isRequestValid(rpcRequest));
    }

    @Test
    public void securityOnAndInvalidToken() {
        initTest(true, TokenType.INVALID);

        RpcRequest rpcRequest = createRequest();

        Assert.assertTrue(isInvalidToken(rpcRequest));
        Assert.assertFalse(isRequestValid(rpcRequest));
    }

    @Test
    public void securityOffAndInvalidToken() {
        initTest(false, TokenType.INVALID);

        RpcRequest rpcRequest = createRequest();

        Assert.assertTrue(isInvalidToken(rpcRequest));
        Assert.assertTrue(isRequestValid(rpcRequest));
    }

    @Test
    public void securityOnAndValidToken() {
        initTest(true, TokenType.VALID);

        RpcRequest rpcRequest = createRequest();

        Assert.assertTrue(isValidToken(rpcRequest));
        Assert.assertTrue(isRequestValid(rpcRequest));
    }

    @Test
    public void securityOffAndValidToken() {
        initTest(false, TokenType.VALID);

        RpcRequest rpcRequest = createRequest();

        Assert.assertTrue(isValidToken(rpcRequest));
        Assert.assertTrue(isRequestValid(rpcRequest));
    }

}
