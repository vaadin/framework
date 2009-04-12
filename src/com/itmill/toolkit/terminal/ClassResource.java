/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.terminal;

import java.io.Serializable;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.service.FileTypeResolver;

/**
 * <code>ClassResource</code> is a named resource accessed with the class
 * loader.
 * 
 * This can be used to access resources such as icons, files, etc.
 * 
 * @see java.lang.Class#getResource(java.lang.String)
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 3.0
 */
@SuppressWarnings("serial")
public class ClassResource implements ApplicationResource, Serializable {

    /**
     * Default buffer size for this stream resource.
     */
    private int bufferSize = 0;

    /**
     * Default cache time for this stream resource.
     */
    private long cacheTime = DEFAULT_CACHETIME;

    /**
     * Associated class used for indetifying the source of the resource.
     */
    private final Class associatedClass;

    /**
     * Name of the resource is relative to the associated class.
     */
    private final String resourceName;

    /**
     * Application used for serving the class.
     */
    private final Application application;

    /**
     * Creates a new application resource instance. The resource id is relative
     * to the location of the application class.
     * 
     * @param resourceName
     *            the Unique identifier of the resource within the application.
     * @param application
     *            the application this resource will be added to.
     */
    public ClassResource(String resourceName, Application application) {
        associatedClass = application.getClass();
        this.resourceName = resourceName;
        this.application = application;
        if (resourceName == null) {
            throw new NullPointerException();
        }
        application.addResource(this);
    }

    /**
     * Creates a new application resource instance.
     * 
     * @param associatedClass
     *            the class of the which the resource is associated.
     * @param resourceName
     *            the Unique identifier of the resource within the application.
     * @param application
     *            the application this resource will be added to.
     */
    public ClassResource(Class associatedClass, String resourceName,
            Application application) {
        this.associatedClass = associatedClass;
        this.resourceName = resourceName;
        this.application = application;
        if (resourceName == null || associatedClass == null) {
            throw new NullPointerException();
        }
        application.addResource(this);
    }

    /**
     * Gets the MIME type of this resource.
     * 
     * @see com.itmill.toolkit.terminal.Resource#getMIMEType()
     */
    public String getMIMEType() {
        return FileTypeResolver.getMIMEType(resourceName);
    }

    /**
     * Gets the application of this resource.
     * 
     * @see com.itmill.toolkit.terminal.ApplicationResource#getApplication()
     */
    public Application getApplication() {
        return application;
    }

    /**
     * Gets the virtual filename for this resource.
     * 
     * @return the file name associated to this resource.
     * @see com.itmill.toolkit.terminal.ApplicationResource#getFilename()
     */
    public String getFilename() {
        int index = 0;
        int next = 0;
        while ((next = resourceName.indexOf('/', index)) > 0
                && next + 1 < resourceName.length()) {
            index = next + 1;
        }
        return resourceName.substring(index);
    }

    /**
     * Gets resource as stream.
     * 
     * @see com.itmill.toolkit.terminal.ApplicationResource#getStream()
     */
    public DownloadStream getStream() {
        final DownloadStream ds = new DownloadStream(associatedClass
                .getResourceAsStream(resourceName), getMIMEType(),
                getFilename());
        ds.setBufferSize(getBufferSize());
        ds.setCacheTime(cacheTime);
        return ds;
    }

    /* documented in superclass */
    public int getBufferSize() {
        return bufferSize;
    }

    /**
     * Sets the size of the download buffer used for this resource.
     * 
     * @param bufferSize
     *            the size of the buffer in bytes.
     */
    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    /* documented in superclass */
    public long getCacheTime() {
        return cacheTime;
    }

    /**
     * Sets the length of cache expiration time.
     * 
     * <p>
     * This gives the adapter the possibility cache streams sent to the client.
     * The caching may be made in adapter or at the client if the client
     * supports caching. Zero or negavive value disbales the caching of this
     * stream.
     * </p>
     * 
     * @param cacheTime
     *            the cache time in milliseconds.
     * 
     */
    public void setCacheTime(long cacheTime) {
        this.cacheTime = cacheTime;
    }
}
