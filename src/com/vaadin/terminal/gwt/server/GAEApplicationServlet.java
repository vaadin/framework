package com.vaadin.terminal.gwt.server;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.apphosting.api.DeadlineExceededException;
import com.vaadin.ui.Window;

public class GAEApplicationServlet extends ApplicationServlet {

    private static final long serialVersionUID = 2179597952818898526L;

    private static final String MUTEX_BASE = "vaadin.gae.mutex.";
    // Note: currently interpreting Retry-After as ms, not sec
    private static final int RETRY_AFTER_MILLISECONDS = 100;
    private static final int KEEP_MUTEX_MILLISECONDS = 100;

    @Override
    protected void service(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {

        boolean locked = false;
        MemcacheService memcache = null;
        String mutex = null;
        try {
            RequestType requestType = getRequestType(request);
            if (requestType == RequestType.UIDL) {
                memcache = MemcacheServiceFactory.getMemcacheService();
                mutex = MUTEX_BASE + request.getSession().getId();
                // try to get lock
                locked = memcache.put(mutex, 1, Expiration.byDeltaSeconds(40),
                        MemcacheService.SetPolicy.ADD_ONLY_IF_NOT_PRESENT);
                if (!locked) {
                    // could not obtain lock, tell client to retry
                    request.setAttribute("noSerialize", new Object());
                    response
                            .setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
                    // Note: currently interpreting Retry-After as ms, not sec
                    response.setHeader("Retry-After", ""
                            + RETRY_AFTER_MILLISECONDS);
                    return;
                }

            }

            super.service(request, response);

            if (request.getAttribute("noSerialize") == null) {
                // Explicitly touch session so it is re-serialized.
                HttpSession session = request.getSession(false);
                if (session != null) {
                    session
                            .setAttribute("sessionUpdated", new Date()
                                    .getTime());
                }
            }

        } catch (DeadlineExceededException e) {
            System.err.println("DeadlineExceeded for "
                    + request.getSession().getId());
            // TODO i18n?
            criticalNotification(
                    request,
                    response,
                    "Deadline Exceeded",
                    "I'm sorry, but the operation took too long to complete. We'll try reloading to see where we're at, please take note of any unsaved data...",
                    "", null);
        } finally {
            // "Next, please!"
            if (locked) {
                memcache.delete(mutex, KEEP_MUTEX_MILLISECONDS);
            }

        }
    }

    protected boolean handleURI(CommunicationManager applicationManager,
            Window window, HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        if (super.handleURI(applicationManager, window, request, response)) {
            request.setAttribute("noSerialize", new Object());
            return true;
        }
        return false;
    }

}
