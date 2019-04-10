package com.vaadin.server;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Type;

import org.junit.Test;

import com.vaadin.server.communication.JSONSerializer;
import com.vaadin.ui.ConnectorTracker;

import elemental.json.JsonValue;

public class CustomJSONSerializerTest {
    
    public static class Foo {

    }

    public static class FooSerializer implements JSONSerializer<Foo> {

        @Override
        public Foo deserialize(Type type, JsonValue jsonValue,
                ConnectorTracker connectorTracker) {
            return null;
        }

        @Override
        public JsonValue serialize(Foo value,
                ConnectorTracker connectorTracker) {
            return null;
        }

    }

    @Test
    public void testMultipleRegistration() {
        boolean thrown = false;
        try {
            JsonCodec.setCustomSerializer(Foo.class, new FooSerializer());
            JsonCodec.setCustomSerializer(Foo.class, new FooSerializer());
        } catch (IllegalStateException ise) {
            thrown = true;
        } finally {
            JsonCodec.setCustomSerializer(Foo.class, null);
        }
        assertTrue("Multiple serializer registrations for one class "
                + "should throw an IllegalStateException", thrown);

    }

}
