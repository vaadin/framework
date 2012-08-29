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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.vaadin.Application;
import com.vaadin.server.Terminal.ErrorEvent;
import com.vaadin.service.FileTypeResolver;

/**
 * <code>FileResources</code> are files or directories on local filesystem. The
 * files and directories are served through URI:s to the client terminal and
 * thus must be registered to an URI context before they can be used. The
 * resource is automatically registered to the application when it is created.
 * 
 * @author Vaadin Ltd.
 * @since 3.0
 */
@SuppressWarnings("serial")
public class FileResource implements ApplicationResource {

    /**
     * Default buffer size for this stream resource.
     */
    private int bufferSize = 0;

    /**
     * File where the downloaded content is fetched from.
     */
    private File sourceFile;

    /**
     * Application.
     */
    private final Application application;

    /**
     * Default cache time for this stream resource.
     */
    private long cacheTime = DownloadStream.DEFAULT_CACHETIME;

    /**
     * Creates a new file resource for providing given file for client
     * terminals.
     */
    public FileResource(File sourceFile, Application application) {
        this.application = application;
        setSourceFile(sourceFile);
        application.addResource(this);
    }

    /**
     * Gets the resource as stream.
     * 
     * @see com.vaadin.server.ApplicationResource#getStream()
     */
    @Override
    public DownloadStream getStream() {
        try {
            final DownloadStream ds = new DownloadStream(new FileInputStream(
                    sourceFile), getMIMEType(), getFilename());
            ds.setParameter("Content-Length",
                    String.valueOf(sourceFile.length()));

            ds.setCacheTime(cacheTime);
            return ds;
        } catch (final FileNotFoundException e) {
            // Log the exception using the application error handler
            getApplication().getErrorHandler().terminalError(new ErrorEvent() {

                @Override
                public Throwable getThrowable() {
                    return e;
                }

            });

            return null;
        }
    }

    /**
     * Gets the source file.
     * 
     * @return the source File.
     */
    public File getSourceFile() {
        return sourceFile;
    }

    /**
     * Sets the source file.
     * 
     * @param sourceFile
     *            the source file to set.
     */
    public void setSourceFile(File sourceFile) {
        this.sourceFile = sourceFile;
    }

    /**
     * @see com.vaadin.server.ApplicationResource#getApplication()
     */
    @Override
    public Application getApplication() {
        return application;
    }

    /**
     * @see com.vaadin.server.ApplicationResource#getFilename()
     */
    @Override
    public String getFilename() {
        return sourceFile.getName();
    }

    /**
     * @see com.vaadin.server.Resource#getMIMEType()
     */
    @Override
    public String getMIMEType() {
        return FileTypeResolver.getMIMEType(sourceFile);
    }

    /**
     * Gets the length of cache expiration time. This gives the adapter the
     * possibility cache streams sent to the client. The caching may be made in
     * adapter or at the client if the client supports caching. Default is
     * <code>DownloadStream.DEFAULT_CACHETIME</code>.
     * 
     * @return Cache time in milliseconds.
     */
    @Override
    public long getCacheTime() {
        return cacheTime;
    }

    /**
     * Sets the length of cache expiration time. This gives the adapter the
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

    /* documented in superclass */
    @Override
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

}
