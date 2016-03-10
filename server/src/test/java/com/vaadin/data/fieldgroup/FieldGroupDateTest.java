/*
 * Copyright 2000-2014 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.data.fieldgroup;

import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Field;
import com.vaadin.ui.PopupDateField;

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
        Assert.assertNotNull(f);
        Assert.assertEquals(PopupDateField.class, f.getClass());
    }

    @Test
    public void testBuildAndBindSqlDate() {
        Field f = fieldGroup.buildAndBind("sqlDate");
        Assert.assertNotNull(f);
        Assert.assertEquals(PopupDateField.class, f.getClass());
    }

    @Test
    public void clearFields() {
        PopupDateField sqlDate = new PopupDateField();
        PopupDateField javaDate = new PopupDateField();
        fieldGroup.bind(sqlDate, "sqlDate");
        fieldGroup.bind(javaDate, "javaDate");

        Assert.assertEquals(new Date(2010, 5, 7), javaDate.getValue());
        Assert.assertEquals(new Date(2011, 6, 8), sqlDate.getValue());

        fieldGroup.clear();
        Assert.assertEquals(null, javaDate.getValue());
        Assert.assertEquals(null, sqlDate.getValue());

    }

}
