/*
 * Copyright 2000-2021 Vaadin Ltd.
 *
 * Licensed under the Commercial Vaadin Developer License version 4.0 (CVDLv4); 
 * you may not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 * https://vaadin.com/license/cvdl-4.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
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
 * @since
 * @author Vaadin Ltd
 */
@SuppressWarnings("serial")
@CustomDeploymentConfiguration({
        @Conf(name = "disable-xsrf-protection", value = "true") })
public class CsrfTokenDisabled extends AbstractCsrfTokenUI {

}
