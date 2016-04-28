package com.vaadin.tests.components.uitest;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.tests.components.uitest.components.AccordionsCssTest;
import com.vaadin.tests.components.uitest.components.ButtonsCssTest;
import com.vaadin.tests.components.uitest.components.DatesCssTest;
import com.vaadin.tests.components.uitest.components.EmbeddedCssTest;
import com.vaadin.tests.components.uitest.components.FormsCssTest;
import com.vaadin.tests.components.uitest.components.LabelsCssTest;
import com.vaadin.tests.components.uitest.components.LayoutsCssTest;
import com.vaadin.tests.components.uitest.components.NotificationsCssTest;
import com.vaadin.tests.components.uitest.components.SelectsCssTest;
import com.vaadin.tests.components.uitest.components.SlidersCssTest;
import com.vaadin.tests.components.uitest.components.TabSheetsCssTest;
import com.vaadin.tests.components.uitest.components.TablesCssTest;
import com.vaadin.tests.components.uitest.components.TextFieldsCssTest;
import com.vaadin.tests.components.uitest.components.TreeCssTest;
import com.vaadin.tests.components.uitest.components.TreeTableCssTest;
import com.vaadin.tests.components.uitest.components.UploadCssTest;
import com.vaadin.tests.components.uitest.components.WindowsCssTest;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

/**
 * Test sampler that creates a tabsheet of "all" the Vaadin UI components. This
 * can be used to test themes and components in general.
 */
public class TestSampler extends TabSheet {
    public static final String ICON_URL = "../runo/icons/16/help.png";

    private List<Component> components = new ArrayList<Component>();

    private ComponentContainer currentTab;

    public TestSampler() {
        setId("testsampler");

        createLabels();
        createButtons();
        createEmbedded();
        createPopupDates();
        createTextFields();
        createSelects();
        createSliders();
        createUploads();
        createForms();

        createTables();
        createTrees();
        createTreeTable();

        createLayouts();
        createTabSheets();
        createAccordions();

        createWindows();
        createNotifications();

    }

    private void createNotifications() {
        NotificationsCssTest notificationsTest = new NotificationsCssTest(this);
        createComponentLayout("Notifications", notificationsTest);
    }

    private void createWindows() {
        WindowsCssTest windows = new WindowsCssTest(this);
        createComponentLayout("Windows", windows);
    }

    private void createAccordions() {
        GridLayout grid = createGridLayoutBase();
        createComponentLayout("Accordions", grid);
        new AccordionsCssTest(this);
    }

    private void createTabSheets() {
        GridLayout grid = createGridLayoutBase();
        createComponentLayout("TabSheets", grid);
        new TabSheetsCssTest(this);
    }

    private GridLayout createGridLayoutBase() {
        GridLayout grid = new GridLayout();
        grid.setColumns(3);
        grid.setWidth("100%");
        return grid;
    }

    private void createLayouts() {
        GridLayout grid = new LayoutsCssTest(this);
        createComponentLayout("Layouts", grid);
    }

    private void createTreeTable() {
        createComponentLayout("TreeTable");
        new TreeTableCssTest(this);
    }

    private void createTrees() {
        createComponentLayout("Trees");
        new TreeCssTest(this);
    }

    private void createTables() {
        createComponentLayout("Tables", new TablesCssTest(this));
    }

    private void createForms() {
        createComponentLayout("Forms", new FormsCssTest(this));
    }

    private void createUploads() {
        createComponentLayout("Uploads");
        new UploadCssTest(this);
    }

    private void createSliders() {
        createComponentLayout("Sliders");
        new SlidersCssTest(this);

    }

    private void createSelects() {
        createComponentLayout("Selects", new SelectsCssTest(this));
    }

    private void createTextFields() {
        createComponentLayout("TextFields", new TextFieldsCssTest(this));
    }

    private void createPopupDates() {
        createComponentLayout("Dates", new DatesCssTest(this));
    }

    private void createEmbedded() {
        createComponentLayout("Embedded");
        new EmbeddedCssTest(this);

    }

    private void createButtons() {
        createComponentLayout("Buttons", new ButtonsCssTest(this));
    }

    private void createLabels() {
        createComponentLayout("Labels", new LabelsCssTest(this));
    }

    private void createComponentLayout(String caption) {

        HorizontalLayout hl = new HorizontalLayout();
        hl.setSpacing(true);
        hl.setWidth("100%");

        createComponentLayout(caption, hl);
    }

    private void createComponentLayout(String caption, ComponentContainer layout) {
        addTab(layout, caption);
        currentTab = layout;
    }

    @Override
    public void addComponent(Component c) {

        currentTab.addComponent(c);
        components.add(c);
    }

    /**
     * Register a component to the TestSampler for style name changes/additions.
     * 
     * @param component
     */
    public void registerComponent(Component component) {
        components.add(component);
    }

    public void addWindow(Window window) {
        UI.getCurrent().addWindow(window);
    }

    public void setCustomStyleNameToComponents(String oldStyleName,
            String newStyleName) {
        for (Component c : components) {
            if (oldStyleName != null) {
                c.removeStyleName(oldStyleName);
            }
            c.addStyleName(newStyleName);
        }
    }
}
