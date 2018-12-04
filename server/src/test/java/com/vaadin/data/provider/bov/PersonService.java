package com.vaadin.data.provider.bov;

import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/**
 * Data access service example.
 *
 * @author Vaadin Ltd
 * @see Person
 */
public interface PersonService extends Serializable {
    List<Person> fetchPersons(int offset, int limit);

    List<Person> fetchPersons(int offset, int limit,
            Collection<PersonSort> personSorts);

    int getPersonCount();

    public interface PersonSort extends Comparator<Person>, Serializable {
    }

    PersonSort createSort(String propertyName, boolean descending);
}
