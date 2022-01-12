/*
 * Copyright 2000-2022 Vaadin Ltd.
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

import java.util.concurrent.Future;

import org.atmosphere.cpr.AtmosphereRequest;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.vaadin.server.MockVaadinServletService;
import com.vaadin.server.MockVaadinSession;
import com.vaadin.server.ServiceException;
import com.vaadin.server.SessionExpiredException;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;

public class PushHandlerTest {

    MockVaadinSession session = null;

    @Test
    public void connectionLost_currentInstancesAreCleared()
            throws SessionExpiredException, ServiceException {
        session = new MockVaadinSession() {
            @Override
            public Future<Void> access(Runnable runnable) {
                runnable.run();
                return Mockito.mock(Future.class);
            }
        };
        VaadinSession.setCurrent(session);
        Assert.assertNotNull(VaadinSession.getCurrent());
        MockVaadinServletService service = null;
        service = new MockVaadinServletService() {
            @Override
            public com.vaadin.server.VaadinSession findVaadinSession(
                    VaadinRequest request) throws SessionExpiredException {
                 return session;
            }

            @Override
            public UI findUI(VaadinRequest request) {
                return null;
            }
        };

        service.init();
        PushHandler handler = new PushHandler(service);

        AtmosphereResource resource = Mockito.mock(AtmosphereResource.class);
        AtmosphereRequest request = Mockito.mock(AtmosphereRequest.class);
        Mockito.when(resource.getRequest()).thenReturn(request);

        AtmosphereResourceEvent event = Mockito
                .mock(AtmosphereResourceEvent.class);
        Mockito.when(event.getResource()).thenReturn(resource);
        handler.connectionLost(event);

        Assert.assertNull(VaadinSession.getCurrent());
    }
}
