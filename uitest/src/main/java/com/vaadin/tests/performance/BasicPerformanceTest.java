package com.vaadin.tests.performance;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.data.bean.Address;
import com.vaadin.tests.data.bean.Country;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.tests.data.bean.Sex;
import com.vaadin.tests.util.TestUtils;
import com.vaadin.tests.widgetset.TestingWidgetSet;
import com.vaadin.tests.widgetset.server.PerformanceExtension;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.TextArea;
import com.vaadin.v7.ui.themes.Reindeer;

@Widgetset(TestingWidgetSet.NAME)
public class BasicPerformanceTest extends UI {

    private int updateOneCount = 0;

    private final VerticalLayout contentLayout = new VerticalLayout();

    private int clientLimit;
    private int serverLimit;
    private PerformanceExtension performanceExtension;
    private boolean reportBootstap = true;
    private String performanceTopic;
    private final Button reportPerformanceButton = new Button(
            "Report some performance", event -> {
                TestUtils.reportPerformance(performanceTopic, serverLimit,
                        clientLimit, reportBootstap);
                event.getButton().setEnabled(false);
                performanceExtension.stop();
            });

    @Override
    public void init(VaadinRequest request) {
        setContent(buildMainLayout());
        updatePerformanceReporting("first load", 100, 100);
        reportBootstap = true;
    }

    private void updatePerformanceReporting(String performanceTopic,
            int serverLimit, int clientLimit) {
        this.performanceTopic = performanceTopic;
        this.serverLimit = serverLimit;
        this.clientLimit = clientLimit;
        reportPerformanceButton
                .setCaption("Report performance for " + performanceTopic);
        reportPerformanceButton.setEnabled(true);
        reportBootstap = false;
    }

    private ComponentContainer buildMainLayout() {
        contentLayout.addComponent(new Label("Content lives here"));

        Panel contentScroller = new Panel(contentLayout);
        contentScroller.setStyleName(Reindeer.PANEL_LIGHT);
        contentScroller.setSizeFull();

        TextArea performanceReportArea = new TextArea();
        performanceReportArea.setWidth("200px");
        TestUtils.installPerformanceReporting(performanceReportArea);

        Label performanceLabel = new Label();
        performanceLabel.setId("performanceLabel");
        performanceExtension = PerformanceExtension.wrap(performanceLabel);

        VerticalLayout leftBar = new VerticalLayout();
        leftBar.setWidth("250px");
        leftBar.addComponent(new Label("This is the left bar"));
        leftBar.addComponent(performanceReportArea);
        leftBar.addComponent(performanceLabel);
        leftBar.addComponent(reportPerformanceButton);

        leftBar.addComponent(new Button("Set 20 panels as content", event -> {
            populateContent(contentLayout, 20, true);
            updatePerformanceReporting("20 panels", 100, 100);
        }));
        leftBar.addComponent(new Button("Set 40 panels as content", event -> {
            populateContent(contentLayout, 40, true);
            updatePerformanceReporting("40 panels", 100, 100);
        }));
        leftBar.addComponent(new Button("Set 40 layouts as content", event -> {
            populateContent(contentLayout, 40, false);
            updatePerformanceReporting("40 layouts", 100, 100);
        }));
        leftBar.addComponent(
                new Button("Set 40 field panels as content", event -> {
                    populateContent(contentLayout, 40, true, true);
                    updatePerformanceReporting("40 field panels", 100, 100);
                }));
        leftBar.addComponent(
                new Button("Set 20 small grids as content", event -> {
                    populateWithGrids(1, 20);
                    updatePerformanceReporting("small grids", 100, 100);
                }));
        leftBar.addComponent(
                new Button("Set 10 medium grids as content", event -> {
                    populateWithGrids(1000, 10);
                    updatePerformanceReporting("medium grids", 100, 100);
                }));
        leftBar.addComponent(
                new Button("Set 5 large grids as content", event -> {
                    populateWithGrids(100000, 5);
                    updatePerformanceReporting("large grids", 100, 100);
                }));

        leftBar.addComponent(new Button("Update all labels", event -> {
            Iterator<Component> componentIterator = contentLayout
                    .getComponentIterator();
            while (componentIterator.hasNext()) {

                Iterator<Component> columHolderIterator;
                Component child = componentIterator.next();
                if (child instanceof Panel) {
                    columHolderIterator = ((ComponentContainer) ((Panel) child)
                            .getContent()).getComponentIterator();
                } else {
                    columHolderIterator = ((ComponentContainer) child)
                            .getComponentIterator();
                }
                while (columHolderIterator.hasNext()) {
                    VerticalLayout column = (VerticalLayout) columHolderIterator
                            .next();
                    Iterator<Component> columnIterator = column
                            .getComponentIterator();
                    while (columnIterator.hasNext()) {
                        Label label = (Label) columnIterator.next();
                        label.setValue("New value");
                    }
                }
            }
            updatePerformanceReporting("Update labels", 100, 100);
        }));

        leftBar.addComponent(new Button("Update one label", event -> {
            Component child = contentLayout.getComponent(0);
            if (child instanceof Panel) {
                Panel panel = (Panel) child;
                child = panel.getContent();
            }

            AbstractOrderedLayout layout = (AbstractOrderedLayout) ((AbstractOrderedLayout) child)
                    .getComponent(0);
            Label label = (Label) layout.getComponent(0);

            label.setValue("New value " + updateOneCount++);

            updatePerformanceReporting("Update one", 10, 10);
        }));

        leftBar.addComponent(new Button("Clear content", event -> {
            contentLayout.removeAllComponents();
            contentLayout.addComponent(new Label("No content"));
            updatePerformanceReporting("No content", 100, 100);
        }));

        HorizontalLayout intermediateLayout = new HorizontalLayout();
        intermediateLayout.setSizeFull();
        intermediateLayout.addComponent(leftBar);
        intermediateLayout.addComponent(contentScroller);
        intermediateLayout.setExpandRatio(contentScroller, 1);

        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();
        mainLayout.addComponent(new Label("This is a header"));
        mainLayout.addComponent(intermediateLayout);
        mainLayout.setExpandRatio(intermediateLayout, 1);

        return mainLayout;
    }

