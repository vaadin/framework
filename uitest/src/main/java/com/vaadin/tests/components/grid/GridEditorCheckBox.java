package com.vaadin.tests.components.grid;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.renderers.HtmlRenderer;

public class GridEditorCheckBox extends AbstractTestUI {

    @Override
    protected String getTestDescription() {
        return "Editor content alignments should match regular row content "
                + "alignments.<br>(Double-click a row to open the editor.)";
    }

    @Override
    protected void setup(VaadinRequest request) {
        List<Person> items = new ArrayList<>();
        items.add(new Person(true, false, false));
        items.add(new Person(false, true, true));

        CheckBox adminEditor = new CheckBox();
        CheckBox staffEditor = new CheckBox();
        staffEditor.setPrimaryStyleName("my-custom-checkbox");

        final Grid<Person> grid = new Grid<Person>();
        grid.setSelectionMode(Grid.SelectionMode.MULTI);

        grid.addColumn(Person::isAdmin)
                .setEditorComponent(adminEditor, Person::setAdmin)
                .setCaption("Default");
        grid.addColumn(Person::isStaff)
                .setEditorComponent(staffEditor, Person::setAdmin)
                .setCaption("Custom");
        grid.addColumn(Person::isSpecialist).setRenderer(
                s -> "<input type=\"checkbox\"  onclick=\"return false;\""
                        + (s ? "checked " : "") + ">",
                new HtmlRenderer()).setCaption("HTML");
        grid.addColumn(Person::isSpecialist).setRenderer(
                s -> "<span><input type=\"checkbox\"  onclick=\"return false;\""
                        + (s ? "" : "checked ") + "></span>",
                new HtmlRenderer()).setCaption("Spanned");
        grid.getEditor().setBuffered(false);
        grid.getEditor().setEnabled(true);
        grid.setItems(items);

        addComponents(grid);
    }

    public class Person {
        private boolean admin;
        private boolean staff;
        private boolean specialist;

        public Person(boolean admin, boolean staff, boolean specialist) {
            this.admin = admin;
            this.staff = staff;
            this.specialist = specialist;
        }

        public boolean isAdmin() {
            return admin;
        }

        public void setAdmin(final boolean admin) {
            this.admin = admin;
        }

        public boolean isStaff() {
            return staff;
        }

        public void setStaff(final boolean staff) {
            this.staff = staff;
        }

        public boolean isSpecialist() {
            return specialist;
        }

        public void setSpecialist(final boolean specialist) {
            this.specialist = specialist;
        }
    }
}
