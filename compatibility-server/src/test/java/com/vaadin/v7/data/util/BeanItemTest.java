package com.vaadin.v7.data.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

import com.vaadin.v7.data.Property;

/**
 * Test BeanItem specific features.
 *
 * Only public API is tested, not the methods with package visibility.
 *
 * See also {@link PropertySetItemTest}, which tests the base class.
 */
public class BeanItemTest {

    @SuppressWarnings("unused")
    protected static class MySuperClass {
        private int superPrivate = 1;
        private int superPrivate2 = 2;
        protected double superProtected = 3.0;
        private double superProtected2 = 4.0;
        public boolean superPublic = true;
        private boolean superPublic2 = true;

        public int getSuperPrivate() {
            return superPrivate;
        }

        public void setSuperPrivate(int superPrivate) {
            this.superPrivate = superPrivate;
        }

        public double getSuperProtected() {
            return superProtected;
        }

        public void setSuperProtected(double superProtected) {
            this.superProtected = superProtected;
        }

        public boolean isSuperPublic() {
            return superPublic;
        }

        public void setSuperPublic(boolean superPublic) {
            this.superPublic = superPublic;
        }

    }

    protected static class MyClass extends MySuperClass {
        private String name;
        public int value = 123;

        public MyClass(String name) {
            this.name = name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setNoField(String name) {
        }

        public String getNoField() {
            return "no field backing this setter";
        }

        public String getName2() {
            return name;
        }
    }

    protected static class MyClass2 extends MyClass {
        public MyClass2(String name) {
            super(name);
        }

        @Override
        public void setName(String name) {
            super.setName(name + "2");
        }

        @Override
        public String getName() {
            return super.getName() + "2";
        }

        @Override
        public String getName2() {
            return super.getName();
        }

        public void setName2(String name) {
            super.setName(name);
        }
    }

    protected static interface MySuperInterface {
        public int getSuper1();

        public void setSuper1(int i);

        public int getOverride();
    }

    protected static interface MySuperInterface2 {
        public int getSuper2();
    }

    protected static class Generic<T> {

        public T getProperty() {
            return null;
        }

        public void setProperty(T t) {
            throw new UnsupportedOperationException();
        }
    }

    protected static class SubClass extends Generic<String> {

        @Override
        // Has a bridged method
        public String getProperty() {
            return "";
        }

        @Override
        // Has a bridged method
        public void setProperty(String t) {
        }
    }

    protected static interface MySubInterface
            extends MySuperInterface, MySuperInterface2 {
        public int getSub();

        public void setSub(int i);

        @Override
        public int getOverride();

        public void setOverride(int i);
    }

    @Test
    public void testGetProperties() {
        BeanItem<MySuperClass> item = new BeanItem<MySuperClass>(
                new MySuperClass());

        Collection<?> itemPropertyIds = item.getItemPropertyIds();
        assertEquals(3, itemPropertyIds.size());
        assertTrue(itemPropertyIds.contains("superPrivate"));
        assertTrue(itemPropertyIds.contains("superProtected"));
        assertTrue(itemPropertyIds.contains("superPublic"));
    }

    @Test
    public void testGetSuperClassProperties() {
        BeanItem<MyClass> item = new BeanItem<MyClass>(new MyClass("bean1"));

        Collection<?> itemPropertyIds = item.getItemPropertyIds();
        assertEquals(6, itemPropertyIds.size());
        assertTrue(itemPropertyIds.contains("superPrivate"));
        assertTrue(itemPropertyIds.contains("superProtected"));
        assertTrue(itemPropertyIds.contains("superPublic"));
        assertTrue(itemPropertyIds.contains("name"));
        assertTrue(itemPropertyIds.contains("noField"));
        assertTrue(itemPropertyIds.contains("name2"));
    }

    @Test
    public void testOverridingProperties() {
        BeanItem<MyClass2> item = new BeanItem<MyClass2>(new MyClass2("bean2"));

        Collection<?> itemPropertyIds = item.getItemPropertyIds();
        assertEquals(6, itemPropertyIds.size());

        assertTrue(MyClass2.class.equals(item.getBean().getClass()));

        // check that name2 accessed via MyClass2, not MyClass
        assertFalse(item.getItemProperty("name2").isReadOnly());
    }

