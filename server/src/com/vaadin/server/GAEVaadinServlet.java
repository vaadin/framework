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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions.Builder;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.apphosting.api.DeadlineExceededException;

/**
 * ApplicationServlet to be used when deploying to Google App Engine, in
 * web.xml:
 * 
 * <pre>
 *      &lt;servlet&gt;
 *              &lt;servlet-name&gt;HelloWorld&lt;/servlet-name&gt;
 *              &lt;servlet-class&gt;com.vaadin.server.GAEApplicationServlet&lt;/servlet-class&gt;
 *              &lt;init-param&gt;
 *                      &lt;param-name&gt;application&lt;/param-name&gt;
 *                      &lt;param-value&gt;com.vaadin.demo.HelloWorld&lt;/param-value&gt;
 *              &lt;/init-param&gt;
 *      &lt;/servlet&gt;
 * </pre>
 * 
 * Session support must be enabled in appengine-web.xml:
 * 
 * <pre>
 *      &lt;sessions-enabled&gt;true&lt;/sessions-enabled&gt;
 * </pre>
 * 
 * Appengine datastore cleanup can be invoked by calling one of the applications
 * with an additional path "/CLEAN". This can be set up as a cron-job in
 * cron.xml (see appengine documentation for more information):
 * 
 * <pre>
 * &lt;cronentries&gt;
 *   &lt;cron&gt;
 *     &lt;url&gt;/HelloWorld/CLEAN&lt;/url&gt;
 *     &lt;description&gt;Clean up sessions&lt;/description&gt;
 *     &lt;schedule&gt;every 2 hours&lt;/schedule&gt;
 *   &lt;/cron&gt;
 * &lt;/cronentries&gt;
 * </pre>
 * 
 * It is recommended (but not mandatory) to extract themes and widgetsets and
 * have App Engine server these statically. Extract VAADIN folder (and it's
 * contents) 'next to' the WEB-INF folder, and add the following to
 * appengine-web.xml:
 * 
 * <pre>
 *      &lt;static-files&gt;
 *           &lt;include path=&quot;/VAADIN/**&quot; /&gt;
 *      &lt;/static-files&gt;
 * </pre>
 * 
 * Additional limitations:
 * <ul>
 * <li/>Do not change application state when serving an ApplicationResource.
 * <li/>Avoid changing application state in transaction handlers, unless you're
 * confident you fully understand the synchronization issues in App Engine.
 * <li/>The application remains locked while uploading - no progressbar is
 * possible.
 * </ul>
 */
public class GAEVaadinServlet extends VaadinServlet {

    // memcache mutex is MUTEX_BASE + sessio id
    private static final String MUTEX_BASE = "_vmutex";

    // used identify ApplicationContext in memcache and datastore
    private static final String AC_BASE = "_vac";

    // UIDL requests will attempt to gain access for this long before telling
    // the client to retry
    private static final int MAX_UIDL_WAIT_MILLISECONDS = 5000;

    // Tell client to retry after this delay.
    // Note: currently interpreting Retry-After as ms, not sec
    private static final int RETRY_AFTER_MILLISECONDS = 100;

    // Properties used in the datastore
    private static final String PROPERTY_EXPIRES = "expires";
    private static final String PROPERTY_DATA = "data";

    // path used for cleanup
    private static final String CLEANUP_PATH = "/CLEAN";
    // max entities to clean at once
    private static final int CLEANUP_LIMIT = 200;
    // appengine session kind
    private static final String APPENGINE_SESSION_KIND = "_ah_SESSION";
    // appengine session expires-parameter
    private static final String PROPERTY_APPENGINE_EXPIRES = "_expires";

    // sessions with undefined (-1) expiration are limited to this, but explicit
    // longer timeouts can be used
    private static final int DEFAULT_MAX_INACTIVE_INTERVAL = 24 * 3600;

    protected void sendDeadlineExceededNotification(
            VaadinServletRequest request, VaadinServletResponse response)
            throws IOException {
        criticalNotification(
                request,
                response,
                "Deadline Exceeded",
                "I'm sorry, but the operation took too long to complete. We'll try reloading to see where we're at, please take note of any unsaved data...",
                "", null);
    }

    protected void sendNotSerializableNotification(
            VaadinServletRequest request, VaadinServletResponse response)
            throws IOException {
        criticalNotification(
                request,
                response,
                "NotSerializableException",
                "I'm sorry, but there seems to be a serious problem, please contact the administrator. And please take note of any unsaved data...",
                "", getApplicationUrl(request).toString()
                        + "?restartApplication");
    }

    protected void sendCriticalErrorNotification(VaadinServletRequest request,
            VaadinServletResponse response) throws IOException {
        criticalNotification(
                request,
                response,
                "Critical error",
                "I'm sorry, but there seems to be a serious problem, please contact the administrator. And please take note of any unsaved data...",
                "", getApplicationUrl(request).toString()
                        + "?restartApplication");
    }

