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

import java.io.File;
import java.net.URISyntaxException;

import org.junit.Test;

import com.vaadin.server.DownloadStream;
import com.vaadin.server.FileResource;

public class FileResourceTest {

    @Test(expected = IllegalArgumentException.class)
    public void nullFile() {
        new FileResource(null);
    }

    @Test(expected = RuntimeException.class)
    public void nonExistingFile() {
        new FileResource(new File("nonexisting")).getStream();
    }

    @Test
    public void bufferSize() throws URISyntaxException {
        File file = new File(getClass().getResource("../styles.scss").toURI());
        FileResource resource = new FileResource(file) {
            @Override
            public long getCacheTime() {
                return 5;
            }
        };
        resource.setBufferSize(100);
        resource.setCacheTime(200);

        DownloadStream downloadStream = resource.getStream();
        assertEquals(resource.getBufferSize(),
                downloadStream.getBufferSize());
        assertEquals(resource.getCacheTime(),
                downloadStream.getCacheTime());
    }
}
