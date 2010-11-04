/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ui.VButton;
import com.vaadin.terminal.gwt.client.ui.VCheckBox;
import com.vaadin.terminal.gwt.client.ui.VFilterSelect;
import com.vaadin.terminal.gwt.client.ui.VListSelect;
import com.vaadin.terminal.gwt.client.ui.VPasswordField;
import com.vaadin.terminal.gwt.client.ui.VSplitPanelHorizontal;
import com.vaadin.terminal.gwt.client.ui.VSplitPanelVertical;
import com.vaadin.terminal.gwt.client.ui.VTextArea;
import com.vaadin.terminal.gwt.client.ui.VTextField;
import com.vaadin.terminal.gwt.client.ui.VUnknownComponent;
import com.vaadin.terminal.gwt.client.ui.VView;
import com.vaadin.terminal.gwt.client.ui.VWindow;

public class WidgetSet {

    /**
     * WidgetSet (and its extensions) delegate instantiation of widgets and
     * client-server matching to WidgetMap. The actual implementations are
     * generated with gwts generators/deferred binding.
     */
    private WidgetMap widgetMap = GWT.create(WidgetMap.class);

    /**
     * Create an uninitialized component that best matches given UIDL. The
     * component must be a {@link Widget} that implements {@link Paintable}.
     * 
     * @param uidl
     *            UIDL to be painted with returned component.
     * @param client
     *            the application connection that whishes to instantiate widget
     * 
     * @return New uninitialized and unregistered component that can paint given
     *         UIDL.
     */
    public Paintable createWidget(UIDL uidl, ApplicationConfiguration conf) {
        /*
         * Yes, this (including the generated code in WidgetMap) may look very
         * odd code, but due the nature of GWT, we cannot do this any cleaner.
         * Luckily this is mostly written by WidgetSetGenerator, here are just
         * some hacks. Extra instantiation code is needed if client side widget
         * has no "native" counterpart on client side.
         * 
         * TODO should try to get rid of these exceptions here
         */

        final Class<? extends Paintable> classType = resolveWidgetType(uidl,
                conf);
        if (classType == null || classType == VUnknownComponent.class) {
            String serverSideName = conf
                    .getUnknownServerClassNameByEncodedTagName(uidl.getTag());
            VUnknownComponent c = GWT.create(VUnknownComponent.class);
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

    protected Class<? extends Paintable> resolveWidgetType(UIDL uidl,
            ApplicationConfiguration conf) {
        final String tag = uidl.getTag();

        Class<? extends Paintable> widgetClass = conf
                .getWidgetClassByEncodedTag(tag);

        // add our historical quirks

        if (widgetClass == VButton.class && uidl.hasAttribute("type")) {
            return VCheckBox.class;
        } else if (widgetClass == VView.class && uidl.hasAttribute("sub")) {
            return VWindow.class;
        } else if (widgetClass == VFilterSelect.class) {
            if (uidl.hasAttribute("type")) {
                final String type = uidl.getStringAttribute("type").intern();
                if ("legacy-multi" == type) {
                    return VListSelect.class;
                }
            }
        } else if (widgetClass == VTextField.class) {
            if (uidl.hasAttribute("multiline")) {
                return VTextArea.class;
            } else if (uidl.hasAttribute("secret")) {
                return VPasswordField.class;
            }
        } else if (widgetClass == VSplitPanelHorizontal.class
                && uidl.hasAttribute("vertical")) {
            return VSplitPanelVertical.class;
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
    public Class<? extends Paintable> getImplementationByClassName(
            String fullyqualifiedName) {
        if (fullyqualifiedName == null) {
            return VUnknownComponent.class;
        }
        Class<? extends Paintable> implementationByServerSideClassName = widgetMap
                .getImplementationByServerSideClassName(fullyqualifiedName);

        /*
         * Also ensure that our historical quirks have their instantiators
         * loaded. Without these, legacy code will throw NPEs when e.g. a Select
         * is in multiselect mode, causing the clientside implementation to
         * *actually* be VListSelect, when the annotation says VFilterSelect
         */
        if (fullyqualifiedName.equals("com.vaadin.ui.Button")) {
            loadImplementation(VCheckBox.class);
        } else if (fullyqualifiedName.equals("com.vaadin.ui.Select")) {
            loadImplementation(VListSelect.class);
        } else if (fullyqualifiedName.equals("com.vaadin.ui.TextField")) {
            loadImplementation(VTextArea.class);
            loadImplementation(VPasswordField.class);
        } else if (fullyqualifiedName.equals("com.vaadin.ui.SplitPanel")) {
            loadImplementation(VSplitPanelVertical.class);
        }

        return implementationByServerSideClassName;

    }

    public Class<? extends Paintable>[] getDeferredLoadedWidgets() {
        return widgetMap.getDeferredLoadedWidgets();
    }

    public void loadImplementation(Class<? extends Paintable> nextType) {
        widgetMap.ensureInstantiator(nextType);
    }

}