    @Override
    protected void service(HttpServletRequest unwrappedRequest,
            HttpServletResponse unwrappedResponse) throws ServletException,
            IOException {
        VaadinServletRequest request = new VaadinServletRequest(
                unwrappedRequest, getService());
        VaadinServletResponse response = new VaadinServletResponse(
                unwrappedResponse, getService());

        if (isCleanupRequest(request)) {
            cleanDatastore();
            return;
        }

        if (isStaticResourceRequest(request)) {
            // no locking needed, let superclass handle
            super.service(request, response);
            cleanSession(request);
            return;
        }

        if (ServletPortletHelper.isAppRequest(request)) {
            // no locking needed, let superclass handle
            getApplicationContext(request,
                    MemcacheServiceFactory.getMemcacheService());
            super.service(request, response);
            cleanSession(request);
            return;
        }

        final HttpSession session = request.getSession(getService()
                .requestCanCreateSession(request));
        if (session == null) {
            try {
                getService().handleSessionExpired(request, response);
            } catch (ServiceException e) {
                throw new ServletException(e);
            }
            cleanSession(request);
            return;
        }

        boolean locked = false;
        MemcacheService memcache = null;
        String mutex = MUTEX_BASE + session.getId();
        memcache = MemcacheServiceFactory.getMemcacheService();
        try {
            // try to get lock
            long started = System.currentTimeMillis();
            while (System.currentTimeMillis() - started < MAX_UIDL_WAIT_MILLISECONDS) {
                locked = memcache.put(mutex, 1, Expiration.byDeltaSeconds(40),
                        MemcacheService.SetPolicy.ADD_ONLY_IF_NOT_PRESENT);
                if (locked || ServletPortletHelper.isUIDLRequest(request)) {
                    /*
                     * Done if we got a lock. Will also avoid retrying if
                     * there's a UIDL request because those are retried from the
                     * client without keeping the server thread stalled.
                     */
                    break;
                }
                try {
                    Thread.sleep(RETRY_AFTER_MILLISECONDS);
                } catch (InterruptedException e) {
                    getLogger().finer(
                            "Thread.sleep() interrupted while waiting for lock. Trying again. "
                                    + e);
                }
            }

            if (!locked) {
                // Not locked; only UIDL can get trough here unlocked: tell
                // client to retry
                response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
                // Note: currently interpreting Retry-After as ms, not sec
                response.setHeader("Retry-After", "" + RETRY_AFTER_MILLISECONDS);
                return;
            }

            // de-serialize or create application context, store in session
            VaadinSession ctx = getApplicationContext(request, memcache);

            super.service(request, response);

            // serialize
            started = new Date().getTime();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(ctx);
            oos.flush();
            byte[] bytes = baos.toByteArray();

            started = new Date().getTime();

            String id = AC_BASE + session.getId();
            Date expire = new Date(started
                    + (getMaxInactiveIntervalSeconds(session) * 1000));
            Expiration expires = Expiration.onDate(expire);

            memcache.put(id, bytes, expires);

            DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
            Entity entity = new Entity(AC_BASE, id);
            entity.setProperty(PROPERTY_EXPIRES, expire.getTime());
            entity.setProperty(PROPERTY_DATA, new Blob(bytes));
            ds.put(entity);

        } catch (DeadlineExceededException e) {
            getLogger().log(Level.WARNING, "DeadlineExceeded for {0}",
                    session.getId());
            sendDeadlineExceededNotification(request, response);
        } catch (NotSerializableException e) {
            getLogger().log(Level.SEVERE, "Not serializable!", e);

            // TODO this notification is usually not shown - should we redirect
            // in some other way - can we?
            sendNotSerializableNotification(request, response);
        } catch (Exception e) {
            getLogger().log(Level.WARNING,
                    "An exception occurred while servicing request.", e);

            sendCriticalErrorNotification(request, response);
        } finally {
            // "Next, please!"
            if (locked) {
                memcache.delete(mutex);
            }
            cleanSession(request);
        }
    }

    /**
     * Returns the maximum inactive time for a session. This is used for
     * handling the expiration of session related information in caches etc.
     * 
     * @param session
     * @return inactive timeout in seconds, greater than zero
     */
    protected int getMaxInactiveIntervalSeconds(final HttpSession session) {
        int interval = session.getMaxInactiveInterval();
        if (interval <= 0) {
            getLogger()
                    .log(Level.FINE,
                            "Undefined session expiration time, using default value instead.");
            return DEFAULT_MAX_INACTIVE_INTERVAL;
        }
        return interval;
    }

