/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.terminal.gwt.client;

import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.ui.IAccordion;
import com.itmill.toolkit.terminal.gwt.client.ui.IButton;
import com.itmill.toolkit.terminal.gwt.client.ui.ICheckBox;
import com.itmill.toolkit.terminal.gwt.client.ui.ICoordinateLayout;
import com.itmill.toolkit.terminal.gwt.client.ui.ICustomComponent;
import com.itmill.toolkit.terminal.gwt.client.ui.ICustomLayout;
import com.itmill.toolkit.terminal.gwt.client.ui.IDateFieldCalendar;
import com.itmill.toolkit.terminal.gwt.client.ui.IEmbedded;
import com.itmill.toolkit.terminal.gwt.client.ui.IFilterSelect;
import com.itmill.toolkit.terminal.gwt.client.ui.IForm;
import com.itmill.toolkit.terminal.gwt.client.ui.IFormLayout;
import com.itmill.toolkit.terminal.gwt.client.ui.IGridLayout;
import com.itmill.toolkit.terminal.gwt.client.ui.ILabel;
import com.itmill.toolkit.terminal.gwt.client.ui.ILink;
import com.itmill.toolkit.terminal.gwt.client.ui.IListSelect;
import com.itmill.toolkit.terminal.gwt.client.ui.IMenuBar;
import com.itmill.toolkit.terminal.gwt.client.ui.INativeSelect;
import com.itmill.toolkit.terminal.gwt.client.ui.IOptionGroup;
import com.itmill.toolkit.terminal.gwt.client.ui.IOrderedLayout;
import com.itmill.toolkit.terminal.gwt.client.ui.IPanel;
import com.itmill.toolkit.terminal.gwt.client.ui.IPasswordField;
import com.itmill.toolkit.terminal.gwt.client.ui.IPopupCalendar;
import com.itmill.toolkit.terminal.gwt.client.ui.IPopupView;
import com.itmill.toolkit.terminal.gwt.client.ui.IProgressIndicator;
import com.itmill.toolkit.terminal.gwt.client.ui.IScrollTable;
import com.itmill.toolkit.terminal.gwt.client.ui.ISlider;
import com.itmill.toolkit.terminal.gwt.client.ui.ISplitPanelHorizontal;
import com.itmill.toolkit.terminal.gwt.client.ui.ISplitPanelVertical;
import com.itmill.toolkit.terminal.gwt.client.ui.ITablePaging;
import com.itmill.toolkit.terminal.gwt.client.ui.ITabsheet;
import com.itmill.toolkit.terminal.gwt.client.ui.ITextArea;
import com.itmill.toolkit.terminal.gwt.client.ui.ITextField;
import com.itmill.toolkit.terminal.gwt.client.ui.ITextualDate;
import com.itmill.toolkit.terminal.gwt.client.ui.ITree;
import com.itmill.toolkit.terminal.gwt.client.ui.ITwinColSelect;
import com.itmill.toolkit.terminal.gwt.client.ui.IUnknownComponent;
import com.itmill.toolkit.terminal.gwt.client.ui.IUpload;
import com.itmill.toolkit.terminal.gwt.client.ui.IUriFragmentUtility;
import com.itmill.toolkit.terminal.gwt.client.ui.IWindow;
import com.itmill.toolkit.terminal.gwt.client.ui.richtextarea.IRichTextArea;

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
        if (ICheckBox.class == classType) {
            return new ICheckBox();
        } else if (IButton.class == classType) {
            return new IButton();
        } else if (IWindow.class == classType) {
            return new IWindow();
        } else if (IOrderedLayout.class == classType) {
            return new IOrderedLayout();
        } else if (ILabel.class == classType) {
            return new ILabel();
        } else if (ILink.class == classType) {
            return new ILink();
        } else if (IGridLayout.class == classType) {
            return new IGridLayout();
        } else if (ITree.class == classType) {
            return new ITree();
        } else if (IOptionGroup.class == classType) {
            return new IOptionGroup();
        } else if (ITwinColSelect.class == classType) {
            return new ITwinColSelect();
        } else if (INativeSelect.class == classType) {
            return new INativeSelect();
        } else if (IListSelect.class == classType) {
            return new IListSelect();
        } else if (IPanel.class == classType) {
            return new IPanel();
        } else if (ITabsheet.class == classType) {
            return new ITabsheet();
        } else if (IEmbedded.class == classType) {
            return new IEmbedded();
        } else if (ICustomLayout.class == classType) {
            return new ICustomLayout();
        } else if (ICustomComponent.class == classType) {
            return new ICustomComponent();
        } else if (ITextArea.class == classType) {
            return new ITextArea();
        } else if (IPasswordField.class == classType) {
            return new IPasswordField();
        } else if (ITextField.class == classType) {
            return new ITextField();
        } else if (ITablePaging.class == classType) {
            return new ITablePaging();
        } else if (IScrollTable.class == classType) {
            return new IScrollTable();
        } else if (IDateFieldCalendar.class == classType) {
            return new IDateFieldCalendar();
        } else if (ITextualDate.class == classType) {
            return new ITextualDate();
        } else if (IPopupCalendar.class == classType) {
            return new IPopupCalendar();
        } else if (ISlider.class == classType) {
            return new ISlider();
        } else if (IForm.class == classType) {
            return new IForm();
        } else if (IFormLayout.class == classType) {
            return new IFormLayout();
        } else if (IUpload.class == classType) {
            return new IUpload();
        } else if (ISplitPanelHorizontal.class == classType) {
            return new ISplitPanelHorizontal();
        } else if (ISplitPanelVertical.class == classType) {
            return new ISplitPanelVertical();
        } else if (IFilterSelect.class == classType) {
            return new IFilterSelect();
        } else if (IProgressIndicator.class == classType) {
            return new IProgressIndicator();
        } else if (IRichTextArea.class == classType) {
            return new IRichTextArea();
        } else if (IAccordion.class == classType) {
            return new IAccordion();
        } else if (IMenuBar.class == classType) {
            return new IMenuBar();
        } else if (IPopupView.class == classType) {
            return new IPopupView();
        } else if (ICoordinateLayout.class == classType) {
            return new ICoordinateLayout();
        } else if (IUriFragmentUtility.class == classType) {
            return new IUriFragmentUtility();
        }

        return new IUnknownComponent();

    }

    protected Class resolveWidgetType(UIDL uidl) {
        final String tag = uidl.getTag();
        if ("button".equals(tag)) {
            if ("switch".equals(uidl.getStringAttribute("type"))) {
                return ICheckBox.class;
            } else {
                return IButton.class;
            }
        } else if ("window".equals(tag)) {
            return IWindow.class;
        } else if ("orderedlayout".equals(tag)) {
            return IOrderedLayout.class;
        } else if ("label".equals(tag)) {
            return ILabel.class;
        } else if ("link".equals(tag)) {
            return ILink.class;
        } else if ("gridlayout".equals(tag)) {
            return IGridLayout.class;
        } else if ("tree".equals(tag)) {
            return ITree.class;
        } else if ("select".equals(tag)) {
            if (uidl.hasAttribute("type")) {
                final String type = uidl.getStringAttribute("type");
                if (type.equals("twincol")) {
                    return ITwinColSelect.class;
                }
                if (type.equals("optiongroup")) {
                    return IOptionGroup.class;
                }
                if (type.equals("native")) {
                    return INativeSelect.class;
                }
                if (type.equals("list")) {
                    return IListSelect.class;
                }
            } else {
                if (uidl.hasAttribute("selectmode")
                        && uidl.getStringAttribute("selectmode")
                                .equals("multi")) {
                    return IListSelect.class;
                } else {
                    return IFilterSelect.class;
                }
            }
        } else if ("panel".equals(tag)) {
            return IPanel.class;
        } else if ("tabsheet".equals(tag)) {
            return ITabsheet.class;
        } else if ("accordion".equals(tag)) {
            return IAccordion.class;
        } else if ("embedded".equals(tag)) {
            return IEmbedded.class;
        } else if ("customlayout".equals(tag)) {
            return ICustomLayout.class;
        } else if ("customcomponent".equals(tag)) {
            return ICustomComponent.class;
        } else if ("textfield".equals(tag)) {
            if (uidl.getBooleanAttribute("richtext")) {
                return IRichTextArea.class;
            } else if (uidl.hasAttribute("multiline")) {
                return ITextArea.class;
            } else if (uidl.getBooleanAttribute("secret")) {
                return IPasswordField.class;
            } else {
                return ITextField.class;
            }
        } else if ("table".equals(tag)) {
            return IScrollTable.class;
        } else if ("pagingtable".equals(tag)) {
            return ITablePaging.class;
        } else if ("datefield".equals(tag)) {
            if (uidl.hasAttribute("type")) {
                if ("inline".equals(uidl.getStringAttribute("type"))) {
                    return IDateFieldCalendar.class;
                } else if ("popup".equals(uidl.getStringAttribute("type"))) {
                    return IPopupCalendar.class;
                }
            }
            // popup calendar is the default
            return IPopupCalendar.class;
        } else if ("slider".equals(tag)) {
            return ISlider.class;
        } else if ("form".equals(tag)) {
            return IForm.class;
        } else if ("formlayout".equals(tag)) {
            return IFormLayout.class;
        } else if ("upload".equals(tag)) {
            return IUpload.class;
        } else if ("hsplitpanel".equals(tag)) {
            return ISplitPanelHorizontal.class;
        } else if ("vsplitpanel".equals(tag)) {
            return ISplitPanelVertical.class;
        } else if ("progressindicator".equals(tag)) {
            return IProgressIndicator.class;
        } else if ("menubar".equals(tag)) {
            return IMenuBar.class;
        } else if ("popupview".equals(tag)) {
            return IPopupView.class;
        } else if ("coordinatelayout".equals(tag)) {
            return ICoordinateLayout.class;
        } else if ("urifragment".equals(tag)) {
            return IUriFragmentUtility.class;
        }

        return IUnknownComponent.class;
    }

    /**
     * Kept here to support 5.2 era widget sets
     * 
     * @deprecated use resolveWidgetType instead
     */
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
