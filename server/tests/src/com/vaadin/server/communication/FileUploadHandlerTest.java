/*
 * Copyright 2000-2014 Vaadin Ltd.
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

import java.io.IOException;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.vaadin.server.VaadinRequest;

/**
 * Tests whether we get infinite loop if InputStream is already read (#10096)
 */
public class FileUploadHandlerTest {

    private FileUploadHandler handler;
    private VaadinRequest request;

    @Before
    public void setup() throws Exception {
        handler = new FileUploadHandler();
        InputStream inputStream = new InputStream() {
            private int counter = 0;

            @Override
            public int read() throws IOException {
                counter++;
                if (counter > 6) {
                    throw new RuntimeException(
                            "-1 is ignored by FileUploadHandler");
                }
                return -1;
            }

        };
        request = Mockito.mock(VaadinRequest.class);
        Mockito.when(request.getInputStream()).thenReturn(inputStream);
        Mockito.when(request.getHeader("Content-Length")).thenReturn("211");
    }

    @Test(expected = IOException.class)
    public void testStreamEnded() throws IOException {
        handler.doHandleSimpleMultipartFileUpload(null, request, null, null,
                null, null, null);

    }

}
