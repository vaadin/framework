package com.vaadin.v7.data.fieldgroup;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.v7.data.util.BeanItem;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.PopupDateField;

public class FieldGroupDateTest {

    private FieldGroup fieldGroup;

    public class TestBean {
        private Date javaDate;
        private java.sql.Date sqlDate;

        public TestBean(Date javaDate, java.sql.Date sqlDate) {
            super();
            this.javaDate = javaDate;
            this.sqlDate = sqlDate;
        }

        public java.sql.Date getSqlDate() {
            return sqlDate;
        }

        public void setSqlDate(java.sql.Date sqlDate) {
            this.sqlDate = sqlDate;
        }

        public Date getJavaDate() {
            return javaDate;
        }

        public void setJavaDate(Date date) {
            javaDate = date;
        }
    }

    @SuppressWarnings("deprecation")
    @Before
    public void setup() {
        fieldGroup = new FieldGroup();
        fieldGroup.setItemDataSource(new BeanItem<TestBean>(new TestBean(
                new Date(2010, 5, 7), new java.sql.Date(2011, 6, 8))));
    }

    @Test
    public void testBuildAndBindDate() {
        Field f = fieldGroup.buildAndBind("javaDate");
        assertNotNull(f);
        assertEquals(PopupDateField.class, f.getClass());
    }

    @Test
    public void testBuildAndBindSqlDate() {
        Field f = fieldGroup.buildAndBind("sqlDate");
        assertNotNull(f);
        assertEquals(PopupDateField.class, f.getClass());
    }

    @Test
    public void clearFields() {
        PopupDateField sqlDate = new PopupDateField();
        PopupDateField javaDate = new PopupDateField();
        fieldGroup.bind(sqlDate, "sqlDate");
        fieldGroup.bind(javaDate, "javaDate");

        assertEquals(new Date(2010, 5, 7), javaDate.getValue());
        assertEquals(new Date(2011, 6, 8), sqlDate.getValue());

        fieldGroup.clear();
        assertEquals(null, javaDate.getValue());
        assertEquals(null, sqlDate.getValue());

    }

}
