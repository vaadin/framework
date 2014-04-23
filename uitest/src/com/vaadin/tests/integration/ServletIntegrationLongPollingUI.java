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
