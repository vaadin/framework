/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client;

import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ui.VAbsoluteLayout;
import com.vaadin.terminal.gwt.client.ui.VAccordion;
import com.vaadin.terminal.gwt.client.ui.VButton;
import com.vaadin.terminal.gwt.client.ui.VCheckBox;
import com.vaadin.terminal.gwt.client.ui.VCustomComponent;
import com.vaadin.terminal.gwt.client.ui.VCustomLayout;
import com.vaadin.terminal.gwt.client.ui.VDateFieldCalendar;
import com.vaadin.terminal.gwt.client.ui.VEmbedded;
import com.vaadin.terminal.gwt.client.ui.VFilterSelect;
import com.vaadin.terminal.gwt.client.ui.VCssLayout;
import com.vaadin.terminal.gwt.client.ui.VForm;
import com.vaadin.terminal.gwt.client.ui.VFormLayout;
import com.vaadin.terminal.gwt.client.ui.VGridLayout;
import com.vaadin.terminal.gwt.client.ui.VHorizontalLayout;
import com.vaadin.terminal.gwt.client.ui.VLabel;
import com.vaadin.terminal.gwt.client.ui.VLink;
import com.vaadin.terminal.gwt.client.ui.VListSelect;
import com.vaadin.terminal.gwt.client.ui.VMenuBar;
import com.vaadin.terminal.gwt.client.ui.VNativeButton;
import com.vaadin.terminal.gwt.client.ui.VNativeSelect;
import com.vaadin.terminal.gwt.client.ui.VOptionGroup;
import com.vaadin.terminal.gwt.client.ui.VOrderedLayout;
import com.vaadin.terminal.gwt.client.ui.VPanel;
import com.vaadin.terminal.gwt.client.ui.VPasswordField;
import com.vaadin.terminal.gwt.client.ui.VPopupCalendar;
import com.vaadin.terminal.gwt.client.ui.VPopupView;
import com.vaadin.terminal.gwt.client.ui.VProgressIndicator;
import com.vaadin.terminal.gwt.client.ui.VScrollTable;
import com.vaadin.terminal.gwt.client.ui.VSlider;
import com.vaadin.terminal.gwt.client.ui.VSplitPanelHorizontal;
import com.vaadin.terminal.gwt.client.ui.VSplitPanelVertical;
import com.vaadin.terminal.gwt.client.ui.VTablePaging;
import com.vaadin.terminal.gwt.client.ui.VTabsheet;
import com.vaadin.terminal.gwt.client.ui.VTextArea;
import com.vaadin.terminal.gwt.client.ui.VTextField;
import com.vaadin.terminal.gwt.client.ui.VTextualDate;
import com.vaadin.terminal.gwt.client.ui.VTree;
import com.vaadin.terminal.gwt.client.ui.VTwinColSelect;
import com.vaadin.terminal.gwt.client.ui.VUnknownComponent;
import com.vaadin.terminal.gwt.client.ui.VUpload;
import com.vaadin.terminal.gwt.client.ui.VUriFragmentUtility;
import com.vaadin.terminal.gwt.client.ui.VVerticalLayout;
import com.vaadin.terminal.gwt.client.ui.VWindow;
import com.vaadin.terminal.gwt.client.ui.richtextarea.VRichTextArea;

public class DefaultWidgetSet implements WidgetSet {

    /**
     * This is the entry point method. It will start the first
     */
    public void onModuleLoad() {
        ApplicationConfiguration.initConfigurations(this);
        ApplicationConfiguration.startNextApplication(); // start first app
    }

