package com.vaadin.tests.server.component.abstractfield;

import java.math.BigDecimal;
import java.util.Locale;

import junit.framework.TestCase;

import com.vaadin.Application;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.tests.data.bean.Address;
import com.vaadin.tests.data.bean.Country;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.tests.data.bean.Sex;
import com.vaadin.ui.TextField;

public class DefaultConverterFactory extends TestCase {

    Person paulaBean = new Person("Paula", "Brilliant", "paula@brilliant.com",
            34, Sex.FEMALE, new Address("Paula street 1", 12345, "P-town",
                    Country.FINLAND));
    {
        paulaBean.setSalary(49000);
        BigDecimal rent = new BigDecimal(57223);
        rent = rent.scaleByPowerOfTen(-2);
        paulaBean.setRent(rent);
    }

    public void testDefaultNumberConversion() {
        Application app = new Application();
        Application.setCurrentApplication(app);
        TextField tf = new TextField();
        tf.setLocale(new Locale("en", "US"));
        tf.setPropertyDataSource(new MethodProperty<Person>(paulaBean, "salary"));
        assertEquals("49,000", tf.getValue());

        tf.setLocale(new Locale("fi", "FI"));
        // FIXME: The following line should not be necessary and should be
        // removed
        tf.setPropertyDataSource(new MethodProperty<Person>(paulaBean, "salary"));
        String value = tf.getValue();
        // Java uses a non-breaking space (ascii 160) instead of space when
        // formatting
        String expected = "49" + (char) 160 + "000";
        assertEquals(expected, value);
    }
}
