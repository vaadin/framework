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

package com.vaadin.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

/**
 * Downloadable stream.
 * <p>
 * Note that the methods in a DownloadStream are called without locking the
 * session to prevent locking the session during long file downloads. If your
 * DownloadStream uses anything from the session, you must handle the locking.
 * </p>
 * 
 * @author Vaadin Ltd.
 * @since 3.0
 */
@SuppressWarnings("serial")
public class DownloadStream implements Serializable {

    /**
     * Maximum cache time.
     */
    public static final long MAX_CACHETIME = Long.MAX_VALUE;

    /**
     * Default cache time.
     */
    public static final long DEFAULT_CACHETIME = 1000 * 60 * 60 * 24;

    private InputStream stream;

    private String contentType;

    private String fileName;

    private Map<String, String> params;

    private long cacheTime = DEFAULT_CACHETIME;

    private int bufferSize = 0;

    /**
     * Creates a new instance of DownloadStream.
     */
    public DownloadStream(InputStream stream, String contentType,
            String fileName) {
        setStream(stream);
        setContentType(contentType);
        setFileName(fileName);
    }

    /**
     * Gets downloadable stream.
     * 
     * @return output stream.
     */
    public InputStream getStream() {
        return stream;
    }

    /**
     * Sets the stream.
     * 
     * @param stream
     *            The stream to set
     */
    public void setStream(InputStream stream) {
        this.stream = stream;
    }

    /**
     * Gets stream content type.
     * 
     * @return type of the stream content.
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Sets stream content type.
     * 
     * @param contentType
     *            the contentType to set
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * Returns the file name.
     * 
     * @return the name of the file.
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Sets the file name.
     * 
     * @param fileName
     *            the file name to set.
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Sets a paramater for download stream. Parameters are optional information
     * about the downloadable stream and their meaning depends on the used
     * adapter. For example in WebAdapter they are interpreted as HTTP response
     * headers.
     * 
     * If the parameters by this name exists, the old value is replaced.
     * 
     * @param name
     *            the Name of the parameter to set.
     * @param value
     *            the Value of the parameter to set.
     */
    public void setParameter(String name, String value) {
        if (params == null) {
            params = new HashMap<String, String>();
        }
        params.put(name, value);
    }

    /**
     * Gets a paramater for download stream. Parameters are optional information
     * about the downloadable stream and their meaning depends on the used
     * adapter. For example in WebAdapter they are interpreted as HTTP response
     * headers.
     * 
     * @param name
     *            the Name of the parameter to set.
     * @return Value of the parameter or null if the parameter does not exist.
     */
    public String getParameter(String name) {
        if (params != null) {
            return params.get(name);
        }
        return null;
    }

    /**
     * Gets the names of the parameters.
     * 
     * @return Iterator of names or null if no parameters are set.
     */
    public Iterator<String> getParameterNames() {
        if (params != null) {
            return params.keySet().iterator();
        }
        return null;
    }

    /**
     * Gets length of cache expiration time. This gives the adapter the
     * possibility cache streams sent to the client. The caching may be made in
     * adapter or at the client if the client supports caching. Default is
     * <code>DEFAULT_CACHETIME</code>.
     * 
     * @return Cache time in milliseconds
     */
    public long getCacheTime() {
        return cacheTime;
    }

    /**
     * Sets length of cache expiration time. This gives the adapter the
     * possibility cache streams sent to the client. The caching may be made in
     * adapter or at the client if the client supports caching. Zero or negavive
     * value disbales the caching of this stream.
     * 
     * @param cacheTime
     *            the cache time in milliseconds.
     */
    public void setCacheTime(long cacheTime) {
        this.cacheTime = cacheTime;
    }

    /**
     * Gets the size of the download buffer.
     * 
     * @return int The size of the buffer in bytes.
     */
    public int getBufferSize() {
        return bufferSize;
    }

    /**
     * Sets the size of the download buffer.
     * 
     * @param bufferSize
     *            the size of the buffer in bytes.
     * 
     * @since 7.0
     */
    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    /**
     * Writes this download stream to a Vaadin response. This takes care of
     * setting response headers according to what is defined in this download
     * stream ({@link #getContentType()}, {@link #getCacheTime()},
     * {@link #getFileName()}) and transferring the data from the stream (
     * {@link #getStream()}) to the response. Defined parameters (
     * {@link #getParameterNames()}) are also included as headers in the
     * response. If there's is a parameter named <code>Location</code>, a
     * redirect (302 Moved temporarily) is sent instead of the contents of this
     * stream.
     * 
     * @param request
     *            the request for which the response should be written
     * @param response
     *            the Vaadin response to write this download stream to
     * 
     * @throws IOException
     *             passed through from the Vaadin response
     * 
     * @since 7.0
     */
    public void writeResponse(VaadinRequest request, VaadinResponse response)
            throws IOException {
        if (getParameter("Location") != null) {
            response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
            response.setHeader("Location", getParameter("Location"));
            return;
        }

        // Download from given stream
        final InputStream data = getStream();
        if (data == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        if (data != null) {

            OutputStream out = null;
            try {
                // Sets content type
                response.setContentType(getContentType());

                // Sets cache headers
                response.setCacheTime(getCacheTime());

                // Copy download stream parameters directly
                // to HTTP headers.
                final Iterator<String> i = getParameterNames();
                if (i != null) {
                    while (i.hasNext()) {
                        final String param = i.next();
                        response.setHeader(param, getParameter(param));
                    }
                }

                // suggest local filename from DownloadStream if
                // Content-Disposition
                // not explicitly set
                String contentDispositionValue = getParameter("Content-Disposition");
                if (contentDispositionValue == null) {
                    contentDispositionValue = "filename=\"" + getFileName()
                            + "\"";
                    response.setHeader("Content-Disposition",
                            contentDispositionValue);
                }

                int bufferSize = getBufferSize();
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
                tryToCloseStream(out);
                tryToCloseStream(data);
            }
        }
    }

    /**
     * Helper method that tries to close an output stream and ignores any
     * exceptions.
     * 
     * @param out
     *            the output stream to close, <code>null</code> is also
     *            supported
     */
    static void tryToCloseStream(OutputStream out) {
        try {
            // try to close output stream (e.g. file handle)
            if (out != null) {
                out.close();
            }
        } catch (IOException e1) {
            // NOP
        }
    }

    /**
     * Helper method that tries to close an input stream and ignores any
     * exceptions.
     * 
     * @param in
     *            the input stream to close, <code>null</code> is also supported
     */
    static void tryToCloseStream(InputStream in) {
        try {
            // try to close output stream (e.g. file handle)
            if (in != null) {
                in.close();
            }
        } catch (IOException e1) {
            // NOP
        }
    }

}
