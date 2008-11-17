/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.terminal.gwt.server;

import java.io.BufferedWriter;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.GeneralSecurityException;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
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

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.Application.SystemMessages;
import com.itmill.toolkit.external.org.apache.commons.fileupload.FileItemIterator;
import com.itmill.toolkit.external.org.apache.commons.fileupload.FileItemStream;
import com.itmill.toolkit.external.org.apache.commons.fileupload.FileUploadException;
import com.itmill.toolkit.external.org.apache.commons.fileupload.ProgressListener;
import com.itmill.toolkit.external.org.apache.commons.fileupload.servlet.ServletFileUpload;
import com.itmill.toolkit.terminal.PaintException;
import com.itmill.toolkit.terminal.Paintable;
import com.itmill.toolkit.terminal.URIHandler;
import com.itmill.toolkit.terminal.UploadStream;
import com.itmill.toolkit.terminal.VariableOwner;
import com.itmill.toolkit.terminal.Paintable.RepaintRequestEvent;
import com.itmill.toolkit.terminal.Terminal.ErrorEvent;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.ui.AbstractField;
import com.itmill.toolkit.ui.Component;
import com.itmill.toolkit.ui.Upload;
import com.itmill.toolkit.ui.Window;

/**
 * Application manager processes changes and paints for single application
 * instance.
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 5.0
 */
public class CommunicationManager implements Paintable.RepaintRequestListener {

    private static String GET_PARAM_REPAINT_ALL = "repaintAll";

    /* Variable records indexes */
    private static final int VAR_PID = 1;
    private static final int VAR_NAME = 2;
    private static final int VAR_TYPE = 3;
    private static final int VAR_VALUE = 0;

    private static final String VAR_RECORD_SEPARATOR = "\u001e";

    private static final String VAR_FIELD_SEPARATOR = "\u001f";

    public static final String VAR_BURST_SEPARATOR = "\u001d";

    private final HashSet currentlyOpenWindowsInClient = new HashSet();

    private static final int MAX_BUFFER_SIZE = 64 * 1024;

    private final ArrayList dirtyPaintabletSet = new ArrayList();

    private final HashMap paintableIdMap = new HashMap();

    private final HashMap idPaintableMap = new HashMap();

    private int idSequence = 0;

    private final ApplicationServlet applicationServlet;

    private final Application application;

    // Note that this is only accessed from synchronized block and
    // thus should be thread-safe.
    private String closingWindowName = null;

    private List locales;

    private int pendingLocalesIndex;

    private int timeoutInterval = -1;

    public CommunicationManager(Application application,
            ApplicationServlet applicationServlet) {
        this.application = application;
        requireLocale(application.getLocale().toString());
        this.applicationServlet = applicationServlet;
    }

    /**
     * Handles file upload request submitted via Upload component.
     * 
     * @param request
     * @param response
     * @throws IOException
     * @throws FileUploadException
     */
    public void handleFileUpload(HttpServletRequest request,
            HttpServletResponse response) throws IOException,
            FileUploadException {
        // Create a new file upload handler
        final ServletFileUpload upload = new ServletFileUpload();

        final UploadProgressListener pl = new UploadProgressListener();

        upload.setProgressListener(pl);

        // Parse the request
        FileItemIterator iter;

        try {
            iter = upload.getItemIterator(request);
            /*
             * ATM this loop is run only once as we are uploading one file per
             * request.
             */
            while (iter.hasNext()) {
                final FileItemStream item = iter.next();
                final String name = item.getFieldName();
                final String filename = item.getName();
                final String mimeType = item.getContentType();
                final InputStream stream = item.openStream();
                if (item.isFormField()) {
                    // ignored, upload requests contains only files
                } else {
                    final String pid = name.split("_")[0];
                    final Upload uploadComponent = (Upload) idPaintableMap
                            .get(pid);
                    if (uploadComponent.isReadOnly()) {
                        throw new FileUploadException(
                                "Warning: ignored file upload because upload component is set as read-only");
                    }
                    if (uploadComponent == null) {
                        throw new FileUploadException(
                                "Upload component not found");
                    }
                    synchronized (application) {
                        // put upload component into receiving state
                        uploadComponent.startUpload();
                    }
                    final UploadStream upstream = new UploadStream() {

                        public String getContentName() {
                            return filename;
                        }

                        public String getContentType() {
                            return mimeType;
                        }

                        public InputStream getStream() {
                            return stream;
                        }

                        public String getStreamName() {
                            return "stream";
                        }

                    };

                    // tell UploadProgressListener which component is receiving
                    // file
                    pl.setUpload(uploadComponent);

                    uploadComponent.receiveUpload(upstream);
                }
            }
        } catch (final FileUploadException e) {
            throw e;
        }

        // Send short response to acknowledge client that request was done
        response.setContentType("text/html");
        final OutputStream out = response.getOutputStream();
        final PrintWriter outWriter = new PrintWriter(new BufferedWriter(
                new OutputStreamWriter(out, "UTF-8")));
        outWriter.print("<html><body>download handled</body></html>");
        outWriter.flush();
        out.close();
    }

