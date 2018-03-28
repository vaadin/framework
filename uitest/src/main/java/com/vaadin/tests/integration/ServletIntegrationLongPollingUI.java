package com.vaadin.tests.integration;

import com.vaadin.annotations.Push;
import com.vaadin.shared.ui.ui.Transport;

/**
 * Server test which uses long polling
 *
 * @since 7.1
 * @author Vaadin Ltd
 */
@Push(transport = Transport.LONG_POLLING)
public class ServletIntegrationLongPollingUI extends ServletIntegrationUI {

}
