package com.itmill.toolkit.terminal.gwt.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.ui.IButton;
import com.itmill.toolkit.terminal.gwt.client.ui.ICheckBox;
import com.itmill.toolkit.terminal.gwt.client.ui.ICustomLayout;
import com.itmill.toolkit.terminal.gwt.client.ui.IDateFieldCalendar;
import com.itmill.toolkit.terminal.gwt.client.ui.IEmbedded;
import com.itmill.toolkit.terminal.gwt.client.ui.IExpandLayout;
import com.itmill.toolkit.terminal.gwt.client.ui.IFilterSelect;
import com.itmill.toolkit.terminal.gwt.client.ui.IForm;
import com.itmill.toolkit.terminal.gwt.client.ui.IFormLayout;
import com.itmill.toolkit.terminal.gwt.client.ui.IGridLayout;
import com.itmill.toolkit.terminal.gwt.client.ui.ILabel;
import com.itmill.toolkit.terminal.gwt.client.ui.ILink;
import com.itmill.toolkit.terminal.gwt.client.ui.IListSelect;
import com.itmill.toolkit.terminal.gwt.client.ui.IOptionGroup;
import com.itmill.toolkit.terminal.gwt.client.ui.IOrderedLayoutHorizontal;
import com.itmill.toolkit.terminal.gwt.client.ui.IOrderedLayoutVertical;
import com.itmill.toolkit.terminal.gwt.client.ui.IPanel;
import com.itmill.toolkit.terminal.gwt.client.ui.IPasswordField;
import com.itmill.toolkit.terminal.gwt.client.ui.IPopupCalendar;
import com.itmill.toolkit.terminal.gwt.client.ui.IProgressIndicator;
import com.itmill.toolkit.terminal.gwt.client.ui.IScrollTable;
import com.itmill.toolkit.terminal.gwt.client.ui.ISelect;
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
import com.itmill.toolkit.terminal.gwt.client.ui.IWindow;
import com.itmill.toolkit.terminal.gwt.client.ui.richtextarea.IRichTextArea;

public class DefaultWidgetSet implements WidgetSet {

