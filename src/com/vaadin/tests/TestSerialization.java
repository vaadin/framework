package com.vaadin.tests;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import junit.framework.TestCase;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.ui.Form;

public class TestSerialization extends TestCase {

    public void testForm() throws Exception {
        Form f = new Form();
        String propertyId = "My property";
        f.addItemProperty(propertyId, new MethodProperty(new Data(),
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
        MethodProperty mp = new MethodProperty(new Data(), "dummyGetter");
        serializeAndDeserialize(mp);
    }

    public void testMethodPropertyGetterAndSetter() throws Exception {
        MethodProperty mp = new MethodProperty(new Data(),
                "dummyGetterAndSetter");
        serializeAndDeserialize(mp);
    }

    public void testMethodPropertyInt() throws Exception {
        MethodProperty mp = new MethodProperty(new Data(), "dummyInt");
        serializeAndDeserialize(mp);
    }

    private static void serializeAndDeserialize(Serializable s)
            throws IOException, ClassNotFoundException {
        // Serialize and deserialize

        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bs);
        out.writeObject(s);
        byte[] data = bs.toByteArray();
        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(
                data));
        Serializable s2 = (Serializable) in.readObject();

        if (s.equals(s2)) {
            System.out.println(s + " equals " + s2);
        } else {
            System.out.println(s + " does NOT equal " + s2);
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