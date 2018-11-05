package com.vaadin.server.communication;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.StringWriter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.vaadin.server.SystemMessages;
import com.vaadin.server.VaadinSession;
import com.vaadin.server.WrappedSession;
import com.vaadin.ui.UI;

public class MetadataWriterTest {

    private UI ui;
    private VaadinSession session;
    private StringWriter writer;
    private SystemMessages messages;

    @Before
    public void setup() {
        ui = Mockito.mock(UI.class);
        session = Mockito.mock(VaadinSession.class);
        Mockito.when(ui.getSession()).thenReturn(session);
        writer = new StringWriter();
        messages = Mockito.mock(SystemMessages.class);
    }

    private void disableSessionExpirationMessages(SystemMessages messages) {
        when(messages.isSessionExpiredNotificationEnabled()).thenReturn(true);
        when(messages.getSessionExpiredMessage()).thenReturn(null);
        when(messages.getSessionExpiredCaption()).thenReturn(null);
    }

    @Test
    public void writeAsyncTag() throws Exception {
        new MetadataWriter().write(ui, writer, false, true, messages);
        assertEquals("{\"async\":true}", writer.getBuffer().toString());
    }

    @Test
    public void writeRepaintTag() throws Exception {
        new MetadataWriter().write(ui, writer, true, false, messages);
        assertEquals("{\"repaintAll\":true}", writer.getBuffer().toString());
    }

    @Test
    public void writeRepaintAndAsyncTag() throws Exception {
        new MetadataWriter().write(ui, writer, true, true, messages);
        assertEquals("{\"repaintAll\":true, \"async\":true}",
                writer.getBuffer().toString());
    }

    @Test
    public void writeRedirectWithExpiredSession() throws Exception {
        disableSessionExpirationMessages(messages);

        new MetadataWriter().write(ui, writer, false, false, messages);
        assertEquals("{}", writer.getBuffer().toString());
    }

    @Test
    public void writeRedirectWithActiveSession() throws Exception {
        WrappedSession wrappedSession = mock(WrappedSession.class);
        when(session.getSession()).thenReturn(wrappedSession);

        disableSessionExpirationMessages(messages);

        new MetadataWriter().write(ui, writer, false, false, messages);
        assertEquals("{\"timedRedirect\":{\"interval\":15,\"url\":\"\"}}",
                writer.getBuffer().toString());
    }

    @Test
    public void writeAsyncWithSystemMessages() throws IOException {
        WrappedSession wrappedSession = mock(WrappedSession.class);
        when(session.getSession()).thenReturn(wrappedSession);

        disableSessionExpirationMessages(messages);

        new MetadataWriter().write(ui, writer, false, true, messages);
        assertEquals(
                "{\"async\":true,\"timedRedirect\":{\"interval\":15,\"url\":\"\"}}",
                writer.getBuffer().toString());
    }
}
