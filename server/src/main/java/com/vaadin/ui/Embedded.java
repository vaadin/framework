/*
 * Copyright 2000-2018 Vaadin Ltd.
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

import java.util.Iterator;

import com.vaadin.event.MouseEvents.ClickEvent;
import com.vaadin.event.MouseEvents.ClickListener;
import com.vaadin.server.Resource;
import com.vaadin.shared.EventId;
import com.vaadin.shared.Registration;
import com.vaadin.shared.ui.embedded.EmbeddedServerRpc;
import com.vaadin.shared.ui.embedded.EmbeddedState;

/**
 * A component for embedding external objects.
 * <p>
 * The {@code Embedded} component is used to display various types of multimedia
 * content using the HTML {@code <object>} element. This includes PDF documents,
 * Java applets, and QuickTime videos. Installing a browser plug-in is usually
 * required to actually view the embedded content.
 * <p>
 * Note that before Vaadin 7, {@code Embedded} was also used to display images,
 * Adobe Flash objects, and embedded web pages. This use of the component is
 * deprecated in Vaadin 7; the {@link Image}, {@link Flash}, and
 * {@link BrowserFrame} components should be used instead, respectively.
 *
 * @see Video
 * @see Audio
 *
 * @author Vaadin Ltd.
 * @since 3.0
 */
@SuppressWarnings("serial")
public class Embedded extends AbstractComponent {

    /**
     * General object type.
     */
    public static final int TYPE_OBJECT = 0;

    /**
     * Image types.
     *
     * @deprecated As of 7.0, use the {@link Image} component instead.
     */
    @Deprecated
    public static final int TYPE_IMAGE = 1;

    /**
     * Browser ("iframe") type.
     *
     * @deprecated As of 7.0, use the {@link BrowserFrame} component instead.
     */
    @Deprecated
    public static final int TYPE_BROWSER = 2;

    private EmbeddedServerRpc rpc = mouseDetails -> fireEvent(
            new ClickEvent(Embedded.this, mouseDetails));

    /**
     * Creates a new empty Embedded object.
     */
    public Embedded() {
        registerRpc(rpc);
    }

    /**
     * Creates a new empty Embedded object with caption.
     *
     * @param caption
     */
    public Embedded(String caption) {
        this();
        setCaption(caption);
    }

    /**
     * Creates a new Embedded object whose contents is loaded from given
     * resource. The dimensions are assumed if possible. The type is guessed
     * from resource.
     *
     * @param caption
     * @param source
     *            the Source of the embedded object.
     */
    public Embedded(String caption, Resource source) {
        this(caption);
        setSource(source);
    }

    /**
     * Sets this component's "alt-text", that is, an alternate text that can be
     * presented instead of this component's normal content, for accessibility
     * purposes. Does not work when {@link #setType(int)} has been called with
     * {@link #TYPE_BROWSER}.
     *
     * @param altText
     *            A short, human-readable description of this component's
     *            content.
     * @since 6.8
     */
    public void setAlternateText(String altText) {
        String oldAltText = getAlternateText();
        if (altText != oldAltText
                || (altText != null && !altText.equals(oldAltText))) {
            getState().altText = altText;
        }
    }

    /**
     * Gets this component's "alt-text".
     *
     * @see #setAlternateText(String)
     */
    public String getAlternateText() {
        return getState(false).altText;
    }

    /**
     * Sets an object parameter. Parameters are optional information, and they
     * are passed to the instantiated object. Parameters are are stored as name
     * value pairs. This overrides the previous value assigned to this
     * parameter.
     *
     * @param name
     *            the name of the parameter.
     * @param value
     *            the value of the parameter.
     */
    public void setParameter(String name, String value) {
        getState().parameters.put(name, value);
    }

    /**
     * Gets the value of an object parameter. Parameters are optional
     * information, and they are passed to the instantiated object. Parameters
     * are are stored as name value pairs.
     *
     * @return the Value of parameter or null if not found.
     */
    public String getParameter(String name) {
        return getState(false).parameters.get(name);
    }

