/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.tests.server.component.combobox;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.data.provider.DataCommunicator;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.server.ClientMethodInvocation;
import com.vaadin.server.ServerRpcManager;
import com.vaadin.shared.ui.combobox.ComboBoxServerRpc;
import com.vaadin.tests.data.bean.Address;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.tests.data.bean.Sex;
import com.vaadin.ui.ComboBox;

/**
 * Test for ComboBox data providers and filtering.
 *
 * @author Vaadin Ltd
 */
public class ComboBoxFilteringTest {
    private static final String[] PERSON_NAMES = new String[] {
            "Enrique Iglesias", "Henry Dunant", "Erwin Engelbrecht" };

    private ComboBox<Person> comboBox;

    @Before
    public void setup() {
        comboBox = new ComboBox<>();
        comboBox.setLocale(Locale.US);
    }

    @Test
    public void setItems_array_defaultFiltering() {
        comboBox.setItemCaptionGenerator(Person::getFirstName);

        // Result: typing "en" into the search field finds "Enrique Iglesias"
        // and "Henry Dunant", but not "Erwin Engelbrecht"
        comboBox.setItems(getPersonArray());

        checkFiltering("en", "ennen", 3, 2);
    }

    @Test
    public void setItems_array_setItemCaptionAfterItems() {
        // Result: typing "en" into the search field finds "Enrique Iglesias"
        // and "Henry Dunant", but not "Erwin Engelbrecht"
        comboBox.setItems(getPersonArray());

        // It shouldn't matter if this is done before or after setItems
        comboBox.setItemCaptionGenerator(Person::getFirstName);

        checkFiltering("en", "ennen", 3, 2);
    }

    @Test
    public void setItems_collection_defaultFiltering() {
        comboBox.setItemCaptionGenerator(Person::getFirstName);

        // Result: typing "en" into the search field finds "Enrique Iglesias"
        // and "Henry Dunant", but not "Erwin Engelbrecht"
        comboBox.setItems(getPersonCollection());

        checkFiltering("en", "ennen", 3, 2);
    }

    @Test
    public void setItems_collection_setItemCaptionAfterItems() {
        // Result: typing "en" into the search field finds "Enrique Iglesias"
        // and "Henry Dunant", but not "Erwin Engelbrecht"
        comboBox.setItems(getPersonCollection());

        // It shouldn't matter if this is done before or after setItems
        comboBox.setItemCaptionGenerator(Person::getFirstName);

        checkFiltering("en", "ennen", 3, 2);
    }

    @Test
    public void setItems_array_customFiltering() {
        comboBox.setItemCaptionGenerator(Person::getFirstName);

        // Result: typing "En" into the search field finds "Enrique Iglesias"
        // but not "Henry Dunant" or "Erwin Engelbrecht"
        comboBox.setItems(String::startsWith, getPersonArray());

        checkFiltering("En", "en", 3, 1);
    }

    @Test
    public void setItems_collection_customFiltering() {
        comboBox.setItemCaptionGenerator(Person::getFirstName);

        // Result: typing "En" into the search field finds "Enrique Iglesias"
        // but not "Henry Dunant" or "Erwin Engelbrecht"
        comboBox.setItems(String::startsWith, getPersonCollection());

        checkFiltering("En", "en", 3, 1);
    }

    @Test
    public void setListDataProvider_defaultFiltering() {
        comboBox.setItemCaptionGenerator(Person::getFirstName);

        // Result: typing "en" into the search field finds "Enrique Iglesias"
        // and "Henry Dunant", but not "Erwin Engelbrecht"
        comboBox.setDataProvider(
                DataProvider.ofCollection(getPersonCollection()));

        checkFiltering("en", "ennen", 3, 2);
    }

    @Test
    public void setListDataProvider_customFiltering() {
        comboBox.setItemCaptionGenerator(Person::getFirstName);

        // Result: typing "En" into the search field finds "Enrique Iglesias"
        // but not "Henry Dunant" or "Erwin Engelbrecht"
        comboBox.setDataProvider(String::startsWith,
                DataProvider.ofCollection(getPersonCollection()));

        checkFiltering("En", "en", 3, 1);
    }

