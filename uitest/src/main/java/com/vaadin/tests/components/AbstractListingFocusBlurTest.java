package com.vaadin.tests.components;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.googlecode.gentyref.GenericTypeReflector;
import com.vaadin.event.FieldEvents.BlurNotifier;
import com.vaadin.event.FieldEvents.FocusNotifier;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.AbstractListing;

/**
 * @author Vaadin Ltd
 *
 */
public abstract class AbstractListingFocusBlurTest<T extends AbstractListing<Integer> & FocusNotifier & BlurNotifier>
        extends AbstractTestUIWithLog {

    @Override
    @SuppressWarnings("unchecked")
    protected void setup(VaadinRequest request) {
        Type valueType = GenericTypeReflector.getTypeParameter(getClass(),
                AbstractListingFocusBlurTest.class.getTypeParameters()[0]);
        if (valueType instanceof ParameterizedType) {
            valueType = ((ParameterizedType) valueType).getRawType();
        }
        if (valueType instanceof Class<?>) {
            Class<T> clazz = (Class<T>) valueType;
            try {
                T select = clazz.newInstance();
                select.setItems(
                        IntStream.range(1, 10).mapToObj(Integer::valueOf)
                                .collect(Collectors.toList()));

                addComponent(select);
                select.addFocusListener(event -> log("Focus Event"));
                select.addBlurListener(event -> log("Blur Event"));
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new RuntimeException(
                    "Unexpected component type " + valueType.getTypeName());
        }
    }

}
