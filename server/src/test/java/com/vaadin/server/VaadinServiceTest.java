package com.vaadin.server;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpSessionBindingEvent;

import java.util.Collections;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


/**
 *
 * @author Vaadin Ltd
 */
public class VaadinServiceTest {

    private class TestSessionDestroyListener implements SessionDestroyListener {

        int callCount = 0;

        @Override
        public void sessionDestroy(SessionDestroyEvent event) {
            callCount++;
        }
    }

    private String createCriticalNotification(String caption, String message,
            String details, String url) {
        return VaadinService.createCriticalNotificationJSON(caption, message,
                details, url);
    }

    @Test
    public void testFireSessionDestroy() throws ServletException {
        ServletConfig servletConfig = new MockServletConfig();
        VaadinServlet servlet = new VaadinServlet();
        servlet.init(servletConfig);
        VaadinService service = servlet.getService();

        TestSessionDestroyListener listener = new TestSessionDestroyListener();

        service.addSessionDestroyListener(listener);

        MockVaadinSession vaadinSession = new MockVaadinSession(service);
        service.fireSessionDestroy(vaadinSession);
        Assert.assertEquals(
                "'fireSessionDestroy' method doesn't call 'close' for the session",
                1, vaadinSession.getCloseCount());

        vaadinSession.valueUnbound(
                EasyMock.createMock(HttpSessionBindingEvent.class));

        Assert.assertEquals(
                "'fireSessionDestroy' method may not call 'close' "
                        + "method for closing session",
                1, vaadinSession.getCloseCount());

        Assert.assertEquals("SessionDestroyListeners not called exactly once",
                1, listener.callCount);
    }

    @Test
    public void captionIsSetToACriticalNotification() {
        String notification = createCriticalNotification("foobar", "message",
                "details", "url");

        assertThat(notification, containsString("\"caption\":\"foobar\""));
    }

    @Test
    public void nullCaptionIsSetToACriticalNotification() {
        String notification = createCriticalNotification(null, "message",
                "details", "url");

        assertThat(notification, containsString("\"caption\":null"));
    }

    @Test
    public void messageWithDetailsIsSetToACriticalNotification() {
        String notification = createCriticalNotification("caption", "foo",
                "bar", "url");

        assertThat(notification, containsString("\"details\":\"bar\""));
    }

    @Test
    public void nullMessageSentAsNullInACriticalNotification() {
        String notification = createCriticalNotification("caption", null,
                "foobar", "url");

        assertThat(notification, containsString("\"message\":null"));
    }

    @Test
    public void nullMessageIsSetToACriticalNotification() {
        String notification = createCriticalNotification("caption", null, null,
                "url");

        assertThat(notification, containsString("\"message\":null"));
    }

    @Test
    public void messageSetToACriticalNotification() {
        String notification = createCriticalNotification("caption", "foobar",
                null, "url");

        assertThat(notification, containsString("\"message\":\"foobar\""));
    }

    @Test
    public void urlIsSetToACriticalNotification() {
        String notification = createCriticalNotification("caption", "message",
                "details", "foobar");

        assertThat(notification, containsString("\"url\":\"foobar\""));
    }

    @Test
    public void nullUrlIsSetToACriticalNotification() {
        String notification = createCriticalNotification("caption", "message",
                "details", null);

        assertThat(notification, containsString("\"url\":null"));
    }
    @Test
    public void reinitializeSession_setVaadinSessionAttriuteWithLock() {
        VaadinRequest request = Mockito.mock(VaadinRequest.class);

        VaadinSession vaadinSession = Mockito.mock(VaadinSession.class);
        VaadinSession newVaadinSession = Mockito.mock(VaadinSession.class);

        WrappedSession session = mockSession(request, vaadinSession, "foo");

        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                mockSession(request, newVaadinSession, "bar");
                return null;
            }
        }).when(session).invalidate();

        VaadinService.reinitializeSession(request);

        Mockito.verify(vaadinSession, Mockito.times(2)).lock();
        Mockito.verify(vaadinSession).setAttribute(
                VaadinService.PRESERVE_UNBOUND_SESSION_ATTRIBUTE, Boolean.TRUE);
        Mockito.verify(vaadinSession).setAttribute(
                VaadinService.PRESERVE_UNBOUND_SESSION_ATTRIBUTE, null);
        Mockito.verify(vaadinSession, Mockito.times(2)).unlock();
    }

    private WrappedSession mockSession(VaadinRequest request,
            VaadinSession vaadinSession, String attributeName) {
        WrappedSession session = Mockito.mock(WrappedSession.class);
        Mockito.when(request.getWrappedSession()).thenReturn(session);

        Mockito.when(session.getAttributeNames())
                .thenReturn(Collections.singleton(attributeName));

        Mockito.when(session.getAttribute(attributeName))
                .thenReturn(vaadinSession);

        VaadinService service = Mockito.mock(VaadinService.class);

        Mockito.when(vaadinSession.getService()).thenReturn(service);
        return session;
    }
}
