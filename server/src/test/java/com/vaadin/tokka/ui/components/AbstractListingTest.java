package com.vaadin.tokka.ui.components;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.tokka.data.DataSource;
import com.vaadin.tokka.server.communication.data.DataCommunicator;
import com.vaadin.tokka.ui.components.AbstractListing.AbstractListingExtension;

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
    };

    @Before
    public void setUp() {
        testComponent.setDataSource(DataSource.create("Foo"));
    }

    @Test
    public void testAddDataGenerator() {
        CountGenerator countGenerator = new CountGenerator();

        countGenerator.extend(testComponent);
        testComponent.getDataCommunicator().beforeClientResponse(true);

        assertEquals("Generator was not called.", 1, countGenerator.callCount);
    }

    @Test
    public void testAddAndRemoveDataGenerator() {
        CountGenerator countGenerator = new CountGenerator();

        countGenerator.extend(testComponent);
        countGenerator.remove();
        testComponent.getDataCommunicator().beforeClientResponse(true);

        assertEquals("Generator was called.", 0, countGenerator.callCount);
    }
}
