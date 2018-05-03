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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.StringTokenizer;

import com.vaadin.annotations.Theme;
import com.vaadin.ui.themes.ValoTheme;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;

import com.vaadin.data.HasValue;
import com.vaadin.event.ActionManager;
import com.vaadin.event.ConnectorActionManager;
import com.vaadin.event.ContextClickEvent;
import com.vaadin.event.ContextClickEvent.ContextClickListener;
import com.vaadin.event.ContextClickEvent.ContextClickNotifier;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.AbstractClientConnector;
import com.vaadin.server.ClientConnector;
import com.vaadin.server.ComponentSizeValidator;
import com.vaadin.server.ErrorMessage;
import com.vaadin.server.Extension;
import com.vaadin.server.Resource;
import com.vaadin.server.Responsive;
import com.vaadin.server.SizeWithUnit;
import com.vaadin.server.Sizeable;
import com.vaadin.server.UserError;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.AbstractComponentState;
import com.vaadin.shared.AbstractFieldState;
import com.vaadin.shared.ComponentConstants;
import com.vaadin.shared.ContextClickRpc;
import com.vaadin.shared.EventId;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.Registration;
import com.vaadin.shared.ui.ComponentStateUtil;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.ErrorLevel;
import com.vaadin.shared.util.SharedUtil;
import com.vaadin.ui.declarative.DesignAttributeHandler;
import com.vaadin.ui.declarative.DesignContext;
import com.vaadin.util.ReflectTools;

/**
 * An abstract class that defines default implementation for the
 * {@link Component} interface. Basic UI components that are not derived from an
 * external component can inherit this class to easily qualify as Vaadin
 * components. Most components in Vaadin do just that.
 *
 * @author Vaadin Ltd.
 * @since 3.0
 */
