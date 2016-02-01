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

import java.util.List;
import java.util.Random;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.shared.data.DataProviderConstants;
import com.vaadin.tests.fieldgroup.ComplexPerson;
import com.vaadin.tests.tb3.SingleBrowserTest;

import elemental.json.Json;
import elemental.json.JsonObject;

public class DummyDataProviderTest extends SingleBrowserTest {

    @Override
    protected Class<?> getUIClass() {
        return DummyDataProviderUI.class;
    }

    @Test
    public void testVerifyJsonContent() {
        Random r = new Random(DummyDataProviderUI.RANDOM_SEED);
        List<ComplexPerson> persons = DummyDataProviderUI.createPersons(
                DummyDataProviderUI.PERSON_COUNT, r);

        openTestURL();

        int size = DummyDataProviderUI.PERSON_COUNT + 1;
        List<WebElement> labels = findElements(By.className("v-label"));

        assertEquals("Label count did not match person count", size,
                labels.size());

        List<WebElement> personData = labels.subList(1, size);

        int key = 0;
        for (WebElement e : personData) {
            JsonObject j = Json.createObject();
            ComplexPerson p = persons.get(key);
            j.put(DataProviderConstants.KEY, "" + (++key));
            j.put("name", p.getLastName() + ", " + p.getFirstName());
            assertEquals("Json did not match.", j.toJson(), e.getText());
        }
    }
}
