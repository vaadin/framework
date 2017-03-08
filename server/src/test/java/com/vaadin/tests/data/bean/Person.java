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
package com.vaadin.tests.data.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

public class Person implements Serializable {
    private String firstName;
    private String lastName;
    private String email;
    private int age;
    private Sex sex;
    private Address address;
    private boolean deceased;
    private LocalDate birthDate;

    private Integer salary; // null if unknown
    private Double salaryDouble; // null if unknown

    private BigDecimal rent;

    public Person() {

    }

    @Override
    public String toString() {
        return "Person [firstName=" + firstName + ", lastName=" + lastName
                + ", email=" + email + ", age=" + age + ", sex=" + sex
                + ", address=" + address + ", deceased=" + deceased
                + ", salary=" + salary + ", salaryDouble=" + salaryDouble
                + ", rent=" + rent + "]";
    }

    public Person(String firstName, String lastName, String email, int age,
            Sex sex, Address address) {
        super();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.age = age;
        this.sex = sex;
        this.address = address;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean getDeceased() {
        return deceased;
    }

    public void setDeceased(boolean deceased) {
        this.deceased = deceased;
    }

    public Integer getSalary() {
        return salary;
    }

    public void setSalary(Integer salary) {
        this.salary = salary;
    }

    public BigDecimal getRent() {
        return rent;
    }

    public void setRent(BigDecimal rent) {
        this.rent = rent;
    }

    public Double getSalaryDouble() {
        return salaryDouble;
    }

    public void setSalaryDouble(Double salaryDouble) {
        this.salaryDouble = salaryDouble;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public static Person createTestPerson1() {
        return new Person("Foo", "Bar", "yeah@cool.com", 46, Sex.MALE,
                new Address("Street", 1123, "Turku", Country.FINLAND));
    }

    public static Person createTestPerson2() {
        return new Person("Maya", "Dinkelstein", "maya@foo.bar", 18, Sex.FEMALE,
                new Address("Red street", 12, "Amsterdam",
                        Country.NETHERLANDS));
    }
}
