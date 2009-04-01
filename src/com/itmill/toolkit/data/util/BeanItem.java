/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.data.util;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import com.itmill.toolkit.data.Property;

/**
 * A wrapper class for adding the Item interface to any Java Bean.
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 3.0
 */
public class BeanItem extends PropertysetItem {

    /**
     * The bean which this Item is based on.
     */
    private final Object bean;

    /**
     * <p>
     * Creates a new instance of <code>BeanItem</code> and adds all properties
     * of a Java Bean to it. The properties are identified by their respective
     * bean names.
     * </p>
     * 
     * <p>
     * Note : This version only supports introspectable bean properties and
     * their getter and setter methods. Stand-alone <code>is</code> and
     * <code>are</code> methods are not supported.
     * </p>
     * 
     * @param bean
     *            the Java Bean to copy properties from.
     * 
     */
    public BeanItem(Object bean) {

        this.bean = bean;

        // Try to introspect, if it fails, we just have an empty Item
        try {
            // Create bean information
            final BeanInfo info = Introspector.getBeanInfo(bean.getClass());
            final PropertyDescriptor[] pd = info.getPropertyDescriptors();

            // Add all the bean properties as MethodProperties to this Item
            for (int i = 0; i < pd.length; i++) {
                final Method getMethod = pd[i].getReadMethod();
                final Method setMethod = pd[i].getWriteMethod();
                final Class<?> type = pd[i].getPropertyType();
                final String name = pd[i].getName();

                if ((getMethod != null)
                        && getMethod.getDeclaringClass() != Object.class) {
                    final Property p = new MethodProperty(type, bean,
                            getMethod, setMethod);
                    addItemProperty(name, p);
                }
            }
        } catch (final java.beans.IntrospectionException ignored) {
        }
    }

    /**
     * <p>
     * Creates a new instance of <code>BeanItem</code> and adds all listed
     * properties of a Java Bean to it - in specified order. The properties are
     * identified by their respective bean names.
     * </p>
     * 
     * <p>
     * Note : This version only supports introspectable bean properties and
     * their getter and setter methods. Stand-alone <code>is</code> and
     * <code>are</code> methods are not supported.
     * </p>
     * 
     * @param bean
     *            the Java Bean to copy properties from.
     * @param propertyIds
     *            id of the property.
     */
    public BeanItem(Object bean, Collection propertyIds) {

        this.bean = bean;

        // Try to introspect, if it fails, we just have an empty Item
        try {
            // Create bean information
            final BeanInfo info = Introspector.getBeanInfo(bean.getClass());
            final PropertyDescriptor[] pd = info.getPropertyDescriptors();

            // Add all the bean properties as MethodProperties to this Item
            final Iterator iter = propertyIds.iterator();
            while (iter.hasNext()) {
                final Object id = iter.next();
                for (int i = 0; i < pd.length; i++) {
                    final String name = pd[i].getName();
                    if (name.equals(id)) {
                        final Method getMethod = pd[i].getReadMethod();
                        final Method setMethod = pd[i].getWriteMethod();
                        final Class<?> type = pd[i].getPropertyType();
                        if ((getMethod != null)) {
                            final Property p = new MethodProperty(type, bean,
                                    getMethod, setMethod);
                            addItemProperty(name, p);
                        }
                    }
                }
            }

        } catch (final java.beans.IntrospectionException ignored) {
        }

    }

    /**
     * <p>
     * Creates a new instance of <code>BeanItem</code> and adds all listed
     * properties of a Java Bean to it - in specified order. The properties are
     * identified by their respective bean names.
     * </p>
     * 
     * <p>
     * Note : This version only supports introspectable bean properties and
     * their getter and setter methods. Stand-alone <code>is</code> and
     * <code>are</code> methods are not supported.
     * </p>
     * 
     * @param bean
     *            the Java Bean to copy properties from.
     * @param propertyIds
     *            ids of the properties.
     */
    public BeanItem(Object bean, String[] propertyIds) {
        this(bean, Arrays.asList(propertyIds));
    }

    /**
     * Gets the underlying JavaBean object.
     * 
     * @return the bean object.
     */
    public Object getBean() {
        return bean;
    }

}
