package com.vaadin.terminal.gwt.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.apphosting.api.DeadlineExceededException;
import com.vaadin.service.ApplicationContext;

public class GAEApplicationServlet extends ApplicationServlet {

    private static final long serialVersionUID = 2179597952818898526L;

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

    protected void sendDeadlineExceededNotification(HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        criticalNotification(
                request,
                response,
                "Deadline Exceeded",
                "I'm sorry, but the operation took too long to complete. We'll try reloading to see where we're at, please take note of any unsaved data...",
                "", null);
    }

    protected void sendNotSerializableNotification(HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        criticalNotification(
                request,
                response,
                "NotSerializableException",
                "I'm sorry, but there seems to be a serious problem, please contact the administrator. And please take note of any unsaved data...",
                "", getApplicationUrl(request).toString()
                        + "?restartApplication");
    }

    @Override
    protected void service(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {

        RequestType requestType = getRequestType(request);

        if (requestType == RequestType.STATIC_FILE) {
            // no locking needed, let superclass handle
            super.service(request, response);
            cleanSession(request);
            return;
        }

        if (requestType == RequestType.APPLICATION_RESOURCE) {
            // no locking needed, let superclass handle
            getApplicationContext(request, MemcacheServiceFactory
                    .getMemcacheService());
            super.service(request, response);
            cleanSession(request);
            return;
        }

        final HttpSession session = request
                .getSession(requestCanCreateApplication(request, requestType));
        if (session == null) {
            handleServiceSessionExpired(request, response);
            cleanSession(request);
            return;
        }

        boolean locked = false;
        MemcacheService memcache = null;
        String mutex = MUTEX_BASE + session.getId();
        memcache = MemcacheServiceFactory.getMemcacheService();
        try {
            // try to get lock
            long started = new Date().getTime();
            // non-UIDL requests will try indefinitely
            while (requestType != RequestType.UIDL
                    || new Date().getTime() - started < MAX_UIDL_WAIT_MILLISECONDS) {
                locked = memcache.put(mutex, 1, Expiration.byDeltaSeconds(40),
                        MemcacheService.SetPolicy.ADD_ONLY_IF_NOT_PRESENT);
                if (locked) {
                    break;
                }
                try {
                    Thread.sleep(RETRY_AFTER_MILLISECONDS);
                } catch (InterruptedException e) {
                    System.err
                            .println("Thread.sleep() interrupted while waiting for lock. Trying again.");
                    e.printStackTrace(System.err);
                }
            }

            if (!locked) {
                // Not locked; only UIDL can get trough here unlocked: tell
                // client to retry
                response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
                // Note: currently interpreting Retry-After as ms, not sec
                response
                        .setHeader("Retry-After", "" + RETRY_AFTER_MILLISECONDS);
                return;
            }

            // de-serialize or create application context, store in session
            ApplicationContext ctx = getApplicationContext(request, memcache);

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
                    + (session.getMaxInactiveInterval() * 1000));
            Expiration expires = Expiration.onDate(expire);

            memcache.put(id, bytes, expires);

            DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
            Entity entity = new Entity(AC_BASE, id);
            entity.setProperty(PROPERTY_EXPIRES, expire.getTime());
            entity.setProperty(PROPERTY_DATA, new Blob(bytes));
            ds.put(entity);

        } catch (DeadlineExceededException e) {
            System.err.println("DeadlineExceeded for " + session.getId());
            sendDeadlineExceededNotification(request, response);
        } catch (NotSerializableException e) {
            // TODO this notification is usually not shown - should we redirect
            // in some other way - can we?
            sendNotSerializableNotification(request, response);
            e.printStackTrace(System.err);
        } finally {
            // "Next, please!"
            if (locked) {
                memcache.delete(mutex);
            }
            cleanSession(request);
        }
    }

    protected ApplicationContext getApplicationContext(
            HttpServletRequest request, MemcacheService memcache) {
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
                memcache.put(AC_BASE + session.getId(), serializedAC,
                        Expiration.byDeltaSeconds(session
                                .getMaxInactiveInterval()),
                        MemcacheService.SetPolicy.ADD_ONLY_IF_NOT_PRESENT);
            }
        }
        if (serializedAC != null) {
            ByteArrayInputStream bais = new ByteArrayInputStream(serializedAC);
            ObjectInputStream ois;
            try {
                ois = new ObjectInputStream(bais);
                ApplicationContext applicationContext = (ApplicationContext) ois
                        .readObject();
                session.setAttribute(WebApplicationContext.class.getName(),
                        applicationContext);
            } catch (IOException e) {
                System.err
                        .println("Could not de-serialize ApplicationContext for "
                                + session.getId()
                                + " A new one will be created.");
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                System.err
                        .println("Could not de-serialize ApplicationContext for "
                                + session.getId()
                                + " A new one will be created.");
                e.printStackTrace();
            }
        }
        // will create new context if the above did not
        return WebApplicationContext.getApplicationContext(session);

    }

    /**
     * Removes the ApplicationContext from the session in order to minimize the
     * data serialized to datastore and memcache.
     * 
     * @param request
     */
    private void cleanSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(WebApplicationContext.class.getName());
        }
    }

}
