/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ui.VButton;
import com.vaadin.terminal.gwt.client.ui.VCheckBox;
import com.vaadin.terminal.gwt.client.ui.VDateFieldCalendar;
import com.vaadin.terminal.gwt.client.ui.VDragAndDropWrapper;
import com.vaadin.terminal.gwt.client.ui.VFilterSelect;
import com.vaadin.terminal.gwt.client.ui.VListSelect;
import com.vaadin.terminal.gwt.client.ui.VNativeSelect;
import com.vaadin.terminal.gwt.client.ui.VOptionGroup;
import com.vaadin.terminal.gwt.client.ui.VPasswordField;
import com.vaadin.terminal.gwt.client.ui.VPopupCalendar;
import com.vaadin.terminal.gwt.client.ui.VSplitPanelHorizontal;
import com.vaadin.terminal.gwt.client.ui.VSplitPanelVertical;
import com.vaadin.terminal.gwt.client.ui.VTextArea;
import com.vaadin.terminal.gwt.client.ui.VTextField;
import com.vaadin.terminal.gwt.client.ui.VTwinColSelect;
import com.vaadin.terminal.gwt.client.ui.VUnknownComponent;
import com.vaadin.terminal.gwt.client.ui.VView;
import com.vaadin.terminal.gwt.client.ui.VWindow;

public class DefaultWidgetSet implements WidgetSet {

    /**
     * DefaultWidgetSet (and its extensions) delegate instantiation of widgets
     * and client-server mathing to WidgetMap. The actual implementations are
     * generated with gwts deferred binding.
     */
    private WidgetMap map;

    /**
     * This is the entry point method. It will start the first
     */
    public void onModuleLoad() {
        try {
            ApplicationConfiguration.initConfigurations(this);
        } catch (Exception e) {
            // Log & don't continue;
            // custom WidgetSets w/ entry points will cause this
            ApplicationConnection.getConsole().log(e.getMessage());
            return;
        }
        ApplicationConfiguration.startNextApplication(); // start first app
        map = GWT.create(WidgetMap.class);
    }

    public Paintable createWidget(UIDL uidl, ApplicationConfiguration conf) {
        final Class<? extends Paintable> classType = resolveWidgetType(uidl,
                conf);
        if (classType == null || classType == VUnknownComponent.class) {
            String serverSideName = conf
                    .getUnknownServerClassNameByEncodedTagName(uidl.getTag());
            return new VUnknownComponent(serverSideName);
        }

        return map.instantiate(classType);
    }

    protected Class<? extends Paintable> resolveWidgetType(UIDL uidl,
            ApplicationConfiguration conf) {
        final String tag = uidl.getTag();

        Class<? extends Paintable> widgetClass = conf
                .getWidgetClassByEncodedTag(tag);

        // TODO add our quirks

        if (widgetClass == VButton.class && uidl.hasAttribute("type")) {
            return VCheckBox.class;
        } else if (widgetClass == VView.class && uidl.hasAttribute("sub")) {
            return VWindow.class;
        } else if (widgetClass == VFilterSelect.class) {
            if (uidl.hasAttribute("type")) {
                // TODO check if all type checks are really neede
                final String type = uidl.getStringAttribute("type").intern();
                if (type == "twincol") {
                    return VTwinColSelect.class;
                } else if (type == "optiongroup") {
                    return VOptionGroup.class;
                } else if (type == "native") {
                    return VNativeSelect.class;
                } else if (type == "list") {
                    return VListSelect.class;
                } else if (uidl.hasAttribute("selectmode")
                        && uidl.getStringAttribute("selectmode")
                                .equals("multi")) {
                    return VListSelect.class;
                }
            }
        } else if (widgetClass == VTextField.class) {
            if (uidl.hasAttribute("multiline")) {
                return VTextArea.class;
            } else if (uidl.hasAttribute("secret")) {
                return VPasswordField.class;
            }
        } else if (widgetClass == VPopupCalendar.class) {
            if (uidl.hasAttribute("type")
                    && uidl.getStringAttribute("type").equals("inline")) {
                return VDateFieldCalendar.class;
            }
        } else if (widgetClass == VSplitPanelHorizontal.class
                && uidl.hasAttribute("vertical")) {
            return VSplitPanelVertical.class;
        }

        return widgetClass;

    }

    public boolean isCorrectImplementation(Widget currentWidget, UIDL uidl,
            ApplicationConfiguration conf) {
        return currentWidget.getClass() == resolveWidgetType(uidl, conf);
    }

    public Class<? extends Paintable> getImplementationByClassName(
            String fullyqualifiedName) {
        Class<? extends Paintable> implementationByServerSideClassName = map
                .getImplementationByServerSideClassName(fullyqualifiedName);
        return implementationByServerSideClassName;

    }
}
