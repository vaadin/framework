package com.vaadin.tests.widgetset.server.csrf.ui;

import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.vaadin.tests.tb3.MultiBrowserTest;
import com.vaadin.tests.widgetset.client.csrf.CsrfButtonConnector;

public abstract class AbstractCsrfTokenUITest extends MultiBrowserTest {

    static final Logger LOGGER = Logger
            .getLogger(AbstractCsrfTokenUITest.class.getName());

    @Test
    public void testTokens() {
        openTestURL();

        final By debugButton = By.id(CsrfButtonConnector.ID);

        final String debugMessage1 = getDriver().findElement(debugButton)
                .getText();

        getDriver().findElement(By.id(CsrfTokenDisabled.PRESS_ID)).click();

        waitUntil(new ExpectedCondition<Boolean>() {

            @Override
            public Boolean apply(WebDriver input) {
                getDriver().findElement(debugButton).click();
                String debugMessage2 = input.findElement(debugButton).getText();

                LOGGER.log(Level.INFO, "1: " + debugMessage1);
                LOGGER.log(Level.INFO, "2: " + debugMessage2);

                if (!debugMessage1.equals(debugMessage2)) {

                    compareMessage(split(debugMessage1), split(debugMessage2));

                    LOGGER.log(Level.INFO, "DONE");

                    return true;

                } else {
                    return false;
                }
            }
        });
    }

    private TokenGroup split(String debugMessage) {
        StringTokenizer tokenizer = new StringTokenizer(debugMessage, ", \"");

        return new TokenGroup(tokenizer.nextToken(), tokenizer.nextToken(),
                tokenizer.nextToken());
    }

    /*
     * Just implement this.
     */
    protected abstract boolean compareMessage(TokenGroup tokenGroup1,
            TokenGroup tokenGroup2);

    boolean isNullOrUndefined(String value) {
        return isNull(value) || isUndefined(value);
    }

    boolean isUndefined(String value) {
        return value.equals("undefined");
    }

    boolean isNull(String value) {
        return value.equals("null");
    }

    /*
     * Wrapps all tokens from the client app.
     */
    static class TokenGroup {

        public final String clientToken;

        public final String tokenReceivedFromServer;

        public final String tokenSentToServer;

        public TokenGroup(String clientToken, String tokenReceivedFromServer,
                String tokenSentToServer) {
            this.clientToken = clientToken;
            this.tokenReceivedFromServer = tokenReceivedFromServer;
            this.tokenSentToServer = tokenSentToServer;
        }

    }

}
