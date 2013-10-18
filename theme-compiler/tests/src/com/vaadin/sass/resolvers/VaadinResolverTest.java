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

/**
 * 
 */
package com.vaadin.sass.resolvers;

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

import java.lang.reflect.Method;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.sass.internal.resolver.AbstractResolver;
import com.vaadin.sass.internal.resolver.ClassloaderResolver;
import com.vaadin.sass.internal.resolver.FilesystemResolver;

public class VaadinResolverTest {

    @Test
    public void testFilesystemResolverPathNormalization() throws Exception {
        testPathNormalization(new FilesystemResolver());
    }

    @Test
    public void testClassloaderResolverPathNormalization() throws Exception {
        testPathNormalization(new ClassloaderResolver());
    }

    public void testPathNormalization(AbstractResolver resolver)
            throws Exception {

        Method normalizeMethod = AbstractResolver.class.getDeclaredMethod(
                "normalize", String.class);
        normalizeMethod.setAccessible(true);

        String identifier, result;

        identifier = "a/b/../../../a b/b.scss";
        result = (String) normalizeMethod.invoke(resolver, identifier);
        Assert.assertEquals("../a b/b.scss", result);

        identifier = "./a/b/../c/d/.././e.scss";
        result = (String) normalizeMethod.invoke(resolver, identifier);
        Assert.assertEquals("a/c/e.scss", result);

        identifier = "/äåäåäääå/:;:;:;/???????/- -/e.scss";
        result = (String) normalizeMethod.invoke(resolver, identifier);
        Assert.assertEquals("/äåäåäääå/:;:;:;/???????/- -/e.scss", result);

        identifier = ".";
        result = (String) normalizeMethod.invoke(resolver, identifier);
        Assert.assertEquals("", result);

        identifier = "../..";
        result = (String) normalizeMethod.invoke(resolver, identifier);
        Assert.assertEquals("../..", result);

        identifier = "./../a.scss";
        result = (String) normalizeMethod.invoke(resolver, identifier);
        Assert.assertEquals("../a.scss", result);
    }

}
