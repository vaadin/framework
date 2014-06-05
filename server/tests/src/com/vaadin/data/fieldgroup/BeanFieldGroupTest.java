/*
 * Copyright 2012 Vaadin Ltd.
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

import org.junit.Assert;
import org.junit.Test;

public class BeanFieldGroupTest {

    class Main {
        private String mainField;

        public String getMainField() {
            return mainField;
        }

        public void setMainField(String mainField) {
            this.mainField = mainField;
        }

    }

    class Sub1 extends Main {
        private Integer sub1Field;

        public Integer getSub1Field() {
            return sub1Field;
        }

        public void setSub1Field(Integer sub1Field) {
            this.sub1Field = sub1Field;
        }

    }

    class Sub2 extends Sub1 {
        private boolean sub2field;

        public boolean isSub2field() {
            return sub2field;
        }

        public void setSub2field(boolean sub2field) {
            this.sub2field = sub2field;
        }

    }

    @Test
    public void propertyTypeWithoutItem() {
        BeanFieldGroup<Sub2> s = new BeanFieldGroup<BeanFieldGroupTest.Sub2>(
                Sub2.class);
        Assert.assertEquals(boolean.class, s.getPropertyType("sub2field"));
        Assert.assertEquals(Integer.class, s.getPropertyType("sub1Field"));
        Assert.assertEquals(String.class, s.getPropertyType("mainField"));
    }
}
