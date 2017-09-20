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
package com.vaadin.data.provider.bov;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.QuerySortOrder;
import com.vaadin.shared.data.sort.SortDirection;

/**
 * Vaadin 8 Example from Book of Vaadin
 *
 * @author Vaadin Ltd
 */
public class DataProviderBoVTest {

    private PersonServiceImpl personService;

    public static class PersonServiceImpl implements PersonService {
        final Person[] persons;

        public PersonServiceImpl(Person... persons) {
            this.persons = persons;
        }

        @Override
        public List<Person> fetchPersons(int offset, int limit) {
            return Arrays.stream(persons).skip(offset).limit(limit)
                    .collect(Collectors.toList());
        }

        @Override
        public List<Person> fetchPersons(int offset, int limit,
                Collection<PersonSort> personSorts) {
            Stream<Person> personStream = Arrays.stream(persons).skip(offset)
                    .limit(limit);
            if (personSorts != null) {
                for (PersonSort personSort : personSorts) {
                    personStream = personStream.sorted(personSort);
                }
            }
            return personStream.collect(Collectors.toList());
        }

        @Override
        public int getPersonCount() {
            return persons.length;
        }

        @Override
        public PersonSort createSort(String propertyName, boolean descending) {
            PersonSort result;
            switch (propertyName) {
            case "name":
                result = (person1, person2) -> String.CASE_INSENSITIVE_ORDER
                        .compare(person1.getName(), person2.getName());
                break;
            case "born":
                result = (person1, person2) -> person2.getBorn()
                        - person1.getBorn();
                break;
            default:
                throw new IllegalArgumentException(
                        "wrong field name " + propertyName);
            }
            if (descending) {
                return (person1, person2) -> result.compare(person2, person1);
            } else {
                return result;
            }
        }
    }

    @Test
    public void testPersons() {
        DataProvider<Person, ?> dataProvider = createUnsortedDataProvider();
        // TODO test if the provider contains all defined Persons in
        // correct(unchanged) order
    }

    private DataProvider<Person, ?> createUnsortedDataProvider() {
        DataProvider<Person, ?> dataProvider = DataProvider.fromCallbacks(
                // First callback fetches items based on a query
                query -> {
                    // The index of the first item to load
                    int offset = query.getOffset();

                    // The number of items to load
                    int limit = query.getLimit();

                    List<Person> persons = getPersonService()
                            .fetchPersons(offset, limit);

                    return persons.stream();
                },
                // Second callback fetches the number of items for a query
                query -> getPersonService().getPersonCount());
        return dataProvider;
    }

    @Test
    public void testSortedPersons() {

        DataProvider<Person, ?> dataProvider = createSortedDataProvider();
        // TODO test if provider contains all defined Persons in correct order
        // TODO test Query.sortOrders correctness
    }

    private DataProvider<Person, ?> createSortedDataProvider() {
        DataProvider<Person, ?> dataProvider = DataProvider.fromCallbacks(
                // First callback fetches items based on a query
                query -> {
                    List<PersonService.PersonSort> sortOrders = new ArrayList<>();
                    for (QuerySortOrder queryOrder : query.getSortOrders()) {
                        PersonService.PersonSort sort = personService
                                .createSort(
                                        // The name of the sorted property
                                        queryOrder.getSorted(),
                                        // The sort direction for this property
                                        queryOrder
                                                .getDirection() == SortDirection.DESCENDING);
                        sortOrders.add(sort);
                    }
                    return getPersonService().fetchPersons(query.getOffset(),
                            query.getLimit(), sortOrders).stream();
                },
                // Second callback fetches the number of items for a query
                query -> getPersonService().getPersonCount());
        return dataProvider;
    }

    public PersonServiceImpl getPersonService() {
        return personService;
    }

    @Before
    public void buildService() {
        personService = new PersonServiceImpl(
                new Person("George Washington", 1732),
                new Person("John Adams", 1735),
                new Person("Thomas Jefferson", 1743),
                new Person("James Madison", 1751));
    }
}