    /**
     * Handles UIDL request
     * 
     * @param request
     * @param response
     * @throws IOException
     * @throws ServletException
     */
    public void handleUidlRequest(HttpServletRequest request,
            HttpServletResponse response, ApplicationServlet applicationServlet)
            throws IOException, ServletException,
            InvalidUIDLSecurityKeyException {

        // repaint requested or session has timed out and new one is created
        boolean repaintAll = (request.getParameter(GET_PARAM_REPAINT_ALL) != null)
                || request.getSession().isNew();

        final OutputStream out = response.getOutputStream();
        final PrintWriter outWriter = new PrintWriter(new BufferedWriter(
                new OutputStreamWriter(out, "UTF-8")));

        // The rest of the process is synchronized with the application
        // in order to guarantee that no parallel variable handling is
        // made
        synchronized (application) {

            // Finds the window within the application
            Window window = null;
            if (application.isRunning()) {
                window = getApplicationWindow(request, application, null);
                // Returns if no window found
                if (window == null) {
                    // This should not happen, no windows exists but
                    // application is still open.
                    System.err
                            .println("Warning, could not get window for application with request URI "
                                    + request.getRequestURI());
                    return;
                }
            } else {
                // application has been closed
                endApplication(request, response, application);
                return;
            }

            // Change all variables based on request parameters
            if (!handleVariables(request, response, application, window)) {

                // var inconsistency; the client is probably out-of-sync
                SystemMessages ci = null;
                try {
                    Method m = application.getClass().getMethod(
                            "getSystemMessages", (Class[]) null);
                    ci = (Application.SystemMessages) m.invoke(null,
                            (Object[]) null);
                } catch (Exception e2) {
                    // FIXME: Handle exception
                    // Not critical, but something is still wrong; print
                    // stacktrace
                    e2.printStackTrace();
                }
                if (ci != null) {
                    String msg = ci.getOutOfSyncMessage();
                    String cap = ci.getOutOfSyncCaption();
                    if (msg != null || cap != null) {
                        applicationServlet.criticalNotification(request,
                                response, cap, msg, ci.getOutOfSyncURL());
                        // will reload page after this
                        return;
                    }
                }
                // No message to show, let's just repaint all.
                repaintAll = true;

            }

            paintAfterVariablechanges(request, response, applicationServlet,
                    repaintAll, outWriter, window);

            // Mark this window to be open on client
            currentlyOpenWindowsInClient.add(window.getName());
            if (closingWindowName != null) {
                currentlyOpenWindowsInClient.remove(closingWindowName);
                closingWindowName = null;
            }
        }

        out.flush();
        out.close();
    }

