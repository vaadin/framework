/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal;

import java.io.InputStream;

import com.vaadin.Application;
import com.vaadin.service.FileTypeResolver;

/**
 * <code>StreamResource</code> is a resource provided to the client directly by
 * the application. The strean resource is fetched from URI that is most often
 * in the context of the application or window. The resource is automatically
 * registered to window in creation.
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 3.0
 */
@SuppressWarnings("serial")
public class StreamResource implements ApplicationResource {

    /**
     * Source stream the downloaded content is fetched from.
     */
    private StreamSource streamSource = null;

    /**
     * Explicit mime-type.
     */
    private String MIMEType = null;

    /**
     * Filename.
     */
    private String filename;

    /**
     * Application.
     */
    private final Application application;

    /**
     * Default buffer size for this stream resource.
     */
    private int bufferSize = 0;

    /**
     * Default cache time for this stream resource.
     */
    private long cacheTime = DEFAULT_CACHETIME;

    /**
     * Creates a new stream resource for downloading from stream.
     * 
     * @param streamSource
     *            the source Stream.
     * @param filename
     *            the name of the file.
     * @param application
     *            the Application object.
     */
    public StreamResource(StreamSource streamSource, String filename,
            Application application) {

        this.application = application;
        setFilename(filename);
        setStreamSource(streamSource);

        // Register to application
        application.addResource(this);

    }

    /**
     * @see com.vaadin.terminal.Resource#getMIMEType()
     */
    public String getMIMEType() {
        if (MIMEType != null) {
            return MIMEType;
        }
        return FileTypeResolver.getMIMEType(filename);
    }

    /**
     * Sets the mime type of the resource.
     * 
     * @param MIMEType
     *            the MIME type to be set.
     */
    public void setMIMEType(String MIMEType) {
        this.MIMEType = MIMEType;
    }

    /**
     * Returns the source for this <code>StreamResource</code>. StreamSource is
     * queried when the resource is about to be streamed to the client.
     * 
     * @return Source of the StreamResource.
     */
    public StreamSource getStreamSource() {
        return streamSource;
    }

    /**
     * Sets the source for this <code>StreamResource</code>.
     * <code>StreamSource</code> is queried when the resource is about to be
     * streamed to the client.
     * 
     * @param streamSource
     *            the source to set.
     */
    public void setStreamSource(StreamSource streamSource) {
        this.streamSource = streamSource;
    }

    /**
     * Gets the filename.
     * 
     * @return the filename.
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Sets the filename.
     * 
     * @param filename
     *            the filename to set.
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * @see com.vaadin.terminal.ApplicationResource#getApplication()
     */
    public Application getApplication() {
        return application;
    }

    /**
     * @see com.vaadin.terminal.ApplicationResource#getStream()
     */
    public DownloadStream getStream() {
        final StreamSource ss = getStreamSource();
        if (ss == null) {
            return null;
        }
        final DownloadStream ds = new DownloadStream(ss.getStream(),
                getMIMEType(), getFilename());
        ds.setBufferSize(getBufferSize());
        ds.setCacheTime(cacheTime);
        return ds;
    }

    /**
     * Interface implemented by the source of a StreamResource.
     * 
     * @author IT Mill Ltd.
     * @version
     * @VERSION@
     * @since 3.0
     */
    public interface StreamSource {

        /**
         * Returns new input stream that is used for reading the resource.
         */
        public InputStream getStream();
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
