package com.vaadin.data;

import org.junit.Test;

import com.vaadin.ui.Grid;
import com.vaadin.ui.TextField;

import static org.junit.Assert.assertEquals;

public class PropertyRetrospection {

    public static class InnerBean {
        private String innerString;

        public String getInnerString() {
            return innerString;
        }

        public void setInnerString(String innerString) {
            this.innerString = innerString;
        }
    }

    public static class BeanOne {
        private String someString;
        private InnerBean innerBean;

        public String getSomeString() {
            return someString;
        }

        public void setSomeString(String someString) {
            this.someString = someString;
        }

        public InnerBean getInnerBean() {
            return innerBean;
        }

        public void setInnerBean(InnerBean innerBean) {
            this.innerBean = innerBean;
        }
    }

    public static class BeanTwo {
        private String someString;
        private InnerBean innerBean;

        public String getSomeString() {
            return someString;
        }

        public void setSomeString(String someString) {
            this.someString = someString;
        }

        public InnerBean getInnerBean() {
            return innerBean;
        }

        public void setInnerBean(InnerBean innerBean) {
            this.innerBean = innerBean;
        }
    }

    @Test
    public void testGridBeanProperties()
    {
        Grid<BeanOne> grid1 = new Grid<>(BeanOne.class);
        assertEquals(2,BeanPropertySet.get(BeanOne.class).getProperties().count());
        assertEquals(2,grid1.getColumns().size());
        grid1.addColumn("innerBean.innerString");
        assertEquals(3,grid1.getColumns().size());
        assertEquals(2,BeanPropertySet.get(BeanOne.class).getProperties().count());


        Grid<BeanOne> grid2 = new Grid<>(BeanOne.class);
        assertEquals(2,BeanPropertySet.get(BeanOne.class).getProperties().count());
        assertEquals(2,grid2.getColumns().size());
        grid2.addColumn("innerBean.innerString");
        assertEquals(3,grid2.getColumns().size());
        assertEquals(2,BeanPropertySet.get(BeanOne.class).getProperties().count());
    }

    @Test
    public void testBinder()
    {
        Binder<BeanTwo> binder1 = new Binder<>(BeanTwo.class);
        assertEquals(2,BeanPropertySet.get(BeanTwo.class).getProperties().count());
        binder1.forField(new TextField()).bind("innerBean.innerString");
        assertEquals(2,BeanPropertySet.get(BeanTwo.class).getProperties().count());

        Binder<BeanTwo> binder2 = new Binder<>(BeanTwo.class);
        assertEquals(2,BeanPropertySet.get(BeanTwo.class).getProperties().count());
        binder2.forField(new TextField()).bind("innerBean.innerString");
        assertEquals(2,BeanPropertySet.get(BeanTwo.class).getProperties().count());
    }
}
