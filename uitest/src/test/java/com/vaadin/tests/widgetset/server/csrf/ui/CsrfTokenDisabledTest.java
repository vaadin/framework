package com.vaadin.tests.widgetset.server.csrf.ui;

import com.vaadin.shared.ApplicationConstants;

/**
 * Test the CSRF Token issue.
 *
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
