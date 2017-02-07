/*
 * Copyright 2000-2014 Vaadin Ltd.
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
package com.vaadin.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.jsoup.nodes.Element;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.vaadin.data.HasDataProvider;
import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.bov.Person;
import com.vaadin.event.selection.SingleSelectionEvent;
import com.vaadin.event.selection.SingleSelectionListener;
import com.vaadin.shared.Registration;
import com.vaadin.shared.data.DataCommunicatorClientRpc;
import com.vaadin.ui.declarative.DesignContext;

/**
 * Test for {@link AbstractSingleSelect} and {@link AbstractSingleSelection}
 *
 * @author Vaadin Ltd
 */
public class AbstractSingleSelectTest {

    private List<Person> selectionChanges;
    private List<Person> oldSelections;

    private static class PersonListing extends AbstractSingleSelect<Person>
            implements HasDataProvider<Person> {

        @Override
        protected Element writeItem(Element design, Person item,
                DesignContext context) {
            return null;
        }

        @Override
        protected void readItems(Element design, DesignContext context) {
        }

        @Override
        public DataProvider<Person, ?> getDataProvider() {
            return internalGetDataProvider();
        }

        @Override
        public void setDataProvider(DataProvider<Person, ?> dataProvider) {
            internalSetDataProvider(dataProvider);
        }
    }

    @Before
    public void initListing() {
        listing = new PersonListing();
        listing.setItems(PERSON_A, PERSON_B, PERSON_C);

        selectionChanges = new ArrayList<>();
        oldSelections = new ArrayList<>();
        listing.addSelectionListener(e -> selectionChanges.add(e.getValue()));
        listing.addSelectionListener(e -> oldSelections.add(e.getOldValue()));
    }

    public static final Person PERSON_C = new Person("c", 3);
    public static final Person PERSON_B = new Person("b", 2);
    public static final Person PERSON_A = new Person("a", 1);
    public static final String RPC_INTERFACE = DataCommunicatorClientRpc.class
            .getName();
    private PersonListing listing;

    @Test
    public void select() {

        listing.setValue(PERSON_B);

        assertTrue(listing.getSelectedItem().isPresent());

        assertEquals(PERSON_B, listing.getSelectedItem().orElse(null));

        assertFalse(listing.isSelected(PERSON_A));
        assertTrue(listing.isSelected(PERSON_B));
        assertFalse(listing.isSelected(PERSON_C));

        assertEquals(Optional.of(PERSON_B), listing.getSelectedItem());

        assertEquals(Arrays.asList(PERSON_B), selectionChanges);
        verifyValueChanges();
    }

    @Test
    public void selectDeselect() {

        listing.setValue(PERSON_B);
        listing.setValue(null);

        assertFalse(listing.getSelectedItem().isPresent());

        assertFalse(listing.isSelected(PERSON_A));
        assertFalse(listing.isSelected(PERSON_B));
        assertFalse(listing.isSelected(PERSON_C));

        assertFalse(listing.getSelectedItem().isPresent());

        assertEquals(Arrays.asList(PERSON_B, null), selectionChanges);
        verifyValueChanges();
    }

    @Test
    public void reselect() {

        listing.setValue(PERSON_B);
        listing.setValue(PERSON_C);

        assertEquals(PERSON_C, listing.getSelectedItem().orElse(null));

        assertFalse(listing.isSelected(PERSON_A));
        assertFalse(listing.isSelected(PERSON_B));
        assertTrue(listing.isSelected(PERSON_C));

        assertEquals(Optional.of(PERSON_C), listing.getSelectedItem());

        assertEquals(Arrays.asList(PERSON_B, PERSON_C), selectionChanges);
        verifyValueChanges();
    }

    @Test
    public void selectTwice() {

        listing.setValue(PERSON_C);
        listing.setValue(PERSON_C);

        assertEquals(PERSON_C, listing.getSelectedItem().orElse(null));

        assertFalse(listing.isSelected(PERSON_A));
        assertFalse(listing.isSelected(PERSON_B));
        assertTrue(listing.isSelected(PERSON_C));

        assertEquals(Optional.of(PERSON_C), listing.getSelectedItem());

        assertEquals(Arrays.asList(PERSON_C), selectionChanges);
        verifyValueChanges();
    }

