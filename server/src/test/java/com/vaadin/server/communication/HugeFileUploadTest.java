package com.vaadin.server.communication;

import com.vaadin.server.StreamVariable;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.ConnectorTracker;
import com.vaadin.ui.UI;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.io.InputStream;

import static com.vaadin.server.ServletPortletHelper.UPLOAD_URL_PREFIX;
import static org.mockito.Mockito.when;

public class HugeFileUploadTest {
    private static final String SEC_KEY = "4";
    private static final String CONN_ID = "2";
    private static final int UI_ID = 1;
    @Mock
    private VaadinSession session;

    @Mock
    private VaadinResponse response;

    @Mock
    private VaadinRequest request;
    @Mock
    private UI ui;
    @Mock
    private StreamVariable streamVariable;
    @Mock
    private ConnectorTracker connectorTracker;

    @Before
    public void setup() throws IOException {
        MockitoAnnotations.initMocks(this);
        // 0= UIid, 1 = cid, 2= name, 3 = sec key
        when(request.getPathInfo()).thenReturn("/" + UPLOAD_URL_PREFIX + UI_ID + "/" + CONN_ID + "/var/" + SEC_KEY);
        when(request.getContentType()).thenReturn("application/multipart-attached;boundary=bbbbb");
        when(session.hasLock()).thenReturn(true);
        when(session.getUIById(UI_ID)).thenReturn(ui);
        when(ui.getConnectorTracker()).thenReturn(connectorTracker);
        when(connectorTracker.getStreamVariable(CONN_ID,"var")).thenReturn(streamVariable);
        when(connectorTracker.getSeckey(streamVariable)).thenReturn(SEC_KEY);
        when(request.getInputStream()).thenReturn(new InputStream() {
            @Override
            public int read() throws IOException {
                return 'a';
            }
        });
    }

    @Test(expected = IOException.class, timeout = 60000)
    public void testHugeFileWithoutNewLine() throws IOException {
        FileUploadHandler fileUploadHandler = new FileUploadHandler();
        fileUploadHandler.handleRequest(session, request, response);
    }

}
