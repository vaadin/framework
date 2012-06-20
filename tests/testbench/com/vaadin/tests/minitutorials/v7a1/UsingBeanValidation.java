/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.tests.minitutorials.v7a1;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import com.vaadin.data.util.BeanItem;
import com.vaadin.data.validator.BeanValidator;
import com.vaadin.terminal.WrappedRequest;
import com.vaadin.ui.Root;
import com.vaadin.ui.TextField;

/**
 * Mini tutorial code for
 * https://vaadin.com/wiki/-/wiki/Main/Using%20Bean%20Validation
 * %20to%20validate%20input
 * 
 * @author Vaadin Ltd
 * @version @VERSION@
 * @since 7.0.0
 */
public class UsingBeanValidation extends Root {
    class Person {

        @Size(min = 5, max = 50)
        private String name;

        @Min(0)
        @Max(100)
        private int age;

        // + constructor + setters + getters

        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        private void setN() {
            // TODO Auto-generated method stub

        }
    }

    @Override
    protected void init(WrappedRequest request) {
        Person person = new Person("John", 26);
        BeanItem<Person> item = new BeanItem<Person>(person);

        TextField firstName = new TextField("First name",
                item.getItemProperty("name"));
        firstName.setImmediate(true);
        addComponent(firstName);

        firstName.addValidator(new BeanValidator(Person.class, "name"));
    }

}
