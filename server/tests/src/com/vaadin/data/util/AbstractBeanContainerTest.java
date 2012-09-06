package com.vaadin.data.util;

/**
 * Automated test for {@link AbstractBeanContainer}.
 * 
 * Only a limited subset of the functionality is tested here, the rest in tests
 * of subclasses including {@link BeanItemContainer} and {@link BeanContainer}.
 */
public abstract class AbstractBeanContainerTest extends
        AbstractInMemoryContainerTest {

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
            simpleName = AbstractContainerTest
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