@SuppressWarnings("serial")
public abstract class AbstractComponent extends AbstractClientConnector
        implements Component, ContextClickNotifier {

    /* Private members */

    /**
     * Application specific data object. The component does not use or modify
     * this.
     */
    private Object applicationData;

    /**
     * The internal error message of the component.
     */
    private ErrorMessage componentError;

    /**
     * Locale of this component.
     */
    private Locale locale;

    /**
     * The component should receive focus (if {@link Focusable}) when attached.
     */
    private boolean delayedFocus;

    /* Sizeable fields */

    private float width = SIZE_UNDEFINED;
    private float height = SIZE_UNDEFINED;
    private Unit widthUnit = Unit.PIXELS;
    private Unit heightUnit = Unit.PIXELS;

    /**
     * Keeps track of the Actions added to this component; the actual
     * handling/notifying is delegated, usually to the containing window.
     */
    private ConnectorActionManager actionManager;

    private boolean visible = true;

    private HasComponents parent;

    protected static final String DESIGN_ATTR_PLAIN_TEXT = "plain-text";

    /* Constructor */

    /**
     * Constructs a new Component.
     */
    public AbstractComponent() {
        // ComponentSizeValidator.setCreationLocation(this);
    }

    /* Get/Set component properties */

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.ui.Component#setId(java.lang.String)
     */
    @Override
    public void setId(String id) {
        getState().id = id;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.ui.Component#getId()
     */
    @Override
    public String getId() {
        return getState(false).id;
    }

    /**
     * @deprecated As of 7.0. Use {@link #setId(String)}
     */
    @Deprecated
    public void setDebugId(String id) {
        setId(id);
    }

    /**
     * @deprecated As of 7.0. Use {@link #getId()}
     */
    @Deprecated
    public String getDebugId() {
        return getId();
    }

    /*
     * Gets the component's style. Don't add a JavaDoc comment here, we use the
     * default documentation from implemented interface.
     */
    @Override
    public String getStyleName() {
        String s = "";
        if (ComponentStateUtil.hasStyles(getState(false))) {
            for (final Iterator<String> it = getState(false).styles
                    .iterator(); it.hasNext();) {
                s += it.next();
                if (it.hasNext()) {
                    s += " ";
                }
            }
        }
        return s;
    }

    /*
     * Sets the component's style. Don't add a JavaDoc comment here, we use the
     * default documentation from implemented interface.
     */
    @Override
    public void setStyleName(String style) {
        if (style == null || style.isEmpty()) {
            getState().styles = null;
            return;
        }
        if (getState().styles == null) {
            getState().styles = new ArrayList<>();
        }
        List<String> styles = getState().styles;
        styles.clear();
        StringTokenizer tokenizer = new StringTokenizer(style, " ");
        while (tokenizer.hasMoreTokens()) {
            styles.add(tokenizer.nextToken());
        }
    }

    @Override
    public void setPrimaryStyleName(String style) {
        getState().primaryStyleName = style;
    }

    @Override
    public String getPrimaryStyleName() {
        return getState(false).primaryStyleName;
    }

    @Override
    public void addStyleName(String style) {
        if (style == null || style.isEmpty()) {
            return;
        }
        if (getState().styles != null && getState().styles.contains(style)) {
            return;
        }
        if (style.contains(" ")) {
            // Split space separated style names and add them one by one.
            StringTokenizer tokenizer = new StringTokenizer(style, " ");
            while (tokenizer.hasMoreTokens()) {
                addStyleName(tokenizer.nextToken());
            }
            return;
        }

        if (getState().styles == null) {
            getState().styles = new ArrayList<>();
        }
        List<String> styles = getState().styles;
        styles.add(style);
    }

    @Override
    public void removeStyleName(String style) {
        if (ComponentStateUtil.hasStyles(getState())) {
            StringTokenizer tokenizer = new StringTokenizer(style, " ");
            while (tokenizer.hasMoreTokens()) {
                getState().styles.remove(tokenizer.nextToken());
            }
        }
    }

    /*
     * Get's the component's caption. Don't add a JavaDoc comment here, we use
     * the default documentation from implemented interface.
     */
    @Override
    public String getCaption() {
        return getState(false).caption;
    }

    @Override
    public void setCaption(String caption) {
        getState().caption = caption;
    }

    /**
     * Sets whether the caption is rendered as HTML.
     * <p>
     * If set to true, the captions are rendered in the browser as HTML and the
     * developer is responsible for ensuring no harmful HTML is used. If set to
     * false, the caption is rendered in the browser as plain text.
     * <p>
     * The default is false, i.e. to render that caption as plain text.
     *
     * @param captionAsHtml
     *            true if the captions are rendered as HTML, false if rendered
     *            as plain text
     */
    public void setCaptionAsHtml(boolean captionAsHtml) {
        getState().captionAsHtml = captionAsHtml;
    }

    /**
     * Checks whether captions are rendered as HTML
     * <p>
     * The default is false, i.e. to render that caption as plain text.
     *
     * @return true if the captions are rendered as HTML, false if rendered as
     *         plain text
     */
    public boolean isCaptionAsHtml() {
        return getState(false).captionAsHtml;
    }

    /*
     * Don't add a JavaDoc comment here, we use the default documentation from
     * implemented interface.
     */
    @Override
    public Locale getLocale() {
        if (locale != null) {
            return locale;
        }
        HasComponents parent = getParent();
        if (parent != null) {
            return parent.getLocale();
        }
        final VaadinSession session = getSession();
        if (session != null) {
            return session.getLocale();
        }
        return null;
    }

    /**
     * Sets the locale of this component.
     *
     * <pre>
     * // Component for which the locale is meaningful
     * InlineDateField date = new InlineDateField(&quot;Datum&quot;);
     *
     * // German language specified with ISO 639-1 language
     * // code and ISO 3166-1 alpha-2 country code.
     * date.setLocale(new Locale(&quot;de&quot;, &quot;DE&quot;));
     *
     * date.setResolution(DateField.RESOLUTION_DAY);
     * layout.addComponent(date);
     * </pre>
     *
     *
     * @param locale
     *            the locale to become this component's locale.
     */
    public void setLocale(Locale locale) {
        this.locale = locale;

        if (locale != null && isAttached()) {
            getUI().getLocaleService().addLocale(locale);
        }

        markAsDirty();
    }

    /*
     * Gets the component's icon resource. Don't add a JavaDoc comment here, we
     * use the default documentation from implemented interface.
     */
    @Override
    public Resource getIcon() {
        return getResource(ComponentConstants.ICON_RESOURCE);
    }

    /**
     * Sets the component's icon.
     *
     * @param icon
     *            the icon to be shown with the component's caption.
     */
    @Override
    public void setIcon(Resource icon) {
        setResource(ComponentConstants.ICON_RESOURCE, icon);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.ui.Component#isEnabled()
     */
    @Override
    public boolean isEnabled() {
        return getState(false).enabled;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.ui.Component#setEnabled(boolean)
     */
    @Override
    public void setEnabled(boolean enabled) {
        getState().enabled = enabled;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.client.Connector#isConnectorEnabled()
     */
    @Override
    public boolean isConnectorEnabled() {
        if (!isVisible()) {
            return false;
        } else if (!isEnabled()) {
            return false;
        } else if (!super.isConnectorEnabled()) {
            return false;
        }
        return !(getParent() instanceof SelectiveRenderer)
                || ((SelectiveRenderer) getParent()).isRendered(this);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.ui.Component#isVisible()
     */
    @Override
    public boolean isVisible() {
        return visible;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.ui.Component#setVisible(boolean)
     */
    @Override
    public void setVisible(boolean visible) {
        if (isVisible() == visible) {
            return;
        }

        this.visible = visible;
        if (visible) {
            /*
             * If the visibility state is toggled from invisible to visible it
             * affects all children (the whole hierarchy) in addition to this
             * component.
             */
            markAsDirtyRecursive();
        }
        if (getParent() != null) {
            // Must always repaint the parent (at least the hierarchy) when
            // visibility of a child component changes.
            getParent().markAsDirty();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.ui.Component#getDescription()
     */
    @Override
    public String getDescription() {
        return getState(false).description;
    }

    /**
     * Sets the component's description. See {@link #getDescription()} for more
     * information on what the description is.
     *
     * @see #setDescription(String, ContentMode)
     * @param description
     *            the new description string for the component.
     */
    public void setDescription(String description) {
        setDescription(description, ContentMode.PREFORMATTED);
    }

    /**
     * Sets the component's description using given content {@code mode}. See
     * {@link #getDescription()} for more information on what the description
     * is.
     * <p>
     * If the content {@code mode} is {@literal ContentMode.HTML} the
     * description is displayed as HTML in tooltips or directly in certain
     * components so care should be taken to avoid creating the possibility for
     * HTML injection and possibly XSS vulnerabilities.
     *
     * @param description
     *            the new description string for the component.
     * @param mode
     *            the content mode for the description
     * @since 8.0
     */
    public void setDescription(String description, ContentMode mode) {
        getState().description = description;
        getState().descriptionContentMode = mode;
    }

    /*
     * Gets the component's parent component. Don't add a JavaDoc comment here,
     * we use the default documentation from implemented interface.
     */
    @Override
    public HasComponents getParent() {
        return parent;
    }

    @Override
    public void setParent(HasComponents parent) {
        // If the parent is not changed, don't do anything
        if (parent == null ? this.parent == null : parent.equals(this.parent)) {
            return;
        }

        if (parent != null && this.parent != null) {
            throw new IllegalStateException(
                    getClass().getName() + " already has a parent.");
        }

        ClientConnector oldParent = getParent();

        // Send a detach event if the component is currently attached
        if (isAttached()) {
            detach();
        }

        // Connect to new parent
        this.parent = parent;

        // Send attach event if the component is now attached
        if (isAttached()) {
            attach();
        }

        if (oldParent != null) {
            oldParent.markAsDirty();
        }
    }

    /**
     * Returns the closest ancestor with the given type.
     * <p>
     * To find the Window that contains the component, use {@code Window w =
     * getParent(Window.class);}
     * </p>
     *
     * @param <T>
     *            The type of the ancestor
     * @param parentType
     *            The ancestor class we are looking for
     * @return The first ancestor that can be assigned to the given class. Null
     *         if no ancestor with the correct type could be found.
     */
    public <T extends HasComponents> T findAncestor(Class<T> parentType) {
        HasComponents p = getParent();
        while (p != null) {
            if (parentType.isAssignableFrom(p.getClass())) {
                return parentType.cast(p);
            }
            p = p.getParent();
        }
        return null;
    }

    /**
     * Gets the error message for this component.
     *
     * @return ErrorMessage containing the description of the error state of the
     *         component or null, if the component contains no errors. Extending
     *         classes should override this method if they support other error
     *         message types such as validation errors or buffering errors. The
     *         returned error message contains information about all the errors.
     */
    public ErrorMessage getErrorMessage() {
        return componentError;
    }

    /**
     * Gets the component's error message.
     *
     * @link Terminal.ErrorMessage#ErrorMessage(String, int)
     *
     * @return the component's error message.
     */
    public ErrorMessage getComponentError() {
        return componentError;
    }

    /**
     * Sets the component's error message. The message may contain certain XML
     * tags, for more information see
     *
     * @link Component.ErrorMessage#ErrorMessage(String, int)
     *
     * @param componentError
     *            the new <code>ErrorMessage</code> of the component.
     */
    public void setComponentError(ErrorMessage componentError) {
        this.componentError = componentError;
        fireComponentErrorEvent();
        markAsDirty();
    }

    /*
     * Notify the component that it's attached to a window. Don't add a JavaDoc
     * comment here, we use the default documentation from implemented
     * interface.
     */
    @Override
    public void attach() {
        super.attach();
        if (delayedFocus) {
            focus();
        }
        setActionManagerViewer();
        if (locale != null) {
            getUI().getLocaleService().addLocale(locale);
        }

    }

    /*
     * Detach the component from application. Don't add a JavaDoc comment here,
     * we use the default documentation from implemented interface.
     */
    @Override
    public void detach() {
        super.detach();
        if (actionManager != null) {
            // Remove any existing viewer. UI cast is just to make the
            // compiler happy
            actionManager.setViewer((UI) null);
        }
    }

    /**
     * Sets the focus for this component if the component is {@link Focusable}.
     */
    protected void focus() {
        if (this instanceof Focusable) {
            final VaadinSession session = getSession();
            if (session != null) {
                getUI().setFocusedComponent((Focusable) this);
                delayedFocus = false;
            } else {
                delayedFocus = true;
            }
        }
    }

    /**
     * Build CSS compatible string representation of height.
     *
     * @return CSS height
     */
    private String getCSSHeight() {
        return getHeight() + getHeightUnits().getSymbol();
    }

    /**
     * Build CSS compatible string representation of width.
     *
     * @return CSS width
     */
    private String getCSSWidth() {
        return getWidth() + getWidthUnits().getSymbol();
    }

    /**
     * Returns the shared state bean with information to be sent from the server
     * to the client.
     *
     * Subclasses should override this method and set any relevant fields of the
     * state returned by super.getState().
     *
     * @since 7.0
     *
     * @return updated component shared state
     */
    @Override
    protected AbstractComponentState getState() {
        return (AbstractComponentState) super.getState();
    }

    @Override
    protected AbstractComponentState getState(boolean markAsDirty) {
        return (AbstractComponentState) super.getState(markAsDirty);
    }

    @Override
    public void beforeClientResponse(boolean initial) {
        super.beforeClientResponse(initial);
        // TODO This logic should be on the client side and the state should
        // simply be a data object with "width" and "height".
        if (getHeight() >= 0 && (getHeightUnits() != Unit.PERCENTAGE
                || ComponentSizeValidator.parentCanDefineHeight(this))) {
            getState().height = "" + getCSSHeight();
        } else {
            getState().height = "";
        }

        if (getWidth() >= 0 && (getWidthUnits() != Unit.PERCENTAGE
                || ComponentSizeValidator.parentCanDefineWidth(this))) {
            getState().width = "" + getCSSWidth();
        } else {
            getState().width = "";
        }

        ErrorMessage error = getErrorMessage();
        if (null != error) {
            getState().errorMessage = error.getFormattedHtmlMessage();
            getState().errorLevel = error.getErrorLevel();
        } else {
            getState().errorMessage = null;
            getState().errorLevel = null;
        }
    }

    /* General event framework */

    private static final Method COMPONENT_EVENT_METHOD = ReflectTools
            .findMethod(Component.Listener.class, "componentEvent",
                    Component.Event.class);

    /* Component event framework */

    /*
     * Registers a new listener to listen events generated by this component.
     * Don't add a JavaDoc comment here, we use the default documentation from
     * implemented interface.
     */
    @Override
    public Registration addListener(Component.Listener listener) {
        return addListener(Component.Event.class, listener,
                COMPONENT_EVENT_METHOD);
    }

    @Override
    @Deprecated
    public void removeListener(Component.Listener listener) {
        removeListener(Component.Event.class, listener, COMPONENT_EVENT_METHOD);
    }

    /**
     * Emits the component event. It is transmitted to all registered listeners
     * interested in such events.
     */
    protected void fireComponentEvent() {
        fireEvent(new Component.Event(this));
    }

    /**
     * Emits the component error event. It is transmitted to all registered
     * listeners interested in such events.
     */
    protected void fireComponentErrorEvent() {
        fireEvent(new Component.ErrorEvent(getComponentError(), this));
    }

    /**
     * Sets the data object, that can be used for any application specific data.
     * The component does not use or modify this data.
     *
     * @param data
     *            the Application specific data.
     * @since 3.1
     */
    public void setData(Object data) {
        applicationData = data;
    }

    /**
     * Gets the application specific data. See {@link #setData(Object)}.
     *
     * @return the Application specific data set with setData function.
     * @since 3.1
     */
    public Object getData() {
        return applicationData;
    }

    /* Sizeable and other size related methods */

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.Sizeable#getHeight()
     */
    @Override
    public float getHeight() {
        return height;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.server.Sizeable#getHeightUnits()
     */
    @Override
    public Unit getHeightUnits() {
        return heightUnit;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.server.Sizeable#getWidth()
     */
    @Override
    public float getWidth() {
        return width;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.server.Sizeable#getWidthUnits()
     */
    @Override
    public Unit getWidthUnits() {
        return widthUnit;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.server.Sizeable#setHeight(float, Unit)
     */
    @Override
    public void setHeight(float height, Unit unit) {
        if (unit == null) {
            throw new IllegalArgumentException("Unit can not be null");
        }
        this.height = height;
        heightUnit = unit;
        markAsDirty();
        // ComponentSizeValidator.setHeightLocation(this);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.server.Sizeable#setSizeFull()
     */
    @Override
    public void setSizeFull() {
        setWidth(100, Unit.PERCENTAGE);
        setHeight(100, Unit.PERCENTAGE);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.server.Sizeable#setSizeUndefined()
     */
    @Override
    public void setSizeUndefined() {
        setWidthUndefined();
        setHeightUndefined();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.server.Sizeable#setWidthUndefined()
     */
    @Override
    public void setWidthUndefined() {
        setWidth(-1, Unit.PIXELS);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.server.Sizeable#setHeightUndefined()
     */
    @Override
    public void setHeightUndefined() {
        setHeight(-1, Unit.PIXELS);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.server.Sizeable#setWidth(float, Unit)
     */
    @Override
    public void setWidth(float width, Unit unit) {
        if (unit == null) {
            throw new IllegalArgumentException("Unit can not be null");
        }
        this.width = width;
        widthUnit = unit;
        markAsDirty();
        // ComponentSizeValidator.setWidthLocation(this);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.server.Sizeable#setWidth(java.lang.String)
     */
    @Override
    public void setWidth(String width) {
        SizeWithUnit size = SizeWithUnit.parseStringSize(width);
        if (size != null) {
            setWidth(size.getSize(), size.getUnit());
        } else {
            setWidth(-1, Unit.PIXELS);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.server.Sizeable#setHeight(java.lang.String)
     */
    @Override
    public void setHeight(String height) {
        SizeWithUnit size = SizeWithUnit.parseStringSize(height);
        if (size != null) {
            setHeight(size.getSize(), size.getUnit());
        } else {
            setHeight(-1, Unit.PIXELS);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.ui.Component#readDesign(org.jsoup.nodes.Element,
     * com.vaadin.ui.declarative.DesignContext)
     */
    @Override
    public void readDesign(Element design, DesignContext designContext) {
        Attributes attr = design.attributes();
        // handle default attributes
        for (String attribute : getDefaultAttributes()) {
            if (design.hasAttr(attribute)) {
                DesignAttributeHandler.assignValue(this, attribute,
                        design.attr(attribute));
            }

        }

        // handle locale
        if (attr.hasKey("locale")) {
            setLocale(getLocaleFromString(attr.get("locale")));
        }
        // handle width and height
        readSize(attr);
        // handle component error
        if (attr.hasKey("error")) {
            UserError error = new UserError(attr.get("error"),
                    com.vaadin.server.AbstractErrorMessage.ContentMode.HTML,
                    ErrorLevel.ERROR);
            setComponentError(error);
        }
        // Tab index when applicable
        if (design.hasAttr("tabindex") && this instanceof Focusable) {
            ((Focusable) this).setTabIndex(DesignAttributeHandler.readAttribute(
                    "tabindex", design.attributes(), Integer.class));
        }

        // check for unsupported attributes
        Set<String> supported = new HashSet<>();
        supported.addAll(getDefaultAttributes());
        supported.addAll(getCustomAttributes());
        for (Attribute a : attr) {
            if (!a.getKey().startsWith(":")
                    && !supported.contains(a.getKey())) {
                // Add unknown attributes as custom attributes to the context
                designContext.setCustomAttribute(this, a.getKey(),
                        a.getValue());
            }
        }
    }

    /**
     * Constructs a Locale corresponding to the given string. The string should
     * consist of one, two or three parts with '_' between the different parts
     * if there is more than one part. The first part specifies the language,
     * the second part the country and the third part the variant of the locale.
     *
     * @param localeString
     *            the locale specified as a string
     * @return the Locale object corresponding to localeString
     */
    private Locale getLocaleFromString(String localeString) {
        if (localeString == null) {
            return null;
        }
        String[] parts = localeString.split("_");
        if (parts.length > 3) {
            throw new RuntimeException(
                    "Cannot parse the locale string: " + localeString);
        }
        switch (parts.length) {
        case 1:
            return new Locale(parts[0]);
        case 2:
            return new Locale(parts[0], parts[1]);
        default:
            return new Locale(parts[0], parts[1], parts[2]);
        }
    }

    /**
     * Toggles responsiveness of this component.
     *
     * @since 7.5.0
     * @param responsive
     *            boolean enables responsiveness, false disables
     */
    public void setResponsive(boolean responsive) {
        if (responsive) {
            // make responsive if necessary
            if (!isResponsive()) {
                Responsive.makeResponsive(this);
            }
        } else {
            // remove responsive extensions
            List<Extension> extensions = new ArrayList<>(getExtensions());
            for (Extension e : extensions) {
                if (e instanceof Responsive) {
                    removeExtension(e);
                }
            }
        }
    }

    /**
     * Returns true if the component is responsive.
     *
     * @since 7.5.0
     * @return true if the component is responsive
     */
    public boolean isResponsive() {
        for (Extension e : getExtensions()) {
            if (e instanceof Responsive) {
                return true;
            }
        }
        return false;
    }

    /**
     * Sets the read-only status in the state of this {@code AbstractComponent}.
     * This method should be made public in {@link Component Components} that
     * implement {@link HasValue}.
     *
     * @param readOnly
     *            a boolean value specifying whether the component is put
     *            read-only mode or not
     */
    protected void setReadOnly(boolean readOnly) {
        if (getState(false) instanceof AbstractFieldState) {
            if (readOnly != isReadOnly()) {
                ((AbstractFieldState) getState()).readOnly = readOnly;
            }
        } else {
            throw new IllegalStateException(
                    "This component does not support the read-only mode, since state is of type "
                            + getStateType().getSimpleName()
                            + " and does not inherit "
                            + AbstractFieldState.class.getSimpleName());
        }
    }

    /**
     * Returns the read-only status from the state of this
     * {@code AbstractComponent}. This method should be made public in
     * {@link Component Components} that implement {@link HasValue}.
     *
     * @return {@code true} if state has read-only on; {@code false} if not
     * @see #setReadOnly(boolean)
     */
    protected boolean isReadOnly() {
        if (getState(false) instanceof AbstractFieldState) {
            return ((AbstractFieldState) getState(false)).readOnly;
        }
        throw new IllegalStateException(
                "This component does not support the read-only mode, since state is of type "
                        + getStateType().getSimpleName()
                        + " and does not inherit "
                        + AbstractFieldState.class.getSimpleName());
    }

    /**
     * Reads the size of this component from the given design attributes. If the
     * attributes do not contain relevant size information, defaults is
     * consulted.
     *
     * @param attributes
     *            the design attributes
     */
    private void readSize(Attributes attributes) {
        // read width
        if (attributes.hasKey("width-auto") || attributes.hasKey("size-auto")) {
            this.setWidth(null);
        } else if (attributes.hasKey("width-full")
                || attributes.hasKey("size-full")) {
            this.setWidth("100%");
        } else if (attributes.hasKey("width")) {
            this.setWidth(attributes.get("width"));
        }

        // read height
        if (attributes.hasKey("height-auto")
                || attributes.hasKey("size-auto")) {
            this.setHeight(null);
        } else if (attributes.hasKey("height-full")
                || attributes.hasKey("size-full")) {
            this.setHeight("100%");
        } else if (attributes.hasKey("height")) {
            this.setHeight(attributes.get("height"));
        }
    }

    /**
     * Writes the size related attributes for the component if they differ from
     * the defaults
     *
     * @param attributes
     *            the attribute map where the attribute are written
     * @param defaultInstance
     *            the default instance of the class for fetching the default
     *            values
     */
    private void writeSize(Attributes attributes, Component defaultInstance) {
        if (hasEqualSize(defaultInstance)) {
            // we have default values -> ignore
            return;
        }
        boolean widthFull = getWidth() == 100f
                && getWidthUnits().equals(Sizeable.Unit.PERCENTAGE);
        boolean heightFull = getHeight() == 100f
                && getHeightUnits().equals(Sizeable.Unit.PERCENTAGE);
        boolean widthAuto = getWidth() == -1;
        boolean heightAuto = getHeight() == -1;

        // first try the full shorthands
        if (widthFull && heightFull) {
            attributes.put("size-full", true);
        } else if (widthAuto && heightAuto) {
            attributes.put("size-auto", true);
        } else {
            // handle width
            if (!hasEqualWidth(defaultInstance)) {
                if (widthFull) {
                    attributes.put("width-full", true);
                } else if (widthAuto) {
                    attributes.put("width-auto", true);
                } else {
                    String widthString = DesignAttributeHandler.getFormatter()
                            .format(getWidth()) + getWidthUnits().getSymbol();
                    attributes.put("width", widthString);

                }
            }
            if (!hasEqualHeight(defaultInstance)) {
                // handle height
                if (heightFull) {
                    attributes.put("height-full", true);
                } else if (heightAuto) {
                    attributes.put("height-auto", true);
                } else {
                    String heightString = DesignAttributeHandler.getFormatter()
                            .format(getHeight()) + getHeightUnits().getSymbol();
                    attributes.put("height", heightString);
                }
            }
        }
    }

    /**
     * Test if the given component has equal width with this instance
     *
     * @param component
     *            the component for the width comparison
     * @return true if the widths are equal
     */
    private boolean hasEqualWidth(Component component) {
        return getWidth() == component.getWidth()
                && getWidthUnits().equals(component.getWidthUnits());
    }

    /**
     * Test if the given component has equal height with this instance
     *
     * @param component
     *            the component for the height comparison
     * @return true if the heights are equal
     */
    private boolean hasEqualHeight(Component component) {
        return getHeight() == component.getHeight()
                && getHeightUnits().equals(component.getHeightUnits());
    }

    /**
     * Test if the given components has equal size with this instance
     *
     * @param component
     *            the component for the size comparison
     * @return true if the sizes are equal
     */
    private boolean hasEqualSize(Component component) {
        return hasEqualWidth(component) && hasEqualHeight(component);
    }

    /**
     * Returns a collection of attributes that do not require custom handling
     * when reading or writing design. These are typically attributes of some
     * primitive type. The default implementation searches setters with
     * primitive values
     *
     * @return a collection of attributes that can be read and written using the
     *         default approach.
     */
    private Collection<String> getDefaultAttributes() {
        Collection<String> attributes = DesignAttributeHandler
                .getSupportedAttributes(this.getClass());
        attributes.removeAll(getCustomAttributes());
        return attributes;
    }

    /**
     * Returns a collection of attributes that should not be handled by the
     * basic implementation of the {@link #readDesign(Element, DesignContext)}
     * and {@link #writeDesign(Element, DesignContext)} methods. Typically these
     * are handled in a custom way in the overridden versions of the above
     * methods
     *
     * @since 7.4
     *
     * @return the collection of attributes that are not handled by the basic
     *         implementation
     */
    protected Collection<String> getCustomAttributes() {
        List<String> l = new ArrayList<>(Arrays.asList(CUSTOM_ATTRIBUTES));
        if (this instanceof Focusable) {
            l.add("tab-index");
            l.add("tabindex");
        }
        return l;
    }

    private static final String[] CUSTOM_ATTRIBUTES = { "width", "height",
            "debug-id", "error", "width-auto", "height-auto", "width-full",
            "height-full", "size-auto", "size-full", "immediate", "locale",
            "read-only", "_id" };

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.ui.Component#writeDesign(org.jsoup.nodes.Element,
     * com.vaadin.ui.declarative.DesignContext)
     */
    @Override
    public void writeDesign(Element design, DesignContext designContext) {
        AbstractComponent def = designContext.getDefaultInstance(this);
        Attributes attr = design.attributes();
        // handle default attributes
        for (String attribute : getDefaultAttributes()) {
            DesignAttributeHandler.writeAttribute(this, attribute, attr, def,
                    designContext);
        }
        // handle locale
        if (getLocale() != null && (getParent() == null
                || !getLocale().equals(getParent().getLocale()))) {
            design.attr("locale", getLocale().toString());
        }
        // handle size
        writeSize(attr, def);
        // handle component error
        String errorMsg = getComponentError() != null
                ? getComponentError().getFormattedHtmlMessage()
                : null;
        String defErrorMsg = def.getComponentError() != null
                ? def.getComponentError().getFormattedHtmlMessage()
                : null;
        if (!SharedUtil.equals(errorMsg, defErrorMsg)) {
            attr.put("error", errorMsg);
        }
        // handle tab index
        if (this instanceof Focusable) {
            DesignAttributeHandler.writeAttribute("tabindex", attr,
                    ((Focusable) this).getTabIndex(),
                    ((Focusable) def).getTabIndex(), Integer.class,
                    designContext);
        }
        // handle custom attributes
        Map<String, String> customAttributes = designContext
                .getCustomAttributes(this);
        if (customAttributes != null) {
            for (Entry<String, String> entry : customAttributes.entrySet()) {
                attr.put(entry.getKey(), entry.getValue());
            }
        }
    }

    /*
     * Actions
     */

    /**
     * Gets the {@link ActionManager} used to manage the
     * {@link ShortcutListener}s added to this component.
     *
     * @return the ActionManager in use
     */
    protected ActionManager getActionManager() {
        if (actionManager == null) {
            actionManager = new ConnectorActionManager(this);
            setActionManagerViewer();
        }
        return actionManager;
    }

    /**
     * Set a viewer for the action manager to be the parent sub window (if the
     * component is in a window) or the UI (otherwise). This is still a
     * simplification of the real case as this should be handled by the parent
     * VOverlay (on the client side) if the component is inside an VOverlay
     * component.
     */
    private void setActionManagerViewer() {
        if (actionManager != null && getUI() != null) {
            // Attached and has action manager
            Window w = findAncestor(Window.class);
            if (w != null) {
                actionManager.setViewer(w);
            } else {
                actionManager.setViewer(getUI());
            }
        }

    }

    public Registration addShortcutListener(ShortcutListener shortcut) {
        Objects.requireNonNull(shortcut, "Listener must not be null.");
        getActionManager().addAction(shortcut);
        return () -> getActionManager().removeAction(shortcut);
    }

    @Deprecated
    public void removeShortcutListener(ShortcutListener shortcut) {
        getActionManager().removeAction(shortcut);
    }

    /**
     * Determine whether a <code>content</code> component is equal to, or the
     * ancestor of this component.
     *
     * @param content
     *            the potential ancestor element
     * @return <code>true</code> if the relationship holds
     */
    protected boolean isOrHasAncestor(Component content) {
        if (content instanceof HasComponents) {
            for (Component parent = this; parent != null; parent = parent
                    .getParent()) {
                if (parent.equals(content)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Registration addContextClickListener(ContextClickListener listener) {
        // Register default Context Click RPC if needed. This RPC won't be
        // called if there are no listeners on the server-side. A client-side
        // connector can override this and use a different RPC channel.
        if (getRpcManager(ContextClickRpc.class.getName()) == null) {
            registerRpc(
                    (ContextClickRpc) (MouseEventDetails details) -> fireEvent(
                            new ContextClickEvent(AbstractComponent.this,
                                    details)));
        }

        return addListener(EventId.CONTEXT_CLICK, ContextClickEvent.class,
                listener, ContextClickEvent.CONTEXT_CLICK_METHOD);
    }

    @Override
    @Deprecated
    public void removeContextClickListener(ContextClickListener listener) {
        removeListener(EventId.CONTEXT_CLICK, ContextClickEvent.class,
                listener);
    }

    /**
     * Sets the visibility of the required indicator. <strong>NOTE: Does not
     * apply for all components!</strong>.
     * <p>
     * If the component supports the required indicator (state extends
     * {@link AbstractFieldState}), then expose this method and
     * {@link #isRequiredIndicatorVisible()} as {@code public} in the component
     * and call this method.
     * <p>
     * This method will throw a {@link IllegalStateException} if the component
     * state (returned by {@link #getState()}) does not inherit
     * {@link AbstractFieldState}.
     *
     * @param visible
     *            <code>true</code> to make the required indicator visible,
     *            <code>false</code> if not
     * @since 8.0
     */
    protected void setRequiredIndicatorVisible(boolean visible) {
        if (getState(false) instanceof AbstractFieldState) {
            ((AbstractFieldState) getState()).required = visible;
        } else {
            throw new IllegalStateException(
                    "This component does not support the required indicator, since state is of type "
                            + getStateType().getSimpleName()
                            + " and does not inherit "
                            + AbstractFieldState.class.getSimpleName());
        }
    }

    /**
     * Checks whether the required indicator is visible or not. <strong>NOTE:
     * Does not apply for all components!</strong>.
     * <p>
     * This method will throw a {@link IllegalStateException} if the component
     * state (returned by {@link #getState()}) does not inherit
     * {@link AbstractFieldState}.
     *
     * @return <code>true</code> if visible, <code>false</code> if not
     * @see #setRequiredIndicatorVisible(boolean)
     * @since 8.0
     */
    protected boolean isRequiredIndicatorVisible() {
        if (getState(false) instanceof AbstractFieldState) {
            return ((AbstractFieldState) getState(false)).required;
        }
        throw new IllegalStateException(
                "This component does not support the required indicator, since state is of type "
                        + getStateType().getSimpleName()
                        + " and does not inherit "
                        + AbstractFieldState.class.getSimpleName());
    }

}
