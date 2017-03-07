/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.server;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
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

        assertThat(value, is("bar"));
    }

    @Test
    public void defaultValueForPortletPreferenceIsNull() {
        when(preferences.getValue(anyString(), isNull(String.class)))
                .thenReturn(null);

        String value = sut.getPortletPreference("foo");

        assertNull(value);
    }

}
