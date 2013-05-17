package com.vaadin.tests.server.component.fieldgroup;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.data.fieldgroup.BeanFieldGroup;

public class BeanFieldGroupTest {

    public static class MyBean {

        private MyNestedBean nestedBean = new MyNestedBean();

        public MyNestedBean getNestedBean() {
            return nestedBean;
        }
    }

    public static class MyNestedBean {

        private String hello = "Hello world";

        public String getHello() {
            return hello;
        }
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

}
