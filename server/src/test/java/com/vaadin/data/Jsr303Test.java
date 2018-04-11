package com.vaadin.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;

import javax.validation.Validation;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.vaadin.data.util.BeanUtil;
import com.vaadin.tests.data.bean.BeanToValidate;
import com.vaadin.ui.TextField;

/**
 * @author Vaadin Ltd
 *
 */
public class Jsr303Test {

    private static class TestClassLoader extends URLClassLoader {

        public TestClassLoader() {
            super(new URL[0], Thread.currentThread().getContextClassLoader());
        }

        @Override
        public Class<?> loadClass(String name) throws ClassNotFoundException {
            String vaadinPackagePrefix = getClass().getPackage().getName();
            vaadinPackagePrefix = vaadinPackagePrefix.substring(0,
                    vaadinPackagePrefix.lastIndexOf('.'));
            if (name.equals(UnitTest.class.getName())) {
                super.loadClass(name);
            } else if (name
                    .startsWith(Validation.class.getPackage().getName())) {
                throw new ClassNotFoundException();
            } else if (name.startsWith(vaadinPackagePrefix)) {
                String path = name.replace('.', '/').concat(".class");
                URL resource = Thread.currentThread().getContextClassLoader()
                        .getResource(path);
                InputStream stream;
                try {
                    stream = resource.openStream();
                    byte[] bytes = IOUtils.toByteArray(stream);
                    return defineClass(name, bytes, 0, bytes.length);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return super.loadClass(name);
        }
    }

    public interface UnitTest {
        void execute();
    }

    public static class Jsr303UnitTest implements UnitTest {

        private final TextField nameField = new TextField();

        @Override
        public void execute() {
            assertFalse(BeanUtil.checkBeanValidationAvailable());

            Binder<BeanToValidate> binder = new Binder<>(BeanToValidate.class);
            BeanToValidate item = new BeanToValidate();
            String name = "Johannes";
            item.setFirstname(name);
            item.setAge(32);

            binder.bind(nameField, "firstname");
            binder.setBean(item);

            assertEquals(name, nameField.getValue());

            // BeanToValidate : @Size(min = 3, max = 16) for the firstName
            nameField.setValue("a");
            assertEquals(nameField.getValue(), item.getFirstname());

            try {
                BeanValidationBinder<BeanToValidate> beanValidationBinder = new BeanValidationBinder<>(
                        BeanToValidate.class);
                fail();
            } catch (IllegalStateException ignore) {
                // an exception has to be thrown
            }
        }

    }

    @Test
    public void beanBinderWithoutJsr303() throws ClassNotFoundException,
            NoSuchMethodException, SecurityException, InstantiationException,
            IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, IOException, InterruptedException {
        try (URLClassLoader loader = new TestClassLoader()) {
            Class<?> clazz = loader.loadClass(Jsr303UnitTest.class.getName());
            UnitTest test = (UnitTest) clazz.newInstance();
            test.execute();
        }
    }

}
