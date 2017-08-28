package com.vaadin.tests.server.component.tree;

import org.junit.Test;

import com.vaadin.data.TreeData;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.tests.server.component.abstractcomponent.AbstractComponentDeclarativeTestBase;
import com.vaadin.ui.Tree;

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
        
        String designString = String.format("<%s>"
                + "<node item='%s'>%s</node>"
                + "<node item='%s' parent='%s'>%s</node>"
                + "<node item='%s' parent='%s'>%s</node>"
                + "<node item='%s' parent='%s'>%s</node>"
                + "<node item='%s'>%s</node>"
                + "</%s>", getComponentTag(),
                person1.toString(), person1.getFirstName(),
                person2.toString(), person1.toString(), person2.getFirstName(),
                person3.toString(), person1.toString(), person3.getFirstName(),
                person4.toString(), person3.toString(), person4.getFirstName(),
                person5.toString(), person5.getFirstName(),
                getComponentTag());
        write(tree, true);
        Tree<String> readTree = testRead(designString, tree);
        testWrite(designString, tree, true);
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

    @Override
    protected String getComponentTag() {
        return "vaadin-tree";
    }

    @Override
    protected Class<? extends Tree> getComponentClass() {
        return Tree.class;
    }

    private Person createPerson(String name) {
        Person person = new Person();
        person.setFirstName(name);
        return person;
    }
}
