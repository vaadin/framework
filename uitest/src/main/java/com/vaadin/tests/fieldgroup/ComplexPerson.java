package com.vaadin.tests.fieldgroup;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Random;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.tests.util.TestDataGenerator;

public class ComplexPerson {

    private String firstName, lastName;
    private Integer age;
    private Date birthDate;
    private BigDecimal salary;
    private boolean alive;
    private Gender gender;
    private ComplexAddress address;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public ComplexAddress getAddress() {
        return address;
    }

    public void setAddress(ComplexAddress address) {
        this.address = address;
    }

    public static BeanItemContainer<ComplexPerson> createContainer(int size) {
        BeanItemContainer<ComplexPerson> bic = new BeanItemContainer<ComplexPerson>(
                ComplexPerson.class);
        Random r = new Random(size);

        for (int i = 0; i < size; i++) {
            ComplexPerson cp = ComplexPerson.create(r);
            bic.addBean(cp);
        }

        return bic;
    }

    public static ComplexPerson create(Random r) {
        ComplexPerson cp = new ComplexPerson();
        cp.setFirstName(TestDataGenerator.getFirstName(r));
        cp.lastName = TestDataGenerator.getLastName(r);
        cp.setAlive(r.nextBoolean());
        cp.setBirthDate(TestDataGenerator.getBirthDate(r));
        cp.setAge((int) ((new Date(2014 - 1900, 1, 1).getTime() - cp
                .getBirthDate().getTime()) / 1000 / 3600 / 24 / 365));
        cp.setSalary(TestDataGenerator.getSalary(r));
        cp.setAddress(ComplexAddress.create(r));
        cp.setGender(TestDataGenerator.getEnum(Gender.class, r));
        return cp;
    }
}
