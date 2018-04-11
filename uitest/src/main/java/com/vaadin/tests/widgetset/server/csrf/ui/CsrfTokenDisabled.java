package com.vaadin.tests.widgetset.server.csrf.ui;

import com.vaadin.launcher.CustomDeploymentConfiguration;
import com.vaadin.launcher.CustomDeploymentConfiguration.Conf;

/**
 * When the disable-xsrf-protection is true csrfToken is not present anymore
 * with the requests.<br/>
 * This is useful mostly when the client is not Vaadin and so it will not push
 * the parameter anyway. So now the server knows how to deal the issue if the
 * csrfToken is not present.
 *
 * @author Vaadin Ltd
 */
@SuppressWarnings("serial")
@CustomDeploymentConfiguration({
        @Conf(name = "disable-xsrf-protection", value = "true") })
public class CsrfTokenDisabled extends AbstractCsrfTokenUI {

}