    protected VaadinSession getApplicationContext(HttpServletRequest request,
            MemcacheService memcache) throws ServletException {
        HttpSession session = request.getSession();
        String id = AC_BASE + session.getId();
        byte[] serializedAC = (byte[]) memcache.get(id);
        if (serializedAC == null) {
            DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
            Key key = KeyFactory.createKey(AC_BASE, id);
            Entity entity = null;
            try {
                entity = ds.get(key);
            } catch (EntityNotFoundException e) {
                // Ok, we were a bit optimistic; we'll create a new one later
            }
            if (entity != null) {
                Blob blob = (Blob) entity.getProperty(PROPERTY_DATA);
                serializedAC = blob.getBytes();
                // bring it to memcache
                memcache.put(
                        AC_BASE + session.getId(),
                        serializedAC,
                        Expiration
                                .byDeltaSeconds(getMaxInactiveIntervalSeconds(session)),
                        MemcacheService.SetPolicy.ADD_ONLY_IF_NOT_PRESENT);
            }
        }
        if (serializedAC != null) {
            ByteArrayInputStream bais = new ByteArrayInputStream(serializedAC);
            ObjectInputStream ois;
            try {
                ois = new ObjectInputStream(bais);
                VaadinSession applicationContext = (VaadinSession) ois
                        .readObject();
                applicationContext.storeInSession(getService(),
                        new WrappedHttpSession(session));
            } catch (IOException e) {
                getLogger().log(
                        Level.WARNING,
                        "Could not de-serialize ApplicationContext for "
                                + session.getId()
                                + " A new one will be created. ", e);
            } catch (ClassNotFoundException e) {
                getLogger().log(
                        Level.WARNING,
                        "Could not de-serialize ApplicationContext for "
                                + session.getId()
                                + " A new one will be created. ", e);
            }
        }

        // will create new context if the above did not
        try {
            return getService().findVaadinSession(createVaadinRequest(request));
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private boolean isCleanupRequest(HttpServletRequest request) {
        String path = request.getPathInfo();
        if (path != null && path.equals(CLEANUP_PATH)) {
            return true;
        }
        return false;
    }

    /**
     * Removes the ApplicationContext from the session in order to minimize the
     * data serialized to datastore and memcache.
     * 
     * @param request
     */
    private void cleanSession(VaadinServletRequest request) {
        // Should really be replaced with a session storage API...
        WrappedSession wrappedSession = request.getWrappedSession(false);
        if (wrappedSession == null) {
            return;
        }
        VaadinSession serviceSession = VaadinSession.getForSession(
                getService(), wrappedSession);
        if (serviceSession == null) {
            return;
        }

        /*
         * Inform VaadinSession.valueUnbound that it should not kill the session
         * even though it gets unbound.
         */
        serviceSession.setAttribute(
                VaadinService.PRESERVE_UNBOUND_SESSION_ATTRIBUTE, Boolean.TRUE);
        serviceSession.removeFromSession(getService());

        // Remove preservation marker
        serviceSession.setAttribute(
                VaadinService.PRESERVE_UNBOUND_SESSION_ATTRIBUTE, null);
    }

    /**
     * This will look at the timestamp and delete expired persisted Vaadin and
     * appengine sessions from the datastore.
     * 
     * TODO Possible improvements include: 1. Use transactions (requires entity
     * groups - overkill?) 2. Delete one-at-a-time, catch possible exception,
     * continue w/ next.
     */
    private void cleanDatastore() {
        long expire = new Date().getTime();
        try {
            DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
            // Vaadin stuff first
            {
                Query q = new Query(AC_BASE);
                q.setKeysOnly();

                q.addFilter(PROPERTY_EXPIRES,
                        FilterOperator.LESS_THAN_OR_EQUAL, expire);
                PreparedQuery pq = ds.prepare(q);
                List<Entity> entities = pq.asList(Builder
                        .withLimit(CLEANUP_LIMIT));
                if (entities != null) {
                    getLogger()
                            .log(Level.INFO,
                                    "Vaadin cleanup deleting {0} expired Vaadin sessions.",
                                    entities.size());
                    List<Key> keys = new ArrayList<Key>();
                    for (Entity e : entities) {
                        keys.add(e.getKey());
                    }
                    ds.delete(keys);
                }
            }
            // Also cleanup GAE sessions
            {
                Query q = new Query(APPENGINE_SESSION_KIND);
                q.setKeysOnly();
                q.addFilter(PROPERTY_APPENGINE_EXPIRES,
                        FilterOperator.LESS_THAN_OR_EQUAL, expire);
                PreparedQuery pq = ds.prepare(q);
                List<Entity> entities = pq.asList(Builder
                        .withLimit(CLEANUP_LIMIT));
                if (entities != null) {
                    getLogger()
                            .log(Level.INFO,
                                    "Vaadin cleanup deleting {0} expired appengine sessions.",
                                    entities.size());
                    List<Key> keys = new ArrayList<Key>();
                    for (Entity e : entities) {
                        keys.add(e.getKey());
                    }
                    ds.delete(keys);
                }
            }
        } catch (Exception e) {
            getLogger().log(Level.WARNING, "Exception while cleaning.", e);
        }
    }

    private static final Logger getLogger() {
        return Logger.getLogger(GAEVaadinServlet.class.getName());
    }
}
