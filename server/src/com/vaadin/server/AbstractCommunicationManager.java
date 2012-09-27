/*
 * Copyright 2011 Vaadin Ltd.
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

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.text.CharacterIterator;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.vaadin.annotations.JavaScript;
import com.vaadin.annotations.StyleSheet;
import com.vaadin.server.ComponentSizeValidator.InvalidLayout;
import com.vaadin.server.RpcManager.RpcInvocationException;
import com.vaadin.server.StreamVariable.StreamingEndEvent;
import com.vaadin.server.StreamVariable.StreamingErrorEvent;
import com.vaadin.server.Terminal.ErrorEvent;
import com.vaadin.server.Terminal.ErrorListener;
import com.vaadin.server.VaadinRequest.BrowserDetails;
import com.vaadin.shared.ApplicationConstants;
import com.vaadin.shared.Connector;
import com.vaadin.shared.JavaScriptConnectorState;
import com.vaadin.shared.Version;
import com.vaadin.shared.communication.LegacyChangeVariablesInvocation;
import com.vaadin.shared.communication.MethodInvocation;
import com.vaadin.shared.communication.SharedState;
import com.vaadin.shared.communication.UidlValue;
import com.vaadin.shared.ui.ui.UIConstants;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Component;
import com.vaadin.ui.ConnectorTracker;
import com.vaadin.ui.HasComponents;
import com.vaadin.ui.UI;
import com.vaadin.ui.UI.LegacyWindow;
import com.vaadin.ui.Window;
import com.vaadin.util.CurrentInstance;

/**
 * This is a common base class for the server-side implementations of the
 * communication system between the client code (compiled with GWT into
 * JavaScript) and the server side components. Its client side counterpart is
 * {@link com.vaadin.client.ApplicationConnection}.
 * <p>
 * TODO Document better!
 * 
 * @deprecated might be refactored or removed before 7.0.0
 */
@Deprecated
@SuppressWarnings("serial")
public abstract class AbstractCommunicationManager implements Serializable {

    private static final String DASHDASH = "--";

    private static final RequestHandler UNSUPPORTED_BROWSER_HANDLER = new UnsupportedBrowserHandler();

    private static final RequestHandler CONNECTOR_RESOURCE_HANDLER = new ConnectorResourceHandler();

    /**
     * TODO Document me!
     * 
     * @author peholmst
     * 
     * @deprecated might be refactored or removed before 7.0.0
     */
    @Deprecated
    public interface Callback extends Serializable {

        public void criticalNotification(VaadinRequest request,
                VaadinResponse response, String cap, String msg,
                String details, String outOfSyncURL) throws IOException;
    }

    static class UploadInterruptedException extends Exception {
        public UploadInterruptedException() {
            super("Upload interrupted by other thread");
        }
    }

    // flag used in the request to indicate that the security token should be
    // written to the response
    private static final String WRITE_SECURITY_TOKEN_FLAG = "writeSecurityToken";

    /* Variable records indexes */
    public static final char VAR_BURST_SEPARATOR = '\u001d';

    public static final char VAR_ESCAPE_CHARACTER = '\u001b';

    private final HashMap<Integer, ClientCache> uiToClientCache = new HashMap<Integer, ClientCache>();

    private static final int MAX_BUFFER_SIZE = 64 * 1024;

    /* Same as in apache commons file upload library that was previously used. */
    private static final int MAX_UPLOAD_BUFFER_SIZE = 4 * 1024;

    private static final String GET_PARAM_ANALYZE_LAYOUTS = "analyzeLayouts";

    /**
     * The session this communication manager is used for
     */
    private final VaadinSession session;

    private List<String> locales;

    private int pendingLocalesIndex;

    private int timeoutInterval = -1;

    private DragAndDropService dragAndDropService;

    private String requestThemeName;

    private int maxInactiveInterval;

    private Connector highlightedConnector;

    private Map<String, Class<?>> connectorResourceContexts = new HashMap<String, Class<?>>();

    private Map<String, Map<String, StreamVariable>> pidToNameToStreamVariable;

    private Map<StreamVariable, String> streamVariableToSeckey;

    /**
     * TODO New constructor - document me!
     * 
     * @param session
     */
    public AbstractCommunicationManager(VaadinSession session) {
        this.session = session;
        session.addRequestHandler(getBootstrapHandler());
        session.addRequestHandler(UNSUPPORTED_BROWSER_HANDLER);
        session.addRequestHandler(CONNECTOR_RESOURCE_HANDLER);
        requireLocale(session.getLocale().toString());
    }

    protected VaadinSession getSession() {
        return session;
    }

    private static final int LF = "\n".getBytes()[0];

    private static final String CRLF = "\r\n";

    private static final String UTF8 = "UTF8";

    private static final String GET_PARAM_HIGHLIGHT_COMPONENT = "highlightComponent";

