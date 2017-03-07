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

import static org.mockito.Matchers.contains;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;

public class DownloadStreamTest {
    private String filename = "A å日.png";
    private String encodedFileName = "A" + "%20" // space
            + "%c3%a5" // å
            + "%e6%97%a5" // 日
            + ".png";
    private DownloadStream stream;

    @Before
    public void setup() {
        stream = new DownloadStream(mock(InputStream.class), "", filename);
    }

    @Test
    public void contentDispositionFilenameIsUtf8Encoded() throws IOException {
        VaadinResponse response = mock(VaadinResponse.class);

        stream.writeResponse(mock(VaadinRequest.class), response);

        verify(response).setHeader(eq(DownloadStream.CONTENT_DISPOSITION),
                contains(String.format("filename=\"%s\";", encodedFileName)));
        verify(response).setHeader(eq(DownloadStream.CONTENT_DISPOSITION),
                contains(
                        String.format("filename*=utf-8''%s", encodedFileName)));
    }
}
