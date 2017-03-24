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
package com.vaadin.tests.server.component.grid;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;
import org.jsoup.select.Selector;
import org.junit.Assert;
import org.junit.Test;

import com.vaadin.data.SelectionModel.Multi;
import com.vaadin.data.SelectionModel.Single;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.Query;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.tests.data.bean.Address;
import com.vaadin.tests.data.bean.Country;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.tests.data.bean.Sex;
import com.vaadin.tests.server.component.abstractlisting.AbstractListingDeclarativeTest;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.components.grid.FooterCell;
import com.vaadin.ui.components.grid.FooterRow;
import com.vaadin.ui.components.grid.HeaderCell;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.declarative.DesignContext;
import com.vaadin.ui.declarative.DesignException;

/**
 * @author Vaadin Ltd
 *
 */
public class GridDeclarativeTest extends AbstractListingDeclarativeTest<Grid> {

    @Test
    public void gridAttributes() {
        Grid<Person> grid = new Grid<>();
        int frozenColumns = 1;
        HeightMode heightMode = HeightMode.ROW;
        double heightByRows = 13.7d;

        grid.addColumn(Person::getFirstName).setCaption("First Name");
        grid.addColumn(Person::getLastName).setId("id").setCaption("Id");

        grid.setFrozenColumnCount(frozenColumns);
        grid.setSelectionMode(SelectionMode.MULTI);
        grid.setHeightMode(heightMode);
        grid.setHeightByRows(heightByRows);

        String design = String.format(
                "<%s height-mode='%s' frozen-columns='%d' rows='%s' selection-mode='%s'><table><colgroup>"
                        + "<col column-id='column0' sortable>"
                        + "<col column-id='id' sortable>" + "</colgroup><thead>"
                        + "<tr default><th plain-text column-ids='column0'>First Name</th>"
                        + "<th plain-text column-ids='id'>Id</th></tr>"
                        + "</thead></table></%s>",
                getComponentTag(),
                heightMode.toString().toLowerCase(Locale.ENGLISH),
                frozenColumns, heightByRows,
                SelectionMode.MULTI.toString().toLowerCase(Locale.ENGLISH),
                getComponentTag());

        testRead(design, grid);
        testWrite(design, grid);
    }

    @Test
    public void mergedHeaderCells() {
        Grid<Person> grid = new Grid<>();

        Column<Person, String> column1 = grid.addColumn(Person::getFirstName)
                .setCaption("First Name");
        Column<Person, String> column2 = grid.addColumn(Person::getLastName)
                .setId("id").setCaption("Id");
        Column<Person, String> column3 = grid.addColumn(Person::getEmail)
                .setId("mail").setCaption("Mail");

        HeaderRow header = grid.addHeaderRowAt(1);
        String headerRowText1 = "foo";
        header.getCell(column1).setText(headerRowText1);
        HeaderCell cell2 = header.getCell(column2);
        HeaderCell join = header.join(cell2, header.getCell(column3));
        String headerRowText3 = "foobar";
        join.setText(headerRowText3);

        String design = String.format(
                "<%s><table><colgroup>" + "<col column-id='column0' sortable>"
                        + "<col column-id='id' sortable>"
                        + "<col column-id='mail' sortable>"
                        + "</colgroup><thead>"
                        + "<tr default><th plain-text column-ids='column0'>First Name</th>"
                        + "<th plain-text column-ids='id'>Id</th>"
                        + "<th plain-text column-ids='mail'>Mail</th></tr>"
                        + "<tr><th plain-text column-ids='column0'>%s</th>"
                        + "<th colspan='2' plain-text column-ids='id,mail'>foobar</th></tr>"
                        + "</thead></table></%s>",
                getComponentTag(), headerRowText1, headerRowText3,
                getComponentTag());

        testRead(design, grid);
        testWrite(design, grid);
    }

