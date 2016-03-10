package com.vaadin.tests.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import junit.framework.TestCase;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Form;

public class SerializationTest extends TestCase {

    public void testValidators() throws Exception {
        RegexpValidator validator = new RegexpValidator(".*", "Error");
        validator.validate("aaa");
        RegexpValidator validator2 = serializeAndDeserialize(validator);
        validator2.validate("aaa");
    }

    public void testForm() throws Exception {
        Form f = new Form();
        String propertyId = "My property";
        f.addItemProperty(propertyId, new MethodProperty<Object>(new Data(),
                "dummyGetterAndSetter"));
        f.replaceWithSelect(propertyId, new Object[] { "a", "b", null },
                new String[] { "Item a", "ITem b", "Null item" });

        serializeAndDeserialize(f);

    }

    public void testIndedexContainerItemIds() throws Exception {
        IndexedContainer ic = new IndexedContainer();
        ic.addContainerProperty("prop1", String.class, null);
        Object id = ic.addItem();
        ic.getItem(id).getItemProperty("prop1").setValue("1");

        Item item2 = ic.addItem("item2");
        item2.getItemProperty("prop1").setValue("2");

        serializeAndDeserialize(ic);
    }

    public void testMethodPropertyGetter() throws Exception {
        MethodProperty<?> mp = new MethodProperty<Object>(new Data(),
                "dummyGetter");
        serializeAndDeserialize(mp);
    }

    public void testMethodPropertyGetterAndSetter() throws Exception {
        MethodProperty<?> mp = new MethodProperty<Object>(new Data(),
                "dummyGetterAndSetter");
        serializeAndDeserialize(mp);
    }

    public void testMethodPropertyInt() throws Exception {
        MethodProperty<?> mp = new MethodProperty<Object>(new Data(),
                "dummyInt");
        serializeAndDeserialize(mp);
    }

    public void testVaadinSession() throws Exception {
        VaadinSession session = new VaadinSession(null);

        session = serializeAndDeserialize(session);

        assertNotNull(
                "Pending access queue was not recreated after deserialization",
                session.getPendingAccessQueue());
    }

    private static <S extends Serializable> S serializeAndDeserialize(S s)
            throws IOException, ClassNotFoundException {
        // Serialize and deserialize

        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bs);
        out.writeObject(s);
        byte[] data = bs.toByteArray();
        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(
                data));
        @SuppressWarnings("unchecked")
        S s2 = (S) in.readObject();

        // using special toString(Object) method to avoid calling
        // Property.toString(), which will be temporarily disabled
        // TODO This is hilariously broken (#12723)
        if (s.equals(s2)) {
            System.out.println(toString(s) + " equals " + toString(s2));
        } else {
            System.out.println(toString(s) + " does NOT equal " + toString(s2));
        }

        return s2;
    }

    private static String toString(Object o) {
        if (o instanceof Property) {
            return String.valueOf(((Property<?>) o).getValue());
        } else {
            return String.valueOf(o);
        }
    }

    public static class Data implements Serializable {
        private String dummyGetter;
        private String dummyGetterAndSetter;
        private int dummyInt;

        public String getDummyGetterAndSetter() {
            return dummyGetterAndSetter;
        }

        public void setDummyGetterAndSetter(String dummyGetterAndSetter) {
            this.dummyGetterAndSetter = dummyGetterAndSetter;
        }

        public int getDummyInt() {
            return dummyInt;
        }

        public void setDummyInt(int dummyInt) {
            this.dummyInt = dummyInt;
        }

        public String getDummyGetter() {
            return dummyGetter;
        }
    }
}
