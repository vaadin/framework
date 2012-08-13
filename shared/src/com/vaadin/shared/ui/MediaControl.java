/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.shared.ui;

import com.vaadin.shared.communication.ClientRpc;

/**
 * Server to client RPC interface for controlling playback of the media.
 * 
 * @since 7.0
 */
public interface MediaControl extends ClientRpc {
    /**
     * Start playing the media.
     */
    public void play();

    /**
     * Pause playback of the media.
     */
    public void pause();
}