    @Test
    public void mergedFooterCells() {
        Grid<Person> grid = new Grid<>();

        Column<Person, String> column1 = grid.addColumn(Person::getFirstName)
                .setCaption("First Name");
        Column<Person, String> column2 = grid.addColumn(Person::getLastName)
                .setId("id").setCaption("Id");
        Column<Person, String> column3 = grid.addColumn(Person::getEmail)
                .setId("mail").setCaption("Mail");

        FooterRow footer = grid.addFooterRowAt(0);

        FooterCell cell1 = footer.getCell(column1);
        String footerRowText1 = "foo";
        cell1.setText(footerRowText1);

        FooterCell cell2 = footer.getCell(column2);

        FooterCell cell3 = footer.getCell(column3);
        String footerRowText2 = "foobar";
        footer.join(cell2, cell3).setHtml(footerRowText2);

        String design = String.format(
                "<%s><table><colgroup>" + "<col column-id='column0' sortable>"
                        + "<col column-id='id' sortable>"
                        + "<col column-id='mail' sortable>"
                        + "</colgroup><thead>"
                        + "<tr default><th plain-text column-ids='column0'>First Name</th>"
                        + "<th plain-text column-ids='id'>Id</th>"
                        + "<th plain-text column-ids='mail'>Mail</th></tr></thead>"
                        + "<tfoot><tr><td plain-text column-ids='column0'>%s</td>"
                        + "<td colspan='2' column-ids='id,mail'>%s</td></tr></tfoot>"
                        + "</table></%s>",
                getComponentTag(), footerRowText1, footerRowText2,
                getComponentTag());

        testRead(design, grid);
        testWrite(design, grid);
    }

    @Test
    public void columnAttributes() {
        Grid<Person> grid = new Grid<>();

        String secondColumnId = "id";
        Column<Person, String> column1 = grid.addColumn(Person::getFirstName)
                .setCaption("First Name");
        Column<Person, String> column2 = grid.addColumn(Person::getLastName)
                .setId(secondColumnId).setCaption("Id");

        String caption = "test-caption";
        column1.setCaption(caption);
        boolean sortable = false;
        column1.setSortable(sortable);
        boolean editable = true;
        column1.setEditorComponent(new TextField(), Person::setLastName);
        column1.setEditable(editable);
        boolean resizable = false;
        column1.setResizable(resizable);
        boolean hidable = true;
        column1.setHidable(hidable);
        boolean hidden = true;
        column1.setHidden(hidden);

        String hidingToggleCaption = "toggle-caption";
        column2.setHidingToggleCaption(hidingToggleCaption);
        double width = 17.3;
        column2.setWidth(width);
        double minWidth = 37.3;
        column2.setMinimumWidth(minWidth);
        double maxWidth = 63.4;
        column2.setMaximumWidth(maxWidth);
        int expandRatio = 83;
        column2.setExpandRatio(expandRatio);

        String design = String.format(
                "<%s><table><colgroup>"
                        + "<col column-id='column0' sortable='%s' editable resizable='%s' hidable hidden>"
                        + "<col column-id='id' sortable hiding-toggle-caption='%s' width='%s' min-width='%s' max-width='%s' expand='%s'>"
                        + "</colgroup><thead>"
                        + "<tr default><th plain-text column-ids='column0'>%s</th>"
                        + "<th plain-text column-ids='id'>%s</th>"
                        + "</tr></thead>" + "</table></%s>",
                getComponentTag(), sortable, resizable, hidingToggleCaption,
                width, minWidth, maxWidth, expandRatio, caption, "Id",
                getComponentTag());

        testRead(design, grid, true);
        testWrite(design, grid);
    }

    @Test
    public void headerFooterSerialization() {
        Grid<Person> grid = new Grid<>();

        Column<Person, String> column1 = grid.addColumn(Person::getFirstName)
                .setCaption("First Name");
        Column<Person, String> column2 = grid.addColumn(Person::getLastName)
                .setId("id").setCaption("Id");

        FooterRow footerRow = grid.addFooterRowAt(0);
        footerRow.getCell(column1).setText("x");
        footerRow.getCell(column2).setHtml("y");

        String design = String.format(
                "<%s><table><colgroup>" + "<col column-id='column0' sortable>"
                        + "<col column-id='id' sortable></colgroup><thead>"
                        + "<tr default><th plain-text column-ids='column0'>First Name</th>"
                        + "<th plain-text column-ids='id'>Id</th></tr>"
                        + "</thead><tbody></tbody>"
                        + "<tfoot><tr><td plain-text column-ids='column0'>x</td>"
                        + "<td column-ids='id'>y</td></tr></tfoot>"
                        + "</table></%s>",
                getComponentTag(), getComponentTag());

        testRead(design, grid);
        testWrite(design, grid, true);
    }

