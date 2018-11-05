package com.vaadin.server;

import static org.junit.Assert.fail;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.junit.Test;

/**
 * We test that AbstractClientConnector has a suitable isThis method which is
 * needed to correctly perform an equals check between a proxy and it's
 * underlying instance.
 *
 * @author Vaadin Ltd
 */
public class AbstractClientConnectorProxyHandlingTest {

    @Test
    public void abstractClientConnectorTest() {
        try {
            Method method = AbstractClientConnector.class
                    .getDeclaredMethod("isThis", Object.class);
            int modifiers = method.getModifiers();
            if (Modifier.isFinal(modifiers) || !Modifier.isProtected(modifiers)
                    || Modifier.isStatic(modifiers)) {
                fail("isThis has invalid modifiers, CDI proxies will not work.");
            }
        } catch (SecurityException e) {
            // Ignore, no can do
        } catch (NoSuchMethodException e) {
            fail("isThis is missing, CDI proxies will not work.");
        }
    }

}