    /**
     * Removes an object parameter from the list.
     *
     * @param name
     *            the name of the parameter to remove.
     */
    public void removeParameter(String name) {
        getState().parameters.remove(name);
    }

    /**
     * Gets the embedded object parameter names.
     *
     * @return the Iterator of parameters names.
     */
    public Iterator<String> getParameterNames() {
        return getState(false).parameters.keySet().iterator();
    }

    /**
     * This attribute specifies the base path used to resolve relative URIs
     * specified by the classid, data, and archive attributes. When absent, its
     * default value is the base URI of the current document.
     *
     * @return the code base.
     */
    public String getCodebase() {
        return getState(false).codebase;
    }

    /**
     * Gets the MIME-Type of the code.
     *
     * @return the MIME-Type of the code.
     */
    public String getCodetype() {
        return getState(false).codetype;
    }

    /**
     * Gets the MIME-Type of the object.
     *
     * @return the MIME-Type of the object.
     */
    public String getMimeType() {
        return getState(false).mimeType;
    }

    /**
     * This attribute specifies a message that a user agent may render while
     * loading the object's implementation and data.
     *
     * @return The text displayed when loading
     */
    public String getStandby() {
        return getState(false).standby;
    }

    /**
     * This attribute specifies the base path used to resolve relative URIs
     * specified by the classid, data, and archive attributes. When absent, its
     * default value is the base URI of the current document.
     *
     * @param codebase
     *            The base path
     */
    public void setCodebase(String codebase) {
        String oldCodebase = getCodebase();
        if (codebase != oldCodebase
                || (codebase != null && !codebase.equals(oldCodebase))) {
            getState().codebase = codebase;
        }
    }

    /**
     * This attribute specifies the content type of data expected when
     * downloading the object specified by classid. This attribute is optional
     * but recommended when classid is specified since it allows the user agent
     * to avoid loading information for unsupported content types. When absent,
     * it defaults to the value of the type attribute.
     *
     * @param codetype
     *            the codetype to set.
     */
    public void setCodetype(String codetype) {
        String oldCodetype = getCodetype();
        if (codetype != oldCodetype
                || (codetype != null && !codetype.equals(oldCodetype))) {
            getState().codetype = codetype;
        }
    }

    /**
     * Sets the mimeType, the MIME-Type of the object.
     *
     * @param mimeType
     *            the mimeType to set.
     */
    public void setMimeType(String mimeType) {
        String oldMimeType = getMimeType();
        if (mimeType != oldMimeType
                || (mimeType != null && !mimeType.equals(oldMimeType))) {
            getState().mimeType = mimeType;
            if ("application/x-shockwave-flash".equals(mimeType)) {
                /*
                 * Automatically add wmode transparent as we use lots of
                 * floating layers in Vaadin. If developers need better flash
                 * performance, they can override this value programmatically
                 * back to "window" (the default).
                 */
                if (getParameter("wmode") == null) {
                    setParameter("wmode", "transparent");
                }
            }
        }
    }

    /**
     * This attribute specifies a message that a user agent may render while
     * loading the object's implementation and data.
     *
     * @param standby
     *            The text to display while loading
     */
    public void setStandby(String standby) {
        String oldStandby = getStandby();
        if (standby != oldStandby
                || (standby != null && !standby.equals(oldStandby))) {
            getState().standby = standby;
        }
    }

    /**
     * This attribute may be used to specify the location of an object's
     * implementation via a URI.
     *
     * @return the classid.
     */
    public String getClassId() {
        return getState(false).classId;
    }

    /**
     * This attribute may be used to specify the location of an object's
     * implementation via a URI.
     *
     * @param classId
     *            the classId to set.
     */
    public void setClassId(String classId) {
        String oldClassId = getClassId();
        if (classId != oldClassId
                || (classId != null && !classId.equals(oldClassId))) {
            getState().classId = classId;
        }
    }

    /**
     * Gets the resource contained in the embedded object.
     *
     * @return the Resource
     */
    public Resource getSource() {
        return getResource("src");
    }

