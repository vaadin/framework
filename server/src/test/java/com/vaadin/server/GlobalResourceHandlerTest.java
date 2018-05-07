package com.vaadin.server;

import java.io.IOException;

import com.vaadin.tests.util.MockUI;
import com.vaadin.ui.LegacyComponent;
import com.vaadin.ui.UI;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GlobalResourceHandlerTest {

    @Test
    public void globalResourceHandlerShouldWorkWithEncodedFilename() throws IOException {
        assertEncodedFilenameIsHandled("simple.txt", "simple.txt");
        assertEncodedFilenameIsHandled("with spaces.txt", "with+spaces.txt");
        assertEncodedFilenameIsHandled("with # hash.txt", "with+%23+hash.txt");
        assertEncodedFilenameIsHandled("with ; semicolon.txt", "with+%3B+semicolon.txt");
        assertEncodedFilenameIsHandled("with , comma.txt", "with+%2C+comma.txt");
    }

    private void assertEncodedFilenameIsHandled(String filename, String expectedFilename) throws IOException {
        DownloadStream stream = mock(DownloadStream.class);
        ConnectorResource resource = mock(ConnectorResource.class);
        when(resource.getFilename()).thenReturn(filename);
        when(resource.getStream()).thenReturn(stream);

        UI ui = new MockUI() {
            @Override
            public int getUIId() {
                return 0;
            }
        };
        ClientConnector connector = mock(LegacyComponent.class);
        when(connector.getUI()).thenReturn(ui);

        GlobalResourceHandler handler = new GlobalResourceHandler();
        handler.register(resource, connector);

        // Verify that file name has been encoded
        String uri = handler.getUri(connector, resource);
        assertThat(uri, endsWith("/" + expectedFilename));

        VaadinSession session = mock(VaadinSession.class);
        VaadinRequest request = mock(VaadinRequest.class);
        VaadinResponse response = mock(VaadinResponse.class);

        // getPathInfo return path decoded but without decoding plus as spaces
        when(request.getPathInfo()).thenReturn("APP/global/0/legacy/0/"+ filename.replace(" ", "+"));
        when(session.getUIById(anyInt())).thenReturn(ui);

        // Verify that decoded path info is correctly handled
        assertTrue("Request not handled", handler.handleRequest(session, request, response));
        verify(stream).writeResponse(request, response);
    }
}
