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
package com.vaadin.tests.dataprovider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.shared.data.DataProviderConstants;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.fieldgroup.ComplexPerson;
import com.vaadin.tests.tb3.SingleBrowserTest;

import elemental.json.Json;
import elemental.json.JsonObject;

public class DummyDataProviderTest extends SingleBrowserTest {

    // Each test uses a set of person objects (generated json) that is supposed
    // to match the data sent to the client-side.
    private List<JsonObject> personObjects = new ArrayList<JsonObject>();
    // Persons are created exactly the same way as in the test, so we should
    // have identical data.
    private List<ComplexPerson> persons = DummyDataProviderUI.createPersons(
            DummyDataProviderUI.PERSON_COUNT, new Random(
                    DummyDataProviderUI.RANDOM_SEED));
    // For each person we generate a string key, that should match the one
    // DataProvider gives to it.
    private Map<ComplexPerson, String> personToKeyMap = new HashMap<ComplexPerson, String>();

    {
        int key = 0;
        for (ComplexPerson p : persons) {
            personToKeyMap.put(p, "" + (++key));
        }
    }

    @Override
    public void setup() throws Exception {
        super.setup();

        setDebug(true);
    }

    @Override
    protected Class<?> getUIClass() {
        return DummyDataProviderUI.class;
    }

    @Test
    public void testVerifyJsonContent() {
        createPersonObjects();

        openTestURL();

        int size = DummyDataProviderUI.PERSON_COUNT + 1;
        List<WebElement> labels = findElements(By.className("v-label"));

        assertEquals("Label count did not match person count", size,
                labels.size());

        List<WebElement> personData = labels.subList(1, size);

        for (int i = 0; i < personData.size(); ++i) {
            WebElement e = personData.get(i);
            JsonObject j = personObjects.get(i);
            assertEquals("Json did not match.", j.toJson(), e.getText());
        }

        assertNoErrorNotifications();
    }

    private void createPersonObjects() {
        personObjects.clear();

        for (ComplexPerson p : persons) {
            JsonObject j = Json.createObject();
            j.put(DataProviderConstants.KEY, personToKeyMap.get(p));
            j.put("name", p.getLastName() + ", " + p.getFirstName());
            personObjects.add(j);
        }
    }

    @Test
    public void testSortDoesNotChangeContent() {
        // Sort our internal data to match the order after sort.
        Collections.sort(persons, DummyDataProviderUI.nameComparator);
        createPersonObjects();

        openTestURL();

        $(ButtonElement.class).id("sort").click();

        // Second sort would show if any keys got destroyed/recreated.
        $(ButtonElement.class).id("sort").click();

        int size = DummyDataProviderUI.PERSON_COUNT + 1;
        List<WebElement> labels = findElements(By.className("v-label"));

        assertEquals("Label count did not match person count", size,
                labels.size());

        List<WebElement> personData = labels.subList(1, size);

        for (int i = 0; i < personData.size(); ++i) {
            WebElement e = personData.get(i);
            JsonObject j = personObjects.get(i);
            assertEquals("Json did not match.", j.toJson(), e.getText());
        }

        assertNoErrorNotifications();
    }

    @Test
    public void testRemoveWorksAfterSort() {
        // Sort our internal data to match the order after sort.
        Collections.sort(persons, DummyDataProviderUI.nameComparator);
        createPersonObjects();

        openTestURL();

        $(ButtonElement.class).id("sort").click();

        String text = findElements(By.className("v-label")).get(3).getText();
        String json = personObjects.get(2).toJson();
        assertEquals("Data not sorted", json, text);

        $(ButtonElement.class).id("remove").click();

        text = findElements(By.className("v-label")).get(3).getText();
        json = personObjects.get(3).toJson();
        assertEquals("Data not removed", json, text);

        assertNoErrorNotifications();
    }

    @Test
    public void testEditFirstItem() {
        persons.retainAll(Arrays.asList(persons.get(0)));
        createPersonObjects();

        openTestURL();

        String json = personObjects.get(0).toJson();
        String text = findElements(By.className("v-label")).get(1).getText();
        assertEquals("Initial data did not match", json, text);

        $(ButtonElement.class).id("edit").click();

        persons.get(0).setFirstName("Foo");
        createPersonObjects();

        json = personObjects.get(0).toJson();

        assertFalse("JsonObject of edited person was not updated",
                json.equals(text));

        text = findElements(By.className("v-label")).get(1).getText();
        assertEquals("Modified data did not match", json, text);
    }
}
