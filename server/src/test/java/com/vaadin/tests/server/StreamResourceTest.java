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
package com.vaadin.tests.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.net.URISyntaxException;

import org.easymock.EasyMock;
import org.junit.Test;

import com.vaadin.server.DownloadStream;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;

/**
 *
 * @author Vaadin Ltd
 */
public class StreamResourceTest {

    @Test
    public void testEqualsWithNullFields() {
        StreamResource resource1 = new StreamResource(null, null);
        StreamResource resource2 = new StreamResource(null, null);

        assertEquals(resource1, resource2);
    }

    @Test
    public void testNotEqualsWithNullFields() {
        StreamResource resource1 = new StreamResource(null, null);
        StreamResource resource2 = new StreamResource(
                EasyMock.createMock(StreamSource.class), "");

        assertNotEquals(resource1, resource2);
    }

    @Test
    public void testHashCodeForNullFields() {
        StreamResource resource = new StreamResource(null, null);
        // No NPE
        resource.hashCode();
    }

    @Test
    public void cacheTime() throws URISyntaxException {
        StreamResource resource = new StreamResource(
                EasyMock.createMock(StreamSource.class), "") {
            @Override
            public long getCacheTime() {
                return 5;
            }
        };
        resource.setBufferSize(100);
        resource.setCacheTime(200);

        DownloadStream downloadStream = resource.getStream();
        assertEquals(
                "DownloadStream buffer size must be same as resource buffer size",
                resource.getBufferSize(), downloadStream.getBufferSize());
        assertEquals(
                "DownloadStream cache time must be same as resource cache time",
                resource.getCacheTime(), downloadStream.getCacheTime());
    }
}
