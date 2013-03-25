/*
 * Copyright 2000-2013 Vaadin Ltd.
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
package com.vaadin.util;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import junit.framework.Assert;

import org.junit.Test;

public class TestCurrentInstance {

    @Test
    public void testInitiallyCleared() throws Exception {
        assertCleared();
    }

    @Test
    public void testClearedAfterRemove() throws Exception {
        CurrentInstance.set(TestCurrentInstance.class, this);
        Assert.assertEquals(this,
                CurrentInstance.get(TestCurrentInstance.class));
        CurrentInstance.set(TestCurrentInstance.class, null);

        assertCleared();
    }

    @Test
    public void testClearedAfterRemoveInheritable() throws Exception {
        CurrentInstance.setInheritable(TestCurrentInstance.class, this);
        Assert.assertEquals(this,
                CurrentInstance.get(TestCurrentInstance.class));
        CurrentInstance.setInheritable(TestCurrentInstance.class, null);

        assertCleared();
    }

    @Test
    public void testInheritableThreadLocal() throws Exception {
        final AtomicBoolean threadFailed = new AtomicBoolean(true);

        CurrentInstance.setInheritable(TestCurrentInstance.class, this);
        Assert.assertEquals(this,
                CurrentInstance.get(TestCurrentInstance.class));
        Thread t = new Thread() {
            @Override
            public void run() {
                Assert.assertEquals(TestCurrentInstance.this,
                        CurrentInstance.get(TestCurrentInstance.class));
                threadFailed.set(false);
            };
        };
        t.start();
        CurrentInstance.set(TestCurrentInstance.class, null);

        assertCleared();
        while (t.isAlive()) {
            Thread.sleep(1000);
        }
        Assert.assertFalse("Thread failed", threadFailed.get());

    }

    @Test
    public void testClearedAfterRemoveInSeparateThread() throws Exception {
        final AtomicBoolean threadFailed = new AtomicBoolean(true);

        CurrentInstance.setInheritable(TestCurrentInstance.class, this);
        Assert.assertEquals(this,
                CurrentInstance.get(TestCurrentInstance.class));
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    Assert.assertEquals(TestCurrentInstance.this,
                            CurrentInstance.get(TestCurrentInstance.class));
                    CurrentInstance.set(TestCurrentInstance.class, null);
                    assertCleared();

                    threadFailed.set(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };
        };
        t.start();

        while (t.isAlive()) {
            Thread.sleep(1000);
        }
        Assert.assertFalse("Thread failed", threadFailed.get());

        // Clearing the threadlocal in the thread should not have cleared it
        // here
        Assert.assertEquals(this,
                CurrentInstance.get(TestCurrentInstance.class));

        // Clearing the only remaining threadlocal should free all memory
        CurrentInstance.set(TestCurrentInstance.class, null);
        assertCleared();
    }

    @Test
    public void testClearedWithClearAll() throws Exception {
        CurrentInstance.set(TestCurrentInstance.class, this);
        Assert.assertEquals(this,
                CurrentInstance.get(TestCurrentInstance.class));
        CurrentInstance.clearAll();

        assertCleared();
    }

    private void assertCleared() throws SecurityException,
            NoSuchFieldException, IllegalAccessException {
        Assert.assertNull(getInternalCurrentInstanceVariable().get());
    }

    private InheritableThreadLocal<Map<Class<?>, CurrentInstance>> getInternalCurrentInstanceVariable()
            throws SecurityException, NoSuchFieldException,
            IllegalAccessException {
        Field f = CurrentInstance.class.getDeclaredField("instances");
        f.setAccessible(true);
        return (InheritableThreadLocal<Map<Class<?>, CurrentInstance>>) f
                .get(null);
    }

    public void testInheritedClearedAfterRemove() {

    }
}
