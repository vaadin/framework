package com.vaadin.ui;

import com.vaadin.server.VaadinRequest;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UIThemeEscapingTest {

    private UI ui;

    private void initUiWithTheme(String theme) {
        VaadinRequest request = getRequestWithTheme(theme);

        ui.doInit(request, 1234, "foobar");
    }

    private VaadinRequest getRequestWithTheme(String theme) {
        VaadinRequest request = mock(VaadinRequest.class);

        when(request.getParameter("theme")).thenReturn(theme);

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