    private static String readLine(InputStream stream) throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        int readByte = stream.read();
        while (readByte != LF) {
            bout.write(readByte);
            readByte = stream.read();
        }
        byte[] bytes = bout.toByteArray();
        return new String(bytes, 0, bytes.length - 1, UTF8);
    }

    /**
     * Method used to stream content from a multipart request (either from
     * servlet or portlet request) to given StreamVariable
     * 
     * 
     * @param request
     * @param response
     * @param streamVariable
     * @param owner
     * @param boundary
     * @throws IOException
     */
    protected void doHandleSimpleMultipartFileUpload(VaadinRequest request,
            VaadinResponse response, StreamVariable streamVariable,
            String variableName, ClientConnector owner, String boundary)
            throws IOException {
        // multipart parsing, supports only one file for request, but that is
        // fine for our current terminal

        final InputStream inputStream = request.getInputStream();

        int contentLength = request.getContentLength();

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
            contentLength -= (readLine.length() + 2);
            if (readLine.startsWith("Content-Disposition:")
                    && readLine.indexOf("filename=") > 0) {
                rawfilename = readLine.replaceAll(".*filename=", "");
                String parenthesis = rawfilename.substring(0, 1);
                rawfilename = rawfilename.substring(1);
                rawfilename = rawfilename.substring(0,
                        rawfilename.indexOf(parenthesis));
                firstFileFieldFound = true;
            } else if (firstFileFieldFound && readLine.equals("")) {
                atStart = true;
            } else if (readLine.startsWith("Content-Type")) {
                rawMimeType = readLine.split(": ")[1];
            }
        }

        contentLength -= (boundary.length() + CRLF.length() + 2
                * DASHDASH.length() + 2); // 2 == CRLF

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
            // TODO Shouldn't this check connectorEnabled?
            if (owner == null) {
                throw new UploadException(
                        "File upload ignored because the connector for the stream variable was not found");
            }
            if (owner instanceof Component) {
                if (((Component) owner).isReadOnly()) {
                    throw new UploadException(
                            "Warning: file upload ignored because the componente was read-only");
                }
            }
            boolean forgetVariable = streamToReceiver(simpleMultiPartReader,
                    streamVariable, filename, mimeType, contentLength);
            if (forgetVariable) {
                cleanStreamVariable(owner, variableName);
            }
        } catch (Exception e) {
            session.getLock().lock();
            try {
                handleChangeVariablesError(session, (Component) owner, e,
                        new HashMap<String, Object>());
            } finally {
                session.getLock().unlock();
            }
        }
        sendUploadResponse(request, response);

    }

    /**
     * Used to stream plain file post (aka XHR2.post(File))
     * 
     * @param request
     * @param response
     * @param streamVariable
     * @param owner
     * @param contentLength
     * @throws IOException
     */
    protected void doHandleXhrFilePost(VaadinRequest request,
            VaadinResponse response, StreamVariable streamVariable,
            String variableName, ClientConnector owner, int contentLength)
            throws IOException {

        // These are unknown in filexhr ATM, maybe add to Accept header that
        // is accessible in portlets
        final String filename = "unknown";
        final String mimeType = filename;
        final InputStream stream = request.getInputStream();
        try {
            /*
             * safe cast as in GWT terminal all variable owners are expected to
             * be components.
             */
            Component component = (Component) owner;
            if (component.isReadOnly()) {
                throw new UploadException(
                        "Warning: file upload ignored because the component was read-only");
            }
            boolean forgetVariable = streamToReceiver(stream, streamVariable,
                    filename, mimeType, contentLength);
            if (forgetVariable) {
                cleanStreamVariable(owner, variableName);
            }
        } catch (Exception e) {
            session.getLock().lock();
            try {
                handleChangeVariablesError(session, (Component) owner, e,
                        new HashMap<String, Object>());
            } finally {
                session.getLock().unlock();
            }
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
    protected final boolean streamToReceiver(final InputStream in,
            StreamVariable streamVariable, String filename, String type,
            int contentLength) throws UploadException {
        if (streamVariable == null) {
            throw new IllegalStateException(
                    "StreamVariable for the post not found");
        }

        final VaadinSession session = getSession();

        OutputStream out = null;
        int totalBytes = 0;
        StreamingStartEventImpl startedEvent = new StreamingStartEventImpl(
                filename, type, contentLength);
        try {
            boolean listenProgress;
            session.getLock().lock();
            try {
                streamVariable.streamingStarted(startedEvent);
                out = streamVariable.getOutputStream();
                listenProgress = streamVariable.listenProgress();
            } finally {
                session.getLock().unlock();
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
            int bytesReadToBuffer = 0;
            while ((bytesReadToBuffer = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesReadToBuffer);
                totalBytes += bytesReadToBuffer;
                if (listenProgress) {
                    // update progress if listener set and contentLength
                    // received
                    session.getLock().lock();
                    try {
                        StreamingProgressEventImpl progressEvent = new StreamingProgressEventImpl(
                                filename, type, contentLength, totalBytes);
                        streamVariable.onProgress(progressEvent);
                    } finally {
                        session.getLock().unlock();
                    }
                }
                if (streamVariable.isInterrupted()) {
                    throw new UploadInterruptedException();
                }
            }

            // upload successful
            out.close();
            StreamingEndEvent event = new StreamingEndEventImpl(filename, type,
                    totalBytes);
            session.getLock().lock();
            try {
                streamVariable.streamingFinished(event);
            } finally {
                session.getLock().unlock();
            }

        } catch (UploadInterruptedException e) {
            // Download interrupted by application code
            tryToCloseStream(out);
            StreamingErrorEvent event = new StreamingErrorEventImpl(filename,
                    type, contentLength, totalBytes, e);
            session.getLock().lock();
            try {
                streamVariable.streamingFailed(event);
            } finally {
                session.getLock().unlock();
            }
            // Note, we are not throwing interrupted exception forward as it is
            // not a terminal level error like all other exception.
        } catch (final Exception e) {
            tryToCloseStream(out);
            session.getLock().lock();
            try {
                StreamingErrorEvent event = new StreamingErrorEventImpl(
                        filename, type, contentLength, totalBytes, e);
                streamVariable.streamingFailed(event);
                // throw exception for terminal to be handled (to be passed to
                // terminalErrorHandler)
                throw new UploadException(e);
            } finally {
                session.getLock().unlock();
            }
        }
        return startedEvent.isDisposed();
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

    /**
     * Internally process a UIDL request from the client.
     * 
     * This method calls
     * {@link #handleVariables(VaadinRequest, VaadinResponse, Callback, VaadinSession, UI)}
     * to process any changes to variables by the client and then repaints
     * affected components using {@link #paintAfterVariableChanges()}.
     * 
     * Also, some cleanup is done when a request arrives for an session that has
     * already been closed.
     * 
     * The method handleUidlRequest(...) in subclasses should call this method.
     * 
     * TODO better documentation
     * 
     * @param request
     * @param response
     * @param callback
     * @param uI
     *            target window for the UIDL request, can be null if target not
     *            found
     * @throws IOException
     * @throws InvalidUIDLSecurityKeyException
     * @throws JSONException
     */
    public void handleUidlRequest(VaadinRequest request,
            VaadinResponse response, Callback callback, UI uI)
            throws IOException, InvalidUIDLSecurityKeyException, JSONException {

        checkWidgetsetVersion(request);
        requestThemeName = request.getParameter("theme");
        maxInactiveInterval = request.getWrappedSession()
                .getMaxInactiveInterval();
        // repaint requested or session has timed out and new one is created
        boolean repaintAll;
        final OutputStream out;

        repaintAll = (request
                .getParameter(ApplicationConstants.URL_PARAMETER_REPAINT_ALL) != null);
        // || (request.getSession().isNew()); FIXME What the h*ll is this??
        out = response.getOutputStream();

        boolean analyzeLayouts = false;
        if (repaintAll) {
            // analyzing can be done only with repaintAll
            analyzeLayouts = (request.getParameter(GET_PARAM_ANALYZE_LAYOUTS) != null);

            if (request.getParameter(GET_PARAM_HIGHLIGHT_COMPONENT) != null) {
                String pid = request
                        .getParameter(GET_PARAM_HIGHLIGHT_COMPONENT);
                highlightedConnector = uI.getConnectorTracker().getConnector(
                        pid);
                highlightConnector(highlightedConnector);
            }
        }

        final PrintWriter outWriter = new PrintWriter(new BufferedWriter(
                new OutputStreamWriter(out, "UTF-8")));

        // The rest of the process is synchronized with the session
        // in order to guarantee that no parallel variable handling is
        // made
        session.getLock().lock();
        try {

            // Verify that there's an UI
            if (uI == null) {
                // This should not happen, no windows exists but
                // session is still open.
                getLogger().warning("Could not get UI for session");
                return;
            }

            // Keep the UI alive
            uI.setLastUidlRequestTime(System.currentTimeMillis());

            // Change all variables based on request parameters
            if (!handleVariables(request, response, callback, session, uI)) {

                // var inconsistency; the client is probably out-of-sync
                SystemMessages ci = response.getService().getSystemMessages();
                String msg = ci.getOutOfSyncMessage();
                String cap = ci.getOutOfSyncCaption();
                if (msg != null || cap != null) {
                    callback.criticalNotification(request, response, cap, msg,
                            null, ci.getOutOfSyncURL());
                    // will reload page after this
                    return;
                }
                // No message to show, let's just repaint all.
                repaintAll = true;
            }

            paintAfterVariableChanges(request, response, callback, repaintAll,
                    outWriter, uI, analyzeLayouts);
            postPaint(uI);
        } finally {
            session.getLock().unlock();
        }

        outWriter.close();
        requestThemeName = null;
    }

    /**
     * Checks that the version reported by the client (widgetset) matches that
     * of the server.
     * 
     * @param request
     */
    private void checkWidgetsetVersion(VaadinRequest request) {
        String widgetsetVersion = request.getParameter("wsver");
        if (widgetsetVersion == null) {
            // Only check when the widgetset version is reported. It is reported
            // in the first UIDL request (not the initial request as it is a
            // plain GET /)
            return;
        }

        if (!Version.getFullVersion().equals(widgetsetVersion)) {
            getLogger().warning(
                    String.format(Constants.WIDGETSET_MISMATCH_INFO,
                            Version.getFullVersion(), widgetsetVersion));
        }
    }

    /**
     * Method called after the paint phase while still being synchronized on the
     * session
     * 
     * @param uI
     * 
     */
    protected void postPaint(UI uI) {
        if (uI instanceof LegacyWindow) {
            LegacyWindow legacyWindow = (LegacyWindow) uI;
            if (!legacyWindow.getApplication().isRunning()) {
                // Detach LegacyWindow if it belongs to a closed
                // LegacyApplication
                legacyWindow.setApplication(null);
                legacyWindow.setSession(null);
            }
        }

        // Remove connectors that have been detached from the session during
        // handling of the request
        uI.getConnectorTracker().cleanConnectorMap();

        if (pidToNameToStreamVariable != null) {
            Iterator<String> iterator = pidToNameToStreamVariable.keySet()
                    .iterator();
            while (iterator.hasNext()) {
                String connectorId = iterator.next();
                if (uI.getConnectorTracker().getConnector(connectorId) == null) {
                    // Owner is no longer attached to the session
                    Map<String, StreamVariable> removed = pidToNameToStreamVariable
                            .get(connectorId);
                    for (String key : removed.keySet()) {
                        streamVariableToSeckey.remove(removed.get(key));
                    }
                    iterator.remove();
                }
            }
        }
    }

    protected void highlightConnector(Connector highlightedConnector) {
        StringBuilder sb = new StringBuilder();
        sb.append("*** Debug details of a component:  *** \n");
        sb.append("Type: ");
        sb.append(highlightedConnector.getClass().getName());
        if (highlightedConnector instanceof AbstractComponent) {
            AbstractComponent component = (AbstractComponent) highlightedConnector;
            sb.append("\nId:");
            sb.append(highlightedConnector.getConnectorId());
            if (component.getCaption() != null) {
                sb.append("\nCaption:");
                sb.append(component.getCaption());
            }

            printHighlightedComponentHierarchy(sb, component);
        }
        getLogger().info(sb.toString());
    }

    protected void printHighlightedComponentHierarchy(StringBuilder sb,
            AbstractComponent component) {
        LinkedList<Component> h = new LinkedList<Component>();
        h.add(component);
        Component parent = component.getParent();
        while (parent != null) {
            h.addFirst(parent);
            parent = parent.getParent();
        }

        sb.append("\nComponent hierarchy:\n");
        VaadinSession session2 = component.getUI().getSession();
        sb.append(session2.getClass().getName());
        sb.append(".");
        sb.append(session2.getClass().getSimpleName());
        sb.append("(");
        sb.append(session2.getClass().getSimpleName());
        sb.append(".java");
        sb.append(":1)");
        int l = 1;
        for (Component component2 : h) {
            sb.append("\n");
            for (int i = 0; i < l; i++) {
                sb.append("  ");
            }
            l++;
            Class<? extends Component> componentClass = component2.getClass();
            Class<?> topClass = componentClass;
            while (topClass.getEnclosingClass() != null) {
                topClass = topClass.getEnclosingClass();
            }
            sb.append(componentClass.getName());
            sb.append(".");
            sb.append(componentClass.getSimpleName());
            sb.append("(");
            sb.append(topClass.getSimpleName());
            sb.append(".java:1)");
        }
    }

    /**
     * TODO document
     * 
     * @param request
     * @param response
     * @param callback
     * @param repaintAll
     * @param outWriter
     * @param window
     * @param analyzeLayouts
     * @throws PaintException
     * @throws IOException
     * @throws JSONException
     */
    private void paintAfterVariableChanges(VaadinRequest request,
            VaadinResponse response, Callback callback, boolean repaintAll,
            final PrintWriter outWriter, UI uI, boolean analyzeLayouts)
            throws PaintException, IOException, JSONException {
        openJsonMessage(outWriter, response);

        // security key
        Object writeSecurityTokenFlag = request
                .getAttribute(WRITE_SECURITY_TOKEN_FLAG);

        if (writeSecurityTokenFlag != null) {
            outWriter.print(getSecurityKeyUIDL(request));
        }

        writeUidlResponse(request, repaintAll, outWriter, uI, analyzeLayouts);

        closeJsonMessage(outWriter);

        outWriter.close();

    }

    /**
     * Gets the security key (and generates one if needed) as UIDL.
     * 
     * @param request
     * @return the security key UIDL or "" if the feature is turned off
     */
    public String getSecurityKeyUIDL(VaadinRequest request) {
        final String seckey = getSecurityKey(request);
        if (seckey != null) {
            return "\"" + ApplicationConstants.UIDL_SECURITY_TOKEN_ID + "\":\""
                    + seckey + "\",";
        } else {
            return "";
        }
    }

    /**
     * Gets the security key (and generates one if needed).
     * 
     * @param request
     * @return the security key
     */
    protected String getSecurityKey(VaadinRequest request) {
        String seckey = null;
        WrappedSession session = request.getWrappedSession();
        seckey = (String) session
                .getAttribute(ApplicationConstants.UIDL_SECURITY_TOKEN_ID);
        if (seckey == null) {
            seckey = UUID.randomUUID().toString();
            session.setAttribute(ApplicationConstants.UIDL_SECURITY_TOKEN_ID,
                    seckey);
        }

        return seckey;
    }

    @SuppressWarnings("unchecked")
    public void writeUidlResponse(VaadinRequest request, boolean repaintAll,
            final PrintWriter outWriter, UI ui, boolean analyzeLayouts)
            throws PaintException, JSONException {
        ArrayList<ClientConnector> dirtyVisibleConnectors = new ArrayList<ClientConnector>();
        VaadinSession session = ui.getSession();
        // Paints components
        ConnectorTracker uiConnectorTracker = ui.getConnectorTracker();
        getLogger().log(Level.FINE, "* Creating response to client");
        if (repaintAll) {
            getClientCache(ui).clear();
            uiConnectorTracker.markAllConnectorsDirty();
            uiConnectorTracker.markAllClientSidesUninitialized();

            // Reset sent locales
            locales = null;
            requireLocale(session.getLocale().toString());
        }

        uiConnectorTracker.setWritingResponse(true);
        try {

            dirtyVisibleConnectors
                    .addAll(getDirtyVisibleConnectors(uiConnectorTracker));

            getLogger().log(
                    Level.FINE,
                    "Found " + dirtyVisibleConnectors.size()
                            + " dirty connectors to paint");
            for (ClientConnector connector : dirtyVisibleConnectors) {
                boolean initialized = uiConnectorTracker
                        .isClientSideInitialized(connector);
                connector.beforeClientResponse(!initialized);
            }

            outWriter.print("\"changes\":[");

            List<InvalidLayout> invalidComponentRelativeSizes = null;

            JsonPaintTarget paintTarget = new JsonPaintTarget(this, outWriter,
                    !repaintAll);
            legacyPaint(paintTarget, dirtyVisibleConnectors);

            if (analyzeLayouts) {
                invalidComponentRelativeSizes = ComponentSizeValidator
                        .validateComponentRelativeSizes(ui.getContent(), null,
                                null);

                // Also check any existing subwindows
                if (ui.getWindows() != null) {
                    for (Window subWindow : ui.getWindows()) {
                        invalidComponentRelativeSizes = ComponentSizeValidator
                                .validateComponentRelativeSizes(
                                        subWindow.getContent(),
                                        invalidComponentRelativeSizes, null);
                    }
                }
            }

            paintTarget.close();
            outWriter.print("], "); // close changes

            // send shared state to client

            // for now, send the complete state of all modified and new
            // components

            // Ideally, all this would be sent before "changes", but that causes
            // complications with legacy components that create sub-components
            // in their paint phase. Nevertheless, this will be processed on the
            // client after component creation but before legacy UIDL
            // processing.
            JSONObject sharedStates = new JSONObject();
            for (ClientConnector connector : dirtyVisibleConnectors) {
                // encode and send shared state
                try {
                    JSONObject stateJson = connector.encodeState();

                    if (stateJson != null && stateJson.length() != 0) {
                        sharedStates.put(connector.getConnectorId(), stateJson);
                    }
                } catch (JSONException e) {
                    throw new PaintException(
                            "Failed to serialize shared state for connector "
                                    + connector.getClass().getName() + " ("
                                    + connector.getConnectorId() + "): "
                                    + e.getMessage(), e);
                }
            }
            outWriter.print("\"state\":");
            outWriter.append(sharedStates.toString());
            outWriter.print(", "); // close states

            // TODO This should be optimized. The type only needs to be
            // sent once for each connector id + on refresh. Use the same cache
            // as
            // widget mapping

            JSONObject connectorTypes = new JSONObject();
            for (ClientConnector connector : dirtyVisibleConnectors) {
                String connectorType = paintTarget.getTag(connector);
                try {
                    connectorTypes.put(connector.getConnectorId(),
                            connectorType);
                } catch (JSONException e) {
                    throw new PaintException(
                            "Failed to send connector type for connector "
                                    + connector.getConnectorId() + ": "
                                    + e.getMessage(), e);
                }
            }
            outWriter.print("\"types\":");
            outWriter.append(connectorTypes.toString());
            outWriter.print(", "); // close states

            // Send update hierarchy information to the client.

            // This could be optimized aswell to send only info if hierarchy has
            // actually changed. Much like with the shared state. Note though
            // that an empty hierarchy is information aswell (e.g. change from 1
            // child to 0 children)

            outWriter.print("\"hierarchy\":");

            JSONObject hierarchyInfo = new JSONObject();
            for (ClientConnector connector : dirtyVisibleConnectors) {
                String connectorId = connector.getConnectorId();
                JSONArray children = new JSONArray();

                for (ClientConnector child : AbstractClientConnector
                        .getAllChildrenIterable(connector)) {
                    if (isVisible(child)) {
                        children.put(child.getConnectorId());
                    }
                }
                try {
                    hierarchyInfo.put(connectorId, children);
                } catch (JSONException e) {
                    throw new PaintException(
                            "Failed to send hierarchy information about "
                                    + connectorId + " to the client: "
                                    + e.getMessage(), e);
                }
            }
            outWriter.append(hierarchyInfo.toString());
            outWriter.print(", "); // close hierarchy

            uiConnectorTracker.markAllConnectorsClean();

            // send server to client RPC calls for components in the UI, in call
            // order

            // collect RPC calls from components in the UI in the order in
            // which they were performed, remove the calls from components

            LinkedList<ClientConnector> rpcPendingQueue = new LinkedList<ClientConnector>(
                    dirtyVisibleConnectors);
            List<ClientMethodInvocation> pendingInvocations = collectPendingRpcCalls(dirtyVisibleConnectors);

            JSONArray rpcCalls = new JSONArray();
            for (ClientMethodInvocation invocation : pendingInvocations) {
                // add invocation to rpcCalls
                try {
                    JSONArray invocationJson = new JSONArray();
                    invocationJson.put(invocation.getConnector()
                            .getConnectorId());
                    invocationJson.put(invocation.getInterfaceName());
                    invocationJson.put(invocation.getMethodName());
                    JSONArray paramJson = new JSONArray();
                    for (int i = 0; i < invocation.getParameterTypes().length; ++i) {
                        Type parameterType = invocation.getParameterTypes()[i];
                        Object referenceParameter = null;
                        // TODO Use default values for RPC parameter types
                        // if (!JsonCodec.isInternalType(parameterType)) {
                        // try {
                        // referenceParameter = parameterType.newInstance();
                        // } catch (Exception e) {
                        // logger.log(Level.WARNING,
                        // "Error creating reference object for parameter of type "
                        // + parameterType.getName());
                        // }
                        // }
                        EncodeResult encodeResult = JsonCodec.encode(
                                invocation.getParameters()[i],
                                referenceParameter, parameterType,
                                ui.getConnectorTracker());
                        paramJson.put(encodeResult.getEncodedValue());
                    }
                    invocationJson.put(paramJson);
                    rpcCalls.put(invocationJson);
                } catch (JSONException e) {
                    throw new PaintException(
                            "Failed to serialize RPC method call parameters for connector "
                                    + invocation.getConnector()
                                            .getConnectorId() + " method "
                                    + invocation.getInterfaceName() + "."
                                    + invocation.getMethodName() + ": "
                                    + e.getMessage(), e);
                }

            }

            if (rpcCalls.length() > 0) {
                outWriter.print("\"rpc\" : ");
                outWriter.append(rpcCalls.toString());
                outWriter.print(", "); // close rpc
            }

            outWriter.print("\"meta\" : {");
            boolean metaOpen = false;

            if (repaintAll) {
                metaOpen = true;
                outWriter.write("\"repaintAll\":true");
                if (analyzeLayouts) {
                    outWriter.write(", \"invalidLayouts\":");
                    outWriter.write("[");
                    if (invalidComponentRelativeSizes != null) {
                        boolean first = true;
                        for (InvalidLayout invalidLayout : invalidComponentRelativeSizes) {
                            if (!first) {
                                outWriter.write(",");
                            } else {
                                first = false;
                            }
                            invalidLayout.reportErrors(outWriter, this,
                                    System.err);
                        }
                    }
                    outWriter.write("]");
                }
                if (highlightedConnector != null) {
                    outWriter.write(", \"hl\":\"");
                    outWriter.write(highlightedConnector.getConnectorId());
                    outWriter.write("\"");
                    highlightedConnector = null;
                }
            }

            SystemMessages ci = request.getService().getSystemMessages();

            // meta instruction for client to enable auto-forward to
            // sessionExpiredURL after timer expires.
            if (ci != null && ci.getSessionExpiredMessage() == null
                    && ci.getSessionExpiredCaption() == null
                    && ci.isSessionExpiredNotificationEnabled()) {
                int newTimeoutInterval = getTimeoutInterval();
                if (repaintAll || (timeoutInterval != newTimeoutInterval)) {
                    String escapedURL = ci.getSessionExpiredURL() == null ? ""
                            : ci.getSessionExpiredURL().replace("/", "\\/");
                    if (metaOpen) {
                        outWriter.write(",");
                    }
                    outWriter.write("\"timedRedirect\":{\"interval\":"
                            + (newTimeoutInterval + 15) + ",\"url\":\""
                            + escapedURL + "\"}");
                    metaOpen = true;
                }
                timeoutInterval = newTimeoutInterval;
            }

            outWriter.print("}, \"resources\" : {");

            // Precache custom layouts

            // TODO We should only precache the layouts that are not
            // cached already (plagiate from usedPaintableTypes)
            int resourceIndex = 0;
            for (final Iterator<Object> i = paintTarget.getUsedResources()
                    .iterator(); i.hasNext();) {
                final String resource = (String) i.next();
                InputStream is = null;
                try {
                    is = getThemeResourceAsStream(ui, getTheme(ui), resource);
                } catch (final Exception e) {
                    // FIXME: Handle exception
                    getLogger().log(Level.FINER,
                            "Failed to get theme resource stream.", e);
                }
                if (is != null) {

                    outWriter.print((resourceIndex++ > 0 ? ", " : "") + "\""
                            + resource + "\" : ");
                    final StringBuffer layout = new StringBuffer();

                    try {
                        final InputStreamReader r = new InputStreamReader(is,
                                "UTF-8");
                        final char[] buffer = new char[20000];
                        int charsRead = 0;
                        while ((charsRead = r.read(buffer)) > 0) {
                            layout.append(buffer, 0, charsRead);
                        }
                        r.close();
                    } catch (final java.io.IOException e) {
                        // FIXME: Handle exception
                        getLogger().log(Level.INFO, "Resource transfer failed",
                                e);
                    }
                    outWriter.print("\""
                            + JsonPaintTarget.escapeJSON(layout.toString())
                            + "\"");
                } else {
                    // FIXME: Handle exception
                    getLogger().severe("CustomLayout not found: " + resource);
                }
            }
            outWriter.print("}");

            Collection<Class<? extends ClientConnector>> usedClientConnectors = paintTarget
                    .getUsedClientConnectors();
            boolean typeMappingsOpen = false;
            ClientCache clientCache = getClientCache(ui);

            List<Class<? extends ClientConnector>> newConnectorTypes = new ArrayList<Class<? extends ClientConnector>>();

            for (Class<? extends ClientConnector> class1 : usedClientConnectors) {
                if (clientCache.cache(class1)) {
                    // client does not know the mapping key for this type, send
                    // mapping to client
                    newConnectorTypes.add(class1);

                    if (!typeMappingsOpen) {
                        typeMappingsOpen = true;
                        outWriter.print(", \"typeMappings\" : { ");
                    } else {
                        outWriter.print(" , ");
                    }
                    String canonicalName = class1.getCanonicalName();
                    outWriter.print("\"");
                    outWriter.print(canonicalName);
                    outWriter.print("\" : ");
                    outWriter.print(getTagForType(class1));
                }
            }
            if (typeMappingsOpen) {
                outWriter.print(" }");
            }

            boolean typeInheritanceMapOpen = false;
            if (typeMappingsOpen) {
                // send the whole type inheritance map if any new mappings
                for (Class<? extends ClientConnector> class1 : usedClientConnectors) {
                    if (!ClientConnector.class.isAssignableFrom(class1
                            .getSuperclass())) {
                        continue;
                    }
                    if (!typeInheritanceMapOpen) {
                        typeInheritanceMapOpen = true;
                        outWriter.print(", \"typeInheritanceMap\" : { ");
                    } else {
                        outWriter.print(" , ");
                    }
                    outWriter.print("\"");
                    outWriter.print(getTagForType(class1));
                    outWriter.print("\" : ");
                    outWriter
                            .print(getTagForType((Class<? extends ClientConnector>) class1
                                    .getSuperclass()));
                }
                if (typeInheritanceMapOpen) {
                    outWriter.print(" }");
                }
            }

            /*
             * Ensure super classes come before sub classes to get script
             * dependency order right. Sub class @JavaScript might assume that
             * 
             * @JavaScript defined by super class is already loaded.
             */
            Collections.sort(newConnectorTypes, new Comparator<Class<?>>() {
                @Override
                public int compare(Class<?> o1, Class<?> o2) {
                    // TODO optimize using Class.isAssignableFrom?
                    return hierarchyDepth(o1) - hierarchyDepth(o2);
                }

                private int hierarchyDepth(Class<?> type) {
                    if (type == Object.class) {
                        return 0;
                    } else {
                        return hierarchyDepth(type.getSuperclass()) + 1;
                    }
                }
            });

            List<String> scriptDependencies = new ArrayList<String>();
            List<String> styleDependencies = new ArrayList<String>();

            for (Class<? extends ClientConnector> class1 : newConnectorTypes) {
                JavaScript jsAnnotation = class1
                        .getAnnotation(JavaScript.class);
                if (jsAnnotation != null) {
                    for (String resource : jsAnnotation.value()) {
                        scriptDependencies.add(registerResource(resource,
                                class1));
                    }
                }

                StyleSheet styleAnnotation = class1
                        .getAnnotation(StyleSheet.class);
                if (styleAnnotation != null) {
                    for (String resource : styleAnnotation.value()) {
                        styleDependencies
                                .add(registerResource(resource, class1));
                    }
                }
            }

            // Include script dependencies in output if there are any
            if (!scriptDependencies.isEmpty()) {
                outWriter.print(", \"scriptDependencies\": "
                        + new JSONArray(scriptDependencies).toString());
            }

            // Include style dependencies in output if there are any
            if (!styleDependencies.isEmpty()) {
                outWriter.print(", \"styleDependencies\": "
                        + new JSONArray(styleDependencies).toString());
            }

            // add any pending locale definitions requested by the client
            printLocaleDeclarations(outWriter);

            if (dragAndDropService != null) {
                dragAndDropService.printJSONResponse(outWriter);
            }

            for (ClientConnector connector : dirtyVisibleConnectors) {
                uiConnectorTracker.markClientSideInitialized(connector);
            }

            assert (uiConnectorTracker.getDirtyConnectors().isEmpty()) : "Connectors have been marked as dirty during the end of the paint phase. This is most certainly not intended.";

            writePerformanceData(outWriter);
        } finally {
            uiConnectorTracker.setWritingResponse(false);
        }
    }

    public static JSONObject encodeState(ClientConnector connector,
            SharedState state) throws JSONException {
        UI uI = connector.getUI();
        ConnectorTracker connectorTracker = uI.getConnectorTracker();
        Class<? extends SharedState> stateType = connector.getStateType();
        Object diffState = connectorTracker.getDiffState(connector);
        boolean supportsDiffState = !JavaScriptConnectorState.class
                .isAssignableFrom(stateType);
        if (diffState == null && supportsDiffState) {
            // Use an empty state object as reference for full
            // repaints

            try {
                SharedState referenceState = stateType.newInstance();
                EncodeResult encodeResult = JsonCodec.encode(referenceState,
                        null, stateType, uI.getConnectorTracker());
                diffState = encodeResult.getEncodedValue();
            } catch (Exception e) {
                getLogger().log(
                        Level.WARNING,
                        "Error creating reference object for state of type "
                                + stateType.getName());
            }
        }
        EncodeResult encodeResult = JsonCodec.encode(state, diffState,
                stateType, uI.getConnectorTracker());
        if (supportsDiffState) {
            connectorTracker.setDiffState(connector,
                    encodeResult.getEncodedValue());
        }
        return (JSONObject) encodeResult.getDiff();
    }

    /**
     * Resolves a resource URI, registering the URI with this
     * {@code AbstractCommunicationManager} if needed and returns a fully
     * qualified URI.
     */
    private String registerResource(String resourceUri, Class<?> context) {
        try {
            URI uri = new URI(resourceUri);
            String protocol = uri.getScheme();

            if ("connector".equals(protocol)) {
                // Strip initial slash
                String resourceName = uri.getPath().substring(1);
                return registerConnectorResource(resourceName, context);
            }

            if (protocol != null || uri.getHost() != null) {
                return resourceUri;
            }

            // Bare path interpreted as connector resource
            return registerConnectorResource(resourceUri, context);
        } catch (URISyntaxException e) {
            getLogger().log(Level.WARNING,
                    "Could not parse resource url " + resourceUri, e);
            return resourceUri;
        }
    }

    private String registerConnectorResource(String name, Class<?> context) {
        synchronized (connectorResourceContexts) {
            // Add to map of names accepted by serveConnectorResource
            if (connectorResourceContexts.containsKey(name)) {
                Class<?> oldContext = connectorResourceContexts.get(name);
                if (oldContext != context) {
                    getLogger().warning(
                            "Resource " + name + " defined by both " + context
                                    + " and " + oldContext + ". Resource from "
                                    + oldContext + " will be used.");
                }
            } else {
                connectorResourceContexts.put(name, context);
            }
        }

        return ApplicationConstants.CONNECTOR_PROTOCOL_PREFIX + "/" + name;
    }

    /**
     * Adds the performance timing data (used by TestBench 3) to the UIDL
     * response.
     */
    private void writePerformanceData(final PrintWriter outWriter) {
        outWriter.write(String.format(", \"timings\":[%d, %d]",
                session.getTotalSessionTime(), session.getLastRequestTime()));
    }

    private void legacyPaint(PaintTarget paintTarget,
            ArrayList<ClientConnector> dirtyVisibleConnectors)
            throws PaintException {
        List<LegacyComponent> legacyComponents = new ArrayList<LegacyComponent>();
        for (Connector connector : dirtyVisibleConnectors) {
            // All Components that want to use paintContent must implement
            // LegacyComponent
            if (connector instanceof LegacyComponent) {
                legacyComponents.add((LegacyComponent) connector);
            }
        }
        sortByHierarchy((List) legacyComponents);
        for (LegacyComponent c : legacyComponents) {
            getLogger().fine(
                    "Painting LegacyComponent " + c.getClass().getName() + "@"
                            + Integer.toHexString(c.hashCode()));
            paintTarget.startTag("change");
            final String pid = c.getConnectorId();
            paintTarget.addAttribute("pid", pid);
            LegacyPaint.paint(c, paintTarget);
            paintTarget.endTag("change");
        }

    }

    private void sortByHierarchy(List<Component> paintables) {
        // Vaadin 6 requires parents to be painted before children as component
        // containers rely on that their updateFromUIDL method has been called
        // before children start calling e.g. updateCaption
        Collections.sort(paintables, new Comparator<Component>() {

            @Override
            public int compare(Component c1, Component c2) {
                int depth1 = 0;
                while (c1.getParent() != null) {
                    depth1++;
                    c1 = c1.getParent();
                }
                int depth2 = 0;
                while (c2.getParent() != null) {
                    depth2++;
                    c2 = c2.getParent();
                }
                if (depth1 < depth2) {
                    return -1;
                }
                if (depth1 > depth2) {
                    return 1;
                }
                return 0;
            }
        });

    }

    private ClientCache getClientCache(UI uI) {
        Integer uiId = Integer.valueOf(uI.getUIId());
        ClientCache cache = uiToClientCache.get(uiId);
        if (cache == null) {
            cache = new ClientCache();
            uiToClientCache.put(uiId, cache);
        }
        return cache;
    }

    /**
     * Checks if the connector is visible in context. For Components,
     * {@link #isVisible(Component)} is used. For other types of connectors, the
     * contextual visibility of its first Component ancestor is used. If no
     * Component ancestor is found, the connector is not visible.
     * 
     * @param connector
     *            The connector to check
     * @return <code>true</code> if the connector is visible to the client,
     *         <code>false</code> otherwise
     */
    public static boolean isVisible(ClientConnector connector) {
        if (connector instanceof Component) {
            return isVisible((Component) connector);
        } else {
            ClientConnector parent = connector.getParent();
            if (parent == null) {
                return false;
            } else {
                return isVisible(parent);
            }
        }
    }

    /**
     * Checks if the component is visible in context, i.e. returns false if the
     * child is hidden, the parent is hidden or the parent says the child should
     * not be rendered (using
     * {@link HasComponents#isComponentVisible(Component)}
     * 
     * @param child
     *            The child to check
     * @return true if the child is visible to the client, false otherwise
     */
    static boolean isVisible(Component child) {
        if (!child.isVisible()) {
            return false;
        }

        HasComponents parent = child.getParent();
        if (parent == null) {
            if (child instanceof UI) {
                return child.isVisible();
            } else {
                return false;
            }
        }

        return parent.isComponentVisible(child) && isVisible(parent);
    }

    private static class NullIterator<E> implements Iterator<E> {

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public E next() {
            return null;
        }

        @Override
        public void remove() {
        }

    }

    /**
     * Collects all pending RPC calls from listed {@link ClientConnector}s and
     * clears their RPC queues.
     * 
     * @param rpcPendingQueue
     *            list of {@link ClientConnector} of interest
     * @return ordered list of pending RPC calls
     */
    private List<ClientMethodInvocation> collectPendingRpcCalls(
            List<ClientConnector> rpcPendingQueue) {
        List<ClientMethodInvocation> pendingInvocations = new ArrayList<ClientMethodInvocation>();
        for (ClientConnector connector : rpcPendingQueue) {
            List<ClientMethodInvocation> paintablePendingRpc = connector
                    .retrievePendingRpcCalls();
            if (null != paintablePendingRpc && !paintablePendingRpc.isEmpty()) {
                List<ClientMethodInvocation> oldPendingRpc = pendingInvocations;
                int totalCalls = pendingInvocations.size()
                        + paintablePendingRpc.size();
                pendingInvocations = new ArrayList<ClientMethodInvocation>(
                        totalCalls);

                // merge two ordered comparable lists
                for (int destIndex = 0, oldIndex = 0, paintableIndex = 0; destIndex < totalCalls; destIndex++) {
                    if (paintableIndex >= paintablePendingRpc.size()
                            || (oldIndex < oldPendingRpc.size() && ((Comparable<ClientMethodInvocation>) oldPendingRpc
                                    .get(oldIndex))
                                    .compareTo(paintablePendingRpc
                                            .get(paintableIndex)) <= 0)) {
                        pendingInvocations.add(oldPendingRpc.get(oldIndex++));
                    } else {
                        pendingInvocations.add(paintablePendingRpc
                                .get(paintableIndex++));
                    }
                }
            }
        }
        return pendingInvocations;
    }

    protected abstract InputStream getThemeResourceAsStream(UI uI,
            String themeName, String resource);

    private int getTimeoutInterval() {
        return maxInactiveInterval;
    }

    private String getTheme(UI uI) {
        String themeName = uI.getTheme();
        String requestThemeName = getRequestTheme();

        if (requestThemeName != null) {
            themeName = requestThemeName;
        }
        if (themeName == null) {
            themeName = VaadinServlet.getDefaultTheme();
        }
        return themeName;
    }

    private String getRequestTheme() {
        return requestThemeName;
    }

    /**
     * Returns false if the cross site request forgery protection is turned off.
     * 
     * @param session
     * @return false if the XSRF is turned off, true otherwise
     */
    public boolean isXSRFEnabled(VaadinSession session) {
        return session.getConfiguration().isXsrfProtectionEnabled();
    }

    /**
     * TODO document
     * 
     * If this method returns false, something was submitted that we did not
     * expect; this is probably due to the client being out-of-sync and sending
     * variable changes for non-existing pids
     * 
     * @return true if successful, false if there was an inconsistency
     */
    private boolean handleVariables(VaadinRequest request,
            VaadinResponse response, Callback callback, VaadinSession session,
            UI uI) throws IOException, InvalidUIDLSecurityKeyException,
            JSONException {
        boolean success = true;

        String changes = getRequestPayload(request);
        if (changes != null) {

            // Manage bursts one by one
            final String[] bursts = changes.split(String
                    .valueOf(VAR_BURST_SEPARATOR));

            // Security: double cookie submission pattern unless disabled by
            // property
            if (isXSRFEnabled(session)) {
                if (bursts.length == 1 && "init".equals(bursts[0])) {
                    // init request; don't handle any variables, key sent in
                    // response.
                    request.setAttribute(WRITE_SECURITY_TOKEN_FLAG, true);
                    return true;
                } else {
                    // ApplicationServlet has stored the security token in the
                    // session; check that it matched the one sent in the UIDL
                    String sessId = (String) request
                            .getWrappedSession()
                            .getAttribute(
                                    ApplicationConstants.UIDL_SECURITY_TOKEN_ID);

                    if (sessId == null || !sessId.equals(bursts[0])) {
                        throw new InvalidUIDLSecurityKeyException(
                                "Security key mismatch");
                    }
                }

            }

            for (int bi = 1; bi < bursts.length; bi++) {
                // unescape any encoded separator characters in the burst
                final String burst = unescapeBurst(bursts[bi]);
                success &= handleBurst(request, uI, burst);

                // In case that there were multiple bursts, we know that this is
                // a special synchronous case for closing window. Thus we are
                // not interested in sending any UIDL changes back to client.
                // Still we must clear component tree between bursts to ensure
                // that no removed components are updated. The painting after
                // the last burst is handled normally by the calling method.
                if (bi < bursts.length - 1) {

                    // We will be discarding all changes
                    final PrintWriter outWriter = new PrintWriter(
                            new CharArrayWriter());

                    paintAfterVariableChanges(request, response, callback,
                            true, outWriter, uI, false);

                }

            }
        }
        /*
         * Note that we ignore inconsistencies while handling unload request.
         * The client can't remove invalid variable changes from the burst, and
         * we don't have the required logic implemented on the server side. E.g.
         * a component is removed in a previous burst.
         */
        return success;
    }

    /**
     * Processes a message burst received from the client.
     * 
     * A burst can contain any number of RPC calls, including legacy variable
     * change calls that are processed separately.
     * 
     * Consecutive changes to the value of the same variable are combined and
     * changeVariables() is only called once for them. This preserves the Vaadin
     * 6 semantics for components and add-ons that do not use Vaadin 7 RPC
     * directly.
     * 
     * @param source
     * @param uI
     *            the UI receiving the burst
     * @param burst
     *            the content of the burst as a String to be parsed
     * @return true if the processing of the burst was successful and there were
     *         no messages to non-existent components
     */
    public boolean handleBurst(VaadinRequest source, UI uI, final String burst) {
        boolean success = true;
        try {
            Set<Connector> enabledConnectors = new HashSet<Connector>();

            List<MethodInvocation> invocations = parseInvocations(
                    uI.getConnectorTracker(), burst);
            for (MethodInvocation invocation : invocations) {
                final ClientConnector connector = getConnector(uI,
                        invocation.getConnectorId());

                if (connector != null && connector.isConnectorEnabled()) {
                    enabledConnectors.add(connector);
                }
            }

            for (int i = 0; i < invocations.size(); i++) {
                MethodInvocation invocation = invocations.get(i);

                final ClientConnector connector = getConnector(uI,
                        invocation.getConnectorId());

                if (connector == null) {
                    getLogger().log(
                            Level.WARNING,
                            "RPC call to " + invocation.getInterfaceName()
                                    + "." + invocation.getMethodName()
                                    + " received for connector "
                                    + invocation.getConnectorId()
                                    + " but no such connector could be found");
                    continue;
                }

                if (!enabledConnectors.contains(connector)) {

                    if (invocation instanceof LegacyChangeVariablesInvocation) {
                        LegacyChangeVariablesInvocation legacyInvocation = (LegacyChangeVariablesInvocation) invocation;
                        // TODO convert window close to a separate RPC call and
                        // handle above - not a variable change

                        // Handle special case where window-close is called
                        // after the window has been removed from the
                        // application or the application has closed
                        Map<String, Object> changes = legacyInvocation
                                .getVariableChanges();
                        if (changes.size() == 1 && changes.containsKey("close")
                                && Boolean.TRUE.equals(changes.get("close"))) {
                            // Silently ignore this
                            continue;
                        }
                    }

                    // Connector is disabled, log a warning and move to the next
                    String msg = "Ignoring RPC call for disabled connector "
                            + connector.getClass().getName();
                    if (connector instanceof Component) {
                        String caption = ((Component) connector).getCaption();
                        if (caption != null) {
                            msg += ", caption=" + caption;
                        }
                    }
                    getLogger().warning(msg);
                    continue;
                }

                if (invocation instanceof ServerRpcMethodInvocation) {
                    try {
                        ServerRpcManager.applyInvocation(connector,
                                (ServerRpcMethodInvocation) invocation);
                    } catch (RpcInvocationException e) {
                        Throwable realException = e.getCause();
                        Component errorComponent = null;
                        if (connector instanceof Component) {
                            errorComponent = (Component) connector;
                        }
                        handleChangeVariablesError(uI.getSession(),
                                errorComponent, realException, null);
                    }
                } else {

                    // All code below is for legacy variable changes
                    LegacyChangeVariablesInvocation legacyInvocation = (LegacyChangeVariablesInvocation) invocation;
                    Map<String, Object> changes = legacyInvocation
                            .getVariableChanges();
                    try {
                        if (connector instanceof VariableOwner) {
                            changeVariables(source, (VariableOwner) connector,
                                    changes);
                        } else {
                            throw new IllegalStateException(
                                    "Received legacy variable change for "
                                            + connector.getClass().getName()
                                            + " ("
                                            + connector.getConnectorId()
                                            + ") which is not a VariableOwner. The client-side connector sent these legacy varaibles: "
                                            + changes.keySet());
                        }
                    } catch (Exception e) {
                        Component errorComponent = null;
                        if (connector instanceof Component) {
                            errorComponent = (Component) connector;
                        } else if (connector instanceof DragAndDropService) {
                            Object dropHandlerOwner = changes.get("dhowner");
                            if (dropHandlerOwner instanceof Component) {
                                errorComponent = (Component) dropHandlerOwner;
                            }
                        }
                        handleChangeVariablesError(uI.getSession(),
                                errorComponent, e, changes);
                    }
                }
            }
        } catch (JSONException e) {
            getLogger().warning(
                    "Unable to parse RPC call from the client: "
                            + e.getMessage());
            // TODO or return success = false?
            throw new RuntimeException(e);
        }

        return success;
    }

    /**
     * Parse a message burst from the client into a list of MethodInvocation
     * instances.
     * 
     * @param connectorTracker
     *            The ConnectorTracker used to lookup connectors
     * @param burst
     *            message string (JSON)
     * @return list of MethodInvocation to perform
     * @throws JSONException
     */
    private List<MethodInvocation> parseInvocations(
            ConnectorTracker connectorTracker, final String burst)
            throws JSONException {
        JSONArray invocationsJson = new JSONArray(burst);

        ArrayList<MethodInvocation> invocations = new ArrayList<MethodInvocation>();

        MethodInvocation previousInvocation = null;
        // parse JSON to MethodInvocations
        for (int i = 0; i < invocationsJson.length(); ++i) {

            JSONArray invocationJson = invocationsJson.getJSONArray(i);

            MethodInvocation invocation = parseInvocation(invocationJson,
                    previousInvocation, connectorTracker);
            if (invocation != null) {
                // Can be null iff the invocation was a legacy invocation and it
                // was merged with the previous one
                invocations.add(invocation);
                previousInvocation = invocation;
            }
        }
        return invocations;
    }

    private MethodInvocation parseInvocation(JSONArray invocationJson,
            MethodInvocation previousInvocation,
            ConnectorTracker connectorTracker) throws JSONException {
        String connectorId = invocationJson.getString(0);
        String interfaceName = invocationJson.getString(1);
        String methodName = invocationJson.getString(2);

        JSONArray parametersJson = invocationJson.getJSONArray(3);

        if (LegacyChangeVariablesInvocation.isLegacyVariableChange(
                interfaceName, methodName)) {
            if (!(previousInvocation instanceof LegacyChangeVariablesInvocation)) {
                previousInvocation = null;
            }

            return parseLegacyChangeVariablesInvocation(connectorId,
                    interfaceName, methodName,
                    (LegacyChangeVariablesInvocation) previousInvocation,
                    parametersJson, connectorTracker);
        } else {
            return parseServerRpcInvocation(connectorId, interfaceName,
                    methodName, parametersJson, connectorTracker);
        }

    }

    private LegacyChangeVariablesInvocation parseLegacyChangeVariablesInvocation(
            String connectorId, String interfaceName, String methodName,
            LegacyChangeVariablesInvocation previousInvocation,
            JSONArray parametersJson, ConnectorTracker connectorTracker)
            throws JSONException {
        if (parametersJson.length() != 2) {
            throw new JSONException(
                    "Invalid parameters in legacy change variables call. Expected 2, was "
                            + parametersJson.length());
        }
        String variableName = parametersJson.getString(0);
        UidlValue uidlValue = (UidlValue) JsonCodec.decodeInternalType(
                UidlValue.class, true, parametersJson.get(1), connectorTracker);

        Object value = uidlValue.getValue();

        if (previousInvocation != null
                && previousInvocation.getConnectorId().equals(connectorId)) {
            previousInvocation.setVariableChange(variableName, value);
            return null;
        } else {
            return new LegacyChangeVariablesInvocation(connectorId,
                    variableName, value);
        }
    }

    private ServerRpcMethodInvocation parseServerRpcInvocation(
            String connectorId, String interfaceName, String methodName,
            JSONArray parametersJson, ConnectorTracker connectorTracker)
            throws JSONException {
        ServerRpcMethodInvocation invocation = new ServerRpcMethodInvocation(
                connectorId, interfaceName, methodName, parametersJson.length());

        Object[] parameters = new Object[parametersJson.length()];
        Type[] declaredRpcMethodParameterTypes = invocation.getMethod()
                .getGenericParameterTypes();

        for (int j = 0; j < parametersJson.length(); ++j) {
            Object parameterValue = parametersJson.get(j);
            Type parameterType = declaredRpcMethodParameterTypes[j];
            parameters[j] = JsonCodec.decodeInternalOrCustomType(parameterType,
                    parameterValue, connectorTracker);
        }
        invocation.setParameters(parameters);
        return invocation;
    }

    protected void changeVariables(Object source, final VariableOwner owner,
            Map<String, Object> m) {
        owner.changeVariables(source, m);
    }

    protected ClientConnector getConnector(UI uI, String connectorId) {
        ClientConnector c = uI.getConnectorTracker().getConnector(connectorId);
        if (c == null
                && connectorId.equals(getDragAndDropService().getConnectorId())) {
            return getDragAndDropService();
        }

        return c;
    }

    private DragAndDropService getDragAndDropService() {
        if (dragAndDropService == null) {
            dragAndDropService = new DragAndDropService(this);
        }
        return dragAndDropService;
    }

    /**
     * Reads the request data from the Request and returns it converted to an
     * UTF-8 string.
     * 
     * @param request
     * @return
     * @throws IOException
     */
    protected String getRequestPayload(VaadinRequest request)
            throws IOException {

        int requestLength = request.getContentLength();
        if (requestLength == 0) {
            return null;
        }

        ByteArrayOutputStream bout = requestLength <= 0 ? new ByteArrayOutputStream()
                : new ByteArrayOutputStream(requestLength);

        InputStream inputStream = request.getInputStream();
        byte[] buffer = new byte[MAX_BUFFER_SIZE];

        while (true) {
            int read = inputStream.read(buffer);
            if (read == -1) {
                break;
            }
            bout.write(buffer, 0, read);
        }
        String result = new String(bout.toByteArray(), "utf-8");

        return result;
    }

    public class ErrorHandlerErrorEvent implements ErrorEvent, Serializable {
        private final Throwable throwable;

        public ErrorHandlerErrorEvent(Throwable throwable) {
            this.throwable = throwable;
        }

        @Override
        public Throwable getThrowable() {
            return throwable;
        }

    }

    /**
     * Handles an error (exception) that occurred when processing variable
     * changes from the client or a failure of a file upload.
     * 
     * For {@link AbstractField} components,
     * {@link AbstractField#handleError(com.vaadin.ui.AbstractComponent.ComponentErrorEvent)}
     * is called. In all other cases (or if the field does not handle the
     * error), {@link ErrorListener#terminalError(ErrorEvent)} for the session
     * error handler is called.
     * 
     * @param session
     * @param owner
     *            component that the error concerns
     * @param e
     *            exception that occurred
     * @param m
     *            map from variable names to values
     */
    private void handleChangeVariablesError(VaadinSession session,
            Component owner, Throwable t, Map<String, Object> m) {
        boolean handled = false;
        ChangeVariablesErrorEvent errorEvent = new ChangeVariablesErrorEvent(
                owner, t, m);

        if (owner instanceof AbstractField) {
            try {
                handled = ((AbstractField<?>) owner).handleError(errorEvent);
            } catch (Exception handlerException) {
                /*
                 * If there is an error in the component error handler we pass
                 * the that error to the session error handler and continue
                 * processing the actual error
                 */
                session.getErrorHandler().terminalError(
                        new ErrorHandlerErrorEvent(handlerException));
                handled = false;
            }
        }

        if (!handled) {
            session.getErrorHandler().terminalError(errorEvent);
        }

    }

    /**
     * Unescape encoded burst separator characters in a burst received from the
     * client. This protects from separator injection attacks.
     * 
     * @param encodedValue
     *            to decode
     * @return decoded value
     */
    protected String unescapeBurst(String encodedValue) {
        final StringBuilder result = new StringBuilder();
        final StringCharacterIterator iterator = new StringCharacterIterator(
                encodedValue);
        char character = iterator.current();
        while (character != CharacterIterator.DONE) {
            if (VAR_ESCAPE_CHARACTER == character) {
                character = iterator.next();
                switch (character) {
                case VAR_ESCAPE_CHARACTER + 0x30:
                    // escaped escape character
                    result.append(VAR_ESCAPE_CHARACTER);
                    break;
                case VAR_BURST_SEPARATOR + 0x30:
                    // +0x30 makes these letters for easier reading
                    result.append((char) (character - 0x30));
                    break;
                case CharacterIterator.DONE:
                    // error
                    throw new RuntimeException(
                            "Communication error: Unexpected end of message");
                default:
                    // other escaped character - probably a client-server
                    // version mismatch
                    throw new RuntimeException(
                            "Invalid escaped character from the client - check that the widgetset and server versions match");
                }
            } else {
                // not a special character - add it to the result as is
                result.append(character);
            }
            character = iterator.next();
        }
        return result.toString();
    }

    /**
     * Prints the queued (pending) locale definitions to a {@link PrintWriter}
     * in a (UIDL) format that can be sent to the client and used there in
     * formatting dates, times etc.
     * 
     * @param outWriter
     */
    private void printLocaleDeclarations(PrintWriter outWriter) {
        /*
         * ----------------------------- Sending Locale sensitive date
         * -----------------------------
         */

        // Send locale informations to client
        outWriter.print(", \"locales\":[");
        for (; pendingLocalesIndex < locales.size(); pendingLocalesIndex++) {

            final Locale l = generateLocale(locales.get(pendingLocalesIndex));
            // Locale name
            outWriter.print("{\"name\":\"" + l.toString() + "\",");

            /*
             * Month names (both short and full)
             */
            final DateFormatSymbols dfs = new DateFormatSymbols(l);
            final String[] short_months = dfs.getShortMonths();
            final String[] months = dfs.getMonths();
            outWriter.print("\"smn\":[\""
                    + // ShortMonthNames
                    short_months[0] + "\",\"" + short_months[1] + "\",\""
                    + short_months[2] + "\",\"" + short_months[3] + "\",\""
                    + short_months[4] + "\",\"" + short_months[5] + "\",\""
                    + short_months[6] + "\",\"" + short_months[7] + "\",\""
                    + short_months[8] + "\",\"" + short_months[9] + "\",\""
                    + short_months[10] + "\",\"" + short_months[11] + "\""
                    + "],");
            outWriter.print("\"mn\":[\""
                    + // MonthNames
                    months[0] + "\",\"" + months[1] + "\",\"" + months[2]
                    + "\",\"" + months[3] + "\",\"" + months[4] + "\",\""
                    + months[5] + "\",\"" + months[6] + "\",\"" + months[7]
                    + "\",\"" + months[8] + "\",\"" + months[9] + "\",\""
                    + months[10] + "\",\"" + months[11] + "\"" + "],");

            /*
             * Weekday names (both short and full)
             */
            final String[] short_days = dfs.getShortWeekdays();
            final String[] days = dfs.getWeekdays();
            outWriter.print("\"sdn\":[\""
                    + // ShortDayNames
                    short_days[1] + "\",\"" + short_days[2] + "\",\""
                    + short_days[3] + "\",\"" + short_days[4] + "\",\""
                    + short_days[5] + "\",\"" + short_days[6] + "\",\""
                    + short_days[7] + "\"" + "],");
            outWriter.print("\"dn\":[\""
                    + // DayNames
                    days[1] + "\",\"" + days[2] + "\",\"" + days[3] + "\",\""
                    + days[4] + "\",\"" + days[5] + "\",\"" + days[6] + "\",\""
                    + days[7] + "\"" + "],");

            /*
             * First day of week (0 = sunday, 1 = monday)
             */
            final Calendar cal = new GregorianCalendar(l);
            outWriter.print("\"fdow\":" + (cal.getFirstDayOfWeek() - 1) + ",");

            /*
             * Date formatting (MM/DD/YYYY etc.)
             */

            DateFormat dateFormat = DateFormat.getDateTimeInstance(
                    DateFormat.SHORT, DateFormat.SHORT, l);
            if (!(dateFormat instanceof SimpleDateFormat)) {
                getLogger().warning(
                        "Unable to get default date pattern for locale "
                                + l.toString());
                dateFormat = new SimpleDateFormat();
            }
            final String df = ((SimpleDateFormat) dateFormat).toPattern();

            int timeStart = df.indexOf("H");
            if (timeStart < 0) {
                timeStart = df.indexOf("h");
            }
            final int ampm_first = df.indexOf("a");
            // E.g. in Korean locale AM/PM is before h:mm
            // TODO should take that into consideration on client-side as well,
            // now always h:mm a
            if (ampm_first > 0 && ampm_first < timeStart) {
                timeStart = ampm_first;
            }
            // Hebrew locale has time before the date
            final boolean timeFirst = timeStart == 0;
            String dateformat;
            if (timeFirst) {
                int dateStart = df.indexOf(' ');
                if (ampm_first > dateStart) {
                    dateStart = df.indexOf(' ', ampm_first);
                }
                dateformat = df.substring(dateStart + 1);
            } else {
                dateformat = df.substring(0, timeStart - 1);
            }

            outWriter.print("\"df\":\"" + dateformat.trim() + "\",");

            /*
             * Time formatting (24 or 12 hour clock and AM/PM suffixes)
             */
            final String timeformat = df.substring(timeStart, df.length());
            /*
             * Doesn't return second or milliseconds.
             * 
             * We use timeformat to determine 12/24-hour clock
             */
            final boolean twelve_hour_clock = timeformat.indexOf("a") > -1;
            // TODO there are other possibilities as well, like 'h' in french
            // (ignore them, too complicated)
            final String hour_min_delimiter = timeformat.indexOf(".") > -1 ? "."
                    : ":";
            // outWriter.print("\"tf\":\"" + timeformat + "\",");
            outWriter.print("\"thc\":" + twelve_hour_clock + ",");
            outWriter.print("\"hmd\":\"" + hour_min_delimiter + "\"");
            if (twelve_hour_clock) {
                final String[] ampm = dfs.getAmPmStrings();
                outWriter.print(",\"ampm\":[\"" + ampm[0] + "\",\"" + ampm[1]
                        + "\"]");
            }
            outWriter.print("}");
            if (pendingLocalesIndex < locales.size() - 1) {
                outWriter.print(",");
            }
        }
        outWriter.print("]"); // Close locales
    }

    protected void closeJsonMessage(PrintWriter outWriter) {
        outWriter.print("}]");
    }

    /**
     * Writes the opening of JSON message to be sent to client.
     * 
     * @param outWriter
     * @param response
     */
    protected void openJsonMessage(PrintWriter outWriter,
            VaadinResponse response) {
        // Sets the response type
        response.setContentType("application/json; charset=UTF-8");
        // some dirt to prevent cross site scripting
        outWriter.print("for(;;);[{");
    }

    /**
     * Returns dirty components which are in given window. Components in an
     * invisible subtrees are omitted.
     * 
     * @param w
     *            UI window for which dirty components is to be fetched
     * @return
     */
    private ArrayList<ClientConnector> getDirtyVisibleConnectors(
            ConnectorTracker connectorTracker) {
        ArrayList<ClientConnector> dirtyConnectors = new ArrayList<ClientConnector>();
        for (ClientConnector c : connectorTracker.getDirtyConnectors()) {
            if (isVisible(c)) {
                dirtyConnectors.add(c);
            }
        }

        return dirtyConnectors;
    }

    /**
     * Queues a locale to be sent to the client (browser) for date and time
     * entry etc. All locale specific information is derived from server-side
     * {@link Locale} instances and sent to the client when needed, eliminating
     * the need to use the {@link Locale} class and all the framework behind it
     * on the client.
     * 
     * @see Locale#toString()
     * 
     * @param value
     */
    public void requireLocale(String value) {
        if (locales == null) {
            locales = new ArrayList<String>();
            locales.add(session.getLocale().toString());
            pendingLocalesIndex = 0;
        }
        if (!locales.contains(value)) {
            locales.add(value);
        }
    }

    /**
     * Constructs a {@link Locale} instance to be sent to the client based on a
     * short locale description string.
     * 
     * @see #requireLocale(String)
     * 
     * @param value
     * @return
     */
    private Locale generateLocale(String value) {
        final String[] temp = value.split("_");
        if (temp.length == 1) {
            return new Locale(temp[0]);
        } else if (temp.length == 2) {
            return new Locale(temp[0], temp[1]);
        } else {
            return new Locale(temp[0], temp[1], temp[2]);
        }
    }

    protected class InvalidUIDLSecurityKeyException extends
            GeneralSecurityException {

        InvalidUIDLSecurityKeyException(String message) {
            super(message);
        }

    }

    private final HashMap<Class<? extends ClientConnector>, Integer> typeToKey = new HashMap<Class<? extends ClientConnector>, Integer>();
    private int nextTypeKey = 0;

    private BootstrapHandler bootstrapHandler;

    String getTagForType(Class<? extends ClientConnector> class1) {
        Integer id = typeToKey.get(class1);
        if (id == null) {
            id = nextTypeKey++;
            typeToKey.put(class1, id);
            getLogger().log(Level.FINE,
                    "Mapping " + class1.getName() + " to " + id);
        }
        return id.toString();
    }

    /**
     * Helper class for terminal to keep track of data that client is expected
     * to know.
     * 
     * TODO make customlayout templates (from theme) to be cached here.
     */
    class ClientCache implements Serializable {

        private final Set<Object> res = new HashSet<Object>();

        /**
         * 
         * @param paintable
         * @return true if the given class was added to cache
         */
        boolean cache(Object object) {
            return res.add(object);
        }

        public void clear() {
            res.clear();
        }

    }

    public String getStreamVariableTargetUrl(ClientConnector owner,
            String name, StreamVariable value) {
        /*
         * We will use the same APP/* URI space as ApplicationResources but
         * prefix url with UPLOAD
         * 
         * eg. APP/UPLOAD/[UIID]/[PID]/[NAME]/[SECKEY]
         * 
         * SECKEY is created on each paint to make URL's unpredictable (to
         * prevent CSRF attacks).
         * 
         * NAME and PID from URI forms a key to fetch StreamVariable when
         * handling post
         */
        String paintableId = owner.getConnectorId();
        int uiId = owner.getUI().getUIId();
        String key = uiId + "/" + paintableId + "/" + name;

        if (pidToNameToStreamVariable == null) {
            pidToNameToStreamVariable = new HashMap<String, Map<String, StreamVariable>>();
        }
        Map<String, StreamVariable> nameToStreamVariable = pidToNameToStreamVariable
                .get(paintableId);
        if (nameToStreamVariable == null) {
            nameToStreamVariable = new HashMap<String, StreamVariable>();
            pidToNameToStreamVariable.put(paintableId, nameToStreamVariable);
        }
        nameToStreamVariable.put(name, value);

        if (streamVariableToSeckey == null) {
            streamVariableToSeckey = new HashMap<StreamVariable, String>();
        }
        String seckey = streamVariableToSeckey.get(value);
        if (seckey == null) {
            seckey = UUID.randomUUID().toString();
            streamVariableToSeckey.put(value, seckey);
        }

        return ApplicationConstants.APP_PROTOCOL_PREFIX
                + ServletPortletHelper.UPLOAD_URL_PREFIX + key + "/" + seckey;

    }

    public void cleanStreamVariable(ClientConnector owner, String name) {
        Map<String, StreamVariable> nameToStreamVar = pidToNameToStreamVariable
                .get(owner.getConnectorId());
        nameToStreamVar.remove(name);
        if (nameToStreamVar.isEmpty()) {
            pidToNameToStreamVariable.remove(owner.getConnectorId());
        }
    }

    /**
     * Gets the bootstrap handler that should be used for generating the pages
     * bootstrapping applications for this communication manager.
     * 
     * @return the bootstrap handler to use
     */
    private BootstrapHandler getBootstrapHandler() {
        if (bootstrapHandler == null) {
            bootstrapHandler = createBootstrapHandler();
        }

        return bootstrapHandler;
    }

    /**
     * @return
     * 
     * @deprecated might be refactored or removed before 7.0.0
     */
    @Deprecated
    protected abstract BootstrapHandler createBootstrapHandler();

    /**
     * Handles a request by passing it to each registered {@link RequestHandler}
     * in turn until one produces a response. This method is used for requests
     * that have not been handled by any specific functionality in the terminal
     * implementation (e.g. {@link VaadinServlet}).
     * <p>
     * The request handlers are invoked in the revere order in which they were
     * added to the session until a response has been produced. This means that
     * the most recently added handler is used first and the first request
     * handler that was added to the session is invoked towards the end unless
     * any previous handler has already produced a response.
     * </p>
     * 
     * @param request
     *            the Vaadin request to get information from
     * @param response
     *            the response to which data can be written
     * @return returns <code>true</code> if a {@link RequestHandler} has
     *         produced a response and <code>false</code> if no response has
     *         been written.
     * @throws IOException
     *             if a handler throws an exception
     * 
     * @see VaadinSession#addRequestHandler(RequestHandler)
     * @see RequestHandler
     * 
     * @since 7.0
     */
    protected boolean handleOtherRequest(VaadinRequest request,
            VaadinResponse response) throws IOException {
        // Use a copy to avoid ConcurrentModificationException
        for (RequestHandler handler : new ArrayList<RequestHandler>(
                session.getRequestHandlers())) {
            if (handler.handleRequest(session, request, response)) {
                return true;
            }
        }
        // If not handled
        return false;
    }

    public void handleBrowserDetailsRequest(VaadinRequest request,
            VaadinResponse response, VaadinSession session) throws IOException {

        session.getLock().lock();

        try {
            assert UI.getCurrent() == null;

            CombinedRequest combinedRequest = new CombinedRequest(request);
            CurrentInstance.set(VaadinRequest.class, combinedRequest);

            response.setContentType("application/json; charset=UTF-8");

            UI uI = getBrowserDetailsUI(combinedRequest);

            JSONObject params = new JSONObject();
            params.put(UIConstants.UI_ID_PARAMETER, uI.getUIId());
            String initialUIDL = getInitialUIDL(combinedRequest, uI);
            params.put("uidl", initialUIDL);

            // NOTE! GateIn requires, for some weird reason, getOutputStream
            // to be used instead of getWriter() (it seems to interpret
            // application/json as a binary content type)
            final OutputStream out = response.getOutputStream();
            final PrintWriter outWriter = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(out, "UTF-8")));

            outWriter.write(params.toString());
            // NOTE GateIn requires the buffers to be flushed to work
            outWriter.flush();
            out.flush();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            session.getLock().unlock();
        }
    }

    private UI getBrowserDetailsUI(VaadinRequest request) {
        VaadinService vaadinService = request.getService();
        VaadinSession session = VaadinSession.getForSession(vaadinService,
                request.getWrappedSession());

        List<UIProvider> uiProviders = session.getUIProviders();

        UIClassSelectionEvent classSelectionEvent = new UIClassSelectionEvent(
                request);

        UIProvider provider = null;
        Class<? extends UI> uiClass = null;
        for (UIProvider p : uiProviders) {
            // Check for existing LegacyWindow
            if (p instanceof LegacyApplicationUIProvider) {
                LegacyApplicationUIProvider legacyProvider = (LegacyApplicationUIProvider) p;

                UI existingUi = legacyProvider
                        .getExistingUI(classSelectionEvent);
                if (existingUi != null) {
                    UI.setCurrent(existingUi);
                    return existingUi;
                }
            }

            uiClass = p.getUIClass(classSelectionEvent);
            if (uiClass != null) {
                provider = p;
                break;
            }
        }

        if (provider == null || uiClass == null) {
            return null;
        }

        // Check for an existing UI based on window.name
        BrowserDetails browserDetails = request.getBrowserDetails();
        boolean hasBrowserDetails = browserDetails != null
                && browserDetails.getUriFragment() != null;

        Map<String, Integer> retainOnRefreshUIs = session
                .getPreserveOnRefreshUIs();
        if (hasBrowserDetails && !retainOnRefreshUIs.isEmpty()) {
            // Check for a known UI

            @SuppressWarnings("null")
            String windowName = browserDetails.getWindowName();
            Integer retainedUIId = retainOnRefreshUIs.get(windowName);

            if (retainedUIId != null) {
                UI retainedUI = session.getUIById(retainedUIId.intValue());
                if (uiClass.isInstance(retainedUI)) {
                    return retainedUI;
                } else {
                    getLogger()
                            .info("Not using retained UI in " + windowName
                                    + " because retained UI was of type "
                                    + retainedUIId.getClass() + " but "
                                    + uiClass + " is expected for the request.");
                }
            }
        }

        // No existing UI found - go on by creating and initializing one

        Integer uiId = Integer.valueOf(session.getNextUIid());

        // Explicit Class.cast to detect if the UIProvider does something
        // unexpected
        UICreateEvent event = new UICreateEvent(request, uiClass, uiId);
        UI ui = uiClass.cast(provider.createInstance(event));

        // Initialize some fields for a newly created UI
        if (ui.getSession() != session) {
            // Session already set for LegacyWindow
            ui.setSession(session);
        }

        // Set thread local here so it is available in init
        UI.setCurrent(ui);

        ui.doInit(request, uiId.intValue());

        session.addUI(ui);

        // Remember if it should be remembered
        if (vaadinService.preserveUIOnRefresh(provider, event)) {
            // Remember this UI
            String windowName = request.getBrowserDetails().getWindowName();
            if (windowName == null) {
                getLogger().warning(
                        "There is no window.name available for UI " + uiClass
                                + " that should be preserved.");
            } else {
                session.getPreserveOnRefreshUIs().put(windowName, uiId);
            }
        }

        return ui;
    }

    /**
     * Generates the initial UIDL message that can e.g. be included in a html
     * page to avoid a separate round trip just for getting the UIDL.
     * 
     * @param request
     *            the request that caused the initialization
     * @param uI
     *            the UI for which the UIDL should be generated
     * @return a string with the initial UIDL message
     * @throws PaintException
     *             if an exception occurs while painting
     * @throws JSONException
     *             if an exception occurs while encoding output
     */
    protected String getInitialUIDL(VaadinRequest request, UI uI)
            throws PaintException, JSONException {
        // TODO maybe unify writeUidlResponse()?
        StringWriter sWriter = new StringWriter();
        PrintWriter pWriter = new PrintWriter(sWriter);
        pWriter.print("{");
        if (isXSRFEnabled(uI.getSession())) {
            pWriter.print(getSecurityKeyUIDL(request));
        }
        writeUidlResponse(request, true, pWriter, uI, false);
        pWriter.print("}");
        String initialUIDL = sWriter.toString();
        getLogger().log(Level.FINE, "Initial UIDL:" + initialUIDL);
        return initialUIDL;
    }

    /**
     * Serve a connector resource from the classpath if the resource has
     * previously been registered by calling
     * {@link #registerResource(String, Class)}. Sending arbitrary files from
     * the classpath is prevented by only accepting resource names that have
     * explicitly been registered. Resources can currently only be registered by
     * including a {@link JavaScript} or {@link StyleSheet} annotation on a
     * Connector class.
     * 
     * @param request
     * @param response
     * 
     * @throws IOException
     */
    public void serveConnectorResource(VaadinRequest request,
            VaadinResponse response) throws IOException {

        String pathInfo = request.getRequestPathInfo();
        // + 2 to also remove beginning and ending slashes
        String resourceName = pathInfo
                .substring(ApplicationConstants.CONNECTOR_RESOURCE_PREFIX
                        .length() + 2);

        final String mimetype = response.getService().getMimeType(resourceName);

        // Security check: avoid accidentally serving from the UI of the
        // classpath instead of relative to the context class
        if (resourceName.startsWith("/")) {
            getLogger().warning(
                    "Connector resource request starting with / rejected: "
                            + resourceName);
            response.sendError(HttpServletResponse.SC_NOT_FOUND, resourceName);
            return;
        }

        // Check that the resource name has been registered
        Class<?> context;
        synchronized (connectorResourceContexts) {
            context = connectorResourceContexts.get(resourceName);
        }

        // Security check: don't serve resource if the name hasn't been
        // registered in the map
        if (context == null) {
            getLogger().warning(
                    "Connector resource request for unknown resource rejected: "
                            + resourceName);
            response.sendError(HttpServletResponse.SC_NOT_FOUND, resourceName);
            return;
        }

        // Resolve file relative to the location of the context class
        InputStream in = context.getResourceAsStream(resourceName);
        if (in == null) {
            getLogger().warning(
                    resourceName + " defined by " + context.getName()
                            + " not found. Verify that the file "
                            + context.getPackage().getName().replace('.', '/')
                            + '/' + resourceName
                            + " is available on the classpath.");
            response.sendError(HttpServletResponse.SC_NOT_FOUND, resourceName);
            return;
        }

        // TODO Check and set cache headers

        OutputStream out = null;
        try {
            if (mimetype != null) {
                response.setContentType(mimetype);
            }

            out = response.getOutputStream();

            final byte[] buffer = new byte[Constants.DEFAULT_BUFFER_SIZE];

            int bytesRead = 0;
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.flush();
        } finally {
            try {
                in.close();
            } catch (Exception e) {
                // Do nothing
            }
            if (out != null) {
                try {
                    out.close();
                } catch (Exception e) {
                    // Do nothing
                }
            }
        }
    }

    /**
     * Handles file upload request submitted via Upload component.
     * 
     * @param UI
     *            The UI for this request
     * 
     * @see #getStreamVariableTargetUrl(ReceiverOwner, String, StreamVariable)
     * 
     * @param request
     * @param response
     * @throws IOException
     * @throws InvalidUIDLSecurityKeyException
     */
    public void handleFileUpload(VaadinSession session, VaadinRequest request,
            VaadinResponse response) throws IOException,
            InvalidUIDLSecurityKeyException {

        /*
         * URI pattern: APP/UPLOAD/[UIID]/[PID]/[NAME]/[SECKEY] See
         * #createReceiverUrl
         */

        String pathInfo = request.getRequestPathInfo();
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
        UI uI = session.getUIById(Integer.parseInt(uiId));
        UI.setCurrent(uI);

        StreamVariable streamVariable = getStreamVariable(connectorId,
                variableName);
        String secKey = streamVariableToSeckey.get(streamVariable);
        if (secKey.equals(parts[3])) {

            ClientConnector source = getConnector(uI, connectorId);
            String contentType = request.getContentType();
            if (contentType.contains("boundary")) {
                // Multipart requests contain boundary string
                doHandleSimpleMultipartFileUpload(request, response,
                        streamVariable, variableName, source,
                        contentType.split("boundary=")[1]);
            } else {
                // if boundary string does not exist, the posted file is from
                // XHR2.post(File)
                doHandleXhrFilePost(request, response, streamVariable,
                        variableName, source, request.getContentLength());
            }
        } else {
            throw new InvalidUIDLSecurityKeyException(
                    "Security key in upload post did not match!");
        }

    }

    /**
     * Handles a heartbeat request. Heartbeat requests are periodically sent by
     * the client-side to inform the server that the UI sending the heartbeat is
     * still alive (the browser window is open, the connection is up) even when
     * there are no UIDL requests for a prolonged period of time. UIs that do
     * not receive either heartbeat or UIDL requests are eventually removed from
     * the session and garbage collected.
     * 
     * @param request
     * @param response
     * @param session
     * @throws IOException
     */
    public void handleHeartbeatRequest(VaadinRequest request,
            VaadinResponse response, VaadinSession session) throws IOException {
        UI ui = null;
        try {
            int uiId = Integer.parseInt(request
                    .getParameter(UIConstants.UI_ID_PARAMETER));
            ui = session.getUIById(uiId);
        } catch (NumberFormatException nfe) {
            // null-check below handles this as well
        }
        if (ui != null) {
            ui.setLastHeartbeatTime(System.currentTimeMillis());
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "UI not found");
        }
    }

    public StreamVariable getStreamVariable(String connectorId,
            String variableName) {
        Map<String, StreamVariable> map = pidToNameToStreamVariable
                .get(connectorId);
        if (map == null) {
            return null;
        }
        StreamVariable streamVariable = map.get(variableName);
        return streamVariable;
    }

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

    private static final Logger getLogger() {
        return Logger.getLogger(AbstractCommunicationManager.class.getName());
    }
}
