package com.vaadin.tests.server.component.abstractfield;

import java.math.BigDecimal;
import java.util.Locale;

import junit.framework.TestCase;

import com.vaadin.data.util.MethodProperty;
import com.vaadin.server.VaadinSession;
import com.vaadin.tests.data.bean.Address;
import com.vaadin.tests.data.bean.Country;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.tests.data.bean.Sex;
import com.vaadin.tests.util.AlwaysLockedVaadinSession;
import com.vaadin.ui.TextField;

public class DefaultConverterFactory extends TestCase {

    public static class FloatBean {
        float f1;
        Float f2;

        public FloatBean(float f1, Float f2) {
            this.f1 = f1;
            this.f2 = f2;
        }

        public float getF1() {
            return f1;
        }

        public void setF1(float f1) {
            this.f1 = f1;
        }

        public Float getF2() {
            return f2;
        }

        public void setF2(Float f2) {
            this.f2 = f2;
        }

    }

    public static class LongBean {
        long l1;
        Long l2;

        public LongBean(long l1, Long l2) {
            this.l1 = l1;
            this.l2 = l2;
        }

        public long getL1() {
            return l1;
        }

        public void setL1(long l1) {
            this.l1 = l1;
        }

        public Long getL2() {
            return l2;
        }

        public void setL2(Long l2) {
            this.l2 = l2;
        }

    }

    Person paulaBean = new Person("Paula", "Brilliant", "paula@brilliant.com",
            34, Sex.FEMALE, new Address("Paula street 1", 12345, "P-town",
                    Country.FINLAND));
    {
        paulaBean.setSalary(49000);
        BigDecimal rent = new BigDecimal(57223);
        rent = rent.scaleByPowerOfTen(-2);
        paulaBean.setRent(rent);
    }

    public void testFloatConversion() {
        VaadinSession sess = new AlwaysLockedVaadinSession(null);
        VaadinSession.setCurrent(sess);

        TextField tf = new TextField();
        tf.setLocale(new Locale("en", "US"));
        tf.setPropertyDataSource(new MethodProperty<Integer>(new FloatBean(12f,
                23f), "f2"));
        assertEquals("23", tf.getValue());
        tf.setValue("24");
        assertEquals("24", tf.getValue());
        assertEquals(24f, tf.getConvertedValue());
        assertEquals(24f, tf.getPropertyDataSource().getValue());
    }

    public void testLongConversion() {
        VaadinSession sess = new AlwaysLockedVaadinSession(null);
        VaadinSession.setCurrent(sess);

        TextField tf = new TextField();
        tf.setLocale(new Locale("en", "US"));
        tf.setPropertyDataSource(new MethodProperty<Integer>(new LongBean(12,
                1982739187238L), "l2"));
        assertEquals("1,982,739,187,238", tf.getValue());
        tf.setValue("1982739187239");
        assertEquals("1,982,739,187,239", tf.getValue());
        assertEquals(1982739187239L, tf.getConvertedValue());
        assertEquals(1982739187239L, tf.getPropertyDataSource().getValue());
    }

    public void testDefaultNumberConversion() {
        VaadinSession app = new AlwaysLockedVaadinSession(null);
        VaadinSession.setCurrent(app);
        TextField tf = new TextField();
        tf.setLocale(new Locale("en", "US"));
        tf.setPropertyDataSource(new MethodProperty<Integer>(paulaBean,
                "salary"));
        assertEquals("49,000", tf.getValue());

        tf.setLocale(new Locale("fi", "FI"));
        // FIXME: The following line should not be necessary and should be
        // removed
        tf.setPropertyDataSource(new MethodProperty<Integer>(paulaBean,
                "salary"));
        String value = tf.getValue();
        // Java uses a non-breaking space (ascii 160) instead of space when
        // formatting
        String expected = "49" + (char) 160 + "000";
        assertEquals(expected, value);
    }
}
