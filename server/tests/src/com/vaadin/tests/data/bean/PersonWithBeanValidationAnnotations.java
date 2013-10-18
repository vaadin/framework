package com.vaadin.tests.data.bean;

import java.math.BigDecimal;
import java.util.Date;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class PersonWithBeanValidationAnnotations {
    @NotNull
    @Size(min = 5, max = 20)
    @Pattern(regexp = "A.*")
    private String firstName;

    @NotNull
    private String lastName;

    private String email;

    @Min(0)
    @Max(100)
    private int age;

    @NotNull
    private Sex sex;

    private Address address;
    private boolean deceased;

    @NotNull
    @Past
    private Date birthDate;

    @Min(0)
    private Integer salary; // null if unknown

    @Digits(integer = 6, fraction = 2)
    private Double salaryDouble; // null if unknown

    private BigDecimal rent;

    public PersonWithBeanValidationAnnotations() {

    }

    @Override
    public String toString() {
        return "Person [firstName=" + firstName + ", lastName=" + lastName
                + ", email=" + email + ", age=" + age + ", sex=" + sex
                + ", address=" + address + ", deceased=" + deceased
                + ", salary=" + salary + ", salaryDouble=" + salaryDouble
                + ", rent=" + rent + "]";
    }

    public PersonWithBeanValidationAnnotations(String firstName,
            String lastName, String email, int age, Sex sex, Address address) {
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

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

}
