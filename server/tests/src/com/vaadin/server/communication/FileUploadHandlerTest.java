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
package com.vaadin.server.communication;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.vaadin.server.ClientConnector;
import com.vaadin.server.ServletPortletHelper;
import com.vaadin.server.StreamVariable;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.ConnectorTracker;
import com.vaadin.ui.UI;

public class FileUploadHandlerTest {

    private FileUploadHandler handler;
    @Mock private VaadinResponse response;
    @Mock private StreamVariable streamVariable;
    @Mock private ClientConnector clientConnector;
    @Mock private VaadinRequest request;
    @Mock private UI ui;
    @Mock private ConnectorTracker connectorTracker;
    @Mock private VaadinSession session;
    @Mock private OutputStream responseOutput;

    private int uiId = 123;
    private final String connectorId = "connectorId";
    private final String variableName = "name";
    private final String expectedSecurityKey = "key";

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);

        handler = new FileUploadHandler();

        mockRequest();
        mockConnectorTracker();
        mockUi();

        when(clientConnector.isConnectorEnabled()).thenReturn(true);
        when(streamVariable.getOutputStream()).thenReturn(mock(OutputStream.class));
        when(response.getOutputStream()).thenReturn(responseOutput);
    }

    private void mockConnectorTracker() {
        when(connectorTracker.getSeckey(streamVariable)).thenReturn(expectedSecurityKey);
        when(connectorTracker.getStreamVariable(connectorId, variableName)).thenReturn(streamVariable);
        when(connectorTracker.getConnector(connectorId)).thenReturn(clientConnector);
    }

    private void mockRequest() throws IOException {
        when(request.getPathInfo()).thenReturn("/" + ServletPortletHelper.UPLOAD_URL_PREFIX + uiId + "/"+ connectorId + "/" + variableName + "/" + expectedSecurityKey);
        when(request.getInputStream()).thenReturn(createInputStream("foobar"));
        when(request.getHeader("Content-Length")).thenReturn("6");
        when(request.getContentType()).thenReturn("foobar");
    }

    private InputStream createInputStream(final String content) {
        return new InputStream() {
            int counter = 0;
            byte[] msg = content.getBytes();

            @Override
            public int read() throws IOException {
                if(counter > msg.length + 1) {
                    throw new AssertionError("-1 was ignored by FileUploadHandler.");
                }

                if(counter >= msg.length) {
                    counter++;
                    return -1;
                }

                return msg[counter++];
            }
        };
    }

    private void mockUi() {
        when(ui.getConnectorTracker()).thenReturn(connectorTracker);
        when(session.getUIById(uiId)).thenReturn(ui);
    }

    /**
     * Tests whether we get infinite loop if InputStream is already read (#10096)
     */
    @Test(expected = IOException.class)
    public void exceptionIsThrownOnUnexpectedEnd() throws IOException {
        when(request.getInputStream()).thenReturn(createInputStream(""));
        when(request.getHeader("Content-Length")).thenReturn("1");

        handler.doHandleSimpleMultipartFileUpload(null, request, null, null,
                null, null, null);
    }

    @Test
    public void responseIsSentOnCorrectSecurityKey() throws IOException {
        when(connectorTracker.getSeckey(streamVariable)).thenReturn(expectedSecurityKey);

        handler.handleRequest(session, request, response);

        verify(responseOutput).close();
    }

    @Test
    public void responseIsNotSentOnIncorrectSecurityKey() throws IOException {
        when(connectorTracker.getSeckey(streamVariable)).thenReturn("another key expected");

        handler.handleRequest(session, request, response);

        verifyZeroInteractions(responseOutput);
    }

    @Test
    public void responseIsNotSentOnMissingSecurityKey() throws IOException {
        when(connectorTracker.getSeckey(streamVariable)).thenReturn(null);

        handler.handleRequest(session, request, response);

        verifyZeroInteractions(responseOutput);
    }
}