    public Paintable createWidget(UIDL uidl) {
        final Class classType = resolveWidgetType(uidl);
        if (VCheckBox.class == classType) {
            return new VCheckBox();
        } else if (VButton.class == classType) {
            return new VButton();
        } else if (VNativeButton.class == classType) {
            return new VNativeButton();
        } else if (VWindow.class == classType) {
            return new VWindow();
        } else if (VOrderedLayout.class == classType) {
            return new VOrderedLayout();
        } else if (VVerticalLayout.class == classType) {
            return new VVerticalLayout();
        } else if (VHorizontalLayout.class == classType) {
            return new VHorizontalLayout();
        } else if (VLabel.class == classType) {
            return new VLabel();
        } else if (VLink.class == classType) {
            return new VLink();
        } else if (VGridLayout.class == classType) {
            return new VGridLayout();
        } else if (VTree.class == classType) {
            return new VTree();
        } else if (VOptionGroup.class == classType) {
            return new VOptionGroup();
        } else if (VTwinColSelect.class == classType) {
            return new VTwinColSelect();
        } else if (VNativeSelect.class == classType) {
            return new VNativeSelect();
        } else if (VListSelect.class == classType) {
            return new VListSelect();
        } else if (VPanel.class == classType) {
            return new VPanel();
        } else if (VTabsheet.class == classType) {
            return new VTabsheet();
        } else if (VEmbedded.class == classType) {
            return new VEmbedded();
        } else if (VCustomLayout.class == classType) {
            return new VCustomLayout();
        } else if (VCustomComponent.class == classType) {
            return new VCustomComponent();
        } else if (VTextArea.class == classType) {
            return new VTextArea();
        } else if (VPasswordField.class == classType) {
            return new VPasswordField();
        } else if (VTextField.class == classType) {
            return new VTextField();
        } else if (VTablePaging.class == classType) {
            return new VTablePaging();
        } else if (VScrollTable.class == classType) {
            return new VScrollTable();
        } else if (VDateFieldCalendar.class == classType) {
            return new VDateFieldCalendar();
        } else if (VTextualDate.class == classType) {
            return new VTextualDate();
        } else if (VPopupCalendar.class == classType) {
            return new VPopupCalendar();
        } else if (VSlider.class == classType) {
            return new VSlider();
        } else if (VForm.class == classType) {
            return new VForm();
        } else if (VFormLayout.class == classType) {
            return new VFormLayout();
        } else if (VUpload.class == classType) {
            return new VUpload();
        } else if (VSplitPanelHorizontal.class == classType) {
            return new VSplitPanelHorizontal();
        } else if (VSplitPanelVertical.class == classType) {
            return new VSplitPanelVertical();
        } else if (VFilterSelect.class == classType) {
            return new VFilterSelect();
        } else if (VProgressIndicator.class == classType) {
            return new VProgressIndicator();
        } else if (VRichTextArea.class == classType) {
            return new VRichTextArea();
        } else if (VAccordion.class == classType) {
            return new VAccordion();
        } else if (VMenuBar.class == classType) {
            return new VMenuBar();
        } else if (VPopupView.class == classType) {
            return new VPopupView();
        } else if (VUriFragmentUtility.class == classType) {
            return new VUriFragmentUtility();
        } else if (VAbsoluteLayout.class == classType) {
            return new VAbsoluteLayout();
        } else if (VCssLayout.class == classType) {
            return new VCssLayout();
        }

        return new VUnknownComponent();

    }