    public void invalid_dataProvider_compile_error() {
        DataProvider<Person, Address> dp = DataProvider
                .ofItems(getPersonArray())
                .filteringByEquals(Person::getAddress);

        // uncommenting this causes a compile time error because of invalid data
        // provider filter type
        // comboBox.setDataProvider(dp);
    }

    @Test
    public void customDataProvider_filterByLastName() {
        comboBox.setItemCaptionGenerator(Person::getFirstName);

        // Filters by last name, regardless of the item caption generator
        ListDataProvider<Person> ldp = DataProvider.ofItems(getPersonArray());
        comboBox.setDataProvider(ldp.withConvertedFilter(
                text -> person -> person.getLastName().contains(text)));

        checkFiltering("u", "ab", 3, 1);
    }

    @Test
    public void customDataProvider_filterByLastNameWithAccessRestriction() {
        comboBox.setItemCaptionGenerator(Person::getFirstName);

        // Filters by last name, regardless of the item caption generator
        ListDataProvider<Person> ldp = DataProvider.ofItems(getPersonArray());
        ldp.setFilter(person -> person.getFirstName().contains("nr"));

        // Same as above, but only showing a subset of the persons
        comboBox.setDataProvider(ldp.withConvertedFilter(
                text -> person -> person.getLastName().contains(text)));

        checkFiltering("t", "Engel", 2, 1);
    }

    @Test
    public void filterEmptyComboBox() {
        // Testing that filtering doesn't cause problems in the edge case where
        // neither setDataProvider nor setItems has been called
        checkFiltering("foo", "bar", 0, 0);
    }

    @Test
    public void setListDataProvider_notWrapped() {
        ListDataProvider<Person> provider = new ListDataProvider<>(
                Collections.emptyList());

        comboBox.setDataProvider(provider);

        Assert.assertSame(provider, comboBox.getDataProvider());
    }

    @Test
    public void setItems_hasListDataProvider() {
        comboBox.setItems();

        Assert.assertEquals(ListDataProvider.class,
                comboBox.getDataProvider().getClass());
    }

    private void checkFiltering(String filterText, String nonMatchingFilterText,
            int totalMatches, int matchingResults) {
        Assert.assertEquals(
                "ComboBox filtered out results with no filter applied",
                totalMatches, comboBoxSizeWithFilter(null));
        Assert.assertEquals(
                "ComboBox filtered out results with empty filter string",
                totalMatches, comboBoxSizeWithFilter(""));
        Assert.assertEquals("ComboBox filtered out wrong number of results",
                matchingResults, comboBoxSizeWithFilter(filterText));
        Assert.assertEquals(
                "ComboBox should have no results with a non-matching filter", 0,
                comboBoxSizeWithFilter(nonMatchingFilterText));
    }

    private int comboBoxSizeWithFilter(String filter) {
        DataCommunicator<Person> dataCommunicator = comboBox
                .getDataCommunicator();

        // Discard any currently pending RPC calls
        dataCommunicator.retrievePendingRpcCalls();

        ServerRpcManager.getRpcProxy(comboBox, ComboBoxServerRpc.class)
                .setFilter(filter);
        dataCommunicator.beforeClientResponse(true);

        ClientMethodInvocation resetInvocation = dataCommunicator
                .retrievePendingRpcCalls().get(0);
        assert resetInvocation.getMethodName().equals("reset");

        return ((Integer) resetInvocation.getParameters()[0]).intValue();
    }

    private List<Person> getPersonCollection() {
        return Stream
                .of(PERSON_NAMES).map(name -> new Person(name.split(" ")[0],
                        name.split(" ")[1], null, 0, Sex.MALE, null))
                .collect(Collectors.toList());
    }

    private Person[] getPersonArray() {
        return getPersonCollection().toArray(new Person[PERSON_NAMES.length]);
    }
}
