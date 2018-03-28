package com.vaadin.client.communication;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

/**
 *
 * @since 7.7
 * @author Vaadin Ltd
 */
public class ServerMessageHandlerTest {

    @Test
    public void unwrapValidJson() {
        String payload = "{'foo': 'bar'}";
        assertEquals(payload,
                MessageHandler.stripJSONWrapping("for(;;);[" + payload + "]"));

    }

    @Test
    public void unwrapUnwrappedJson() {
        String payload = "{'foo': 'bar'}";
        assertNull(MessageHandler.stripJSONWrapping(payload));

    }

    @Test
    public void unwrapNull() {
        assertNull(MessageHandler.stripJSONWrapping(null));

    }

    @Test
    public void unwrapEmpty() {
        assertNull(MessageHandler.stripJSONWrapping(""));

    }
}
