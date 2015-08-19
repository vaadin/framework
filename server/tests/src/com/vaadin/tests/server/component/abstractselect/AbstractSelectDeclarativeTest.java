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
package com.vaadin.tests.server.component.abstractselect;

import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.junit.Assert;
import org.junit.Test;

import com.vaadin.data.Container;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.Resource;
import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.declarative.DesignContext;
import com.vaadin.ui.declarative.DesignException;

/**
 * Test cases for reading the properties of selection components.
 * 
 * @author Vaadin Ltd
 */
public class AbstractSelectDeclarativeTest extends
        DeclarativeTestBase<AbstractSelect> {

    public String getDesignSingleSelectNewItemsAllowed() {
        return "<v-combo-box new-items-allowed='true' item-caption-mode='icon_only'"
                + " null-selection-item-id='nullIid'/>";

    }

    public AbstractSelect getExpectedSingleSelectNewItemsAllowed() {
        ComboBox c = new ComboBox();
        c.setNewItemsAllowed(true);
        c.setItemCaptionMode(ItemCaptionMode.ICON_ONLY);
        c.setNullSelectionAllowed(true);// Default
        c.setNullSelectionItemId("nullIid");
        return c;
    }

    public String getDesignMultiSelect() {
        return "<v-list-select multi-select='true' null-selection-allowed='false' new-items-allowed='true' item-caption-mode='property' />";
    }

    public AbstractSelect getExpectedMultiSelect() {
        ListSelect c = new ListSelect();
        c.setNewItemsAllowed(true);
        c.setNullSelectionAllowed(false);
        c.setItemCaptionMode(ItemCaptionMode.PROPERTY);
        c.setMultiSelect(true);
        return c;
    }

    @Test
    public void testReadSingleSelectNewItemsAllowed() {
        testRead(getDesignSingleSelectNewItemsAllowed(),
                getExpectedSingleSelectNewItemsAllowed());
    }

    @Test
    public void testWriteSingleSelectNewItemsAllowed() {
        testWrite(getDesignSingleSelectNewItemsAllowed(),
                getExpectedSingleSelectNewItemsAllowed());
    }

    @Test
    public void testReadMultiSelect() {
        testRead(getDesignMultiSelect(), getExpectedMultiSelect());
    }

    @Test
    public void testWriteMultiSelect() {
        testWrite(getDesignMultiSelect(), getExpectedMultiSelect());
    }

    @Test
    public void testReadInlineData() {
        testRead(getDesignForInlineData(), getExpectedComponentForInlineData());
    }

    @Test(expected = DesignException.class)
    public void testReadMultipleValuesForSingleSelect() {
        testRead("<v-list-select>" + "<option selected>1</option>"
                + "<option selected>2</option>" + "</v-list-select>", null);
    }

    @Test
    public void testReadMultipleValuesForMultiSelect() {
        ListSelect ls = new ListSelect();
        ls.setMultiSelect(true);
        ls.addItem("1");
        ls.addItem("2");
        ls.select("1");
        ls.select("2");
        testRead("<v-list-select multi-select>" + "<option selected>1</option>"
                + "<option selected>2</option>" + "</v-list-select>", ls);
    }

    @Test
    public void testReadSingleValueForMultiSelect() {
        ListSelect ls = new ListSelect();
        ls.setMultiSelect(true);
        ls.addItem("1");
        ls.addItem("2");
        ls.select("1");
        testRead("<v-list-select multi-select>" + "<option selected>1</option>"
                + "<option>2</option>" + "</v-list-select>", ls);
    }

    @Test
    public void testReadSingleValueForSingleSelect() {
        ListSelect ls = new ListSelect();
        ls.setMultiSelect(false);
        ls.addItem("1");
        ls.addItem("2");
        ls.select("1");
        testRead("<v-list-select>" + "<option selected>1</option>"
                + "<option>2</option>" + "</v-list-select>", ls);
    }

    @Test
    public void testWriteInlineDataIgnored() {
        // No data is written by default
        testWrite(stripOptionTags(getDesignForInlineData()),
                getExpectedComponentForInlineData());
    }

    @Test
    public void testWriteInlineData() {
        testWrite(getDesignForInlineData(),
                getExpectedComponentForInlineData(), true);
    }

    private String getDesignForInlineData() {
        return "<v-list-select>\n"
                + "        <option icon='http://some.url/icon.png'>Value 1</option>\n" //
                + "        <option selected>Value 2</option>\n"//
                + "</v-list-select>";
    }

    private AbstractSelect getExpectedComponentForInlineData() {
        AbstractSelect as = new ListSelect();
        as.addItem("Value 1");
        as.setItemIcon("Value 1", new ExternalResource(
                "http://some.url/icon.png"));
        as.addItem("Value 2");
        as.setValue("Value 2");
        return as;
    }

    @Test
    public void testReadAttributesSingleSelect() {
        Element design = createDesignWithAttributesSingleSelect();
        ComboBox cb = new ComboBox();
        IndexedContainer container = new IndexedContainer();
        container.addContainerProperty("icon", Resource.class, null);
        container.addContainerProperty("name", String.class, null);
        cb.setContainerDataSource(container);
        cb.readDesign(design, new DesignContext());
        Assert.assertTrue("Adding new items should be allowed.",
                cb.isNewItemsAllowed());
        assertEquals("Wrong item caption mode.",
                AbstractSelect.ItemCaptionMode.PROPERTY,
                cb.getItemCaptionMode());
        assertEquals("Wrong item caption property id.", "name",
                cb.getItemCaptionPropertyId());
        assertEquals("Wrong item icon property id.", "icon",
                cb.getItemIconPropertyId());
        Assert.assertTrue("Null selection should be allowed.",
                cb.isNullSelectionAllowed());
        assertEquals("Wrong null selection item id.", "No items selected",
                cb.getNullSelectionItemId());
    }

    @Test
    public void testReadAttributesMultiSelect() {
        Element design = createDesignWithAttributesMultiSelect();
        ListSelect ls = new ListSelect();
        ls.readDesign(design, new DesignContext());
        Assert.assertTrue("Multi select should be allowed.", ls.isMultiSelect());
        assertEquals("Wrong caption mode.",
                AbstractSelect.ItemCaptionMode.EXPLICIT,
                ls.getItemCaptionMode());
        Assert.assertFalse("Null selection should not be allowed.",
                ls.isNullSelectionAllowed());
    }

    private Element createDesignWithAttributesSingleSelect() {
        Attributes attributes = new Attributes();
        attributes.put("new-items-allowed", "");
        attributes.put("multi-select", "false");
        attributes.put("item-caption-mode", "property");
        attributes.put("item-caption-property-id", "name");
        attributes.put("item-icon-property-id", "icon");
        attributes.put("null-selection-allowed", "true");
        attributes.put("null-selection-item-id", "No items selected");
        return new Element(Tag.valueOf("v-combo-box"), "", attributes);
    }

    private Element createDesignWithAttributesMultiSelect() {
        Attributes attributes = new Attributes();
        attributes.put("multi-select", "");
        attributes.put("item-caption-mode", "EXPLICIT");
        attributes.put("null-selection-allowed", "false");
        return new Element(Tag.valueOf("v-list-select"), "", attributes);
    }

    @Test
    public void testWriteAttributesSingleSelect() {
        ComboBox cb = createSingleSelectWithOnlyAttributes();
        Element e = new Element(Tag.valueOf("v-combo-box"), "");
        cb.writeDesign(e, new DesignContext());
        assertEquals("Wrong caption for the combo box.", "A combo box",
                e.attr("caption"));
        Assert.assertTrue("Adding new items should be allowed.",
                "true".equals(e.attr("new-items-allowed")));
        assertEquals("Wrong item caption mode.", "icon_only",
                e.attr("item-caption-mode"));
        assertEquals("Wrong item icon property id.", "icon",
                e.attr("item-icon-property-id"));
        Assert.assertTrue(
                "Null selection should be allowed.",
                "".equals(e.attr("null-selection-allowed"))
                        || "true".equals(e.attr("null-selection-allowed")));
        assertEquals("Wrong null selection item id.", "No item selected",
                e.attr("null-selection-item-id"));
    }

    @Test
    public void testWriteMultiListSelect() {
        ListSelect ls = createMultiSelect();
        Element e = new Element(Tag.valueOf("v-list-select"), "");
        ls.writeDesign(e, new DesignContext());
        assertEquals("Null selection should not be allowed.", "false",
                e.attr("null-selection-allowed"));
        Assert.assertTrue(
                "Multi select should be allowed.",
                "".equals(e.attr("multi-select"))
                        || "true".equals(e.attr("multi-select")));
    }

    public ComboBox createSingleSelectWithOnlyAttributes() {
        ComboBox cb = new ComboBox();
        Container dataSource = new IndexedContainer();
        dataSource.addContainerProperty("icon", Resource.class, null);
        cb.setContainerDataSource(dataSource);
        cb.setCaption("A combo box");
        cb.setNewItemsAllowed(true);
        cb.setItemCaptionMode(ItemCaptionMode.ICON_ONLY);
        cb.setItemIconPropertyId("icon");
        cb.setNullSelectionAllowed(true);
        cb.setNullSelectionItemId("No item selected");
        return cb;
    }

    public ListSelect createMultiSelect() {
        ListSelect ls = new ListSelect();
        ls.setNullSelectionAllowed(false);
        ls.setMultiSelect(true);
        return ls;
    }

}