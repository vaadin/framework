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
package com.vaadin.tests.widgetset.server.csrf.ui;

import com.vaadin.shared.ApplicationConstants;

/**
 * Test the CSRF Token issue.
 * 
 * @since
 * @author Vaadin Ltd
 */
public class CsrfTokenDisabledTest extends AbstractCsrfTokenUITest {

    @Override
    protected boolean compareMessage(TokenGroup tokenGroup1,
            TokenGroup tokenGroup2) {

        return tokenGroup1.clientToken
                .equals(ApplicationConstants.CSRF_TOKEN_DEFAULT_VALUE)
                && isUndefined(tokenGroup1.tokenReceivedFromServer)
                && isUndefined(tokenGroup1.tokenSentToServer)
                && tokenGroup2.clientToken
                        .equals(ApplicationConstants.CSRF_TOKEN_DEFAULT_VALUE)
                && isUndefined(tokenGroup2.tokenReceivedFromServer)
                // This is it actually, no token sent to the server.
                && isNull(tokenGroup2.tokenSentToServer);
    }

}
