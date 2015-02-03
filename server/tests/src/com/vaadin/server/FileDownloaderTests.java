package com.vaadin.server;

import static org.mockito.Matchers.contains;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;

import org.junit.Before;
import org.junit.Test;

public class FileDownloaderTests {
    private String filename = "日本語.png";
    private DownloadStream stream;

    @Before
    public void setup() {
        stream = new DownloadStream(mock(InputStream.class), "", filename);
    }

    @Test
    public void contentDispositionFilenameIsUtf8Encoded() throws IOException {
        VaadinResponse response = mock(VaadinResponse.class);

        stream.writeResponse(mock(VaadinRequest.class), response);

        verify(response).setHeader(eq("Content-Disposition"),
                contains("attachment;"));
        String encodedFileName = URLEncoder.encode(filename, "utf-8");
        verify(response).setHeader(eq("Content-Disposition"),
                contains(String.format("filename=\"%s\";", encodedFileName)));
        verify(response)
                .setHeader(
                        eq("Content-Disposition"),
                        contains(String.format("filename*=utf-8''%s",
                                encodedFileName)));
    }
}
