package com.vaadin.tests.util;

import java.io.Serializable;
import java.util.Random;

import com.vaadin.data.util.BeanItemContainer;

@SuppressWarnings("serial")
public class PersonContainer extends BeanItemContainer<Person> implements
        Serializable {

    /**
     * Natural property order for Person bean. Used in tables and forms.
     */
    public static final Object[] NATURAL_COL_ORDER = new Object[] {
            "firstName", "lastName", "email", "phoneNumber",
            "address.streetAddress", "address.postalCode", "address.city" };

    /**
     * "Human readable" captions for properties in same order as in
     * NATURAL_COL_ORDER.
     */
    public static final String[] COL_HEADERS_ENGLISH = new String[] {
            "First name", "Last name", "Email", "Phone number",
            "Street Address", "Postal Code", "City" };

    public PersonContainer() throws InstantiationException,
            IllegalAccessException {
        super(Person.class);
        addNestedContainerProperty("address.streetAddress");
        addNestedContainerProperty("address.postalCode");
        addNestedContainerProperty("address.city");
    }

    public static PersonContainer createWithTestData() {
        final String[] fnames = { "Peter", "Alice", "Joshua", "Mike", "Olivia",
                "Nina", "Alex", "Rita", "Dan", "Umberto", "Henrik", "Rene",
                "Lisa", "Marge" };
        final String[] lnames = { "Smith", "Gordon", "Simpson", "Brown",
                "Clavel", "Simons", "Verne", "Scott", "Allison", "Gates",
                "Rowling", "Barks", "Ross", "Schneider", "Tate" };
        final String cities[] = { "Amsterdam", "Berlin", "Helsinki",
                "Hong Kong", "London", "Luxemburg", "New York", "Oslo",
                "Paris", "Rome", "Stockholm", "Tokyo", "Turku" };
        final String streets[] = { "4215 Blandit Av.", "452-8121 Sem Ave",
                "279-4475 Tellus Road", "4062 Libero. Av.", "7081 Pede. Ave",
                "6800 Aliquet St.", "P.O. Box 298, 9401 Mauris St.",
                "161-7279 Augue Ave", "P.O. Box 496, 1390 Sagittis. Rd.",
                "448-8295 Mi Avenue", "6419 Non Av.",
                "659-2538 Elementum Street", "2205 Quis St.",
                "252-5213 Tincidunt St.", "P.O. Box 175, 4049 Adipiscing Rd.",
                "3217 Nam Ave", "P.O. Box 859, 7661 Auctor St.",
                "2873 Nonummy Av.", "7342 Mi, Avenue",
                "539-3914 Dignissim. Rd.", "539-3675 Magna Avenue",
                "Ap #357-5640 Pharetra Avenue", "416-2983 Posuere Rd.",
                "141-1287 Adipiscing Avenue", "Ap #781-3145 Gravida St.",
                "6897 Suscipit Rd.", "8336 Purus Avenue", "2603 Bibendum. Av.",
                "2870 Vestibulum St.", "Ap #722 Aenean Avenue",
                "446-968 Augue Ave", "1141 Ultricies Street",
                "Ap #992-5769 Nunc Street", "6690 Porttitor Avenue",
                "Ap #105-1700 Risus Street",
                "P.O. Box 532, 3225 Lacus. Avenue", "736 Metus Street",
                "414-1417 Fringilla Street", "Ap #183-928 Scelerisque Road",
                "561-9262 Iaculis Avenue" };
        PersonContainer c = null;
        Random r = new Random(0);
        try {
            c = new PersonContainer();
            for (int i = 0; i < 100; i++) {
                Person p = new Person();
                p.setFirstName(fnames[r.nextInt(fnames.length)]);
                p.setLastName(lnames[r.nextInt(lnames.length)]);
                p.getAddress().setCity(cities[r.nextInt(cities.length)]);
                p.setEmail(p.getFirstName().toLowerCase() + "."
                        + p.getLastName().toLowerCase() + "@vaadin.com");
                p.setPhoneNumber("+358 02 555 " + r.nextInt(10) + r.nextInt(10)
                        + r.nextInt(10) + r.nextInt(10));
                int n = r.nextInt(100000);
                if (n < 10000) {
                    n += 10000;
                }
                p.getAddress().setPostalCode(n);
                p.getAddress().setStreetAddress(
                        streets[r.nextInt(streets.length)]);
                c.addItem(p);
            }
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return c;
    }

}
