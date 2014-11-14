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

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import com.vaadin.server.ClientConnector;
import com.vaadin.server.NoInputStreamException;
import com.vaadin.server.NoOutputStreamException;
import com.vaadin.server.RequestHandler;
import com.vaadin.server.ServletPortletHelper;
import com.vaadin.server.StreamVariable;
import com.vaadin.server.StreamVariable.StreamingEndEvent;
import com.vaadin.server.StreamVariable.StreamingErrorEvent;
import com.vaadin.server.UploadException;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Component;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload.FailedEvent;

/**
 * Handles a file upload request submitted via an Upload component.
 * 
 * @author Vaadin Ltd
 * @since 7.1
 */
public class FileUploadHandler implements RequestHandler {

    /**
     * Stream that extracts content from another stream until the boundary
     * string is encountered.
     * 
     * Public only for unit tests, should be considered private for all other
     * purposes.
     */
    public static class SimpleMultiPartInputStream extends InputStream {

        /**
         * Counter of how many characters have been matched to boundary string
         * from the stream
         */
        int matchedCount = -1;

        /**
         * Used as pointer when returning bytes after partly matched boundary
         * string.
         */
        int curBoundaryIndex = 0;
        /**
         * The byte found after a "promising start for boundary"
         */
        private int bufferedByte = -1;
        private boolean atTheEnd = false;

        private final char[] boundary;

        private final InputStream realInputStream;

        public SimpleMultiPartInputStream(InputStream realInputStream,
                String boundaryString) {
            boundary = (CRLF + DASHDASH + boundaryString).toCharArray();
            this.realInputStream = realInputStream;
        }

        @Override
        public int read() throws IOException {
            if (atTheEnd) {
                // End boundary reached, nothing more to read
                return -1;
            } else if (bufferedByte >= 0) {
                /* Purge partially matched boundary if there was such */
                return getBuffered();
            } else if (matchedCount != -1) {
                /*
                 * Special case where last "failed" matching ended with first
                 * character from boundary string
                 */
                return matchForBoundary();
            } else {
                int fromActualStream = realInputStream.read();
                if (fromActualStream == -1) {
                    // unexpected end of stream
                    throw new IOException(
                            "The multipart stream ended unexpectedly");
                }
                if (boundary[0] == fromActualStream) {
                    /*
                     * If matches the first character in boundary string, start
                     * checking if the boundary is fetched.
                     */
                    return matchForBoundary();
                }
                return fromActualStream;
            }
        }

        /**
         * Reads the input to expect a boundary string. Expects that the first
         * character has already been matched.
         * 
         * @return -1 if the boundary was matched, else returns the first byte
         *         from boundary
         * @throws IOException
         */
        private int matchForBoundary() throws IOException {
            matchedCount = 0;
            /*
             * Going to "buffered mode". Read until full boundary match or a
             * different character.
             */
            while (true) {
                matchedCount++;
                if (matchedCount == boundary.length) {
                    /*
                     * The whole boundary matched so we have reached the end of
                     * file
                     */
                    atTheEnd = true;
                    return -1;
                }
                int fromActualStream = realInputStream.read();
                if (fromActualStream != boundary[matchedCount]) {
                    /*
                     * Did not find full boundary, cache the mismatching byte
                     * and start returning the partially matched boundary.
                     */
                    bufferedByte = fromActualStream;
                    return getBuffered();
                }
            }
        }

