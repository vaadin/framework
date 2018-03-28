package com.vaadin.tests.widgetset.server.csrf.ui;

/**
 * Test the CSRF Token issue.
 *
 * @since
 * @author Vaadin Ltd
 */
public class CsrfTokenEnabledTest extends AbstractCsrfTokenUITest {

    @Override
    protected boolean compareMessage(TokenGroup tokenGroup1,
            TokenGroup tokenGroup2) {

        return tokenGroup1.clientToken.equals(tokenGroup2.clientToken)
                // Valid token received and set on the client
                && tokenGroup1.clientToken
                        .equals(tokenGroup1.tokenReceivedFromServer)
                // No token sent yet to the server.
                && isUndefined(tokenGroup1.tokenSentToServer)
                // Token is sent to the server.
                && tokenGroup2.clientToken.equals(tokenGroup2.tokenSentToServer)
                // And no more token received from the server.
                && isUndefined(tokenGroup2.tokenReceivedFromServer);
    }

}
