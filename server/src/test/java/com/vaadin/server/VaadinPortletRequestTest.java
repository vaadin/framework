package com.vaadin.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;

import org.junit.Before;
import org.junit.Test;

public class VaadinPortletRequestTest {

    private PortletRequest request;
    private VaadinPortletRequest sut;
    private VaadinPortletService service;
    private PortletPreferences preferences;

    @Before
    public void setup() {
        request = mock(PortletRequest.class);
        service = mock(VaadinPortletService.class);

        sut = new VaadinPortletRequest(request, service);

        preferences = mock(PortletPreferences.class);
        when(request.getPreferences()).thenReturn(preferences);
    }

    @Test
    public void portletPreferenceIsFetched() {
        when(preferences.getValue(eq("foo"), anyString())).thenReturn("bar");

        String value = sut.getPortletPreference("foo");

        assertEquals("bar", value);
    }

    @Test
    public void defaultValueForPortletPreferenceIsNull() {
        when(preferences.getValue(anyString(), isNull(String.class)))
                .thenReturn(null);

        String value = sut.getPortletPreference("foo");

        assertNull(value);
    }

}