    private void paintAfterVariablechanges(HttpServletRequest request,
            HttpServletResponse response,
            ApplicationServlet applicationServlet, boolean repaintAll,
            final PrintWriter outWriter, Window window) throws IOException,
            ServletException, PaintException {

        // If repaint is requested, clean all ids in this root window
        if (repaintAll) {
            for (final Iterator it = idPaintableMap.keySet().iterator(); it
                    .hasNext();) {
                final Component c = (Component) idPaintableMap.get(it.next());
                if (isChildOf(window, c)) {
                    it.remove();
                    paintableIdMap.remove(c);
                }
            }
        }

        // Removes application if it has stopped during variable changes
        if (!application.isRunning()) {
            endApplication(request, response, application);
            return;
        }

        // Sets the response type
        response.setContentType("application/json; charset=UTF-8");
        // some dirt to prevent cross site scripting
        outWriter.print("for(;;);[{");

        outWriter.print("\"changes\":[");

        ArrayList<Paintable> paintables = null;

        // If the browser-window has been closed - we do not need to paint it at
        // all
        if (!window.getName().equals(closingWindowName)) {

            // re-get window - may have been changed
            Window newWindow = getApplicationWindow(request, application,
                    window);
            if (newWindow != window) {
                window = newWindow;
                repaintAll = true;
            }

            JsonPaintTarget paintTarget = new JsonPaintTarget(this, outWriter,
                    !repaintAll);

            // Paints components
            if (repaintAll) {
                paintables = new ArrayList<Paintable>();
                paintables.add(window);

                // Reset sent locales
                locales = null;
                requireLocale(application.getLocale().toString());

            } else {
                // remove detached components from paintableIdMap so they
                // can be GC'ed
                for (Iterator it = paintableIdMap.keySet().iterator(); it
                        .hasNext();) {
                    Component p = (Component) it.next();
                    if (p.getApplication() == null) {
                        idPaintableMap.remove(paintableIdMap.get(p));
                        it.remove();
                        dirtyPaintabletSet.remove(p);
                        p.removeListener(this);
                    }
                }
                paintables = getDirtyComponents(window);
            }
            if (paintables != null) {

                // We need to avoid painting children before parent.
                // This is ensured by ordering list by depth in component
                // tree
                Collections.sort(paintables, new Comparator() {
                    public int compare(Object o1, Object o2) {
                        Component c1 = (Component) o1;
                        Component c2 = (Component) o2;
                        int d1 = 0;
                        while (c1.getParent() != null) {
                            d1++;
                            c1 = c1.getParent();
                        }
                        int d2 = 0;
                        while (c2.getParent() != null) {
                            d2++;
                            c2 = c2.getParent();
                        }
                        if (d1 < d2) {
                            return -1;
                        }
                        if (d1 > d2) {
                            return 1;
                        }
                        return 0;
                    }
                });

                for (final Iterator i = paintables.iterator(); i.hasNext();) {
                    final Paintable p = (Paintable) i.next();

                    // TODO CLEAN
                    if (p instanceof Window) {
                        final Window w = (Window) p;
                        if (w.getTerminal() == null) {
                            w.setTerminal(application.getMainWindow()
                                    .getTerminal());
                        }
                    }
                    /*
                     * This does not seem to happen in tk5, but remember this
                     * case: else if (p instanceof Component) { if (((Component)
                     * p).getParent() == null || ((Component)
                     * p).getApplication() == null) { // Component requested
                     * repaint, but is no // longer attached: skip
                     * paintablePainted(p); continue; } }
                     */

                    // TODO we may still get changes that have been
                    // rendered already (changes with only cached flag)
                    if (paintTarget.needsToBePainted(p)) {
                        paintTarget.startTag("change");
                        paintTarget.addAttribute("format", "uidl");
                        final String pid = getPaintableId(p);
                        paintTarget.addAttribute("pid", pid);

                        p.paint(paintTarget);

                        paintTarget.endTag("change");
                    }
                    paintablePainted(p);
                }
            }

            paintTarget.close();
            outWriter.print("]"); // close changes

            outWriter.print(", \"meta\" : {");
            boolean metaOpen = false;

            if (repaintAll) {
                metaOpen = true;
                outWriter.write("\"repaintAll\":true");
            }

            SystemMessages ci = null;
            try {
                Method m = application.getClass().getMethod(
                        "getSystemMessages", null);
                ci = (Application.SystemMessages) m.invoke(null, null);
            } catch (NoSuchMethodException e1) {
                e1.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

            // meta instruction for client to enable auto-forward to
            // sessionExpiredURL after timer expires.
            if (ci != null && ci.getSessionExpiredMessage() == null
                    && ci.getSessionExpiredCaption() == null
                    && ci.isSessionExpiredNotificationEnabled()) {
                int newTimeoutInterval = request.getSession()
                        .getMaxInactiveInterval();
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
            String themeName = window.getTheme();
            if (request.getParameter("theme") != null) {
                themeName = request.getParameter("theme");
            }
            if (themeName == null) {
                themeName = "default";
            }

            // TODO We should only precache the layouts that are not
            // cached already
            int resourceIndex = 0;
            for (final Iterator i = paintTarget.getPreCachedResources()
                    .iterator(); i.hasNext();) {
                final String resource = (String) i.next();
                InputStream is = null;
                try {
                    is = applicationServlet
                            .getServletContext()
                            .getResourceAsStream(
                                    "/"
                                            + ApplicationServlet.THEME_DIRECTORY_PATH
                                            + themeName + "/" + resource);
                } catch (final Exception e) {
                    // FIXME: Handle exception
                    e.printStackTrace();
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
                        System.err.println("Resource transfer failed:  "
                                + request.getRequestURI() + ". ("
                                + e.getMessage() + ")");
                    }
                    outWriter.print("\""
                            + JsonPaintTarget.escapeJSON(layout.toString())
                            + "\"");
                } else {
                    // FIXME: Handle exception
                    System.err.println("CustomLayout " + "/"
                            + ApplicationServlet.THEME_DIRECTORY_PATH
                            + themeName + "/" + resource + " not found!");
                }
            }
            outWriter.print("}");

            printLocaleDeclarations(outWriter);

            outWriter.print("}]");
        }
        outWriter.flush();
        outWriter.close();

    }

    /**
     * If this method returns false, something was submitted that we did not
     * expect; this is probably due to the client being out-of-sync and sending
     * variable changes for non-existing pids
     * 
     * @param request
     * @param application2
     * @return true if successful, false if there was an inconsistency
     * @throws IOException
     */
    private boolean handleVariables(HttpServletRequest request,
            HttpServletResponse response, Application application2,
            Window window) throws IOException, InvalidUIDLSecurityKeyException {
        boolean success = true;

        if (request.getContentLength() > 0) {

            byte[] buffer = new byte[request.getContentLength()];
            ServletInputStream inputStream = request.getInputStream();
            int totalBytesRead = 0;
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer, totalBytesRead,
                    MAX_BUFFER_SIZE)) != -1) {
                totalBytesRead += bytesRead;
            }

