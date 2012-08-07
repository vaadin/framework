/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import com.vaadin.shared.communication.URLReference;
import com.vaadin.shared.ui.AbstractMediaState;
import com.vaadin.shared.ui.MediaControl;
import com.vaadin.terminal.gwt.client.Util;
import com.vaadin.terminal.gwt.client.communication.StateChangeEvent;

public abstract class MediaBaseConnector extends AbstractComponentConnector {

    @Override
    protected void init() {
        super.init();

        registerRpc(MediaControl.class, new MediaControl() {
            @Override
            public void play() {
                getWidget().play();
            }

            @Override
            public void pause() {
                getWidget().pause();
            }
        });
    }

    @Override
    public AbstractMediaState getState() {
        return (AbstractMediaState) super.getState();
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);

        getWidget().setControls(getState().isShowControls());
        getWidget().setAutoplay(getState().isAutoplay());
        getWidget().setMuted(getState().isMuted());
        for (int i = 0; i < getState().getSources().size(); i++) {
            URLReference source = getState().getSources().get(i);
            String sourceType = getState().getSourceTypes().get(i);
            getWidget().addSource(source.getURL(), sourceType);
        }
        setAltText(getState().getAltText());
    }

    @Override
    public VMediaBase getWidget() {
        return (VMediaBase) super.getWidget();
    }

    private void setAltText(String altText) {

        if (altText == null || "".equals(altText)) {
            altText = getDefaultAltHtml();
        } else if (!getState().isHtmlContentAllowed()) {
            altText = Util.escapeHTML(altText);
        }
        getWidget().setAltText(altText);
    }

    /**
     * @return the default HTML to show users with browsers that do not support
     *         HTML5 media markup.
     */
    protected abstract String getDefaultAltHtml();

}
