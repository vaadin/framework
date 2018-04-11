package com.vaadin.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.jsoup.nodes.Element;
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
        listing.addSelectionListener(
                event -> selectionChanges.add(event.getValue()));
        listing.addSelectionListener(
                event -> oldSelections.add(event.getOldValue()));
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

        assertEquals(PERSON_B, listing.getValue());

        listing.setValue(null);
        assertNull(listing.getValue());
        verifyValueChanges();
    }

    @Test
    @SuppressWarnings({ "rawtypes" })
    public void getValue_isDelegatedTo_getSelectedItem() {
        AbstractSingleSelect select = Mockito.mock(AbstractSingleSelect.class);
        Optional selected = Optional.of(new Object());
        Mockito.when(select.getSelectedItem()).thenReturn(selected);
        Mockito.doCallRealMethod().when(select).getValue();

        assertSame(selected.get(), select.getValue());

        selected = Optional.empty();
        Mockito.when(select.getSelectedItem()).thenReturn(selected);
        assertNull(select.getValue());
    }

    @Test
    public void setValue() {
        listing.setValue(PERSON_C);

        assertEquals(PERSON_C, listing.getSelectedItem().get());

        listing.setValue(null);

        assertFalse(listing.getSelectedItem().isPresent());
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
            assertNull(event.get());
            event.set(evt);
        });
        assertSame(registration, actualRegistration);

        selectionListener.get().selectionChange(
                new SingleSelectionEvent<>(select, value, true));

        assertEquals(select, event.get().getComponent());
        assertEquals(value, event.get().getOldValue());
        assertEquals(value, event.get().getValue());
        assertTrue(event.get().isUserOriginated());
    }

    private void verifyValueChanges() {
        if (!oldSelections.isEmpty()) {
            assertEquals(null, oldSelections.get(0));
            assertEquals(selectionChanges.size(), oldSelections.size());
            for (int i = 0; i < oldSelections.size() - 1; i++) {
                assertEquals(selectionChanges.get(i), oldSelections.get(i + 1));
            }
        }
    }
}