            String changes = new String(buffer, "utf-8");

            // Manage bursts one by one
            final String[] bursts = changes.split(VAR_BURST_SEPARATOR);

            // Security: double cookie submission pattern
            boolean nocheck = "true".equals(application2
                    .getProperty("disable-xsrf-protection"));
            if (bursts.length == 1 && "init".equals(bursts[0])) {
                // initial request, no variable changes: send key
                String uuid = (String) request.getSession().getAttribute(
                        ApplicationConnection.UIDL_SECURITY_HEADER);
                if (uuid == null) {
                    uuid = UUID.randomUUID().toString();
                }
                /*
                 * Cookie c = new Cookie(
                 * ApplicationConnection.UIDL_SECURITY_COOKIE_NAME, uuid);
                 * response.addCookie(c);
                 */
                response.setHeader(ApplicationConnection.UIDL_SECURITY_HEADER,
                        uuid);
                request.getSession().setAttribute(
                        ApplicationConnection.UIDL_SECURITY_HEADER, uuid);
                return true;
            } else if (!nocheck) {
                // check the key
                String sessId = (String) request.getSession().getAttribute(
                        ApplicationConnection.UIDL_SECURITY_HEADER);
                if (sessId == null || !sessId.equals(bursts[0])) {
                    throw new InvalidUIDLSecurityKeyException(
                            "Security key mismatch");
                }

            }

