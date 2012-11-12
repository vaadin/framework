/*
 * Copyright 2011 Vaadin Ltd.
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

package com.vaadin.client.ui;

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
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.BrowserInfo;
import com.vaadin.client.VConsole;
import com.vaadin.client.ui.upload.UploadIFrameOnloadStrategy;

/**
 * 
 * Note, we are not using GWT FormPanel as we want to listen submitcomplete
 * events even though the upload component is already detached.
 * 
 */
public class VUpload extends SimplePanel {

    private final class MyFileUpload extends FileUpload {
        @Override
        public void onBrowserEvent(Event event) {
            super.onBrowserEvent(event);
            if (event.getTypeInt() == Event.ONCHANGE) {
                if (immediate && fu.getFilename() != null
                        && !"".equals(fu.getFilename())) {
                    submit();
                }
            } else if (BrowserInfo.get().isIE()
                    && event.getTypeInt() == Event.ONFOCUS) {
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
     * <p>
     * For internal use only. May be removed or replaced in the future.
     */
    public FileUpload fu = new MyFileUpload();

    Panel panel = new FlowPanel();

    UploadIFrameOnloadStrategy onloadstrategy = GWT
            .create(UploadIFrameOnloadStrategy.class);

    /** For internal use only. May be removed or replaced in the future. */
    public ApplicationConnection client;

    /** For internal use only. May be removed or replaced in the future. */
    public String paintableId;

    /**
     * Button that initiates uploading.
     * <p>
     * For internal use only. May be removed or replaced in the future.
     */
    public final VButton submitButton;

    /**
     * When expecting big files, programmer may initiate some UI changes when
     * uploading the file starts. Bit after submitting file we'll visit the
     * server to check possible changes.
     * <p>
     * For internal use only. May be removed or replaced in the future.
     */
    public Timer t;

    /**
     * some browsers tries to send form twice if submit is called in button
     * click handler, some don't submit at all without it, so we need to track
     * if form is already being submitted
     */
    private boolean submitted = false;

    private boolean enabled = true;

    private boolean immediate;

    private Hidden maxfilesize = new Hidden();

    /** For internal use only. May be removed or replaced in the future. */
    public FormElement element;

    private com.google.gwt.dom.client.Element synthesizedFrame;

    /** For internal use only. May be removed or replaced in the future. */
    public int nextUploadId;

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
            @Override
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
    }-*/;

    /** For internal use only. May be removed or replaced in the future. */
    public void setImmediate(boolean booleanAttribute) {
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

    /** For internal use only. May be removed or replaced in the future. */
    public void disableUpload() {
        submitButton.setEnabled(false);
        if (!submitted) {
            // Cannot disable the fileupload while submitting or the file won't
            // be submitted at all
            fu.getElement().setPropertyBoolean("disabled", true);
        }
        enabled = false;
    }

    /** For internal use only. May be removed or replaced in the future. */
    public void enableUpload() {
        submitButton.setEnabled(true);
        fu.getElement().setPropertyBoolean("disabled", false);
        enabled = true;
        if (submitted) {
            /*
             * An old request is still in progress (most likely cancelled),
             * ditching that target frame to make it possible to send a new
             * file. A new target frame is created later."
             */
            cleanTargetFrame();
            submitted = false;
        }
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
            @Override
            public void execute() {
                if (submitted) {
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
            }
        });
    }

    /** For internal use only. May be removed or replaced in the future. */
    public void submit() {
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

    /** For internal use only. May be removed or replaced in the future. */
    public void ensureTargetFrame() {
        if (synthesizedFrame == null) {
            // Attach a hidden IFrame to the form. This is the target iframe to
            // which the form will be submitted. We have to create the iframe
            // using innerHTML, because setting an iframe's 'name' property
            // dynamically doesn't work on most browsers.
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