        /**
         * Returns the partly matched boundary string and the byte following
         * that.
         * 
         * @return
         * @throws IOException
         */
        private int getBuffered() throws IOException {
            int b;
            if (matchedCount == 0) {
                // The boundary has been returned, return the buffered byte.
                b = bufferedByte;
                bufferedByte = -1;
                matchedCount = -1;
            } else {
                b = boundary[curBoundaryIndex++];
                if (curBoundaryIndex == matchedCount) {
                    // The full boundary has been returned, remaining is the
                    // char that did not match the boundary.

                    curBoundaryIndex = 0;
                    if (bufferedByte != boundary[0]) {
                        /*
                         * next call for getBuffered will return the
                         * bufferedByte that came after the partial boundary
                         * match
                         */
                        matchedCount = 0;
                    } else {
                        /*
                         * Special case where buffered byte again matches the
                         * boundaryString. This could be the start of the real
                         * end boundary.
                         */
                        matchedCount = 0;
                        bufferedByte = -1;
                    }
                }
            }
            if (b == -1) {
                throw new IOException("The multipart stream ended unexpectedly");
            }
            return b;
        }
    }

    /**
     * An UploadInterruptedException will be thrown by an ongoing upload if
     * {@link StreamVariable#isInterrupted()} returns <code>true</code>.
     * 
     * By checking the exception of an {@link StreamingErrorEvent} or
     * {@link FailedEvent} against this class, it is possible to determine if an
     * upload was interrupted by code or aborted due to any other exception.
     */
    public static class UploadInterruptedException extends Exception {

        /**
         * Constructs an instance of <code>UploadInterruptedException</code>.
         */
        public UploadInterruptedException() {
            super("Upload interrupted by other thread");
        }
    }

    /**
     * as per RFC 2045, line delimiters in headers are always CRLF, i.e. 13 10
     */
    private static final int LF = 10;

    private static final String CRLF = "\r\n";

    private static final String UTF8 = "UTF-8";

    private static final String DASHDASH = "--";

    /* Same as in apache commons file upload library that was previously used. */
    private static final int MAX_UPLOAD_BUFFER_SIZE = 4 * 1024;

    /* Minimum interval which will be used for streaming progress events. */
    public static final int DEFAULT_STREAMING_PROGRESS_EVENT_INTERVAL_MS = 500;

    @Override
    public boolean handleRequest(VaadinSession session, VaadinRequest request,
            VaadinResponse response) throws IOException {
        if (!ServletPortletHelper.isFileUploadRequest(request)) {
            return false;
        }

        /*
         * URI pattern: APP/UPLOAD/[UIID]/[PID]/[NAME]/[SECKEY] See
         * #createReceiverUrl
         */

        String pathInfo = request.getPathInfo();
        // strip away part until the data we are interested starts
        int startOfData = pathInfo
                .indexOf(ServletPortletHelper.UPLOAD_URL_PREFIX)
                + ServletPortletHelper.UPLOAD_URL_PREFIX.length();
        String uppUri = pathInfo.substring(startOfData);
        String[] parts = uppUri.split("/", 4); // 0= UIid, 1 = cid, 2= name, 3
                                               // = sec key
        String uiId = parts[0];
        String connectorId = parts[1];
        String variableName = parts[2];

        // These are retrieved while session is locked
        ClientConnector source;
        StreamVariable streamVariable;

        session.lock();
        try {
            UI uI = session.getUIById(Integer.parseInt(uiId));
            UI.setCurrent(uI);

            streamVariable = uI.getConnectorTracker().getStreamVariable(
                    connectorId, variableName);
            String secKey = uI.getConnectorTracker().getSeckey(streamVariable);
            if (!secKey.equals(parts[3])) {
                // TODO Should rethink error handling
                return true;
            }

            source = uI.getConnectorTracker().getConnector(connectorId);
        } finally {
            session.unlock();
        }

        String contentType = request.getContentType();
        if (contentType.contains("boundary")) {
            // Multipart requests contain boundary string
            doHandleSimpleMultipartFileUpload(session, request, response,
                    streamVariable, variableName, source,
                    contentType.split("boundary=")[1]);
        } else {
            // if boundary string does not exist, the posted file is from
            // XHR2.post(File)
            doHandleXhrFilePost(session, request, response, streamVariable,
                    variableName, source, getContentLength(request));
        }
        return true;
    }