    protected ApplicationConnection appConn;

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
        appConn = new ApplicationConnection(this);
    }

    public Widget createWidget(UIDL uidl) {

        String className = resolveWidgetTypeName(uidl);
        if ("com.itmill.toolkit.terminal.gwt.client.ui.ICheckBox"
                .equals(className)) {
            return new ICheckBox();
        } else if ("com.itmill.toolkit.terminal.gwt.client.ui.IButton"
                .equals(className)) {
            return new IButton();
        } else if ("com.itmill.toolkit.terminal.gwt.client.ui.IWindow"
                .equals(className)) {
            return new IWindow();
        } else if ("com.itmill.toolkit.terminal.gwt.client.ui.IOrderedLayoutVertical"
                .equals(className)) {
            return new IOrderedLayoutVertical();
        } else if ("com.itmill.toolkit.terminal.gwt.client.ui.IOrderedLayoutHorizontal"
                .equals(className)) {
            return new IOrderedLayoutHorizontal();
        } else if ("com.itmill.toolkit.terminal.gwt.client.ui.ILabel"
                .equals(className)) {
            return new ILabel();
        } else if ("com.itmill.toolkit.terminal.gwt.client.ui.ILink"
                .equals(className)) {
            return new ILink();
        } else if ("com.itmill.toolkit.terminal.gwt.client.ui.IGridLayout"
                .equals(className)) {
            return new IGridLayout();
        } else if ("com.itmill.toolkit.terminal.gwt.client.ui.ITree"
                .equals(className)) {
            return new ITree();
        } else if ("com.itmill.toolkit.terminal.gwt.client.ui.IOptionGroup"
                .equals(className)) {
            return new IOptionGroup();
        } else if ("com.itmill.toolkit.terminal.gwt.client.ui.ITwinColSelect"
                .equals(className)) {
            return new ITwinColSelect();
        } else if ("com.itmill.toolkit.terminal.gwt.client.ui.ISelect"
                .equals(className)) {
            return new ISelect();
        } else if ("com.itmill.toolkit.terminal.gwt.client.ui.IListSelect"
                .equals(className)) {
            return new IListSelect();
        } else if ("com.itmill.toolkit.terminal.gwt.client.ui.IPanel"
                .equals(className)) {
            return new IPanel();
        } else if ("com.itmill.toolkit.terminal.gwt.client.ui.ITabsheet"
                .equals(className)) {
            return new ITabsheet();
        } else if ("com.itmill.toolkit.terminal.gwt.client.ui.IEmbedded"
                .equals(className)) {
            return new IEmbedded();
        } else if ("com.itmill.toolkit.terminal.gwt.client.ui.ICustomLayout"
                .equals(className)) {
            return new ICustomLayout();
        } else if ("com.itmill.toolkit.terminal.gwt.client.ui.ITextArea"
                .equals(className)) {
            return new ITextArea();
        } else if ("com.itmill.toolkit.terminal.gwt.client.ui.IPasswordField"
                .equals(className)) {
            return new IPasswordField();
        } else if ("com.itmill.toolkit.terminal.gwt.client.ui.ITextField"
                .equals(className)) {
            return new ITextField();
        } else if ("com.itmill.toolkit.terminal.gwt.client.ui.ITablePaging"
                .equals(className)) {
            return new ITablePaging();
        } else if ("com.itmill.toolkit.terminal.gwt.client.ui.IScrollTable"
                .equals(className)) {
            return new IScrollTable();
        } else if ("com.itmill.toolkit.terminal.gwt.client.ui.IDateFieldCalendar"
                .equals(className)) {
            return new IDateFieldCalendar();
        } else if ("com.itmill.toolkit.terminal.gwt.client.ui.ITextualDate"
                .equals(className)) {
            return new ITextualDate();
        } else if ("com.itmill.toolkit.terminal.gwt.client.ui.IPopupCalendar"
                .equals(className)) {
            return new IPopupCalendar();
        } else if ("com.itmill.toolkit.terminal.gwt.client.ui.ISlider"
                .equals(className)) {
            return new ISlider();
        } else if ("com.itmill.toolkit.terminal.gwt.client.ui.IForm"
                .equals(className)) {
            return new IForm();
        } else if ("com.itmill.toolkit.terminal.gwt.client.ui.IFormLayout"
                .equals(className)) {
            return new IFormLayout();
        } else if ("com.itmill.toolkit.terminal.gwt.client.ui.IUpload"
                .equals(className)) {
            return new IUpload();
        } else if ("com.itmill.toolkit.terminal.gwt.client.ui.ISplitPanelHorizontal"
                .equals(className)) {
            return new ISplitPanelHorizontal();
        } else if ("com.itmill.toolkit.terminal.gwt.client.ui.ISplitPanelVertical"
                .equals(className)) {
            return new ISplitPanelVertical();
        } else if ("com.itmill.toolkit.terminal.gwt.client.ui.IFilterSelect"
                .equals(className)) {
            return new IFilterSelect();
        } else if ("com.itmill.toolkit.terminal.gwt.client.ui.IProgressIndicator"
                .equals(className)) {
            return new IProgressIndicator();
        } else if ("com.itmill.toolkit.terminal.gwt.client.ui.IExpandLayout"
                .equals(className)) {
            return new IExpandLayout();
        } else if ("com.itmill.toolkit.terminal.gwt.client.ui.richtextarea.IRichTextArea"
                .equals(className)) {
            return new IRichTextArea();
        }

        return new IUnknownComponent();

        /*
         * TODO: Class based impl, use when GWT supports return
         * (Widget)GWT.create(resolveWidgetClass(uidl));
         */
    }

    protected String resolveWidgetTypeName(UIDL uidl) {

        String tag = uidl.getTag();
        if ("button".equals(tag)) {
            if ("switch".equals(uidl.getStringAttribute("type"))) {
                return "com.itmill.toolkit.terminal.gwt.client.ui.ICheckBox";
            } else {
                return "com.itmill.toolkit.terminal.gwt.client.ui.IButton";
            }
        } else if ("window".equals(tag)) {
            return "com.itmill.toolkit.terminal.gwt.client.ui.IWindow";
        } else if ("orderedlayout".equals(tag)) {
            if ("horizontal".equals(uidl.getStringAttribute("orientation"))) {
                return "com.itmill.toolkit.terminal.gwt.client.ui.IOrderedLayoutHorizontal";
            } else {
                return "com.itmill.toolkit.terminal.gwt.client.ui.IOrderedLayoutVertical";
            }
        } else if ("label".equals(tag)) {
            return "com.itmill.toolkit.terminal.gwt.client.ui.ILabel";
        } else if ("link".equals(tag)) {
            return "com.itmill.toolkit.terminal.gwt.client.ui.ILink";
        } else if ("gridlayout".equals(tag)) {
            return "com.itmill.toolkit.terminal.gwt.client.ui.IGridLayout";
        } else if ("tree".equals(tag)) {
            return "com.itmill.toolkit.terminal.gwt.client.ui.ITree";
        } else if ("select".equals(tag)) {
            if (uidl.hasAttribute("type")) {
                String type = uidl.getStringAttribute("type");
                if (type.equals("twincol")) {
                    return "com.itmill.toolkit.terminal.gwt.client.ui.ITwinColSelect";
                }
                if (type.equals("optiongroup")) {
                    return "com.itmill.toolkit.terminal.gwt.client.ui.IOptionGroup";
                }
                if (type.equals("native")) {
                    return "com.itmill.toolkit.terminal.gwt.client.ui.ISelect";
                }
                if (type.equals("list")) {
                    return "com.itmill.toolkit.terminal.gwt.client.ui.IListSelect";
                }
            } else {
                if (uidl.hasAttribute("selectmode")
                        && uidl.getStringAttribute("selectmode")
                                .equals("multi")) {
                    return "com.itmill.toolkit.terminal.gwt.client.ui.IListSelect";
                } else {
                    return "com.itmill.toolkit.terminal.gwt.client.ui.IFilterSelect";
                }
            }
        } else if ("panel".equals(tag)) {
            return "com.itmill.toolkit.terminal.gwt.client.ui.IPanel";
        } else if ("tabsheet".equals(tag)) {
            return "com.itmill.toolkit.terminal.gwt.client.ui.ITabsheet";
        } else if ("embedded".equals(tag)) {
            return "com.itmill.toolkit.terminal.gwt.client.ui.IEmbedded";
        } else if ("customlayout".equals(tag)) {
            return "com.itmill.toolkit.terminal.gwt.client.ui.ICustomLayout";
        } else if ("textfield".equals(tag)) {
            if (uidl.getBooleanAttribute("richtext")) {
                return "com.itmill.toolkit.terminal.gwt.client.ui.richtextarea.IRichTextArea";
            } else if (uidl.hasAttribute("multiline")) {
                return "com.itmill.toolkit.terminal.gwt.client.ui.ITextArea";
            } else if (uidl.getBooleanAttribute("secret")) {
                return "com.itmill.toolkit.terminal.gwt.client.ui.IPasswordField";
            } else {
                return "com.itmill.toolkit.terminal.gwt.client.ui.ITextField";
            }
        } else if ("table".equals(tag)) {
            return "com.itmill.toolkit.terminal.gwt.client.ui.IScrollTable";
        } else if ("pagingtable".equals(tag)) {
            return "com.itmill.toolkit.terminal.gwt.client.ui.ITablePaging";
        } else if ("datefield".equals(tag)) {
            if (uidl.hasAttribute("style")) {
                if ("calendar".equals(uidl.getStringAttribute("style"))) {
                    return "com.itmill.toolkit.terminal.gwt.client.ui.IDateFieldCalendar";
                } else if ("text".equals(uidl.getStringAttribute("style"))) {
                    return "com.itmill.toolkit.terminal.gwt.client.ui.ITextualDate";
                }
            } else {
                return "com.itmill.toolkit.terminal.gwt.client.ui.IPopupCalendar";
            }
        } else if ("slider".equals(tag)) {
            return "com.itmill.toolkit.terminal.gwt.client.ui.ISlider";
        } else if ("form".equals(tag)) {
            return "com.itmill.toolkit.terminal.gwt.client.ui.IForm";
        } else if ("formlayout".equals(tag)) {
            return "com.itmill.toolkit.terminal.gwt.client.ui.IFormLayout";
        } else if ("upload".equals(tag)) {
            return "com.itmill.toolkit.terminal.gwt.client.ui.IUpload";
        } else if ("hsplitpanel".equals(tag)) {
            return "com.itmill.toolkit.terminal.gwt.client.ui.ISplitPanelHorizontal";
        } else if ("vsplitpanel".equals(tag)) {
            return "com.itmill.toolkit.terminal.gwt.client.ui.ISplitPanelVertical";
        } else if ("progressindicator".equals(tag)) {
            return "com.itmill.toolkit.terminal.gwt.client.ui.IProgressIndicator";
        } else if ("expandlayout".equals(tag)) {
            return "com.itmill.toolkit.terminal.gwt.client.ui.IExpandLayout";
        }

        return "com.itmill.toolkit.terminal.gwt.client.ui.IUnknownComponent";

        /*
         * TODO: use class based impl when GWT supports it
         */
    }

    public boolean isCorrectImplementation(Widget currentWidget, UIDL uidl) {
        return GWT.getTypeName(currentWidget).equals(
                resolveWidgetTypeName(uidl));
    }

}