    @Test
    public void testGetInterfaceProperties() throws SecurityException,
            NoSuchMethodException, IllegalArgumentException,
            IllegalAccessException, InvocationTargetException {
        Method method = BeanItem.class
                .getDeclaredMethod("getPropertyDescriptors", Class.class);
        method.setAccessible(true);
        LinkedHashMap<String, VaadinPropertyDescriptor<Class>> propertyDescriptors = (LinkedHashMap<String, VaadinPropertyDescriptor<Class>>) method
                .invoke(null, MySuperInterface.class);

        assertEquals(2, propertyDescriptors.size());
        assertTrue(propertyDescriptors.containsKey("super1"));
        assertTrue(propertyDescriptors.containsKey("override"));

        MethodProperty<?> property = (MethodProperty<?>) propertyDescriptors
                .get("override").createProperty(getClass());
        assertTrue(property.isReadOnly());
    }

    @Test
    public void testGetSuperInterfaceProperties() throws SecurityException,
            NoSuchMethodException, IllegalArgumentException,
            IllegalAccessException, InvocationTargetException {
        Method method = BeanItem.class
                .getDeclaredMethod("getPropertyDescriptors", Class.class);
        method.setAccessible(true);
        LinkedHashMap<String, VaadinPropertyDescriptor<Class>> propertyDescriptors = (LinkedHashMap<String, VaadinPropertyDescriptor<Class>>) method
                .invoke(null, MySubInterface.class);

        assertEquals(4, propertyDescriptors.size());
        assertTrue(propertyDescriptors.containsKey("sub"));
        assertTrue(propertyDescriptors.containsKey("super1"));
        assertTrue(propertyDescriptors.containsKey("super2"));
        assertTrue(propertyDescriptors.containsKey("override"));

        MethodProperty<?> property = (MethodProperty<?>) propertyDescriptors
                .get("override").createProperty(getClass());
        assertFalse(property.isReadOnly());
    }

    @Test
    public void testPropertyExplicitOrder() {
        Collection<String> ids = new ArrayList<String>();
        ids.add("name");
        ids.add("superPublic");
        ids.add("name2");
        ids.add("noField");

        BeanItem<MyClass> item = new BeanItem<MyClass>(new MyClass("bean1"),
                ids);

        Iterator<?> it = item.getItemPropertyIds().iterator();
        assertEquals("name", it.next());
        assertEquals("superPublic", it.next());
        assertEquals("name2", it.next());
        assertEquals("noField", it.next());
        assertFalse(it.hasNext());
    }

    @Test
    public void testPropertyExplicitOrder2() {
        BeanItem<MyClass> item = new BeanItem<MyClass>(new MyClass("bean1"),
                new String[] { "name", "superPublic", "name2", "noField" });

        Iterator<?> it = item.getItemPropertyIds().iterator();
        assertEquals("name", it.next());
        assertEquals("superPublic", it.next());
        assertEquals("name2", it.next());
        assertEquals("noField", it.next());
        assertFalse(it.hasNext());
    }

    @Test
    public void testPropertyBadPropertyName() {
        Collection<String> ids = new ArrayList<String>();
        ids.add("name3");
        ids.add("name");

        // currently silently ignores non-existent properties
        BeanItem<MyClass> item = new BeanItem<MyClass>(new MyClass("bean1"),
                ids);

        Iterator<?> it = item.getItemPropertyIds().iterator();
        assertEquals("name", it.next());
        assertFalse(it.hasNext());
    }

    @Test
    public void testRemoveProperty() {
        BeanItem<MyClass> item = new BeanItem<MyClass>(new MyClass("bean1"));

        Collection<?> itemPropertyIds = item.getItemPropertyIds();
        assertEquals(6, itemPropertyIds.size());

        item.removeItemProperty("name2");
        assertEquals(5, itemPropertyIds.size());
        assertFalse(itemPropertyIds.contains("name2"));
    }

    @Test
    public void testRemoveSuperProperty() {
        BeanItem<MyClass> item = new BeanItem<MyClass>(new MyClass("bean1"));

        Collection<?> itemPropertyIds = item.getItemPropertyIds();
        assertEquals(6, itemPropertyIds.size());

        item.removeItemProperty("superPrivate");
        assertEquals(5, itemPropertyIds.size());
        assertFalse(itemPropertyIds.contains("superPrivate"));
    }

