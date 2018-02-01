package com.vaadin.tests.components.treegrid;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.RandomStringUtils;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TreeGrid;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class TreeGridExpandCollapseRecursively extends AbstractTestUI {

    private static class Directory {

        private String name;
        private Directory parent;
        private List<Directory> subDirectories = new ArrayList<>();

        public Directory(String name, Directory parent) {
            this.name = name;
            this.parent = parent;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Directory getParent() {
            return parent;
        }

        public void setParent(
                Directory parent) {
            this.parent = parent;
        }

        public List<Directory> getSubDirectories() {
            return subDirectories;
        }

        public void setSubDirectories(List<Directory> subDirectories) {
            this.subDirectories = subDirectories;
        }
    }

    private static final int DEPTH = 4;
    private static final int CHILDREN = 5;

    @Override
    protected void setup(VaadinRequest request) {

        Collection<Directory> roots = generateDirectoryStructure(DEPTH);

        TreeGrid<Directory> grid = new TreeGrid<>();
        grid.addColumn(Directory::getName);

        grid.setItems(roots, Directory::getSubDirectories);

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.addComponent(new Button("Expand all", e -> grid
                .expandRecursively(roots, DEPTH - 1)));
        buttons.addComponent(new Button("Collapse all", e -> grid
                .collapseRecursively(roots, DEPTH - 1)));

        addComponents(buttons, grid);
    }

    private Collection<Directory> generateDirectoryStructure(int depth) {
        return generateDirectories(depth, null, CHILDREN);
    }

    private Collection<Directory> generateDirectories(int depth, Directory parent, int childCount) {
        Collection<Directory> dirs = new ArrayList<>();
        if (depth >= 0) {
            for (int i = 0; i < childCount; i++) {
                Directory dir = new Directory(RandomStringUtils.randomAlphabetic(20), parent);
                if (parent != null) {
                    parent.getSubDirectories().add(dir);
                }
                dirs.add(dir);

                generateDirectories(depth - 1, dir, childCount);
            }
        }
        return dirs;
    }
}
