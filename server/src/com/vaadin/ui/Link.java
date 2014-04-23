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

package com.vaadin.ui;

import com.vaadin.server.Resource;
import com.vaadin.shared.ui.BorderStyle;
import com.vaadin.shared.ui.link.LinkConstants;
import com.vaadin.shared.ui.link.LinkState;

/**
 * Link is used to create external or internal URL links.
 * 
 * @author Vaadin Ltd.
 * @since 3.0
 */
@SuppressWarnings("serial")
public class Link extends AbstractComponent {

    /**
     * @deprecated As of 7.0, use {@link BorderStyle#NONE} instead
     */
    @Deprecated
    public static final BorderStyle TARGET_BORDER_NONE = BorderStyle.NONE;

    /**
     * @deprecated As of 7.0, use {@link BorderStyle#MINIMAL} instead
     */
    @Deprecated
    public static final BorderStyle TARGET_BORDER_MINIMAL = BorderStyle.MINIMAL;

    /**
     * @deprecated As of 7.0, use {@link BorderStyle#DEFAULT} instead
     */
    @Deprecated
    public static final BorderStyle TARGET_BORDER_DEFAULT = BorderStyle.DEFAULT;

    /**
     * Creates a new link.
     */
    public Link() {

    }

    /**
     * Creates a new instance of Link.
     * 
     * @param caption
     * @param resource
     */
    public Link(String caption, Resource resource) {
        setCaption(caption);
        setResource(resource);
    }

    /**
     * Creates a new instance of Link that opens a new window.
     * 
     * 
     * @param caption
     *            the Link text.
     * @param targetName
     *            the name of the target window where the link opens to. Empty
     *            name of null implies that the target is opened to the window
     *            containing the link.
     * @param width
     *            the Width of the target window.
     * @param height
     *            the Height of the target window.
     * @param border
     *            the Border style of the target window.
     * 
     */
    public Link(String caption, Resource resource, String targetName,
            int width, int height, BorderStyle border) {
        setCaption(caption);
        setResource(resource);
        setTargetName(targetName);
        setTargetWidth(width);
        setTargetHeight(height);
        setTargetBorder(border);
    }

    @Override
    protected LinkState getState() {
        return (LinkState) super.getState();
    }

    @Override
    protected LinkState getState(boolean markAsDirty) {
        return (LinkState) super.getState(markAsDirty);
    }

    /**
     * Returns the target window border.
     * 
     * @return the target window border.
     */
    public BorderStyle getTargetBorder() {
        return getState(false).targetBorder;
    }

    /**
     * Returns the target window height or -1 if not set.
     * 
     * @return the target window height.
     */
    public int getTargetHeight() {
        return getState(false).targetHeight < 0 ? -1
                : getState(false).targetHeight;
    }

    /**
     * Returns the target window name. Empty name of null implies that the
     * target is opened to the window containing the link.
     * 
     * @return the target window name.
     */
    public String getTargetName() {
        return getState(false).target;
    }

    /**
     * Returns the target window width or -1 if not set.
     * 
     * @return the target window width.
     */
    public int getTargetWidth() {
        return getState(false).targetWidth < 0 ? -1
                : getState(false).targetWidth;
    }

    /**
     * Sets the border of the target window.
     * 
     * @param targetBorder
     *            the targetBorder to set.
     */
    public void setTargetBorder(BorderStyle targetBorder) {
        getState().targetBorder = targetBorder;
    }

    /**
     * Sets the target window height.
     * 
     * @param targetHeight
     *            the targetHeight to set.
     */
    public void setTargetHeight(int targetHeight) {
        getState().targetHeight = targetHeight;
    }

    /**
     * Sets the target window name.
     * 
     * @param targetName
     *            the targetName to set.
     */
    public void setTargetName(String targetName) {
        getState().target = targetName;
    }

    /**
     * Sets the target window width.
     * 
     * @param targetWidth
     *            the targetWidth to set.
     */
    public void setTargetWidth(int targetWidth) {
        getState().targetWidth = targetWidth;
    }

    /**
     * Returns the resource this link opens.
     * 
     * @return the Resource.
     */
    public Resource getResource() {
        return getResource(LinkConstants.HREF_RESOURCE);
    }

    /**
     * Sets the resource this link opens.
     * 
     * @param resource
     *            the resource to set.
     */
    public void setResource(Resource resource) {
        setResource(LinkConstants.HREF_RESOURCE, resource);
    }

}