    protected Class resolveWidgetType(UIDL uidl) {
        final String tag = uidl.getTag();
        if ("button".equals(tag) || "nativebutton".equals(tag)) {
            if ("switch".equals(uidl.getStringAttribute("type"))) {
                return VCheckBox.class;
            } else if ("nativebutton".equals(tag)) {
                return VNativeButton.class;
            } else {
                return VButton.class;
            }
        } else if ("window".equals(tag)) {
            return VWindow.class;
        } else if ("orderedlayout".equals(tag)) {
            return VOrderedLayout.class;
        } else if ("verticallayout".equals(tag)) {
            return VVerticalLayout.class;
        } else if ("horizontallayout".equals(tag)) {
            return VHorizontalLayout.class;
        } else if ("label".equals(tag)) {
            return VLabel.class;
        } else if ("link".equals(tag)) {
            return VLink.class;
        } else if ("gridlayout".equals(tag)) {
            return VGridLayout.class;
        } else if ("tree".equals(tag)) {
            return VTree.class;
        } else if ("select".equals(tag)) {
            if (uidl.hasAttribute("type")) {
                final String type = uidl.getStringAttribute("type");
                if (type.equals("twincol")) {
                    return VTwinColSelect.class;
                }
                if (type.equals("optiongroup")) {
                    return VOptionGroup.class;
                }
                if (type.equals("native")) {
                    return VNativeSelect.class;
                }
                if (type.equals("list")) {
                    return VListSelect.class;
                }
            } else {
                if (uidl.hasAttribute("selectmode")
                        && uidl.getStringAttribute("selectmode")
                                .equals("multi")) {
                    return VListSelect.class;
                } else {
                    return VFilterSelect.class;
                }
            }
        } else if ("panel".equals(tag)) {
            return VPanel.class;
        } else if ("tabsheet".equals(tag)) {
            return VTabsheet.class;
        } else if ("accordion".equals(tag)) {
            return VAccordion.class;
        } else if ("embedded".equals(tag)) {
            return VEmbedded.class;
        } else if ("customlayout".equals(tag)) {
            return VCustomLayout.class;
        } else if ("customcomponent".equals(tag)) {
            return VCustomComponent.class;
        } else if ("textfield".equals(tag)) {
            if (uidl.getBooleanAttribute("richtext")) {
                return VRichTextArea.class;
            } else if (uidl.hasAttribute("multiline")) {
                return VTextArea.class;
            } else if (uidl.getBooleanAttribute("secret")) {
                return VPasswordField.class;
            } else {
                return VTextField.class;
            }
        } else if ("table".equals(tag)) {
            return VScrollTable.class;
        } else if ("pagingtable".equals(tag)) {
            return VTablePaging.class;
        } else if ("datefield".equals(tag)) {
            if (uidl.hasAttribute("type")) {
                if ("inline".equals(uidl.getStringAttribute("type"))) {
                    return VDateFieldCalendar.class;
                } else if ("popup".equals(uidl.getStringAttribute("type"))) {
                    return VPopupCalendar.class;
                }
            }
            // popup calendar is the default
            return VPopupCalendar.class;
        } else if ("slider".equals(tag)) {
            return VSlider.class;
        } else if ("form".equals(tag)) {
            return VForm.class;
        } else if ("formlayout".equals(tag)) {
            return VFormLayout.class;
        } else if ("upload".equals(tag)) {
            return VUpload.class;
        } else if ("hsplitpanel".equals(tag)) {
            return VSplitPanelHorizontal.class;
        } else if ("vsplitpanel".equals(tag)) {
            return VSplitPanelVertical.class;
        } else if ("progressindicator".equals(tag)) {
            return VProgressIndicator.class;
        } else if ("menubar".equals(tag)) {
            return VMenuBar.class;
        } else if ("popupview".equals(tag)) {
            return VPopupView.class;
        } else if ("urifragment".equals(tag)) {
            return VUriFragmentUtility.class;
        } else if (VAbsoluteLayout.TAGNAME.equals(tag)) {
            return VAbsoluteLayout.class;
        } else if (VCssLayout.TAGNAME.equals(tag)) {
            return VCssLayout.class;
        }

        return VUnknownComponent.class;
    }

    /**
     * Kept here to support 5.2 era widget sets
     * 
     * @deprecated use resolveWidgetType instead
     */
    @Deprecated
    protected String resolveWidgetTypeName(UIDL uidl) {
        Class type = resolveWidgetType(uidl);
        return type.getName();
    }

    public boolean isCorrectImplementation(Widget currentWidget, UIDL uidl) {
        // TODO remove backwardscompatibility check
        return currentWidget.getClass() == resolveWidgetType(uidl)
                || currentWidget.getClass().getName().equals(
                        resolveWidgetTypeName(uidl));
    }

}