    @Override
    public void dataSerialization() throws InstantiationException,
            IllegalAccessException, InvocationTargetException {
        Grid<Person> grid = new Grid<>();

        Person person1 = createPerson("foo", "bar");
        Person person2 = createPerson("name", "last-name");
        grid.setItems(person1, person2);

        grid.addColumn(Person::getFirstName).setCaption("First Name");
        grid.addColumn(Person::getLastName).setId("id").setCaption("Id");

        String design = String.format(
                "<%s><table><colgroup>" + "<col column-id='column0' sortable>"
                        + "<col column-id='id' sortable></colgroup><thead>"
                        + "<tr default><th plain-text column-ids='column0'>First Name</th>"
                        + "<th plain-text column-ids='id'>Id</th></tr>"
                        + "</thead><tbody>"
                        + "<tr item='%s'><td>%s</td><td>%s</td></tr>"
                        + "<tr item='%s'><td>%s</td><td>%s</td></tr>"
                        + "</tbody></table></%s>",
                getComponentTag(), person1.toString(), person1.getFirstName(),
                person1.getLastName(), person2.toString(),
                person2.getFirstName(), person2.getLastName(),
                getComponentTag());

        Grid<?> readGrid = testRead(design, grid, true, true);
        Assert.assertEquals(2, readGrid.getDataProvider().size(new Query<>()));
        testWrite(design, grid, true);
    }

