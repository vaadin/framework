/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;

public class VUpload extends FormPanel implements Paintable,
        SubmitCompleteHandler, SubmitHandler {

    private final class MyFileUpload extends FileUpload {
        @Override
        public void onBrowserEvent(Event event) {
            super.onBrowserEvent(event);
            if (event.getTypeInt() == Event.ONCHANGE) {
                if (immediate && fu.getFilename() != null
                        && !"".equals(fu.getFilename())) {
                    submit();
                }
            } else if (event.getTypeInt() == Event.ONFOCUS) {
                // IE and user has clicked on hidden textarea part of upload
                // field. Manually open file selector, other browsers do it by
                // default.
                fireNativeClick(fu.getElement());
                // also remove focus to enable hack if user presses cancel
                // button
                fireNativeBlur(fu.getElement());
            }
        }
    }

    public static final String CLASSNAME = "v-upload";

    /**
     * FileUpload component that opens native OS dialog to select file.
     */
    FileUpload fu = new MyFileUpload();

    Panel panel = new FlowPanel();

    ApplicationConnection client;

    private String paintableId;

    /**
     * Button that initiates uploading
     */
    private final VButton submitButton;

    /**
     * When expecting big files, programmer may initiate some UI changes when
     * uploading the file starts. Bit after submitting file we'll visit the
     * server to check possible changes.
     */
    private Timer t;

    /**
     * some browsers tries to send form twice if submit is called in button
     * click handler, some don't submit at all without it, so we need to track
     * if form is already being submitted
     */
    private boolean submitted = false;

    private boolean enabled = true;

    private boolean immediate;

    private Hidden maxfilesize = new Hidden();

    public VUpload() {
        super();
        setEncoding(FormPanel.ENCODING_MULTIPART);
        setMethod(FormPanel.METHOD_POST);

        setWidget(panel);
        panel.add(maxfilesize);
        panel.add(fu);
        submitButton = new VButton();
        submitButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                if (immediate) {
                    // fire click on upload (eg. focused button and hit space)
                    fireNativeClick(fu.getElement());
                } else {
                    submit();
                }
            }
        });
        panel.add(submitButton);

        addSubmitCompleteHandler(this);
        addSubmitHandler(this);

        setStyleName(CLASSNAME);
    }

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        if (client.updateComponent(this, uidl, true)) {
            return;
        }
        setImmediate(uidl.getBooleanAttribute("immediate"));
        this.client = client;
        paintableId = uidl.getId();
        setAction(client.getAppUri());
        submitButton.setText(uidl.getStringAttribute("buttoncaption"));
        fu.setName(paintableId + "_file");

        if (uidl.hasAttribute("disabled") || uidl.hasAttribute("readonly")) {
            disableUpload();
        } else if (uidl.getBooleanAttribute("state")) {
            enableUploaod();
        }
    }

    private void setImmediate(boolean booleanAttribute) {
        if (immediate != booleanAttribute) {
            immediate = booleanAttribute;
            if (immediate) {
                fu.sinkEvents(Event.ONCHANGE);
                fu.sinkEvents(Event.ONFOCUS);
            }
        }
        setStyleName(getElement(), CLASSNAME + "-immediate", immediate);
    }

    private static native void fireNativeClick(Element element)
    /*-{
        element.click();
    }-*/;

    private static native void fireNativeBlur(Element element)
    /*-{
        element.blur();
    }-*/;

    protected void disableUpload() {
        submitButton.setEnabled(false);
        // fu.getElement().setPropertyBoolean("disabled", true);
        enabled = false;
    }

    protected void enableUploaod() {
        submitButton.setEnabled(true);
        // fu.getElement().setPropertyBoolean("disabled", false);
        enabled = true;
    }

    /**
     * Re-creates file input field and populates panel. This is needed as we
     * want to clear existing values from our current file input field.
     */
    private void rebuildPanel() {
        panel.remove(submitButton);
        panel.remove(fu);
        fu = new MyFileUpload();
        fu.setName(paintableId + "_file");
        // fu.getElement().setPropertyBoolean("disabled", !enabled);
        panel.add(fu);
        panel.add(submitButton);
        if (immediate) {
            fu.sinkEvents(Event.ONCHANGE);
        }
    }

    public void onSubmitComplete(SubmitCompleteEvent event) {
        if (client != null) {
            if (t != null) {
                t.cancel();
            }
            ApplicationConnection.getConsole().log("Submit complete");
            client.sendPendingVariableChanges();
        }

        rebuildPanel();

        submitted = false;
        enableUploaod();
    }

    public void onSubmit(SubmitEvent event) {
        if (fu.getFilename().length() == 0 || submitted || !enabled) {
            event.cancel();
            ApplicationConnection
                    .getConsole()
                    .log(
                            "Submit cancelled (disabled, no file or already submitted)");
            return;
        }
        // flush possibly pending variable changes, so they will be handled
        // before upload
        client.sendPendingVariableChanges();

        submitted = true;
        ApplicationConnection.getConsole().log("Submitted form");

        disableUpload();

        /*
         * Visit server a moment after upload has started to see possible
         * changes from UploadStarted event. Will be cleared on complete.
         */
        t = new Timer() {
            @Override
            public void run() {
                client.sendPendingVariableChanges();
            }
        };
        t.schedule(800);
    }

}