    private void populateWithGrids(int itemCount, int gridCount) {
        performanceExtension.start();
        contentLayout.removeAllComponents();
        for (int i = 0; i < gridCount; i++) {
            Grid<Person> grid = createGrid();
            grid.setItems(createBeans(itemCount));
            contentLayout.addComponent(grid);
        }
    }

    private void populateContent(VerticalLayout contentLayout, int childCount,
            boolean wrapInPanel) {
        populateContent(contentLayout, childCount, wrapInPanel, false);
    }

    private void populateContent(VerticalLayout contentLayout, int childCount,
            boolean wrapInPanel, boolean useFields) {
        performanceExtension.start();
        contentLayout.removeAllComponents();
        for (int i = 0; i < childCount; i++) {
            VerticalLayout left = new VerticalLayout();
            if (useFields) {
                left.addComponent(new TextField("Field 1"));
                left.addComponent(new ComboBox<String>("Field 2"));
                left.addComponent(new DateField("Field 3"));
            } else {
                left.addComponent(new Label("Label 1"));
                left.addComponent(new Label("Label 2"));
                left.addComponent(new Label("Label 3"));
            }

            VerticalLayout right = new VerticalLayout();
            if (useFields) {
                right.addComponent(new TextField("Field 4"));
                right.addComponent(new ComboBox<String>("Field 5"));
                right.addComponent(new DateField("Field 6"));
            } else {
                right.addComponent(new Label("Label 4"));
                right.addComponent(new Label("Label 5"));
                right.addComponent(new Label("Label 6"));
            }

            HorizontalLayout columns = new HorizontalLayout();
            columns.addComponent(left);
            columns.addComponent(right);
            columns.setHeight(null);
            columns.setWidth("100%");

            if (wrapInPanel) {
                Panel panel = new Panel("Data " + i, columns);
                panel.setWidth("100%");
                panel.setHeight(null);

                contentLayout.addComponent(panel);
            } else {
                contentLayout.addComponent(columns);
            }
        }
    }

    protected Grid<Person> createGrid() {
        Grid<Person> grid = new Grid<>();
        grid.addColumn(Person::getFirstName).setCaption("First Name");
        grid.addColumn(Person::getLastName).setCaption("Last Name");
        grid.addColumn(person -> Optional.ofNullable(person.getAddress())
                .map(Address::getStreetAddress).orElse(null))
                .setCaption("Street");
        grid.addColumn(person -> Optional.ofNullable(person.getAddress())
                .map(Address::getPostalCode).map(Object::toString).orElse(""))
                .setCaption("Zip");
        grid.addColumn(person -> Optional.ofNullable(person.getAddress())
                .map(Address::getCity).orElse(null)).setCaption("City");
        return grid;
    }

    private Random random = new Random();

    protected List<Person> createBeans(int size) {
        return IntStream.range(0, size).mapToObj(this::createPerson)
                .collect(Collectors.toList());
    }

    protected Person createPerson(int index) {
        random.setSeed(index);
        Person person = new Person();
        person.setFirstName("First Name " + random.nextInt());
        person.setLastName("Last Name " + random.nextInt());
        person.setAge(random.nextInt());
        person.setBirthDate(new Date(random.nextLong()));
        person.setDeceased(random.nextBoolean());
        person.setEmail(random.nextInt() + "user@example.com");
        person.setRent(new BigDecimal(random.nextLong()));
        person.setSalary(random.nextInt());
        person.setSalaryDouble(random.nextDouble());
        person.setSex(Sex.values()[random.nextInt(Sex.values().length)]);

        Address address = new Address();
        person.setAddress(address);
        address.setCity("city " + random.nextInt());
        address.setPostalCode(random.nextInt());
        address.setStreetAddress("street address " + random.nextInt());
        address.setCountry(
                Country.values()[random.nextInt(Country.values().length)]);
        return person;
    }
}
