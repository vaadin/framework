package com.vaadin.tests.server.component.tree;

import java.io.ByteArrayInputStream;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.data.SelectionModel;
import com.vaadin.data.TreeData;
import com.vaadin.data.provider.HierarchicalQuery;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.tests.server.component.abstractcomponent.AbstractComponentDeclarativeTestBase;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.IconGenerator;
import com.vaadin.ui.ItemCaptionGenerator;
import com.vaadin.ui.Tree;
import com.vaadin.ui.declarative.Design;

public class TreeDeclarativeTest
        extends AbstractComponentDeclarativeTestBase<Tree> {

    @Test
    public void dataSerialization() {
        Person person1 = createPerson("a");
        Person person2 = createPerson("a/a");
        Person person3 = createPerson("a/b");
        Person person4 = createPerson("a/b/c");
        Person person5 = createPerson("b");

        TreeData<Person> data = new TreeData<>();
        data.addItems(null, person1, person5);
        data.addItems(person1, person2, person3);
        data.addItem(person3, person4);

        Tree<Person> tree = new Tree<>();
        tree.setTreeData(data);
        tree.setItemCaptionGenerator(item -> item.getFirstName());

        String designString = String.format(
                "<%s>" + "<node item='%s'>%s</node>"
                        + "<node item='%s' parent='%s'>%s</node>"
                        + "<node item='%s' parent='%s'>%s</node>"
                        + "<node item='%s' parent='%s'>%s</node>"
                        + "<node item='%s'>%s</node>" + "</%s>",
                getComponentTag(), person1.toString(), person1.getFirstName(),
                person2.toString(), person1.toString(), person2.getFirstName(),
                person3.toString(), person1.toString(), person3.getFirstName(),
                person4.toString(), person3.toString(), person4.getFirstName(),
                person5.toString(), person5.getFirstName(), getComponentTag());

        testWrite(designString, tree, true);
        Tree<String> readTree = testRead(designString, tree);
        assertEquals(2, readTree.getDataProvider()
                .getChildCount(new HierarchicalQuery<>(null, null)));
        assertEquals(2, readTree.getDataProvider().getChildCount(
                new HierarchicalQuery<>(null, person1.toString())));
        assertEquals(1, readTree.getDataProvider().getChildCount(
                new HierarchicalQuery<>(null, person3.toString())));
    }

    @Test
    public void htmlContentMode() {
        Person person = createPerson("A Person");
        Tree<Person> tree = new Tree<>();
        tree.setItems(person);
        tree.setItemCaptionGenerator(
                item -> String.format("<b>%s</b>", item.getFirstName()));
        tree.setContentMode(ContentMode.HTML);

        String designString = String.format(
                "<%s content-mode='html'><node item='%s'><b>%s</b></node></%s>",
                getComponentTag(), person.toString(), person.getFirstName(),
                getComponentTag());

        testWrite(designString, tree, true);
        testRead(designString, tree);
    }

    @Test
    public void selectionMode() {
        Tree<Person> tree = new Tree<>();
        tree.setSelectionMode(SelectionMode.MULTI);

        String designString = String.format("<%s selection-mode='multi'></%s>",
                getComponentTag(), getComponentTag());

        testRead(designString, tree);
        testWrite(designString, tree, false);
    }

    @Test
    @Override
    public void heightFullDeserialization()
            throws InstantiationException, IllegalAccessException {
        // width is full by default
        String design = String.format("<%s size-full/>", getComponentTag());

        Tree<String> tree = new Tree<>();

        tree.setHeight("100%");
        testRead(design, tree);
        testWrite(design, tree);
    }

    @Test
    @Override
    public void sizeUndefinedDeserialization()
            throws InstantiationException, IllegalAccessException {
        String design = String.format("<%s size-auto/>", getComponentTag());

        Tree<String> tree = new Tree<>();

        tree.setSizeUndefined();
        testRead(design, tree);
        testWrite(design, tree);
    }

    @Test
    @Override
    public void widthFullDeserialization()
            throws InstantiationException, IllegalAccessException {
        // width is full by default
        String design = String.format("<%s/>", getComponentTag());

        Tree<String> tree = new Tree<>();

        tree.setWidth("100%");
        testRead(design, tree);
        testWrite(design, tree);
    }

    @Test
    @Override
    public void widthUndefinedDeserialization()
            throws InstantiationException, IllegalAccessException {
        String design = String.format("<%s size-auto/>", getComponentTag());

        Tree<String> tree = new Tree<>();

        tree.setWidthUndefined();
        testRead(design, tree);
        testWrite(design, tree);
    }

    @Test
    public void testUpdateExisting() {
        Tree tree = new Tree();

        String treeDesign = "<vaadin-tree selection-mode=\"MULTI\">"
                + "<node item=\"A\">A</node>" + "<node item=\"B\">B</node>"
                + "<node item=\"AA\" parent=\"A\">AA</node>" + "</vaadin-tree>";

        Design.read(new ByteArrayInputStream(treeDesign.getBytes()), tree);
        Object[] items = tree.getDataProvider()
                .fetchChildren(new HierarchicalQuery(null, null)).toArray();
        assertArrayEquals(new Object[] { "A", "B" }, items);
        Object[] itemsA = tree.getDataProvider()
                .fetchChildren(new HierarchicalQuery(null, "A")).toArray();
        assertArrayEquals(new Object[] { "AA" }, itemsA);
        long countB = tree.getDataProvider()
                .fetchChildren(new HierarchicalQuery(null, "B")).count();
        assertEquals(0L, countB);
        assertTrue(tree.getSelectionModel() instanceof SelectionModel.Multi);
    }

    @Override
    protected String getComponentTag() {
        return "vaadin-tree";
    }

    @Override
    protected Class<? extends Tree> getComponentClass() {
        return Tree.class;
    }

    @Override
    protected void assertEquals(String message, Object o1, Object o2) {
        if (o1 instanceof ItemCaptionGenerator) {
            assertTrue(o2 instanceof ItemCaptionGenerator);
            return;
        }
        if (o1 instanceof IconGenerator) {
            assertTrue(o2 instanceof IconGenerator);
            return;
        }
        super.assertEquals(message, o1, o2);
    }

    private Person createPerson(String name) {
        Person person = new Person();
        person.setFirstName(name);
        return person;
    }
}