    /**
     * Value for single select
     */
    @Override
    @Test
    public void valueSerialization() throws InstantiationException,
            IllegalAccessException, InvocationTargetException {
        valueSingleSelectSerialization();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void valueMultiSelectSerialization() throws InstantiationException,
            IllegalAccessException, InvocationTargetException {
        Grid<Person> grid = new Grid<>();

        Person person1 = createPerson("foo", "bar");
        Person person2 = createPerson("name", "last-name");
        Person person3 = createPerson("foo", "last-name");
        grid.setItems(person1, person2, person3);

        grid.addColumn(Person::getFirstName).setCaption("First Name");
        grid.addColumn(Person::getLastName).setId("id").setCaption("Id");

        Multi<Person> model = (Multi<Person>) grid
                .setSelectionMode(SelectionMode.MULTI);
        model.selectItems(person1, person3);

        String design = String.format(
                "<%s selection-mode='multi'><table><colgroup>"
                        + "<col column-id='column0' sortable>"
                        + "<col column-id='id' sortable></colgroup><thead>"
                        + "<tr default><th plain-text column-ids='column0'>First Name</th>"
                        + "<th plain-text column-ids='id'>Id</th></tr>"
                        + "</thead><tbody>"
                        + "<tr item='%s' selected><td>%s</td><td>%s</td></tr>"
                        + "<tr item='%s'><td>%s</td><td>%s</td></tr>"
                        + "<tr item='%s' selected><td>%s</td><td>%s</td></tr>"
                        + "</tbody></table></%s>",
                getComponentTag(), person1.toString(), person1.getFirstName(),
                person1.getLastName(), person2.toString(),
                person2.getFirstName(), person2.getLastName(),
                person3.toString(), person3.getFirstName(),
                person3.getLastName(), getComponentTag());

        Grid<?> readGrid = testRead(design, grid, true, true);
        Assert.assertEquals(3, readGrid.getDataProvider().size(new Query<>()));
        testWrite(design, grid, true);
    }

    @SuppressWarnings("unchecked")
    private void valueSingleSelectSerialization() throws InstantiationException,
            IllegalAccessException, InvocationTargetException {
        Grid<Person> grid = new Grid<>();

        Person person1 = createPerson("foo", "bar");
        Person person2 = createPerson("name", "last-name");
        grid.setItems(person1, person2);

        grid.addColumn(Person::getFirstName).setCaption("First Name");
        grid.addColumn(Person::getLastName).setId("id").setCaption("Id");

        Single<Person> model = (Single<Person>) grid
                .setSelectionMode(SelectionMode.SINGLE);
        model.select(person2);

        String design = String.format(
                "<%s><table><colgroup>" + "<col column-id='column0' sortable>"
                        + "<col column-id='id' sortable></colgroup><thead>"
                        + "<tr default><th plain-text column-ids='column0'>First Name</th>"
                        + "<th plain-text column-ids='id'>Id</th></tr>"
                        + "</thead><tbody>"
                        + "<tr item='%s'><td>%s</td><td>%s</td></tr>"
                        + "<tr item='%s' selected><td>%s</td><td>%s</td></tr>"
                        + "</tbody></table></%s>",
                getComponentTag(), person1.toString(), person1.getFirstName(),
                person1.getLastName(), person2.toString(),
                person2.getFirstName(), person2.getLastName(),
                getComponentTag());

        Grid<?> readGrid = testRead(design, grid, true, true);
        Assert.assertEquals(2, readGrid.getDataProvider().size(new Query<>()));
        testWrite(design, grid, true);
    }

    @Override
    public void readOnlySelection() throws InstantiationException,
            IllegalAccessException, InvocationTargetException {
        Grid<Person> grid = new Grid<>();

        Person person1 = createPerson("foo", "bar");
        Person person2 = createPerson("name", "last-name");
        grid.setItems(person1, person2);

        grid.addColumn(Person::getFirstName).setCaption("First Name");
        grid.addColumn(Person::getLastName).setId("id").setCaption("Id");

        grid.setSelectionMode(SelectionMode.MULTI);
        grid.asMultiSelect().setReadOnly(true);

        String formatString = "<%s %s selection-allowed><table><colgroup>"
                + "<col column-id='column0' sortable>"
                + "<col column-id='id' sortable>" + "</colgroup><thead>"
                + "<tr default><th plain-text column-ids='column0'>First Name</th>"
                + "<th plain-text column-ids='id'>Id</th></tr>"
                + "</thead><tbody>"
                + "<tr item='%s'><td>%s</td><td>%s</td></tr>"
                + "<tr item='%s'><td>%s</td><td>%s</td></tr>"
                + "</tbody></table></%s>";

        String design = String.format(formatString, getComponentTag(),
                "selection-mode='multi'", person1.toString(),
                person1.getFirstName(), person1.getLastName(),
                person2.toString(), person2.getFirstName(),
                person2.getLastName(), getComponentTag());

        Grid<?> readGrid = testRead(design, grid, true, true);
        Assert.assertEquals(2, readGrid.getDataProvider().size(new Query<>()));
        testWrite(design, grid, true);

        grid.setSelectionMode(SelectionMode.SINGLE);
        grid.asSingleSelect().setReadOnly(true);

        design = String.format(formatString, getComponentTag(), "",
                person1.toString(), person1.getFirstName(),
                person1.getLastName(), person2.toString(),
                person2.getFirstName(), person2.getLastName(),
                getComponentTag());

        readGrid = testRead(design, grid, true, true);
        Assert.assertEquals(2, readGrid.getDataProvider().size(new Query<>()));
        testWrite(design, grid, true);
    }

    @Test
    public void testComponentInGridHeader() {
        Grid<Person> grid = new Grid<>();
        Column<Person, String> column = grid.addColumn(Person::getFirstName)
                .setCaption("First Name");

        String html = "<b>Foo</b>";
        Label component = new Label(html);
        component.setContentMode(ContentMode.HTML);

        //@formatter:off
        String design = String.format( "<%s><table>"
                + "<colgroup>"
                + "   <col sortable column-id='column0'>"
                + "</colgroup>"
                + "<thead>"
                + "<tr default><th column-ids='column0'><vaadin-label>%s</vaadin-label></th></tr>"
                + "</thead>"
                + "</table></%s>", getComponentTag(), html, getComponentTag());
        //@formatter:on

        grid.getDefaultHeaderRow().getCell(column).setComponent(component);

        testRead(design, grid, true);
        testWrite(design, grid);
    }

    @Test
    public void testComponentInGridFooter() {
        Grid<Person> grid = new Grid<>();
        Column<Person, String> column = grid.addColumn(Person::getFirstName)
                .setCaption("First Name");

        String html = "<b>Foo</b>";
        Label component = new Label(html);
        component.setContentMode(ContentMode.HTML);

        grid.prependFooterRow().getCell(column).setComponent(component);
        grid.removeHeaderRow(grid.getDefaultHeaderRow());

      //@formatter:off
        String design = String.format( "<%s><table>"
                + "<colgroup>"
                + "   <col sortable column-id='column0'>"
                + "</colgroup>"
                + "<thead>"
                +"<tfoot>"
                + "<tr><td column-ids='column0'><vaadin-label>%s</vaadin-label></td></tr>"
                + "</tfoot>"
                + "</table>"
                + "</%s>", getComponentTag(), html, getComponentTag());
        //@formatter:on

        testRead(design, grid, true);
        testWrite(design, grid);
    }

    @Test
    public void testNoHeaderRows() {
        //@formatter:off
        String design = "<vaadin-grid><table>"
                + "<colgroup>"
                + "   <col sortable column-id='column0'>"
                + "</colgroup>"
                + "<thead />"
                + "</table>"
                + "</vaadin-grid>";
        //@formatter:on
        Grid<Person> grid = new Grid<>();
        grid.addColumn(Person::getFirstName).setCaption("First Name");
        grid.removeHeaderRow(grid.getDefaultHeaderRow());

        testWrite(design, grid);
        testRead(design, grid, true);
    }

    @Test
    public void testReadEmptyGrid() {
        String design = "<vaadin-grid />";
        testRead(design, new Grid<String>(), false);
    }

    @Test
    public void testEmptyGrid() {
        String design = "<vaadin-grid></vaadin-grid>";
        Grid<String> expected = new Grid<>();
        testWrite(design, expected);
        testRead(design, expected, true);
    }

    @Test(expected = DesignException.class)
    public void testMalformedGrid() {
        String design = "<vaadin-grid><vaadin-label /></vaadin-grid>";
        testRead(design, new Grid<String>());
    }

    @Test(expected = DesignException.class)
    public void testGridWithNoColGroup() {
        String design = "<vaadin-grid><table><thead><tr><th>Foo</tr></thead></table></vaadin-grid>";
        testRead(design, new Grid<String>());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testHtmlEntitiesinGridHeaderFooter() {
        String id = "> id";
        String plainText = "plain-text";
        //@formatter:off
        String design = String.format( "<%s><table>"
                + "<colgroup>"
                + "  <col sortable column-id='%s'>"
                + "</colgroup>"
                + "<thead>"
                +"   <tr default><th %s column-ids='%s'>&gt; Test</th>"
                + "</thead>"
                + "<tfoot>"
                + "<tr><td %s column-ids='%s'>&gt; Test</td></tr>"
                + "</tfoot>"
                + "<tbody />"
                + "</table></%s>",
                getComponentTag() , id, plainText, id, plainText, id, getComponentTag());
        //@formatter:on

        Grid<Person> grid = read(design);
        String actualHeader = grid.getHeaderRow(0).getCell(id).getText();
        String actualFooter = grid.getFooterRow(0).getCell(id).getText();
        String expected = "> Test";

        Assert.assertEquals(expected, actualHeader);
        Assert.assertEquals(expected, actualFooter);

        design = design.replace(plainText, "");
        grid = read(design);
        actualHeader = grid.getHeaderRow(0).getCell(id).getHtml();
        actualFooter = grid.getFooterRow(0).getCell(id).getHtml();
        expected = "&gt; Test";

        Assert.assertEquals(expected, actualHeader);
        Assert.assertEquals(expected, actualFooter);

        grid = new Grid<>();
        Column<Person, String> column = grid.addColumn(Person::getFirstName)
                .setId(id);
        HeaderRow header = grid.addHeaderRowAt(0);
        FooterRow footer = grid.addFooterRowAt(0);
        grid.removeHeaderRow(grid.getDefaultHeaderRow());

        // entities should be encoded when writing back, not interpreted as HTML
        header.getCell(column).setText("&amp; Test");
        footer.getCell(column).setText("&amp; Test");

        Element root = new Element(Tag.valueOf(getComponentTag()), "");
        grid.writeDesign(root, new DesignContext());

        Assert.assertEquals("&amp;amp; Test",
                root.getElementsByTag("th").get(0).html());
        Assert.assertEquals("&amp;amp; Test",
                root.getElementsByTag("td").get(0).html());

        header = grid.addHeaderRowAt(0);
        footer = grid.addFooterRowAt(0);

        // entities should not be encoded, this is already given as HTML
        header.getCell(id).setHtml("&amp; Test");
        footer.getCell(id).setHtml("&amp; Test");

        root = new Element(Tag.valueOf(getComponentTag()), "");
        grid.writeDesign(root, new DesignContext());

        Assert.assertEquals("&amp; Test",
                root.getElementsByTag("th").get(0).html());
        Assert.assertEquals("&amp; Test",
                root.getElementsByTag("td").get(0).html());

    }

    @SuppressWarnings("rawtypes")
    @Override
    public Grid<?> testRead(String design, Grid expected) {
        return testRead(design, expected, false);
    }

    @Override
    @SuppressWarnings("rawtypes")
    public Grid<?> testRead(String design, Grid expected, boolean retestWrite) {
        return testRead(design, expected, retestWrite, false);
    }

    @SuppressWarnings("rawtypes")
    public Grid<?> testRead(String design, Grid expected, boolean retestWrite,
            boolean writeData) {
        Grid<?> actual = super.testRead(design, expected);

        compareGridColumns(expected, actual);
        compareHeaders(expected, actual);
        compareFooters(expected, actual);

        if (retestWrite) {
            testWrite(design, actual, writeData);
        }

        return actual;
    }

    private void compareHeaders(Grid<?> expected, Grid<?> actual) {
        Assert.assertEquals("Different header row count",
                expected.getHeaderRowCount(), actual.getHeaderRowCount());
        for (int i = 0; i < expected.getHeaderRowCount(); ++i) {
            HeaderRow expectedRow = expected.getHeaderRow(i);
            HeaderRow actualRow = actual.getHeaderRow(i);

            if (expectedRow.equals(expected.getDefaultHeaderRow())) {
                Assert.assertEquals("Different index for default header row",
                        actual.getDefaultHeaderRow(), actualRow);
            }

            for (Column<?, ?> column : expected.getColumns()) {
                String baseError = "Difference when comparing cell for "
                        + column.toString() + " on header row " + i + ": ";
                HeaderCell expectedCell = expectedRow.getCell(column);
                HeaderCell actualCell = actualRow.getCell(column);

                switch (expectedCell.getCellType()) {
                case TEXT:
                    Assert.assertEquals(baseError + "Text content",
                            expectedCell.getText(), actualCell.getText());
                    break;
                case HTML:
                    Assert.assertEquals(baseError + "HTML content",
                            expectedCell.getHtml(), actualCell.getHtml());
                    break;
                case WIDGET:
                    assertEquals(baseError + "Component content",
                            expectedCell.getComponent(),
                            actualCell.getComponent());
                    break;
                }
            }
        }
    }

    private void compareFooters(Grid<?> expected, Grid<?> actual) {
        Assert.assertEquals("Different footer row count",
                expected.getFooterRowCount(), actual.getFooterRowCount());
        for (int i = 0; i < expected.getFooterRowCount(); ++i) {
            FooterRow expectedRow = expected.getFooterRow(i);
            FooterRow actualRow = actual.getFooterRow(i);

            for (Column<?, ?> column : expected.getColumns()) {
                String baseError = "Difference when comparing cell for "
                        + column.toString() + " on footer row " + i + ": ";
                FooterCell expectedCell = expectedRow.getCell(column);
                FooterCell actualCell = actualRow.getCell(column);

                switch (expectedCell.getCellType()) {
                case TEXT:
                    Assert.assertEquals(baseError + "Text content",
                            expectedCell.getText(), actualCell.getText());
                    break;
                case HTML:
                    Assert.assertEquals(baseError + "HTML content",
                            expectedCell.getHtml(), actualCell.getHtml());
                    break;
                case WIDGET:
                    assertEquals(baseError + "Component content",
                            expectedCell.getComponent(),
                            actualCell.getComponent());
                    break;
                }
            }
        }
    }

    private void compareGridColumns(Grid<?> expected, Grid<?> actual) {
        List<?> columns = expected.getColumns();
        List<?> actualColumns = actual.getColumns();
        Assert.assertEquals("Different amount of columns", columns.size(),
                actualColumns.size());
        for (int i = 0; i < columns.size(); ++i) {
            Column<?, ?> col1 = (Column<?, ?>) columns.get(i);
            Column<?, ?> col2 = (Column<?, ?>) actualColumns.get(i);
            String baseError = "Error when comparing columns for property "
                    + col1.getId() + ": ";
            assertEquals(baseError + "Width", col1.getWidth(), col2.getWidth());
            assertEquals(baseError + "Maximum width", col1.getMaximumWidth(),
                    col2.getMaximumWidth());
            assertEquals(baseError + "Minimum width", col1.getMinimumWidth(),
                    col2.getMinimumWidth());
            assertEquals(baseError + "Expand ratio", col1.getExpandRatio(),
                    col2.getExpandRatio());
            assertEquals(baseError + "Sortable", col1.isSortable(),
                    col2.isSortable());
            assertEquals(baseError + "Editable", col1.isEditable(),
                    col2.isEditable());
            assertEquals(baseError + "Hidable", col1.isHidable(),
                    col2.isHidable());
            assertEquals(baseError + "Hidden", col1.isHidden(),
                    col2.isHidden());
            assertEquals(baseError + "HidingToggleCaption",
                    col1.getHidingToggleCaption(),
                    col2.getHidingToggleCaption());
        }
    }

    @Override
    protected String getComponentTag() {
        return "vaadin-grid";
    }

    @Override
    protected Class<? extends Grid> getComponentClass() {
        return Grid.class;
    }

    @Override
    protected boolean acceptProperty(Class<?> clazz, Method readMethod,
            Method writeMethod) {
        if (readMethod != null) {
            Class<?> returnType = readMethod.getReturnType();
            if (HeaderRow.class.equals(returnType)
                    || DataProvider.class.equals(returnType)) {
                return false;
            }
        }
        return super.acceptProperty(clazz, readMethod, writeMethod);
    }

    private Person createPerson(String name, String lastName) {
        Person person = new Person() {
            @Override
            public String toString() {
                return getFirstName() + " " + getLastName();
            }
        };
        person.setFirstName(name);
        person.setLastName(lastName);
        return person;
    }

    @Test
    public void beanItemType() throws Exception {
        Class<Person> beanClass = Person.class;
        String beanClassName = beanClass.getName();
        //@formatter:off
        String design = String.format( "<%s data-item-type=\"%s\"></%s>",
                getComponentTag() , beanClassName, getComponentTag());
        //@formatter:on

        @SuppressWarnings("unchecked")
        Grid<Person> grid = read(design);
        Assert.assertEquals(beanClass, grid.getBeanType());

        testWrite(design, grid);
    }

    @Test
    public void beanGridDefaultColumns() {
        Grid<Person> grid = new Grid<>(Person.class);
        String design = write(grid, false);
        assertDeclarativeColumnCount(11, design);

        Person testPerson = new Person("the first", "the last", "The email", 64,
                Sex.MALE, new Address("the street", 12313, "The city",
                        Country.SOUTH_AFRICA));
        @SuppressWarnings("unchecked")
        Grid<Person> readGrid = read(design);

        assertColumns(11, grid.getColumns(), readGrid.getColumns(), testPerson);
    }

    private void assertDeclarativeColumnCount(int i, String design) {
        Document html = Jsoup.parse(design);
        Elements cols = Selector.select("vaadin-grid", html)
                .select("colgroup > col");
        Assert.assertEquals("Number of columns in the design file", i,
                cols.size());

    }

    private void assertColumns(int expectedCount,
            List<Column<Person, ?>> expectedColumns,
            List<Column<Person, ?>> columns, Person testPerson) {
        Assert.assertEquals(expectedCount, expectedColumns.size());
        Assert.assertEquals(expectedCount, columns.size());
        for (int i = 0; i < expectedColumns.size(); i++) {
            Column<Person, ?> expectedColumn = expectedColumns.get(i);
            Column<Person, ?> column = columns.get(i);

            // Property mapping
            Assert.assertEquals(expectedColumn.getId(), column.getId());
            // Header caption
            Assert.assertEquals(expectedColumn.getCaption(),
                    column.getCaption());

            // Value providers are not stored in the declarative file
            // so this only works for bean properties
            if (column.getId() != null
                    && !column.getId().equals("column" + i)) {
                Assert.assertEquals(
                        expectedColumn.getValueProvider().apply(testPerson),
                        column.getValueProvider().apply(testPerson));
            }
        }

    }

    @Test
    public void beanGridNoColumns() {
        Grid<Person> grid = new Grid<>(Person.class);
        grid.setColumns();
        String design = write(grid, false);
        assertDeclarativeColumnCount(0, design);

        Person testPerson = new Person("the first", "the last", "The email", 64,
                Sex.MALE, new Address("the street", 12313, "The city",
                        Country.SOUTH_AFRICA));
        @SuppressWarnings("unchecked")
        Grid<Person> readGrid = read(design);

        assertColumns(0, grid.getColumns(), readGrid.getColumns(), testPerson);

        // Can add a mapped property
        Assert.assertEquals("The email", readGrid.addColumn("email")
                .getValueProvider().apply(testPerson));
    }

    @Test
    public void beanGridOnlyCustomColumns() {
        // Writes columns without propertyId even though name matches, reads
        // columns without propertyId mapping, can add new columns using
        // propertyId
        Grid<Person> grid = new Grid<>(Person.class);
        grid.setColumns();
        grid.addColumn(Person::getFirstName).setCaption("First Name");
        String design = write(grid, false);
        assertDeclarativeColumnCount(1, design);
        Person testPerson = new Person("the first", "the last", "The email", 64,
                Sex.MALE, new Address("the street", 12313, "The city",
                        Country.SOUTH_AFRICA));
        @SuppressWarnings("unchecked")
        Grid<Person> readGrid = read(design);

        assertColumns(1, grid.getColumns(), readGrid.getColumns(), testPerson);
        // First name should not be mapped to the property
        Assert.assertNull(readGrid.getColumns().get(0).getValueProvider()
                .apply(testPerson));

        // Can add a mapped property
        Assert.assertEquals("the last", readGrid.addColumn("lastName")
                .getValueProvider().apply(testPerson));
    }

    @Test
    public void beanGridOneCustomizedColumn() {
        // Writes columns with propertyId except one without
        // Reads columns to match initial setup
        Grid<Person> grid = new Grid<>(Person.class);
        grid.addColumn(
                person -> person.getFirstName() + " " + person.getLastName())
                .setCaption("First and Last");
        String design = write(grid, false);
        assertDeclarativeColumnCount(12, design);
        Person testPerson = new Person("the first", "the last", "The email", 64,
                Sex.MALE, new Address("the street", 12313, "The city",
                        Country.SOUTH_AFRICA));
        @SuppressWarnings("unchecked")
        Grid<Person> readGrid = read(design);

        assertColumns(12, grid.getColumns(), readGrid.getColumns(), testPerson);
        // First and last name should not be mapped to anything but should exist
        Assert.assertNull(readGrid.getColumns().get(11).getValueProvider()
                .apply(testPerson));

    }
}
