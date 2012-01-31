/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ui.VFilterSelectPaintable;
import com.vaadin.terminal.gwt.client.ui.VListSelectPaintable;
import com.vaadin.terminal.gwt.client.ui.VSplitPanelHorizontal;
import com.vaadin.terminal.gwt.client.ui.VUnknownComponentPaintable;
import com.vaadin.terminal.gwt.client.ui.VVerticalSplitPanelPaintable;
import com.vaadin.terminal.gwt.client.ui.VView;
import com.vaadin.terminal.gwt.client.ui.VWindow;
import com.vaadin.terminal.gwt.client.ui.VWindowPaintable;

public class WidgetSet {

    /**
     * WidgetSet (and its extensions) delegate instantiation of widgets and
     * client-server matching to WidgetMap. The actual implementations are
     * generated with gwts generators/deferred binding.
     */
    private WidgetMap widgetMap = GWT.create(WidgetMap.class);

    /**
     * Create an uninitialized component that best matches given UIDL. The
     * component must be a {@link Widget} that implements
     * {@link VPaintableWidget}.
     * 
     * @param uidl
     *            UIDL to be painted with returned component.
     * @param client
     *            the application connection that whishes to instantiate widget
     * 
     * @return New uninitialized and unregistered component that can paint given
     *         UIDL.
     */
    public VPaintableWidget createWidget(UIDL uidl,
            ApplicationConfiguration conf) {
        /*
         * Yes, this (including the generated code in WidgetMap) may look very
         * odd code, but due the nature of GWT, we cannot do this any cleaner.
         * Luckily this is mostly written by WidgetSetGenerator, here are just
         * some hacks. Extra instantiation code is needed if client side widget
         * has no "native" counterpart on client side.
         * 
         * TODO should try to get rid of these exceptions here
         */

        final Class<? extends VPaintableWidget> classType = resolveWidgetType(
                uidl, conf);
        if (classType == null || classType == VUnknownComponentPaintable.class) {
            String serverSideName = conf
                    .getUnknownServerClassNameByEncodedTagName(uidl.getTag());
            VUnknownComponentPaintable c = GWT
                    .create(VUnknownComponentPaintable.class);
            c.setServerSideClassName(serverSideName);
            return c;
        } else if (VWindow.class == classType) {
            return GWT.create(VWindow.class);
        } else {
            /*
             * let the auto generated code instantiate this type
             */
            return widgetMap.instantiate(classType);
        }

    }

    protected Class<? extends VPaintableWidget> resolveWidgetType(UIDL uidl,
            ApplicationConfiguration conf) {
        final String tag = uidl.getTag();

        Class<? extends VPaintableWidget> widgetClass = conf
                .getWidgetClassByEncodedTag(tag);

        // add our historical quirks

        if (widgetClass == VView.class && uidl.hasAttribute("sub")) {
            return VWindowPaintable.class;
        } else if (widgetClass == VFilterSelectPaintable.class) {
            if (uidl.hasAttribute("type")) {
                final String type = uidl.getStringAttribute("type").intern();
                if ("legacy-multi" == type) {
                    return VListSelectPaintable.class;
                }
            }
        } else if (widgetClass == VSplitPanelHorizontal.class
                && uidl.hasAttribute("vertical")) {
            return VVerticalSplitPanelPaintable.class;
        }

        return widgetClass;

    }

    /**
     * Test if the given component implementation conforms to UIDL.
     * 
     * @param currentWidget
     *            Current implementation of the component
     * @param uidl
     *            UIDL to test against
     * @return true iff createWidget would return a new component of the same
     *         class than currentWidget
     */
    public boolean isCorrectImplementation(Widget currentWidget, UIDL uidl,
            ApplicationConfiguration conf) {
        return currentWidget.getClass() == resolveWidgetType(uidl, conf);
    }

    /**
     * Due its nature, GWT does not support dynamic classloading. To bypass this
     * limitation, widgetset must have function that returns Class by its fully
     * qualified name.
     * 
     * @param fullyQualifiedName
     * @param applicationConfiguration
     * @return
     */
    public Class<? extends VPaintableWidget> getImplementationByClassName(
            String fullyqualifiedName) {
        if (fullyqualifiedName == null) {
            return VUnknownComponentPaintable.class;
        }
        Class<? extends VPaintableWidget> implementationByServerSideClassName = widgetMap
                .getImplementationByServerSideClassName(fullyqualifiedName);

        /*
         * Also ensure that our historical quirks have their instantiators
         * loaded. Without these, legacy code will throw NPEs when e.g. a Select
         * is in multiselect mode, causing the clientside implementation to
         * *actually* be VListSelect, when the annotation says VFilterSelect
         */
        if (fullyqualifiedName.equals("com.vaadin.ui.Select")) {
            loadImplementation(VListSelectPaintable.class);
        } else if (fullyqualifiedName.equals("com.vaadin.ui.SplitPanel")) {
            loadImplementation(VVerticalSplitPanelPaintable.class);
        }

        return implementationByServerSideClassName;

    }

    public Class<? extends VPaintableWidget>[] getDeferredLoadedWidgets() {
        return widgetMap.getDeferredLoadedWidgets();
    }

    public void loadImplementation(Class<? extends VPaintableWidget> nextType) {
        widgetMap.ensureInstantiator(nextType);
    }

}
