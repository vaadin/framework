package com.vaadin.tests.integration;

import com.vaadin.annotations.Push;
import com.vaadin.shared.ui.ui.Transport;

/**
 * Server test which uses streaming
 *
 * @since 7.1
 * @author Vaadin Ltd
 */
@Push(transport = Transport.STREAMING)
public class ServletIntegrationStreamingUI extends ServletIntegrationUI {

}
