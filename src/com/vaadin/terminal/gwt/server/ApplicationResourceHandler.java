package com.vaadin.terminal.gwt.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import com.vaadin.Application;
import com.vaadin.terminal.ApplicationResource;
import com.vaadin.terminal.DownloadStream;
import com.vaadin.terminal.RequestHandler;
import com.vaadin.terminal.WrappedRequest;
import com.vaadin.terminal.WrappedResponse;

public class ApplicationResourceHandler implements RequestHandler {
    private static final DateFormat HTTP_DATE_FORMAT = new SimpleDateFormat(
            "EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);
    static {
        HTTP_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
    }
    private static final Pattern APP_RESOURCE_PATTERN = Pattern
            .compile("/APP/(\\d+)/.*");

    public boolean handleRequest(Application application,
            WrappedRequest request, WrappedResponse response)
            throws IOException {
        // Check for application resources
        String requestPath = request.getRequestPathInfo();
        Matcher resourceMatcher = APP_RESOURCE_PATTERN.matcher(requestPath);

        if (resourceMatcher.matches()) {
            ApplicationResource resource = application
                    .getResource(resourceMatcher.group(1));
            if (resource != null) {
                DownloadStream stream = resource.getStream();
                if (stream != null) {
                    stream.setCacheTime(resource.getCacheTime());
                    serveResource(stream, response);
                    return true;
                }
            }
        }

        return false;
    }

    protected void serveResource(DownloadStream stream, WrappedResponse response)
            throws IOException {
        if (stream.getParameter("Location") != null) {
            response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
            response.setHeader("Location", stream.getParameter("Location"));
            return;
        }

        // Download from given stream
        final InputStream data = stream.getStream();
        if (data != null) {

            OutputStream out = null;
            try {
                // Sets content type
                response.setContentType(stream.getContentType());

                // Sets cache headers
                final long cacheTime = stream.getCacheTime();
                if (cacheTime <= 0) {
                    response.setHeader("Cache-Control", "no-cache");
                    response.setHeader("Pragma", "no-cache");
                    response.setHeader("Expires",
                            HTTP_DATE_FORMAT.format(new Date(0)));
                } else {
                    response.setHeader("Cache-Control", "max-age=" + cacheTime
                            / 1000);
                    response.setHeader(
                            "Expires",
                            HTTP_DATE_FORMAT.format(new Date(System
                                    .currentTimeMillis() + cacheTime)));
                    // Required to apply caching in some Tomcats
                    response.setHeader("Pragma", "cache");
                }

                // Copy download stream parameters directly
                // to HTTP headers.
                final Iterator<String> i = stream.getParameterNames();
                if (i != null) {
                    while (i.hasNext()) {
                        final String param = i.next();
                        response.setHeader(param, stream.getParameter(param));
                    }
                }

                // suggest local filename from DownloadStream if
                // Content-Disposition
                // not explicitly set
                String contentDispositionValue = stream
                        .getParameter("Content-Disposition");
                if (contentDispositionValue == null) {
                    contentDispositionValue = "filename=\""
                            + stream.getFileName() + "\"";
                    response.setHeader("Content-Disposition",
                            contentDispositionValue);
                }

                int bufferSize = stream.getBufferSize();
                if (bufferSize <= 0 || bufferSize > Constants.MAX_BUFFER_SIZE) {
                    bufferSize = Constants.DEFAULT_BUFFER_SIZE;
                }
                final byte[] buffer = new byte[bufferSize];
                int bytesRead = 0;

                out = response.getOutputStream();

                long totalWritten = 0;
                while ((bytesRead = data.read(buffer)) > 0) {
                    out.write(buffer, 0, bytesRead);

                    totalWritten += bytesRead;
                    if (totalWritten >= buffer.length) {
                        // Avoid chunked encoding for small resources
                        out.flush();
                    }
                }
            } finally {
                AbstractCommunicationManager.tryToCloseStream(out);
                AbstractCommunicationManager.tryToCloseStream(data);
            }
        }
    }
}
