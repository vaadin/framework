package com.vaadin.tests.server.component.fieldgroup;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Field;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.TextField;

public class BeanFieldGroupTest {

    private static final String DEFAULT_FOR_BASIC_FIELD = "default";

    public static class MyBean {

        private String basicField = DEFAULT_FOR_BASIC_FIELD;

        private String anotherField;

        private MyNestedBean nestedBean = new MyNestedBean();

        public MyNestedBean getNestedBean() {
            return nestedBean;
        }

        /**
         * @return the basicField
         */
        public String getBasicField() {
            return basicField;
        }

        /**
         * @param basicField
         *            the basicField to set
         */
        public void setBasicField(String basicField) {
            this.basicField = basicField;
        }

        /**
         * @return the anotherField
         */
        public String getAnotherField() {
            return anotherField;
        }

        /**
         * @param anotherField
         *            the anotherField to set
         */
        public void setAnotherField(String anotherField) {
            this.anotherField = anotherField;
        }
    }

    public static class MyNestedBean {

        private String hello = "Hello world";

        public String getHello() {
            return hello;
        }
    }

    public static class ViewStub {

        TextField basicField = new TextField();

        @PropertyId("anotherField")
        TextField boundWithAnnotation = new TextField();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testStaticBindingHelper() {
        MyBean myBean = new MyBean();

        ViewStub viewStub = new ViewStub();
        BeanFieldGroup<MyBean> bindFields = BeanFieldGroup
                .bindFieldsUnbuffered(myBean, viewStub);

        Field<String> field = (Field<String>) bindFields.getField("basicField");
        Assert.assertEquals(DEFAULT_FOR_BASIC_FIELD, myBean.basicField);
        field.setValue("Foo");
        Assert.assertEquals("Foo", myBean.basicField);

        field = (Field<String>) bindFields.getField("anotherField");
        field.setValue("Foo");
        Assert.assertEquals("Foo", myBean.anotherField);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testStaticBufferedBindingHelper() throws CommitException {
        MyBean myBean = new MyBean();

        ViewStub viewStub = new ViewStub();
        BeanFieldGroup<MyBean> bindFields = BeanFieldGroup.bindFieldsBuffered(
                myBean, viewStub);

        Field<String> basicField = (Field<String>) bindFields
                .getField("basicField");
        basicField.setValue("Foo");
        Assert.assertEquals(DEFAULT_FOR_BASIC_FIELD, myBean.basicField);

        Field<String> anotherField = (Field<String>) bindFields
                .getField("anotherField");
        anotherField.setValue("Foo");
        Assert.assertNull(myBean.anotherField);

        bindFields.commit();

        Assert.assertEquals("Foo", myBean.basicField);
        Assert.assertEquals("Foo", myBean.anotherField);

    }

    @Test
    public void buildAndBindNestedProperty() {

        MyBean bean = new MyBean();

        BeanFieldGroup<MyBean> bfg = new BeanFieldGroup<MyBean>(MyBean.class);
        bfg.setItemDataSource(bean);

        com.vaadin.ui.Field<?> helloField = bfg.buildAndBind("Hello string",
                "nestedBean.hello");
        assertEquals(bean.nestedBean.hello, helloField.getValue().toString());
    }

    @Test
    public void buildAndBindNestedRichTextAreaProperty() {

        MyBean bean = new MyBean();

        BeanFieldGroup<MyBean> bfg = new BeanFieldGroup<MyBean>(MyBean.class);
        bfg.setItemDataSource(bean);

        RichTextArea helloField = bfg.buildAndBind("Hello string",
                "nestedBean.hello", RichTextArea.class);
        assertEquals(bean.nestedBean.hello, helloField.getValue().toString());
    }

    @Test
    public void setDataSource_nullBean_nullBeanIsSetInDataSource() {
        BeanFieldGroup<MyBean> group = new BeanFieldGroup<MyBean>(MyBean.class);

        group.setItemDataSource((MyBean) null);

        BeanItem<MyBean> dataSource = group.getItemDataSource();
        Assert.assertNull("Data source is null for null bean", dataSource);
    }

    @Test
    public void setDataSource_nullItem_nullDataSourceIsSet() {
        BeanFieldGroup<MyBean> group = new BeanFieldGroup<MyBean>(MyBean.class);

        group.setItemDataSource((Item) null);
        BeanItem<MyBean> dataSource = group.getItemDataSource();
        Assert.assertNull("Group returns not null data source", dataSource);
    }

}
