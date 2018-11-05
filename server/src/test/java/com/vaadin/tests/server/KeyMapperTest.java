package com.vaadin.tests.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.vaadin.server.KeyMapper;

public class KeyMapperTest {

    @Test
    public void testAdd() {
        KeyMapper<Object> mapper = createKeyMapper();
        Object o1 = createObject();
        Object o2 = createObject();
        Object o3 = createObject();

        // Create new ids
        String key1 = mapper.key(o1);
        String key2 = mapper.key(o2);
        String key3 = mapper.key(o3);

        assertSame(mapper.get(key1), o1);
        assertSame(mapper.get(key2), o2);
        assertSame(mapper.get(key3), o3);
        assertNotSame(key1, key2);
        assertNotSame(key1, key3);
        assertNotSame(key2, key3);

        assertSize(mapper, 3);

        // Key should not add if there already is a mapping
        assertEquals(mapper.key(o3), key3);
        assertSize(mapper, 3);

        // Remove -> add should return a new key
        mapper.remove(o1);
        String newkey1 = mapper.key(o1);
        assertNotSame(key1, newkey1);

    }

    protected Object createObject() {
        return new Object();
    }

    protected KeyMapper<Object> createKeyMapper() {
        return new KeyMapper<>();
    }

    @Test
    public void testRemoveAll() {
        KeyMapper<Object> mapper = createKeyMapper();
        Object o1 = createObject();
        Object o2 = createObject();
        Object o3 = createObject();

        // Create new ids
        mapper.key(o1);
        mapper.key(o2);
        mapper.key(o3);

        assertSize(mapper, 3);
        mapper.removeAll();
        assertSize(mapper, 0);

    }

    @Test
    public void testRemove() {
        KeyMapper<Object> mapper = createKeyMapper();
        Object o1 = createObject();
        Object o2 = createObject();
        Object o3 = createObject();

        // Create new ids
        mapper.key(o1);
        mapper.key(o2);
        mapper.key(o3);

        assertSize(mapper, 3);
        mapper.remove(o1);
        assertSize(mapper, 2);
        mapper.key(o1);
        assertSize(mapper, 3);
        mapper.remove(o1);
        assertSize(mapper, 2);

        mapper.remove(o2);
        mapper.remove(o3);
        assertSize(mapper, 0);

    }

    private void assertSize(KeyMapper<?> mapper, int i) {
        try {
            Field f1 = KeyMapper.class.getDeclaredField("objectIdKeyMap");
            Field f2 = KeyMapper.class.getDeclaredField("keyObjectMap");
            f1.setAccessible(true);
            f2.setAccessible(true);

            Map<?, ?> h1 = (HashMap<?, ?>) f1.get(mapper);
            Map<?, ?> h2 = (HashMap<?, ?>) f2.get(mapper);

            assertEquals(i, h1.size());
            assertEquals(i, h2.size());
        } catch (Throwable t) {
            t.printStackTrace();
            fail();
        }
    }
}
