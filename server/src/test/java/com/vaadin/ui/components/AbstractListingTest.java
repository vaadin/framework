package com.vaadin.ui.components;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.server.communication.data.typed.AbstractDataSource;
import com.vaadin.server.communication.data.typed.DataProvider;
import com.vaadin.server.communication.data.typed.DataSource;
import com.vaadin.ui.components.AbstractListing.AbstractListingExtension;

import elemental.json.JsonObject;

public class AbstractListingTest {

    private final class CountGenerator extends AbstractListingExtension<String> {

        int callCount = 0;

        @Override
        public void generateData(String data, JsonObject jsonObject) {
            ++callCount;
        }

        @Override
        public void destroyData(String data) {
        }
    }

    AbstractListing<String> testComponent = new AbstractListing<String>() {

        DataSource<String> data;

        @Override
        public void setDataSource(DataSource<String> data) {
            this.data = data;
            setDataProvider(DataProvider.create(data, this));
        }

        @Override
        public DataSource<String> getDataSource() {
            return data;
        }
    };

    @Before
    public void setUp() {
        testComponent.setDataSource(new AbstractDataSource<String>() {

            @Override
            public void save(String data) {
            }

            @Override
            public void remove(String data) {
            }

            @Override
            public Iterator<String> iterator() {
                return Arrays.asList("Foo").iterator();
            }
        });
    }

    @Test
    public void testAddDataGenerator() {
        CountGenerator countGenerator = new CountGenerator();

        countGenerator.extend(testComponent);
        testComponent.getDataProvider().beforeClientResponse(true);

        assertEquals("Generator was not called.", 1, countGenerator.callCount);
    }

    @Test
    public void testAddAndRemoveDataGenerator() {
        CountGenerator countGenerator = new CountGenerator();

        countGenerator.extend(testComponent);
        countGenerator.remove();
        testComponent.getDataProvider().beforeClientResponse(true);

        assertEquals("Generator was called.", 0, countGenerator.callCount);
    }
}
