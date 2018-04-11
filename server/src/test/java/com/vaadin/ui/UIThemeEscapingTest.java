package com.vaadin.ui;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Locale;

import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.server.DeploymentConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;

public class UIThemeEscapingTest {

    private UI ui;

    private void initUiWithTheme(String theme) {
        VaadinRequest request = getRequestWithTheme(theme);

        IMocksControl control = EasyMock.createNiceControl();
        VaadinSession session = control.createMock(VaadinSession.class);
        DeploymentConfiguration dc = control
                .createMock(DeploymentConfiguration.class);

        EasyMock.expect(session.hasLock()).andStubReturn(true);
        EasyMock.expect(session.getConfiguration()).andStubReturn(dc);
        EasyMock.expect(session.getLocale()).andStubReturn(Locale.getDefault());

        control.replay();

        ui.setSession(session);
        ui.getPage().init(request);
        ui.doInit(request, 1234, "foobar");
    }

    private VaadinRequest getRequestWithTheme(String theme) {
        VaadinRequest request = mock(VaadinRequest.class);

        // when(request.getParameter())
        when(request.getParameter("theme")).thenReturn(theme);
        when(request.getParameter("v-loc")).thenReturn("http://localhost/");

        return request;
    }

    @Before
    public void setup() {
        ui = new UI() {
            @Override
            protected void init(VaadinRequest request) {
                // Nothing to do
            }
        };
    }

    @Test
    public void dangerousCharactersAreRemoved() {
        ui.setTheme("a<å(_\"$");

        assertThat(ui.getTheme(), is("aå_$"));
    }

    @Test
    public void nullThemeIsSet() {
        ui.setTheme("foobar");

        ui.setTheme(null);

        assertThat(ui.getTheme(), is(nullValue()));
    }

    @Test
    public void themeIsSetOnInit() {
        ui.setTheme("foobar");

        initUiWithTheme("bar");

        assertThat(ui.getTheme(), is("bar"));
    }

    @Test
    public void nullThemeIsSetOnInit() {
        ui.setTheme("foobar");

        initUiWithTheme(null);

        assertThat(ui.getTheme(), is(nullValue()));
    }
}
