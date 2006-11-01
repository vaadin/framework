/* *************************************************************************
 
                               IT Mill Toolkit 

               Development of Browser User Intarfaces Made Easy

                    Copyright (C) 2000-2006 IT Mill Ltd
                     
   *************************************************************************

   This product is distributed under commercial license that can be found
   from the product package on license/license.txt. Use of this product might 
   require purchasing a commercial license from IT Mill Ltd. For guidelines 
   on usage, see license/licensing-guidelines.html

   *************************************************************************
   
   For more information, contact:
   
   IT Mill Ltd                           phone: +358 2 4802 7180
   Ruukinkatu 2-4                        fax:   +358 2 4802 7181
   20540, Turku                          email:  info@itmill.com
   Finland                               company www: www.itmill.com
   
   Primary source for information and releases: www.itmill.com

   ********************************************************************** */

package com.itmill.toolkit.data.util;

import java.beans.Introspector;
import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;

import com.itmill.toolkit.data.Property;

/** A wrapper class for adding the Item interface to any Java Bean.
 * 
 * @author IT Mill Ltd.
 * @version @VERSION@
 * @since 3.0
 */
public class BeanItem extends PropertysetItem {

	/** The bean wich this Item is based on. */
	private Object bean;

	/** <p>Creates a new instance of BeanItem and adds all properties of a
	 * Java Bean to it. The properties are identified by their respective
	 * bean names.</p>
	 * 
	 * <p>Note that this version only supports introspectable bean
	 * properties and their getter and setter methods. Stand-alone "is" and
	 * "are" methods are not supported.</p>
	 * 
	 * @param bean the Java Bean to copy properties from
	 */
	public BeanItem(Object bean) {

		this.bean = bean;

		// Try to introspect, if it fails, we just have an empty Item
		try {
			// Create bean information
			BeanInfo info = Introspector.getBeanInfo(bean.getClass());
			PropertyDescriptor[] pd = info.getPropertyDescriptors();

			// Add all the bean properties as MethodProperties to this Item
			for (int i = 0; i < pd.length; i++) {
				Method getMethod = pd[i].getReadMethod();
				Method setMethod = pd[i].getWriteMethod();
				Class type = pd[i].getPropertyType();
				String name = pd[i].getName();

				Property p =
					new MethodProperty(type, bean, getMethod, setMethod);
				addItemProperty(name, p);
			}
		} catch (java.beans.IntrospectionException ignored) {
		}
	}

	/** <p>Creates a new instance of BeanItem and adds all listed properties of a
	 * Java Bean to it - in specified order. The properties are identified by their 
	 * respective bean names.</p>
	 * 
	 * <p>Note that this version only supports introspectable bean
	 * properties and their getter and setter methods. Stand-alone "is" and
	 * "are" methods are not supported.</p>
	 * 
	 * @param bean the Java Bean to copy properties from
	 */
	public BeanItem(Object bean, Collection propertyIds) {
		
		this.bean = bean;

		// Try to introspect, if it fails, we just have an empty Item
		try {
			// Create bean information
			BeanInfo info = Introspector.getBeanInfo(bean.getClass());
			PropertyDescriptor[] pd = info.getPropertyDescriptors();

			// Add all the bean properties as MethodProperties to this Item
			for (Iterator iter = propertyIds.iterator(); iter.hasNext();) {
				Object id = iter.next();
				for (int i = 0; i < pd.length; i++) {
					String name = pd[i].getName();
					if (name.equals(id)) {
						Method getMethod = pd[i].getReadMethod();
						Method setMethod = pd[i].getWriteMethod();
						Class type = pd[i].getPropertyType();

						Property p =
							new MethodProperty(
								type,
								bean,
								getMethod,
								setMethod);
						addItemProperty(name, p);
					}
				}
			}

		} catch (java.beans.IntrospectionException ignored) {
		}

	}

	/** Get the underlying JavaBean object. 
	 * @return the bean object.
	 */
	public Object getBean() {
		return bean;
	}

}
