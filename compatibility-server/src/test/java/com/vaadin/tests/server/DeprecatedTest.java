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
package com.vaadin.tests.server;

import java.io.File;
import java.net.URISyntaxException;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Vaadin Ltd
 *
 */
public class DeprecatedTest {

    @Test
    public void allTypesAreDeprecated() throws URISyntaxException {
        AtomicInteger count = new AtomicInteger(0);

        File testRoot = new File(DeprecatedTest.class.getResource("/").toURI());

        new ClasspathHelper()
                .getVaadinClassesFromClasspath(
                        entry -> entry.contains("compatibility-server")
                                && !testRoot.equals(new File(entry)))
                .forEach(cls -> {
                    count.incrementAndGet();
                    Assert.assertNotNull(
                            "Class " + cls
                                    + " is in compatability package and it's not deprecated",
                            cls.getAnnotation(Deprecated.class));
                });
        Assert.assertNotEquals("Total number of checked classes", 0,
                count.get());
    }

}
