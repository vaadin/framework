package com.vaadin.tests.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.junit.Test;

import com.vaadin.server.AbstractJavaScriptExtension;
import com.vaadin.ui.JavaScriptFunction;

import elemental.json.JsonArray;

public class SerializationTest {

    public static class JSE extends AbstractJavaScriptExtension {
        {
            addFunction("foo", new JavaScriptFunction() {
                @Override
                public void call(JsonArray arguments) {
                    System.out.println("Foo called");
                }
            });
        }
    }

    @Test
    public void testJSExtension() throws Exception {
        serializeAndDeserialize(new JSE());
    }

    private static <S extends Serializable> S serializeAndDeserialize(S s)
            throws IOException, ClassNotFoundException {
        // Serialize and deserialize

        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bs);
        out.writeObject(s);
        byte[] data = bs.toByteArray();
        ObjectInputStream in = new ObjectInputStream(
                new ByteArrayInputStream(data));
        @SuppressWarnings("unchecked")
        S s2 = (S) in.readObject();

        if (s.equals(s2)) {
            System.out.println(s + " equals " + s2);
        } else {
            System.out.println(s + " does NOT equal " + s2);
        }

        return s2;
    }
}
