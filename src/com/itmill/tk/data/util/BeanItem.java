/* *************************************************************************
 
   								Millstone(TM) 
   				   Open Sourced User Interface Library for
   		 		       Internet Development with Java

             Millstone is a registered trademark of IT Mill Ltd
                  Copyright (C) 2000-2005 IT Mill Ltd
                     
   *************************************************************************

   This library is free software; you can redistribute it and/or
   modify it under the terms of the GNU Lesser General Public
   license version 2.1 as published by the Free Software Foundation.

   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   Lesser General Public License for more details.

   You should have received a copy of the GNU Lesser General Public
   License along with this library; if not, write to the Free Software
   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

   *************************************************************************
   
   For more information, contact:
   
   IT Mill Ltd                           phone: +358 2 4802 7180
   Ruukinkatu 2-4                        fax:  +358 2 4802 7181
   20540, Turku                          email: info@itmill.com
   Finland                               company www: www.itmill.com
   
   Primary source for MillStone information and releases: www.millstone.org

   ********************************************************************** */

package com.itmill.tk.data.util;

import java.beans.Introspector;
import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;

import com.itmill.tk.data.Property;

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
