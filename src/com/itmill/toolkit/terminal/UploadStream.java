/* *************************************************************************
 
 IT Mill Toolkit 

 Development of Browser User Interfaces Made Easy

 Copyright (C) 2000-2006 IT Mill Ltd
 
 *************************************************************************

 This product is distributed under commercial license that can be found
 from the product package on license.pdf. Use of this product might 
 require purchasing a commercial license from IT Mill Ltd. For guidelines 
 on usage, see licensing-guidelines.html

 *************************************************************************
 
 For more information, contact:
 
 IT Mill Ltd                           phone: +358 2 4802 7180
 Ruukinkatu 2-4                        fax:   +358 2 4802 7181
 20540, Turku                          email:  info@itmill.com
 Finland                               company www: www.itmill.com
 
 Primary source for information and releases: www.itmill.com

 ********************************************************************** */

package com.itmill.toolkit.terminal;

import java.io.InputStream;

/**
 * Defines a variable type, that is used for passing uploaded files from
 * terminal. Most often, file upload is implented using the
 * {@link com.itmill.toolkit.ui.Upload Upload} component.
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 3.0
 */
public interface UploadStream {

    /**
     * Gets the name of the stream.
     * 
     * @return the name of the stream.
     */
    public String getStreamName();

    /**
     * Gets the input stream.
     * 
     * @return the Input stream.
     */
    public InputStream getStream();

    /**
     * Gets the input stream content type.
     * 
     * @return the content type of the input stream.
     */
    public String getContentType();

    /**
     * Gets stream content name. Stream content name usually differs from the
     * actual stream name. It is used to identify the content of the stream.
     * 
     * @return the Name of the stream content.
     */
    public String getContentName();
}
