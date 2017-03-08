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
package com.vaadin.v7.data.util;

/**
 * Automated test for {@link AbstractBeanContainer}.
 *
 * Only a limited subset of the functionality is tested here, the rest in tests
 * of subclasses including {@link BeanItemContainer} and {@link BeanContainer}.
 */
public abstract class AbstractBeanContainerTestBase
        extends AbstractInMemoryContainerTestBase {

    public static class Person {
        private String name;

        public Person(String name) {
            setName(name);
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public static class ClassName {
        // field names match constants in parent test class
        private String fullyQualifiedName;
        private String simpleName;
        private String reverseFullyQualifiedName;
        private Integer idNumber;

        public ClassName(String fullyQualifiedName, Integer idNumber) {
            this.fullyQualifiedName = fullyQualifiedName;
            simpleName = AbstractContainerTestBase
                    .getSimpleName(fullyQualifiedName);
            reverseFullyQualifiedName = reverse(fullyQualifiedName);
            this.idNumber = idNumber;
        }

        public String getFullyQualifiedName() {
            return fullyQualifiedName;
        }

        public void setFullyQualifiedName(String fullyQualifiedName) {
            this.fullyQualifiedName = fullyQualifiedName;
        }

        public String getSimpleName() {
            return simpleName;
        }

        public void setSimpleName(String simpleName) {
            this.simpleName = simpleName;
        }

        public String getReverseFullyQualifiedName() {
            return reverseFullyQualifiedName;
        }

        public void setReverseFullyQualifiedName(
                String reverseFullyQualifiedName) {
            this.reverseFullyQualifiedName = reverseFullyQualifiedName;
        }

        public Integer getIdNumber() {
            return idNumber;
        }

        public void setIdNumber(Integer idNumber) {
            this.idNumber = idNumber;
        }
    }

}