            for (int bi = 1; bi < bursts.length; bi++) {

                // extract variables to two dim string array
                final String[] tmp = bursts[bi].split(VAR_RECORD_SEPARATOR);
                final String[][] variableRecords = new String[tmp.length][4];
                for (int i = 0; i < tmp.length; i++) {
                    variableRecords[i] = tmp[i].split(VAR_FIELD_SEPARATOR);
                }

                for (int i = 0; i < variableRecords.length; i++) {
                    String[] variable = variableRecords[i];
                    String[] nextVariable = null;
                    if (i + 1 < variableRecords.length) {
                        nextVariable = variableRecords[i + 1];
                    }
                    final VariableOwner owner = (VariableOwner) idPaintableMap
                            .get(variable[VAR_PID]);
                    if (owner != null && owner.isEnabled()) {
                        Map m;
                        if (nextVariable != null
                                && variable[VAR_PID]
                                        .equals(nextVariable[VAR_PID])) {
                            // we have more than one value changes in row for
                            // one variable owner, collect em in HashMap
                            m = new HashMap();
                            m.put(variable[VAR_NAME], convertVariableValue(
                                    variable[VAR_TYPE].charAt(0),
                                    variable[VAR_VALUE]));
                        } else {
                            // use optimized single value map
                            m = new SingleValueMap(variable[VAR_NAME],
                                    convertVariableValue(variable[VAR_TYPE]
                                            .charAt(0), variable[VAR_VALUE]));
                        }

                        // collect following variable changes for this owner
                        while (nextVariable != null
                                && variable[VAR_PID]
                                        .equals(nextVariable[VAR_PID])) {
                            i++;
                            variable = nextVariable;
                            if (i + 1 < variableRecords.length) {
                                nextVariable = variableRecords[i + 1];
                            } else {
                                nextVariable = null;
                            }
                            m.put(variable[VAR_NAME], convertVariableValue(
                                    variable[VAR_TYPE].charAt(0),
                                    variable[VAR_VALUE]));
                        }
                        try {
                            owner.changeVariables(request, m);

                            // Special-case of closing browser-level windows:
                            // track browser-windows currently open in client
                            if (owner instanceof Window
                                    && ((Window) owner).getParent() == null) {
                                final Boolean close = (Boolean) m.get("close");
                                if (close != null && close.booleanValue()) {
                                    closingWindowName = ((Window) owner)
                                            .getName();
                                }
                            }
                        } catch (Exception e) {
                            handleChangeVariablesError(application2,
                                    (Component) owner, e, m);
                        }
                    } else {

                        // Handle special case where window-close is called
                        // after the window has been removed from the
                        // application or the application has closed
                        if ("close".equals(variable[VAR_NAME])
                                && "true".equals(variable[VAR_VALUE])) {
                            // Silently ignore this
                            continue;
                        }

                        // Ignore variable change
                        String msg = "Warning: Ignoring variable change for ";
                        if (owner != null) {
                            msg += "disabled component " + owner.getClass();
                            String caption = ((Component) owner).getCaption();
                            if (caption != null) {
                                msg += ", caption=" + caption;
                            }
                        } else {
                            msg += "non-existent component, VAR_PID="
                                    + variable[VAR_PID];
                            success = false;
                        }
                        System.err.println(msg);
                        continue;
                    }
                }

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
                    try {
                        paintAfterVariablechanges(request, response,
                                applicationServlet, true, outWriter, window);
                    } catch (ServletException e) {
                        // We will ignore all servlet exceptions
                    }
                }

            }
        }
        return success;
    }

    public class ErrorHandlerErrorEvent implements ErrorEvent {

        private final Throwable throwable;

        public ErrorHandlerErrorEvent(Throwable throwable) {
            this.throwable = throwable;
        }

        public Throwable getThrowable() {
            return throwable;
        }

    }

    private void handleChangeVariablesError(Application application,
            Component owner, Exception e, Map m) {
        boolean handled = false;
        ChangeVariablesErrorEvent errorEvent = new ChangeVariablesErrorEvent(
                owner, e, m);

        if (owner instanceof AbstractField) {
            try {
                handled = ((AbstractField) owner).handleError(errorEvent);
            } catch (Exception handlerException) {
                /*
                 * If there is an error in the component error handler we pass
                 * the that error to the application error handler and continue
                 * processing the actual error
                 */
                application.getErrorHandler().terminalError(
                        new ErrorHandlerErrorEvent(handlerException));
                handled = false;
            }
        }

        if (!handled) {
            application.getErrorHandler().terminalError(errorEvent);
        }

    }

    private Object convertVariableValue(char variableType, String strValue) {
        Object val = null;
        switch (variableType) {
        case 'a':
            val = strValue.split(",");
            break;
        case 's':
            val = strValue;
            break;
        case 'i':
            val = Integer.valueOf(strValue);
            break;
        case 'l':
            val = Long.valueOf(strValue);
            break;
        case 'f':
            val = Float.valueOf(strValue);
            break;
        case 'd':
            val = Double.valueOf(strValue);
            break;
        case 'b':
            val = Boolean.valueOf(strValue);
            break;
        }

        return val;
    }

    private void printLocaleDeclarations(PrintWriter outWriter) {
        /*
         * ----------------------------- Sending Locale sensitive date
         * -----------------------------
         */

        // Store JVM default locale for later restoration
        // (we'll have to change the default locale for a while)
        final Locale jvmDefault = Locale.getDefault();

        // Send locale informations to client
        outWriter.print(", \"locales\":[");
        for (; pendingLocalesIndex < locales.size(); pendingLocalesIndex++) {

            final Locale l = generateLocale((String) locales
                    .get(pendingLocalesIndex));
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
            // Force our locale as JVM default for a while (SimpleDateFormat
            // uses JVM default)
            Locale.setDefault(l);
            final String df = new SimpleDateFormat().toPattern();
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
            final String dateformat = df.substring(0, timeStart - 1);

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

        // Restore JVM default locale
        Locale.setDefault(jvmDefault);
    }

    /**
     * Gets the existing application or create a new one. Get a window within an
     * application based on the requested URI.
     * 
     * @param request
     *            the HTTP Request.
     * @param application
     *            the Application to query for window.
     * @param assumedWindow
     *            if the window has been already resolved once, this parameter
     *            must contain the window.
     * @return Window mathing the given URI or null if not found.
     * @throws ServletException
     *             if an exception has occurred that interferes with the
     *             servlet's normal operation.
     */
    private Window getApplicationWindow(HttpServletRequest request,
            Application application, Window assumedWindow)
            throws ServletException {

        Window window = null;

        // If the client knows which window to use, use it if possible
        String windowClientRequestedName = request.getParameter("windowName");
        if (assumedWindow != null
                && application.getWindows().contains(assumedWindow)) {
            windowClientRequestedName = assumedWindow.getName();
        }
        if (windowClientRequestedName != null) {
            window = application.getWindow(windowClientRequestedName);
            if (window != null) {
                return window;
            }
        }

        // If client does not know what window it wants
        if (window == null) {

            // Get the path from URL
            String path = request.getPathInfo();
            if (applicationServlet.isApplicationRunnerServlet) {
                path = path
                        .substring(1 + applicationServlet.applicationRunnerClassname
                                .length());
            }
            path = path.substring("/UIDL".length());

            // If the path is specified, create name from it
            if (path != null && path.length() > 0 && !path.equals("/")) {
                String windowUrlName = null;
                if (path.charAt(0) == '/') {
                    path = path.substring(1);
                }
                final int index = path.indexOf('/');
                if (index < 0) {
                    windowUrlName = path;
                    path = "";
                } else {
                    windowUrlName = path.substring(0, index);
                    path = path.substring(index + 1);
                }

                window = application.getWindow(windowUrlName);
            }
        }

        // By default, use mainwindow
        if (window == null) {
            window = application.getMainWindow();
        }

        // If the requested window is already open, resolve conflict
        if (currentlyOpenWindowsInClient.contains(window.getName())) {
            String newWindowName = window.getName();
            while (currentlyOpenWindowsInClient.contains(newWindowName)) {
                newWindowName = window.getName() + "_"
                        + ((int) (Math.random() * 100000000));
            }

            window = application.getWindow(newWindowName);

            // If everything else fails, use main window even in case of
            // conflicts
            if (window == null) {
                window = application.getMainWindow();
            }
        }

        return window;
    }

    /**
     * Ends the Application.
     * 
     * @param request
     *            the HTTP request instance.
     * @param response
     *            the HTTP response to write to.
     * @param application
     *            the Application to end.
     * @throws IOException
     *             if the writing failed due to input/output error.
     */
    private void endApplication(HttpServletRequest request,
            HttpServletResponse response, Application application)
            throws IOException {

        String logoutUrl = application.getLogoutURL();
        if (logoutUrl == null) {
            logoutUrl = application.getURL().toString();
        }
        // clients JS app is still running, send a special json file to tell
        // client that application has quit and where to point browser now
        // Set the response type
        response.setContentType("application/json; charset=UTF-8");
        final ServletOutputStream out = response.getOutputStream();
        final PrintWriter outWriter = new PrintWriter(new BufferedWriter(
                new OutputStreamWriter(out, "UTF-8")));
        outWriter.print("for(;;);[{");
        outWriter.print("\"redirect\":{");
        outWriter.write("\"url\":\"" + logoutUrl + "\"}}]");
        outWriter.flush();
        outWriter.close();
        out.flush();
    }

    /**
     * Gets the Paintable Id. If Paintable has debug id set it will be used
     * prefixed with "PID_S". Otherwise a sequenced ID is created.
     * 
     * @param paintable
     * @return the paintable Id.
     */
    public String getPaintableId(Paintable paintable) {

        String id = (String) paintableIdMap.get(paintable);
        if (id == null) {
            // use testing identifier as id if set
            id = paintable.getDebugId();
            if (id == null) {
                id = "PID" + Integer.toString(idSequence++);
            } else {
                id = "PID_S" + id;
            }
            paintableIdMap.put(paintable, id);
            idPaintableMap.put(id, paintable);
        }

        return id;
    }

    public boolean hasPaintableId(Paintable paintable) {
        return paintableIdMap.containsKey(paintable);
    }

    /**
     * @param w
     *            root window for which dirty components is to be fetched
     * @return
     */
    private ArrayList getDirtyComponents(Window w) {
        final ArrayList resultset = new ArrayList(dirtyPaintabletSet);

        // The following algorithm removes any components that would be painted
        // as a direct descendant of other components from the dirty components
        // list. The result is that each component should be painted exactly
        // once and any unmodified components will be painted as "cached=true".

        for (final Iterator i = dirtyPaintabletSet.iterator(); i.hasNext();) {
            final Paintable p = (Paintable) i.next();
            if (p instanceof Component) {
                final Component component = (Component) p;
                if (component.getApplication() == null) {
                    // component is detached after requestRepaint is called
                    resultset.remove(p);
                    i.remove();
                } else {
                    Window componentsRoot = component.getWindow();
                    if (componentsRoot.getParent() != null) {
                        // this is a subwindow
                        componentsRoot = (Window) componentsRoot.getParent();
                    }
                    if (componentsRoot != w) {
                        resultset.remove(p);
                    }
                }
            }
        }

        return resultset;
    }

    /**
     * @see com.itmill.toolkit.terminal.Paintable.RepaintRequestListener#repaintRequested(com.itmill.toolkit.terminal.Paintable.RepaintRequestEvent)
     */
    public void repaintRequested(RepaintRequestEvent event) {
        final Paintable p = event.getPaintable();
        if (!dirtyPaintabletSet.contains(p)) {
            dirtyPaintabletSet.add(p);
        }
    }

    /**
     * 
     * @param p
     */
    private void paintablePainted(Paintable p) {
        dirtyPaintabletSet.remove(p);
        p.requestRepaintRequests();
    }

    private final class SingleValueMap implements Map {
        private final String name;

        private final Object value;

        private SingleValueMap(String name, Object value) {
            this.name = name;
            this.value = value;
        }

        public void clear() {
            throw new UnsupportedOperationException();
        }

        public boolean containsKey(Object key) {
            if (name == null) {
                return key == null;
            }
            return name.equals(key);
        }

        public boolean containsValue(Object v) {
            if (value == null) {
                return v == null;
            }
            return value.equals(v);
        }

        public Set entrySet() {
            final Set s = new HashSet();
            s.add(new Map.Entry() {

                public Object getKey() {
                    return name;
                }

                public Object getValue() {
                    return value;
                }

                public Object setValue(Object value) {
                    throw new UnsupportedOperationException();
                }
            });
            return s;
        }

        public Object get(Object key) {
            if (!name.equals(key)) {
                return null;
            }
            return value;
        }

        public boolean isEmpty() {
            return false;
        }

        public Set keySet() {
            final Set s = new HashSet();
            s.add(name);
            return s;
        }

        public Object put(Object key, Object value) {
            throw new UnsupportedOperationException();
        }

        public void putAll(Map t) {
            throw new UnsupportedOperationException();
        }

        public Object remove(Object key) {
            throw new UnsupportedOperationException();
        }

        public int size() {
            return 1;
        }

        public Collection values() {
            final LinkedList s = new LinkedList();
            s.add(value);
            return s;

        }
    }

    /**
     * Implementation of URIHandler.ErrorEvent interface.
     */
    public class URIHandlerErrorImpl implements URIHandler.ErrorEvent {

        private final URIHandler owner;

        private final Throwable throwable;

        /**
         * 
         * @param owner
         * @param throwable
         */
        private URIHandlerErrorImpl(URIHandler owner, Throwable throwable) {
            this.owner = owner;
            this.throwable = throwable;
        }

        /**
         * @see com.itmill.toolkit.terminal.Terminal.ErrorEvent#getThrowable()
         */
        public Throwable getThrowable() {
            return throwable;
        }

        /**
         * @see com.itmill.toolkit.terminal.URIHandler.ErrorEvent#getURIHandler()
         */
        public URIHandler getURIHandler() {
            return owner;
        }
    }

    public void requireLocale(String value) {
        if (locales == null) {
            locales = new ArrayList();
            locales.add(application.getLocale().toString());
            pendingLocalesIndex = 0;
        }
        if (!locales.contains(value)) {
            locales.add(value);
        }
    }

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

    /*
     * Upload progress listener notifies upload component once when Jakarta
     * FileUpload can determine content length. Used to detect files total size,
     * uploads progress can be tracked inside upload.
     */
    private class UploadProgressListener implements ProgressListener {
        Upload uploadComponent;

        boolean updated = false;

        public void setUpload(Upload u) {
            uploadComponent = u;
        }

        public void update(long bytesRead, long contentLength, int items) {
            if (!updated && uploadComponent != null) {
                uploadComponent.setUploadSize(contentLength);
                updated = true;
            }
        }
    }

    /**
     * Helper method to test if a component contains another
     * 
     * @param parent
     * @param child
     */
    private static boolean isChildOf(Component parent, Component child) {
        Component p = child.getParent();
        while (p != null) {
            if (parent == p) {
                return true;
            }
            p = p.getParent();
        }
        return false;
    }

    private class InvalidUIDLSecurityKeyException extends
            GeneralSecurityException {

        InvalidUIDLSecurityKeyException(String message) {
            super(message);
        }

    }
}
