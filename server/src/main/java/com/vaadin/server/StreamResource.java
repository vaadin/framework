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

import java.io.InputStream;
import java.io.Serializable;
import java.util.Arrays;

import com.vaadin.shared.util.SharedUtil;
import com.vaadin.util.FileTypeResolver;

/**
 * <code>StreamResource</code> is a resource provided to the client directly by
 * the application.
 * 
 * @author Vaadin Ltd.
 * @since 3.0
 */
@SuppressWarnings("serial")
public class StreamResource implements ConnectorResource {

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
     * Default buffer size for this stream resource.
     */
    private int bufferSize = 0;

    /**
     * Default cache time for this stream resource.
     */
    private long cacheTime = DownloadStream.DEFAULT_CACHETIME;

    /**
     * Creates a new stream resource for downloading from stream.
     * 
     * @param streamSource
     *            the source Stream.
     * @param filename
     *            the name of the file.
     */
    public StreamResource(StreamSource streamSource, String filename) {
        setFilename(filename);
        setStreamSource(streamSource);
    }

    /**
     * @see com.vaadin.server.Resource#getMIMEType()
     */
    @Override
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
    @Override
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

    @Override
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
     * @author Vaadin Ltd.
     * @since 3.0
     */
    public interface StreamSource extends Serializable {

        /**
         * Returns new input stream that is used for reading the resource.
         */
        public InputStream getStream();
    }

    /**
     * Gets the size of the download buffer used for this resource.
     * 
     * <p>
     * If the buffer size is 0, the buffer size is decided by the terminal
     * adapter. The default value is 0.
     * </p>
     * 
     * @return the size of the buffer in bytes.
     */
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

    /**
     * Gets the length of cache expiration time. This gives the adapter the
     * possibility cache streams sent to the client. The caching may be made in
     * adapter or at the client if the client supports caching. Default is
     * <code>DownloadStream.DEFAULT_CACHETIME</code>.
     * 
     * @return Cache time in milliseconds.
     */
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof StreamResource) {
            StreamResource that = (StreamResource) obj;
            return SharedUtil.equals(getStreamSource(), that.getStreamSource())
                    && SharedUtil.equals(MIMEType, that.MIMEType)
                    && SharedUtil.equals(getFilename(), that.getFilename())
                    && getBufferSize() == that.getBufferSize()
                    && getCacheTime() == that.getCacheTime();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] { getStreamSource(), MIMEType,
                getFilename(), getBufferSize(), getCacheTime() });
    }

}
