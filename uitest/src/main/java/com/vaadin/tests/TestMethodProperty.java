/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.tests;

import com.vaadin.v7.data.util.MethodProperty;

public class TestMethodProperty {

    /**
     * @param args
     */
    public static void main(String[] args) {

        MyTest myTest = new MyTest();

        MethodProperty<Integer> methodProperty2 = new MethodProperty<>(
                Integer.TYPE, myTest, "getInt", "setInt", new Object[0],
                new Object[] { null }, 0);

        methodProperty2.setValue(3);

        System.out.println("Succeeded");

    }

    public static class MyTest {

        int integer;

        public void setInteger(Integer integer) {
            System.out.println("setInteger");
            this.integer = integer;
        }

        public Integer getInteger() {
            System.out.println("getInteger");
            return Integer.valueOf(integer);
        }

        public void setInt(int i) {
            System.out.println("setInt");
            integer = i;
        }

        public int getInt() {
            System.out.println("getInt");
            return integer;
        }
    }
}
