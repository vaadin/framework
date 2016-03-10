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
package com.vaadin.tests.design;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.junit.Test;

import com.vaadin.server.ExternalResource;
import com.vaadin.shared.util.SharedUtil;
import com.vaadin.ui.AbstractEmbedded;
import com.vaadin.ui.BrowserFrame;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Flash;
import com.vaadin.ui.Image;
import com.vaadin.ui.declarative.Design;

/**
 * Tests declarative support for implementations of {@link AbstractEmbedded} and
 * {@link Embedded}.
 * 
 * @since 7.4
 * @author Vaadin Ltd
 */
public class EmbeddedsTest {

    public static final boolean equals(ExternalResource obj,
            ExternalResource other) {
        return SharedUtil.equals(obj.getURL(), other.getURL())
                && SharedUtil.equals(obj.getMIMEType(), other.getMIMEType());
    }

    @Test
    public void testAbstractEmbeddedsToFromDesign() throws Exception {
        for (AbstractEmbedded ae : new AbstractEmbedded[] { new Image(),
                new Flash(), new BrowserFrame() }) {
            ae.setSource(new ExternalResource("http://www.example.org"));
            ae.setAlternateText("some alternate text");
            ae.setCaption("some <b>caption</b>");
            ae.setCaptionAsHtml(true);
            ae.setDescription("some description");
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            Design.write(ae, bos);
            AbstractEmbedded result = (AbstractEmbedded) Design
                    .read(new ByteArrayInputStream(bos.toByteArray()));
            assertTrue(equals((ExternalResource) ae.getSource(),
                    (ExternalResource) result.getSource()));
            assertEquals(ae.getAlternateText(), result.getAlternateText());
            assertEquals(ae.getCaption(), result.getCaption());
            assertEquals(ae.isCaptionAsHtml(), result.isCaptionAsHtml());
            assertEquals(ae.getDescription(), result.getDescription());
        }
    }

    @Test
    public void testFlashToFromDesign() throws Exception {
        Flash ae = new Flash();
        ae.setSource(new ExternalResource("http://www.example.org"));
        ae.setAlternateText("some alternate text");
        ae.setCaption("some <b>caption</b>");
        ae.setCaptionAsHtml(true);
        ae.setDescription("some description");
        ae.setCodebase("codebase");
        ae.setArchive("archive");
        ae.setCodetype("codetype");
        ae.setParameter("foo", "bar");
        ae.setParameter("something", "else");
        ae.setStandby("foobar");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Design.write(ae, bos);
        Flash result = (Flash) Design.read(new ByteArrayInputStream(bos
                .toByteArray()));
        assertTrue(equals((ExternalResource) ae.getSource(),
                (ExternalResource) result.getSource()));
        assertEquals(ae.getAlternateText(), result.getAlternateText());
        assertEquals(ae.getCaption(), result.getCaption());
        assertEquals(ae.isCaptionAsHtml(), result.isCaptionAsHtml());
        assertEquals(ae.getDescription(), result.getDescription());
        assertEquals(ae.getCodebase(), result.getCodebase());
        assertEquals(ae.getArchive(), result.getArchive());
        assertEquals(ae.getCodetype(), result.getCodetype());
        assertEquals(ae.getParameter("foo"), result.getParameter("foo"));
        assertEquals(ae.getParameter("something"),
                result.getParameter("something"));
        assertEquals(ae.getStandby(), result.getStandby());
    }

}
