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
package com.vaadin.server;

import org.junit.Assert;
import org.junit.Test;

import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonObject;
import elemental.json.JsonValue;

public class JsonEqualsTest {

    @Test
    public void differentTypes_notEqual() {
        boolean equals = JsonCodec.jsonEquals(Json.create(5), Json.create("5"));

        Assert.assertFalse("Different types should not be equal", equals);
    }

    @Test
    public void nulls_equal() {
        boolean equals = JsonCodec.jsonEquals(Json.createNull(),
                Json.createNull());

        Assert.assertTrue("Null and null should be equal", equals);
    }

    @Test
    public void differentBooleans_notEqual() {
        boolean equals = JsonCodec.jsonEquals(Json.create(true),
                Json.create(false));

        Assert.assertFalse("Different booleans should not be equal", equals);
    }

    @Test
    public void sameBooleans_equal() {
        boolean equals = JsonCodec.jsonEquals(Json.create(false),
                Json.create(false));

        Assert.assertTrue("Same booleans should be equal", equals);
    }

    @Test
    public void differentNumbers_notEqual() {
        boolean equals = JsonCodec.jsonEquals(Json.create(2), Json.create(5.6));

        Assert.assertFalse("Different numbers should not be equal", equals);
    }

    @Test
    public void sameNumbers_equal() {
        boolean equals = JsonCodec.jsonEquals(Json.create(3.14),
                Json.create(3.14));

        Assert.assertTrue("Same numbers should be equal", equals);
    }

    @Test
    public void differentStrings_notEqual() {
        boolean equals = JsonCodec.jsonEquals(Json.create("abc"),
                Json.create("def"));

        Assert.assertFalse("Different strings should not be equal", equals);
    }

    @Test
    public void sameStrings_equal() {
        boolean equals = JsonCodec.jsonEquals(Json.create("abc"),
                Json.create("abc"));

        Assert.assertTrue("Same strings should be equal", equals);
    }

    @Test
    public void differentKeyCountObject_notEqual() {
        JsonObject o1 = Json.createObject();
        o1.put("key", "value");

        JsonObject o2 = Json.createObject();

        boolean equals = JsonCodec.jsonEquals(o1, o2);

        Assert.assertFalse(
                "Object with different key counts should not be equal", equals);
    }

    @Test
    public void differentKeySetObject_notEqual() {
        JsonObject o1 = Json.createObject();
        o1.put("key", "value");

        JsonObject o2 = Json.createObject();
        o2.put("key2", "value");

        boolean equals = JsonCodec.jsonEquals(o1, o2);

        Assert.assertFalse("Object with different keys should not be equal",
                equals);
    }

    @Test
    public void differentChildValuesObject_notEqual() {
        JsonObject o1 = Json.createObject();
        o1.put("key", "value");

        JsonObject o2 = Json.createObject();
        o2.put("key", true);

        boolean equals = JsonCodec.jsonEquals(o1, o2);

        Assert.assertFalse(
                "Object with different child values should not be equal",
                equals);
    }

    @Test
    public void emptyObjects_equal() {
        JsonObject o1 = Json.createObject();
        JsonObject o2 = Json.createObject();

        boolean equals = JsonCodec.jsonEquals(o1, o2);

        Assert.assertTrue("Empty objects should be equal", equals);
    }

    @Test
    public void sameObjects_equal() {
        JsonObject o1 = Json.createObject();
        o1.put("key", "value");

        JsonObject o2 = Json.createObject();
        o2.put("key", "value");

        boolean equals = JsonCodec.jsonEquals(o1, o2);

        Assert.assertTrue("Same objects should be equal", equals);
    }

    @Test
    public void sameObjectsWithNullValues_equal() {
        JsonObject o1 = Json.createObject();
        o1.put("key", Json.createNull());

        JsonObject o2 = Json.createObject();
        o2.put("key", Json.createNull());

        boolean equals = JsonCodec.jsonEquals(o1, o2);

        Assert.assertTrue("Same objects should be equal", equals);
    }

    @Test
    public void differentSizeArray_notEqual() {
        JsonArray a1 = Json.createArray();
        a1.set(0, 0);

        JsonArray a2 = Json.createArray();

        boolean equals = JsonCodec.jsonEquals(a1, a2);

        Assert.assertFalse("Arrays with different sizes should not be equal",
                equals);
    }

    @Test
    public void differentContentArray_notEqual() {
        JsonArray a1 = Json.createArray();
        a1.set(0, 0);

        JsonArray a2 = Json.createArray();
        a2.set(0, 1);

        boolean equals = JsonCodec.jsonEquals(a1, a2);

        Assert.assertFalse("Arrays with different content should not be equal",
                equals);
    }

    @Test
    public void differentOrderArray_notEqual() {
        JsonArray a1 = Json.createArray();
        a1.set(0, 0);
        a1.set(1, true);

        JsonArray a2 = Json.createArray();
        a2.set(0, true);
        a2.set(1, 0);

        boolean equals = JsonCodec.jsonEquals(a1, a2);

        Assert.assertFalse("Arrays with different order should not be equal",
                equals);
    }

    @Test
    public void emptyArrays_equal() {
        JsonArray a1 = Json.createArray();
        JsonArray a2 = Json.createArray();

        boolean equals = JsonCodec.jsonEquals(a1, a2);

        Assert.assertTrue("Empty arrays should be equal", equals);
    }

    @Test
    public void sameArrays_equal() {
        JsonArray a1 = Json.createArray();
        a1.set(0, 0);
        a1.set(1, true);

        JsonArray a2 = Json.createArray();
        a2.set(0, 0);
        a2.set(1, true);

        boolean equals = JsonCodec.jsonEquals(a1, a2);

        Assert.assertTrue("Same arrays should be equal", equals);
    }

    @Test
    public void sameArraysWitNull_equal() {
        JsonArray a1 = Json.createArray();
        a1.set(0, Json.createNull());

        JsonArray a2 = Json.createArray();
        a2.set(0, Json.createNull());

        boolean equals = JsonCodec.jsonEquals(a1, a2);

        Assert.assertTrue("Same arrays should be equal", equals);
    }

    @Test
    public void differentDeeplyNested_notEquals() {
        boolean equals = JsonCodec.jsonEquals(createDeeplyNestedValue(1),
                createDeeplyNestedValue(2));

        Assert.assertFalse("Values should not be equal", equals);
    }

    @Test
    public void sameDeeplyNested_equals() {
        boolean equals = JsonCodec.jsonEquals(createDeeplyNestedValue(1),
                createDeeplyNestedValue(1));

        Assert.assertTrue("Values should be equal", equals);
    }

    private static JsonValue createDeeplyNestedValue(int leafValue) {
        JsonObject childObject = Json.createObject();
        childObject.put("value", leafValue);

        JsonArray childArray = Json.createArray();
        childArray.set(0, childObject);

        JsonObject value = Json.createObject();
        value.put("child", childArray);
        return value;
    }
}
