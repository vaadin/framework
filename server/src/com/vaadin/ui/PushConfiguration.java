/*
 * Copyright 2000-2014 Vaadin Ltd.
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

package com.vaadin.ui;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;

import com.vaadin.server.VaadinSession;
import com.vaadin.server.communication.AtmospherePushConnection;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.shared.ui.ui.UIState.PushConfigurationState;

/**
 * Provides method for configuring the push channel.
 * 
 * @since 7.1
 * @author Vaadin Ltd
 */
public interface PushConfiguration extends Serializable {

    /**
     * Returns the mode of bidirectional ("push") communication that is used.
     * 
     * @return The push mode.
     */
    public PushMode getPushMode();

    /**
     * Sets the mode of bidirectional ("push") communication that should be
     * used.
     * <p>
     * Add-on developers should note that this method is only meant for the
     * application developer. An add-on should not set the push mode directly,
     * rather instruct the user to set it.
     * </p>
     * 
     * @param pushMode
     *            The push mode to use.
     * 
     * @throws IllegalArgumentException
     *             if the argument is null.
     * @throws IllegalStateException
     *             if push support is not available.
     */
    public void setPushMode(PushMode pushMode);

    /**
     * Returns the primary transport type for push.
     * <p>
     * Note that if you set the transport type using
     * {@link #setParameter(String, String)} to an unsupported type this method
     * will return null. Supported types are defined by {@link Transport}.
     * 
     * @return The primary transport type
     */
    public Transport getTransport();

    /**
     * Sets the primary transport type for push.
     * <p>
     * Note that the new transport type will not be used until the push channel
     * is disconnected and reconnected if already active.
     * 
     * @param transport
     *            The primary transport type
     */
    public void setTransport(Transport transport);

    /**
     * Returns the fallback transport type for push.
     * <p>
     * Note that if you set the transport type using
     * {@link #setParameter(String, String)} to an unsupported type this method
     * will return null. Supported types are defined by {@link Transport}.
     * 
     * @return The fallback transport type
     */
    public Transport getFallbackTransport();

    /**
     * Sets the fallback transport type for push.
     * <p>
     * Note that the new transport type will not be used until the push channel
     * is disconnected and reconnected if already active.
     * 
     * @param fallbackTransport
     *            The fallback transport type
     */
    public void setFallbackTransport(Transport fallbackTransport);

    /**
     * Returns the given parameter, if set.
     * <p>
     * This method provides low level access to push parameters and is typically
     * not needed for normal application development.
     * 
     * @since 7.1
     * @param parameter
     *            The parameter name
     * @return The parameter value or null if not set
     */
    public String getParameter(String parameter);

    /**
     * Returns the parameters which have been defined.
     * 
     * @since 7.1
     * @return A collection of parameter names
     */
    public Collection<String> getParameterNames();

    /**
     * Sets the given parameter.
     * <p>
     * This method provides low level access to push parameters and is typically
     * not needed for normal application development.
     * 
     * @since 7.1
     * @param parameter
     *            The parameter name
     * @param value
     *            The value
     */
    public void setParameter(String parameter, String value);

}

class PushConfigurationImpl implements PushConfiguration {
    private UI ui;

    public PushConfigurationImpl(UI ui) {
        this.ui = ui;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.PushConfiguration#getPushMode()
     */
    @Override
    public PushMode getPushMode() {
        return getState(false).mode;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.ui.PushConfiguration#setPushMode(com.vaadin.shared.communication
     * .PushMode)
     */
    @Override
    public void setPushMode(PushMode pushMode) {
        if (pushMode == null) {
            throw new IllegalArgumentException("Push mode cannot be null");
        }

        VaadinSession session = ui.getSession();

        if (session == null) {
            throw new UIDetachedException(
                    "Cannot set the push mode for a detached UI");
        }

        assert session.hasLock();

        if (pushMode.isEnabled() && !session.getService().ensurePushAvailable()) {
            throw new IllegalStateException(
                    "Push is not available. See previous log messages for more information.");
        }

        PushMode oldMode = getState().mode;
        if (oldMode != pushMode) {
            getState().mode = pushMode;

            if (!oldMode.isEnabled() && pushMode.isEnabled()) {
                // The push connection is initially in a disconnected state;
                // the client will establish the connection
                ui.setPushConnection(new AtmospherePushConnection(ui));
            }
            // Nothing to do here if disabling push;
            // the client will close the connection
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.PushConfiguration#getTransport()
     */
    @Override
    public Transport getTransport() {
        try {
            return Transport
                    .valueOf(getParameter(PushConfigurationState.TRANSPORT_PARAM));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.ui.PushConfiguration#setTransport(com.vaadin.shared.ui.ui.
     * Transport)
     */
    @Override
    public void setTransport(Transport transport) {
        setParameter(PushConfigurationState.TRANSPORT_PARAM,
                transport.getIdentifier());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.PushConfiguration#getFallbackTransport()
     */
    @Override
    public Transport getFallbackTransport() {
        try {
            return Transport
                    .valueOf(getParameter(PushConfigurationState.FALLBACK_TRANSPORT_PARAM));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.ui.PushConfiguration#setFallbackTransport(com.vaadin.shared
     * .ui.ui.Transport)
     */
    @Override
    public void setFallbackTransport(Transport fallbackTransport) {
        setParameter(PushConfigurationState.FALLBACK_TRANSPORT_PARAM,
                fallbackTransport.getIdentifier());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.PushConfiguration#getParameter(java.lang.String)
     */
    @Override
    public String getParameter(String parameter) {
        return getState(false).parameters.get(parameter);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.PushConfiguration#setParameter(java.lang.String,
     * java.lang.String)
     */
    @Override
    public void setParameter(String parameter, String value) {
        getState().parameters.put(parameter, value);

    }

    private PushConfigurationState getState() {
        return ui.getState().pushConfiguration;
    }

    private PushConfigurationState getState(boolean markAsDirty) {
        return ui.getState(markAsDirty).pushConfiguration;
    }

    @Override
    public Collection<String> getParameterNames() {
        return Collections.unmodifiableCollection(getState(false).parameters
                .keySet());
    }

}