    @Test
    public void deselectTwice() {

        listing.setValue(PERSON_C);
        listing.setValue(null);
        listing.setValue(null);

        assertFalse(listing.getSelectedItem().isPresent());

        assertFalse(listing.isSelected(PERSON_A));
        assertFalse(listing.isSelected(PERSON_B));
        assertFalse(listing.isSelected(PERSON_C));

        assertFalse(listing.getSelectedItem().isPresent());

        assertEquals(Arrays.asList(PERSON_C, null), selectionChanges);
        verifyValueChanges();
    }

    @Test
    public void getValue() {
        listing.setSelectedItem(PERSON_B);

        Assert.assertEquals(PERSON_B, listing.getValue());

        listing.setValue(null);
        Assert.assertNull(listing.getValue());
        verifyValueChanges();
    }

    @Test
    @SuppressWarnings({ "rawtypes" })
    public void getValue_isDelegatedTo_getSelectedItem() {
        AbstractSingleSelect select = Mockito.mock(AbstractSingleSelect.class);
        Optional selected = Optional.of(new Object());
        Mockito.when(select.getSelectedItem()).thenReturn(selected);
        Mockito.doCallRealMethod().when(select).getValue();

        Assert.assertSame(selected.get(), select.getValue());

        selected = Optional.empty();
        Mockito.when(select.getSelectedItem()).thenReturn(selected);
        Assert.assertNull(select.getValue());
    }

    @Test
    public void setValue() {
        listing.setValue(PERSON_C);

        Assert.assertEquals(PERSON_C, listing.getSelectedItem().get());

        listing.setValue(null);

        Assert.assertFalse(listing.getSelectedItem().isPresent());
        verifyValueChanges();
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void setValue_isDelegatedTo_setSelectedItem() {
        AbstractSingleSelect select = Mockito.mock(AbstractSingleSelect.class);
        Mockito.doCallRealMethod().when(select).setValue(Mockito.any());

        Object value = new Object();
        select.setValue(value);
        Mockito.verify(select).setSelectedItem(value);

        select.setValue(null);
        Mockito.verify(select).setSelectedItem(null);
    }

    @SuppressWarnings("serial")
    @Test
    public void addValueChangeListener() {
        AtomicReference<SingleSelectionListener<String>> selectionListener = new AtomicReference<>();
        Registration registration = Mockito.mock(Registration.class);
        String value = "foo";
        AbstractSingleSelect<String> select = new AbstractSingleSelect<String>() {
            @Override
            public Registration addSelectionListener(
                    SingleSelectionListener<String> listener) {
                selectionListener.set(listener);
                return registration;
            }

            @Override
            public String getValue() {
                return value;
            }

            @Override
            protected Element writeItem(Element design, String item,
                    DesignContext context) {
                return null;
            }

            @Override
            protected void readItems(Element design, DesignContext context) {
            }

            @Override
            public void setItems(Collection<String> items) {
                throw new UnsupportedOperationException(
                        "Not needed in this test");
            }

            @Override
            public DataProvider<String, ?> getDataProvider() {
                return null;
            }
        };

        AtomicReference<ValueChangeEvent<?>> event = new AtomicReference<>();
        Registration actualRegistration = select.addValueChangeListener(evt -> {
            Assert.assertNull(event.get());
            event.set(evt);
        });
        Assert.assertSame(registration, actualRegistration);

        selectionListener.get().selectionChange(
                new SingleSelectionEvent<>(select, value, true));

        Assert.assertEquals(select, event.get().getComponent());
        Assert.assertEquals(value, event.get().getOldValue());
        Assert.assertEquals(value, event.get().getValue());
        Assert.assertTrue(event.get().isUserOriginated());
    }

    private void verifyValueChanges() {
        if (oldSelections.size() > 0) {
            assertEquals(null, oldSelections.get(0));
            assertEquals(selectionChanges.size(), oldSelections.size());
            for (int i = 0; i < oldSelections.size() - 1; i++) {
                assertEquals(selectionChanges.get(i), oldSelections.get(i + 1));
            }
        }
    }
}
