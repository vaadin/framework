/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.FormElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.VConsole;

/**
 * 
 * Note, we are not using GWT FormPanel as we want to listen submitcomplete
 * events even though the upload component is already detached.
 * 
 */
public class VUpload extends SimplePanel implements Paintable {

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

    UploadIFrameOnloadStrategy onloadstrategy = GWT
            .create(UploadIFrameOnloadStrategy.class);

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

    private FormElement element;

    private com.google.gwt.dom.client.Element synthesizedFrame;

    private int nextUploadId;

    public VUpload() {
        super(com.google.gwt.dom.client.Document.get().createFormElement());

        element = getElement().cast();
        setEncoding(getElement(), FormPanel.ENCODING_MULTIPART);
        element.setMethod(FormPanel.METHOD_POST);

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

        setStyleName(CLASSNAME);
    }

    private static native void setEncoding(Element form, String encoding)
    /*-{
      form.enctype = encoding;
      // For IE6
      form.encoding = encoding;
    }-*/;

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        if (client.updateComponent(this, uidl, true)) {
            return;
        }
        if (uidl.hasAttribute("notStarted")) {
            t.schedule(400);
            return;
        }
        if(uidl.hasAttribute("forceSubmit")) {
            element.submit();
            return;
        }
        setImmediate(uidl.getBooleanAttribute("immediate"));
        this.client = client;
        paintableId = uidl.getId();
        nextUploadId = uidl.getIntAttribute("nextid");
        element.setAction(uidl.getStringVariable("action"));
        if(uidl.hasAttribute("buttoncaption")) {
            submitButton.setText(uidl.getStringAttribute("buttoncaption"));
            submitButton.setVisible(true);
        } else {
            submitButton.setVisible(false);
        }
        fu.setName(paintableId + "_file");

        if (uidl.hasAttribute("disabled") || uidl.hasAttribute("readonly")) {
            disableUpload();
        } else if (!uidl.getBooleanAttribute("state")) {
            // Enable the button only if an upload is not in progress
            enableUpload();
            ensureTargetFrame();
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
        if (!submitted) {
            // Cannot disable the fileupload while submitting or the file won't
            // be submitted at all
            fu.getElement().setPropertyBoolean("disabled", true);
        }
        enabled = false;
    }

    protected void enableUpload() {
        submitButton.setEnabled(true);
        fu.getElement().setPropertyBoolean("disabled", false);
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
        fu.getElement().setPropertyBoolean("disabled", !enabled);
        panel.add(fu);
        panel.add(submitButton);
        if (immediate) {
            fu.sinkEvents(Event.ONCHANGE);
        }
    }

    /**
     * Called by JSNI (hooked via {@link #onloadstrategy})
     */
    private void onSubmitComplete() {
        /* Needs to be run dereferred to avoid various browser issues. */
        Scheduler.get().scheduleDeferred(new Command() {
            public void execute() {
                if (client != null) {
                    if (t != null) {
                        t.cancel();
                    }
                    VConsole.log("VUpload:Submit complete");
                    client.sendPendingVariableChanges();
                }

                rebuildPanel();

                submitted = false;
                enableUpload();
                if (!isAttached()) {
                    /*
                     * Upload is complete when upload is already abandoned.
                     */
                    cleanTargetFrame();
                }
            }
        });
    }

    private void submit() {
        if (fu.getFilename().length() == 0 || submitted || !enabled) {
            VConsole.log("Submit cancelled (disabled, no file or already submitted)");
            return;
        }
        // flush possibly pending variable changes, so they will be handled
        // before upload
        client.sendPendingVariableChanges();

        element.submit();
        submitted = true;
        VConsole.log("Submitted form");

        disableUpload();

        /*
         * Visit server a moment after upload has started to see possible
         * changes from UploadStarted event. Will be cleared on complete.
         */
        t = new Timer() {
            @Override
            public void run() {
                VConsole.log("Visiting server to see if upload started event changed UI.");
                client.updateVariable(paintableId, "pollForStart",
                        nextUploadId, true);
            }
        };
        t.schedule(800);
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        if (client != null) {
            ensureTargetFrame();
        }
    }

    private void ensureTargetFrame() {
        if (synthesizedFrame == null) {
            // Attach a hidden IFrame to the form. This is the target iframe to
            // which
            // the form will be submitted. We have to create the iframe using
            // innerHTML,
            // because setting an iframe's 'name' property dynamically doesn't
            // work on
            // most browsers.
            DivElement dummy = Document.get().createDivElement();
            dummy.setInnerHTML("<iframe src=\"javascript:''\" name='"
                    + getFrameName()
                    + "' style='position:absolute;width:0;height:0;border:0'>");
            synthesizedFrame = dummy.getFirstChildElement();
            Document.get().getBody().appendChild(synthesizedFrame);
            element.setTarget(getFrameName());
            onloadstrategy.hookEvents(synthesizedFrame, this);
        }
    }

    private String getFrameName() {
        return paintableId + "_TGT_FRAME";
    }

    @Override
    protected void onDetach() {
        super.onDetach();
        if (!submitted) {
            cleanTargetFrame();
        }
    }

    private void cleanTargetFrame() {
        if (synthesizedFrame != null) {
            Document.get().getBody().removeChild(synthesizedFrame);
            onloadstrategy.unHookEvents(synthesizedFrame);
            synthesizedFrame = null;
        }
    }

}
