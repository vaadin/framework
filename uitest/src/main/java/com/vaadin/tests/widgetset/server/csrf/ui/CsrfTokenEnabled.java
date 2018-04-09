package com.vaadin.tests.widgetset.server.csrf.ui;

import com.vaadin.launcher.CustomDeploymentConfiguration;
import com.vaadin.launcher.CustomDeploymentConfiguration.Conf;

@SuppressWarnings("serial")
@CustomDeploymentConfiguration({
        @Conf(name = "disable-xsrf-protection", value = "false") })
public class CsrfTokenEnabled extends AbstractCsrfTokenUI {

}