    @Test
    public void testPropertyTypes() {
        BeanItem<MyClass> item = new BeanItem<MyClass>(new MyClass("bean1"));

        assertTrue(Integer.class
                .equals(item.getItemProperty("superPrivate").getType()));
        assertTrue(Double.class
                .equals(item.getItemProperty("superProtected").getType()));
        assertTrue(Boolean.class
                .equals(item.getItemProperty("superPublic").getType()));
        assertTrue(String.class.equals(item.getItemProperty("name").getType()));
    }

    @Test
    public void testPropertyReadOnly() {
        BeanItem<MyClass> item = new BeanItem<MyClass>(new MyClass("bean1"));

        assertFalse(item.getItemProperty("name").isReadOnly());
        assertTrue(item.getItemProperty("name2").isReadOnly());
    }

    @Test
    public void testCustomProperties() throws Exception {
        LinkedHashMap<String, VaadinPropertyDescriptor<MyClass>> propertyDescriptors = new LinkedHashMap<String, VaadinPropertyDescriptor<MyClass>>();
        propertyDescriptors.put("myname",
                new MethodPropertyDescriptor<BeanItemTest.MyClass>("myname",
                        MyClass.class,
                        MyClass.class.getDeclaredMethod("getName"),
                        MyClass.class.getDeclaredMethod("setName",
                                String.class)));
        MyClass instance = new MyClass("bean1");
        Constructor<BeanItem> constructor = BeanItem.class
                .getDeclaredConstructor(Object.class, Map.class);
        constructor.setAccessible(true);
        BeanItem<MyClass> item = constructor.newInstance(instance,
                propertyDescriptors);

        assertEquals(1, item.getItemPropertyIds().size());
        assertEquals("bean1", item.getItemProperty("myname").getValue());
    }

    @Test
    public void testAddRemoveProperty() throws Exception {
        MethodPropertyDescriptor<BeanItemTest.MyClass> pd = new MethodPropertyDescriptor<BeanItemTest.MyClass>(
                "myname", MyClass.class,
                MyClass.class.getDeclaredMethod("getName"),
                MyClass.class.getDeclaredMethod("setName", String.class));

        BeanItem<MyClass> item = new BeanItem<MyClass>(new MyClass("bean1"));

        assertEquals(6, item.getItemPropertyIds().size());
        assertEquals(null, item.getItemProperty("myname"));

        item.addItemProperty("myname", pd.createProperty(item.getBean()));
        assertEquals(7, item.getItemPropertyIds().size());
        assertEquals("bean1", item.getItemProperty("myname").getValue());
        item.removeItemProperty("myname");
        assertEquals(6, item.getItemPropertyIds().size());
        assertEquals(null, item.getItemProperty("myname"));
    }

    @Test
    public void testOverriddenGenericMethods() {
        BeanItem<SubClass> item = new BeanItem<SubClass>(new SubClass());

        Property<?> property = item.getItemProperty("property");
        assertEquals("Unexpected class for property type", String.class,
                property.getType());

        assertEquals("Unexpected property value", "", property.getValue());

        // Should not be exception
        property.setValue(null);
    }

    @Test
    public void testChangeBean() {
        BeanItem<MyClass> beanItem = new BeanItem<BeanItemTest.MyClass>(
                new MyClass("Foo"));
        beanItem.setBean(new MyClass("Bar"));
        assertEquals("Bar", beanItem.getItemProperty("name").getValue());
    }

    @Test
    public void testChangeBeanNestedProperty() {
        BeanItem<MyClass> beanItem = new BeanItem<BeanItemTest.MyClass>(
                new MyClass("Foo"));
        beanItem.setBean(new MyClass("Bar"));
        assertEquals("Bar", beanItem.getItemProperty("name").getValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testChangeBeanToIncompatibleOne() {
        BeanItem<Object> beanItem = new BeanItem<Object>(new MyClass("Foo"));
        beanItem.setBean(new Generic<String>());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testChangeBeanToSubclass() {
        BeanItem<MyClass> beanItem = new BeanItem<BeanItemTest.MyClass>(
                new MyClass("Foo"));
        beanItem.setBean(new MyClass("Bar"));
        beanItem.setBean(new MyClass2("foo"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testChangeBeanToNull() {
        BeanItem<Object> beanItem = new BeanItem<Object>(new MyClass("Foo"));
        beanItem.setBean(null);
    }

}
