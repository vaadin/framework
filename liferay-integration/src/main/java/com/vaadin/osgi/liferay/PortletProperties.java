package com.vaadin.osgi.liferay;

import javax.portlet.Portlet;

import com.vaadin.ui.UI;

/**
 * Constants for Liferay {@link Portlet portlets}. This doesn't have to be used
 * by the application developer.
 * 
 * @author Vaadin Ltd.
 * @since 8.1
 */
public final class PortletProperties {
    private PortletProperties() {

    }

    /**
     * Property key for the Liferay category property. By default this is
     * "category.vaadin"
     */
    public static final String DISPLAY_CATEGORY = "com.liferay.portlet.display-category";

    /**
     * Property key for the name of the {@link Portlet}. It is recommended to
     * use something like the bundle symbolic name and a version string appended
     * for the value of this property as this is used as a {@link Portlet} id.
     */
    public static final String PORTLET_NAME = "javax.portlet.name";

    /**
     * Property key for the {@link UI} visible name of the {@link Portlet}.
     */
    public static final String DISPLAY_NAME = "javax.portlet.display-name";

    /**
     * Property key for the security roles mapped to the {@link Portlet}.
     */
    public static final String PORTLET_SECURITY_ROLE = "javax.portlet.security-role-ref";

    /**
     * This property is used to mark the UI service as a {@link Portlet}
     * {@link UI}. The value of this property must be non-null and will be
     * ignored but must be present to use the {@link UI} as a {@link Portlet}.
     * 
     * <p>
     * The alternative is to simply annotate the {@link UI} with
     * {@link VaadinLiferayPortletConfiguration}.
     */
    public static final String PORTLET_UI_PROPERTY = "com.vaadin.osgi.liferay.portlet-ui";
}
