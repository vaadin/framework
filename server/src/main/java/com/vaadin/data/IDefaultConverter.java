/*
 * Copyright 2000-2018 Vaadin Ltd.
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
package com.vaadin.data.converter;

import com.vaadin.data.Binder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * Interface for converting values in a Vaadin 8 binder.
 */
public interface IDefaultConverter {

	/**
	 * Converts a {@link Binder.BindingBuilder} to a given class for an entity property
	 * @param builder the field builder
	 * @param propertyType the entity property class type
	 * @return the converted binder builder
	 */
	default Binder.BindingBuilder build(Binder.BindingBuilder builder, Class<?> fieldType, Class<?> propertyType) {
		if (isSupported(fieldType, propertyType)){
			if (fieldType.equals(String.class)){
				if (propertyType == Integer.class){
					// Convert java.lang.String to java.lang.Integer
					builder = builder.withNullRepresentation("").withConverter(new StringToIntegerConverter("Must be an integer!"));
				}else if (propertyType == Double.class){
					// Convert java.lang.String to java.lang.Double
					builder = builder.withNullRepresentation("").withConverter(new StringToDoubleConverter("Must be a double!"));
				}else if (propertyType == Long.class){
					// Convert java.lang.String to java.lang.Long
					builder = builder.withNullRepresentation("").withConverter(new StringToLongConverter("Must be a long!"));
				}else if (propertyType == Float.class){
					// Convert java.lang.String to java.lang.Float
					builder = builder.withNullRepresentation("").withConverter(new StringToFloatConverter("Must be a float!"));
				}
			}else if (fieldType.equals(LocalDateTime.class)){
				if (propertyType == Date.class){
					// Convert java.time.LocalDateTime to java.util.
					builder = builder.withNullRepresentation("").withConverter(new LocalDateTimeToDateConverter(ZoneId.systemDefault()));
				}
			}else if (fieldType.equals(LocalDate.class)){
				if (propertyType == Date.class){
					// Convert java.time.LocalDate to java.util.Date
					builder = builder.withNullRepresentation("").withConverter(new LocalDateToDateConverter(ZoneId.systemDefault()));
				}
			}
		}
		return builder;
	}

	default Map<Class<?>, Set<Class<?>>> getSupportedConverts(){
		HashMap<Class<?>, Set<Class<?>>> map = new HashMap<>();
		map.put(String.class, new HashSet<>(Arrays.asList(String.class, Integer.class, Double.class, Long.class)));
		map.put(LocalDateTime.class, new HashSet<>(Collections.singletonList(Date.class)));
		return map;
	}

	/**
	 * Is it possible to convert the field class to a given property type class?
	 * @param fieldClass class of the field (String for {@link com.vaadin.ui.TextField})
	 * @param propertyType class of the property type in the entity
	 * @return true if it possible, false if not
	 */
	default boolean isSupported(Class<?> fieldClass, Class<?> propertyType){
		Set<Class<?>> supported = getSupportedConverts().get(fieldClass);
		return supported != null && supported.size() > 0 && supported.contains(propertyType);
	}
}
