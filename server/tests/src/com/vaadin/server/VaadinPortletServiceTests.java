/*
 * Copyright 2000-2013 Vaadin Ltd.
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

public class VaadinPortletServiceTests {

    private VaadinPortletService sut;
    private VaadinPortletRequest request;

    @Before
    public void setup() throws ServiceException {
        VaadinPortlet portlet = mock(VaadinPortlet.class);
        DeploymentConfiguration conf = mock(DeploymentConfiguration.class);

        sut = new VaadinPortletService(portlet, conf);

        request = mock(VaadinPortletRequest.class);
    }

    private void mockRequestToReturnLocation(String location) {
        when(request.getPortalProperty(
                Constants.PORTAL_PARAMETER_VAADIN_RESOURCE_PATH))
                .thenReturn(location);
    }

    @Test
    public void trailingSlashesAreTrimmedFromStaticFileLocation()
            throws ServiceException {

        mockRequestToReturnLocation("/content////");

        String staticFileLocation = sut
                .getStaticFileLocation(request);

        assertThat(staticFileLocation, is("/content"));
    }
}