    /**
     * Gets the type of the embedded object.
     * <p>
     * This can be one of the following:
     * <ul>
     * <li>TYPE_OBJECT <i>(This is the default)</i>
     * <li>TYPE_IMAGE
     * </ul>
     * </p>
     *
     * @return the type.
     */
    public int getType() {
        return getState(false).type;
    }

    /**
     * Sets the object source resource. The dimensions are assumed if possible.
     * The type is guessed from resource.
     *
     * @param source
     *            the source to set.
     */
    public void setSource(Resource source) {
        if (source != null && !source.equals(getSource())) {
            setResource("src", source);
            final String mt = source.getMIMEType();

            if (getMimeType() == null) {
                getState().mimeType = mt;
            }

            if (mt.equals("image/svg+xml")) {
                getState().type = TYPE_OBJECT;
            } else if ((mt.substring(0, mt.indexOf('/'))
                    .equalsIgnoreCase("image"))) {
                getState().type = TYPE_IMAGE;
            } else {
                // Keep previous type
            }
        }
    }

    /**
     * Sets the object type.
     * <p>
     * This can be one of the following:
     * <ul>
     * <li>{@link #TYPE_OBJECT} <i>(This is the default)</i>
     * <li>{@link #TYPE_IMAGE} <i>(Deprecated)</i>
     * <li>{@link #TYPE_BROWSER} <i>(Deprecated)</i>
     * </ul>
     * </p>
     *
     * @param type
     *            the type to set.
     */
    public void setType(int type) {
        if (type != TYPE_OBJECT && type != TYPE_IMAGE && type != TYPE_BROWSER) {
            throw new IllegalArgumentException("Unsupported type");
        }
        if (type != getType()) {
            getState().type = type;
        }
    }

    /**
     * This attribute may be used to specify a space-separated list of URIs for
     * archives containing resources relevant to the object, which may include
     * the resources specified by the classid and data attributes. Preloading
     * archives will generally result in reduced load times for objects.
     * Archives specified as relative URIs should be interpreted relative to the
     * codebase attribute.
     *
     * @return Space-separated list of URIs with resources relevant to the
     *         object
     */
    public String getArchive() {
        return getState(false).archive;
    }

    /**
     * This attribute may be used to specify a space-separated list of URIs for
     * archives containing resources relevant to the object, which may include
     * the resources specified by the classid and data attributes. Preloading
     * archives will generally result in reduced load times for objects.
     * Archives specified as relative URIs should be interpreted relative to the
     * codebase attribute.
     *
     * @param archive
     *            Space-separated list of URIs with resources relevant to the
     *            object
     */
    public void setArchive(String archive) {
        String oldArchive = getArchive();
        if (archive != oldArchive
                || (archive != null && !archive.equals(oldArchive))) {
            getState().archive = archive;
        }
    }

    /**
     * Add a click listener to the component. The listener is called whenever
     * the user clicks inside the component. Depending on the content the event
     * may be blocked and in that case no event is fired.
     *
     * @see Registration
     *
     * @param listener
     *            The listener to add
     * @return a registration object for removing the listener
     * @since 8.0
     */
    public Registration addClickListener(ClickListener listener) {
        return addListener(EventId.CLICK_EVENT_IDENTIFIER, ClickEvent.class,
                listener, ClickListener.clickMethod);
    }

    /**
     * Remove a click listener from the component. The listener should earlier
     * have been added using {@link #addClickListener(ClickListener)}.
     *
     * @param listener
     *            The listener to remove
     *
     * @deprecated As of 8.0, replaced by {@link Registration#remove()} in the
     *             registration object returned from
     *             {@link #addClickListener(ClickListener)}.
     */
    @Deprecated
    public void removeClickListener(ClickListener listener) {
        removeListener(EventId.CLICK_EVENT_IDENTIFIER, ClickEvent.class,
                listener);
    }

    @Override
    protected EmbeddedState getState() {
        return (EmbeddedState) super.getState();
    }

    @Override
    protected EmbeddedState getState(boolean markAsDirty) {
        return (EmbeddedState) super.getState(markAsDirty);
    }
}