    private static String readLine(InputStream stream) throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        int readByte = stream.read();
        while (readByte != LF) {
            if (readByte == -1) {
                throw new IOException("The multipart stream ended unexpectedly");
            }
            bout.write(readByte);
            readByte = stream.read();
        }
        byte[] bytes = bout.toByteArray();
        return new String(bytes, 0, bytes.length - 1, UTF8);
    }

    /**
     * Method used to stream content from a multipart request (either from
     * servlet or portlet request) to given StreamVariable.
     * <p>
     * This method takes care of locking the session as needed and does not
     * assume the caller has locked the session. This allows the session to be
     * locked only when needed and not when handling the upload data.
     * </p>
     * 
     * @param session
     *            The session containing the stream variable
     * @param request
     *            The upload request
     * @param response
     *            The upload response
     * @param streamVariable
     *            The destination stream variable
     * @param variableName
     *            The name of the destination stream variable
     * @param owner
     *            The owner of the stream variable
     * @param boundary
     *            The mime boundary used in the upload request
     * @throws IOException
     *             If there is a problem reading the request or writing the
     *             response
     */
    protected void doHandleSimpleMultipartFileUpload(VaadinSession session,
            VaadinRequest request, VaadinResponse response,
            StreamVariable streamVariable, String variableName,
            ClientConnector owner, String boundary) throws IOException {
        // multipart parsing, supports only one file for request, but that is
        // fine for our current terminal

        final InputStream inputStream = request.getInputStream();

        long contentLength = getContentLength(request);

        boolean atStart = false;
        boolean firstFileFieldFound = false;

        String rawfilename = "unknown";
        String rawMimeType = "application/octet-stream";

        /*
         * Read the stream until the actual file starts (empty line). Read
         * filename and content type from multipart headers.
         */
        while (!atStart) {
            String readLine = readLine(inputStream);
            contentLength -= (readLine.getBytes(UTF8).length + CRLF.length());
            if (readLine.startsWith("Content-Disposition:")
                    && readLine.indexOf("filename=") > 0) {
                rawfilename = readLine.replaceAll(".*filename=", "");
                char quote = rawfilename.charAt(0);
                rawfilename = rawfilename.substring(1);
                rawfilename = rawfilename.substring(0,
                        rawfilename.indexOf(quote));
                firstFileFieldFound = true;
            } else if (firstFileFieldFound && readLine.equals("")) {
                atStart = true;
            } else if (readLine.startsWith("Content-Type")) {
                rawMimeType = readLine.split(": ")[1];
            }
        }

        contentLength -= (boundary.length() + CRLF.length() + 2
                * DASHDASH.length() + CRLF.length());

        /*
         * Reads bytes from the underlying stream. Compares the read bytes to
         * the boundary string and returns -1 if met.
         * 
         * The matching happens so that if the read byte equals to the first
         * char of boundary string, the stream goes to "buffering mode". In
         * buffering mode bytes are read until the character does not match the
         * corresponding from boundary string or the full boundary string is
         * found.
         * 
         * Note, if this is someday needed elsewhere, don't shoot yourself to
         * foot and split to a top level helper class.
         */
        InputStream simpleMultiPartReader = new SimpleMultiPartInputStream(
                inputStream, boundary);

        /*
         * Should report only the filename even if the browser sends the path
         */
        final String filename = removePath(rawfilename);
        final String mimeType = rawMimeType;

        try {
            handleFileUploadValidationAndData(session, simpleMultiPartReader,
                    streamVariable, filename, mimeType, contentLength, owner,
                    variableName);
        } catch (UploadException e) {
            session.getCommunicationManager().handleConnectorRelatedException(
                    owner, e);
        }
        sendUploadResponse(request, response);

    }

    /*
     * request.getContentLength() is limited to "int" by the Servlet
     * specification. To support larger file uploads manually evaluate the
     * Content-Length header which can contain long values.
     */
    private long getContentLength(VaadinRequest request) {
        try {
            return Long.parseLong(request.getHeader("Content-Length"));
        } catch (NumberFormatException e) {
            return -1l;
        }
    }

    private void handleFileUploadValidationAndData(VaadinSession session,
            InputStream inputStream, StreamVariable streamVariable,
            String filename, String mimeType, long contentLength,
            ClientConnector connector, String variableName)
            throws UploadException {
        session.lock();
        try {
            if (connector == null) {
                throw new UploadException(
                        "File upload ignored because the connector for the stream variable was not found");
            }
            if (!connector.isConnectorEnabled()) {
                throw new UploadException("Warning: file upload ignored for "
                        + connector.getConnectorId()
                        + " because the component was disabled");
            }
            if ((connector instanceof Component)
                    && ((Component) connector).isReadOnly()) {
                // Only checked for legacy reasons
                throw new UploadException(
                        "File upload ignored because the component is read-only");
            }
        } finally {
            session.unlock();
        }
        try {
            boolean forgetVariable = streamToReceiver(session, inputStream,
                    streamVariable, filename, mimeType, contentLength);
            if (forgetVariable) {
                cleanStreamVariable(session, connector, variableName);
            }
        } catch (Exception e) {
            session.lock();
            try {
                session.getCommunicationManager()
                        .handleConnectorRelatedException(connector, e);
            } finally {
                session.unlock();
            }
        }
    }

    /**
     * Used to stream plain file post (aka XHR2.post(File))
     * <p>
     * This method takes care of locking the session as needed and does not
     * assume the caller has locked the session. This allows the session to be
     * locked only when needed and not when handling the upload data.
     * </p>
     * 
     * @param session
     *            The session containing the stream variable
     * @param request
     *            The upload request
     * @param response
     *            The upload response
     * @param streamVariable
     *            The destination stream variable
     * @param variableName
     *            The name of the destination stream variable
     * @param owner
     *            The owner of the stream variable
     * @param contentLength
     *            The length of the request content
     * @throws IOException
     *             If there is a problem reading the request or writing the
     *             response
     */
    protected void doHandleXhrFilePost(VaadinSession session,
            VaadinRequest request, VaadinResponse response,
            StreamVariable streamVariable, String variableName,
            ClientConnector owner, long contentLength) throws IOException {

        // These are unknown in filexhr ATM, maybe add to Accept header that
        // is accessible in portlets
        final String filename = "unknown";
        final String mimeType = filename;
        final InputStream stream = request.getInputStream();

        try {
            handleFileUploadValidationAndData(session, stream, streamVariable,
                    filename, mimeType, contentLength, owner, variableName);
        } catch (UploadException e) {
            session.getCommunicationManager().handleConnectorRelatedException(
                    owner, e);
        }
        sendUploadResponse(request, response);
    }

    /**
     * @param in
     * @param streamVariable
     * @param filename
     * @param type
     * @param contentLength
     * @return true if the streamvariable has informed that the terminal can
     *         forget this variable
     * @throws UploadException
     */
    protected final boolean streamToReceiver(VaadinSession session,
            final InputStream in, StreamVariable streamVariable,
            String filename, String type, long contentLength)
            throws UploadException {
        if (streamVariable == null) {
            throw new IllegalStateException(
                    "StreamVariable for the post not found");
        }

        OutputStream out = null;
        long totalBytes = 0;
        StreamingStartEventImpl startedEvent = new StreamingStartEventImpl(
                filename, type, contentLength);
        try {
            boolean listenProgress;
            session.lock();
            try {
                streamVariable.streamingStarted(startedEvent);
                out = streamVariable.getOutputStream();
                listenProgress = streamVariable.listenProgress();
            } finally {
                session.unlock();
            }

            // Gets the output target stream
            if (out == null) {
                throw new NoOutputStreamException();
            }

            if (null == in) {
                // No file, for instance non-existent filename in html upload
                throw new NoInputStreamException();
            }

            final byte buffer[] = new byte[MAX_UPLOAD_BUFFER_SIZE];
            long lastStreamingEvent = 0;
            int bytesReadToBuffer = 0;
            do {
                bytesReadToBuffer = in.read(buffer);
                if (bytesReadToBuffer > 0) {
                    out.write(buffer, 0, bytesReadToBuffer);
                    totalBytes += bytesReadToBuffer;
                }
                if (listenProgress) {
                    long now = System.currentTimeMillis();
                    // to avoid excessive session locking and event storms,
                    // events are sent in intervals, or at the end of the file.
                    if (lastStreamingEvent + getProgressEventInterval() <= now
                            || bytesReadToBuffer <= 0) {
                        lastStreamingEvent = now;
                        session.lock();
                        try {
                            StreamingProgressEventImpl progressEvent = new StreamingProgressEventImpl(
                                    filename, type, contentLength, totalBytes);
                            streamVariable.onProgress(progressEvent);
                        } finally {
                            session.unlock();
                        }
                    }
                }
                if (streamVariable.isInterrupted()) {
                    throw new UploadInterruptedException();
                }
            } while (bytesReadToBuffer > 0);

            // upload successful
            out.close();
            StreamingEndEvent event = new StreamingEndEventImpl(filename, type,
                    totalBytes);
            session.lock();
            try {
                streamVariable.streamingFinished(event);
            } finally {
                session.unlock();
            }

        } catch (UploadInterruptedException e) {
            // Download interrupted by application code
            tryToCloseStream(out);
            StreamingErrorEvent event = new StreamingErrorEventImpl(filename,
                    type, contentLength, totalBytes, e);
            session.lock();
            try {
                streamVariable.streamingFailed(event);
            } finally {
                session.unlock();
            }
            // Note, we are not throwing interrupted exception forward as it is
            // not a terminal level error like all other exception.
        } catch (final Exception e) {
            tryToCloseStream(out);
            session.lock();
            try {
                StreamingErrorEvent event = new StreamingErrorEventImpl(
                        filename, type, contentLength, totalBytes, e);
                streamVariable.streamingFailed(event);
                // throw exception for terminal to be handled (to be passed to
                // terminalErrorHandler)
                throw new UploadException(e);
            } finally {
                session.unlock();
            }
        }
        return startedEvent.isDisposed();
    }

    /**
     * To prevent event storming, streaming progress events are sent in this
     * interval rather than every time the buffer is filled. This fixes #13155.
     * To adjust this value override the method, and register your own handler
     * in VaadinService.createRequestHandlers(). The default is 500ms, and
     * setting it to 0 effectively restores the old behavior.
     */
    protected int getProgressEventInterval() {
        return DEFAULT_STREAMING_PROGRESS_EVENT_INTERVAL_MS;
    }

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
     * Removes any possible path information from the filename and returns the
     * filename. Separators / and \\ are used.
     * 
     * @param name
     * @return
     */
    private static String removePath(String filename) {
        if (filename != null) {
            filename = filename.replaceAll("^.*[/\\\\]", "");
        }

        return filename;
    }

    /**
     * TODO document
     * 
     * @param request
     * @param response
     * @throws IOException
     */
    protected void sendUploadResponse(VaadinRequest request,
            VaadinResponse response) throws IOException {
        response.setContentType("text/html");
        final OutputStream out = response.getOutputStream();
        final PrintWriter outWriter = new PrintWriter(new BufferedWriter(
                new OutputStreamWriter(out, "UTF-8")));
        outWriter.print("<html><body>download handled</body></html>");
        outWriter.flush();
        out.close();
    }

    private void cleanStreamVariable(VaadinSession session,
            final ClientConnector owner, final String variableName) {
        session.accessSynchronously(new Runnable() {
            @Override
            public void run() {
                owner.getUI()
                        .getConnectorTracker()
                        .cleanStreamVariable(owner.getConnectorId(),
                                variableName);
            }
        });
    }
}
