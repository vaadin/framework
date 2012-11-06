package com.vaadin.tests.tickets;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.vaadin.data.Container;
import com.vaadin.data.Validator;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Layout.MarginHandler;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Reindeer;

/**
 * Table layout is very slow in Firefox 3.0.10 when the table contains
 * components.
 * 
 * This is adapted from the HbnContainer example application WorkoutLog.
 * 
 * Other browsers are much faster.
 */
public class Ticket2998 extends LegacyApplication {
    private Table table;
    private VerticalLayout mainLayout;

    public class Workout implements Serializable {
        private Long id;
        private Date date = new Date();
        private String title = " -- new workout -- ";
        private float kilometers;

        private String trainingType;

        private Set<String> secondaryTypes;

        public Workout() {
        }

        public Long getId() {
            return id;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public float getKilometers() {
            return kilometers;
        }

        public void setKilometers(float kilometers) {
            this.kilometers = kilometers;
        }

        public String getTrainingType() {
            return trainingType;
        }

        public void setTrainingType(String trainingType) {
            this.trainingType = trainingType;
        }

        public void setSecondaryTypes(Set<String> secondaryTypes) {
            this.secondaryTypes = secondaryTypes;
        }

        public Set<String> getSecondaryTypes() {
            return secondaryTypes;
        }

    }

    public class WorkoutEditor extends Window {

        private DateField date = new DateField("Date");
        private TextField kilomiters = new TextField("Kilometers");
        private TextField title = new TextField("Title/note");

        private Ticket2998 workoutLog;

        public WorkoutEditor(Ticket2998 app) {
            super("Edit workout");
            workoutLog = app;
            Layout main = new VerticalLayout();
            setContent(main);
            main.setSizeUndefined();
            main.setStyleName(Reindeer.PANEL_LIGHT);

            FormLayout form = new FormLayout();
            form.setSizeUndefined();
            date.setResolution(DateField.RESOLUTION_MIN);
            form.addComponent(date);
            form.addComponent(kilomiters);
            form.addComponent(title);
            main.addComponent(form);

        }

        public void loadRun(Workout run) {
            if (run == null) {
                close();
            } else {
                date.setValue(run.getDate());
                kilomiters.setValue(String.valueOf(run.getKilometers()));
                title.setValue(run.getTitle());
                if (getParent() == null) {
                    workoutLog.getMainWindow().addWindow(this);
                }
                kilomiters.focus();
            }
        }
    }

    public class MyFieldFactory extends DefaultFieldFactory {

        public MyFieldFactory(Ticket2998 app) {
        }

        @Override
        public Field<?> createField(Container container, Object itemId,
                Object propertyId, Component uiContext) {

            /*
             * trainingType is manyToOne relation, give it a combobox
             */
            if (propertyId.equals("trainingType")) {
                return getTrainingTypeComboboxFor(itemId);
            }

            /*
             * Secondarytypes is manyToMany relation, give it a multiselect list
             */
            if (propertyId.equals("secondaryTypes")) {
                return getSecondaryTypesList(itemId);
            }

            final Field f = super.createField(container, itemId, propertyId,
                    uiContext);
            if (f != null) {
                if (f instanceof TextField) {
                    TextField tf = (TextField) f;
                    tf.setWidth("100%");
                }
                if (propertyId.equals("kilometers")) {
                    f.setWidth("4em");
                    f.addValidator(new Validator() {
                        @Override
                        public void validate(Object value)
                                throws InvalidValueException {
                            // FIXME this does not follow the standard pattern
                            // for validators and has side effects!
                            try {
                                @SuppressWarnings("unused")
                                float f = Float.parseFloat((String) value);
                            } catch (Exception e) {
                                Notification.show("Bad number value");
                                f.setValue(0);
                            }
                        }
                    });
                }
                if (propertyId.equals("date")) {
                    ((DateField) f).setResolution(DateField.RESOLUTION_MIN);
                }
            }
            return f;

        }

        private Map<Object, ListSelect> workoutIdToList = new HashMap<Object, ListSelect>();

        private Field<?> getSecondaryTypesList(Object itemId) {
            ListSelect list = workoutIdToList.get(itemId);
            if (list == null) {
                list = new ListSelect();
                list.setMultiSelect(true);
                list.addItem("Item1");
                list.addItem("Item2");
                list.addItem("Item3");
                list.addItem("Item4");
                list.addItem("Item5");
                // list.setContainerDataSource(trainingTypes);
                list.setRows(4);
                workoutIdToList.put(itemId, list);
            }
            return list;
        }

        private Map<Object, ComboBox> workoutIdToCombobox = new HashMap<Object, ComboBox>();

        private Field<?> getTrainingTypeComboboxFor(Object itemId) {
            ComboBox cb = workoutIdToCombobox.get(itemId);
            if (cb == null) {
                final ComboBox cb2 = new ComboBox();
                cb2.addItem("value1");
                cb2.addItem("value2");
                cb2.addItem("value3");
                cb2.addItem("value4");
                cb2.setNewItemsAllowed(true);

                workoutIdToCombobox.put(itemId, cb2);
                cb = cb2;
            }
            return cb;
        }
    }

    @Override
    public void init() {
        buildView();
        setTheme("reindeer");
    }

    /**
     * Builds a simple view for application with Table and a row of buttons
     * below it.
     */
    private void buildView() {

        final LegacyWindow w = new LegacyWindow("Workout Log");

        // set theme and some layout stuff
        setMainWindow(w);
        w.getContent().setSizeFull();
        ((MarginHandler) w.getContent()).setMargin(false);

        Panel p = new Panel("Workout Log");
        p.setStyleName(Reindeer.PANEL_LIGHT);
        w.addComponent(p);
        mainLayout = new VerticalLayout();
        p.setContent(mainLayout);

        populateAndConfigureTable();

        mainLayout.addComponent(table);

        // make table consume all extra space
        p.setSizeFull();
        mainLayout.setSizeFull();
        mainLayout.setExpandRatio(table, 1);
        table.setSizeFull();
    }

    protected void populateAndConfigureTable() {
        table = new Table();

        table.setWidth("100%");
        table.setSelectable(true);
        table.setImmediate(true);
        table.setColumnCollapsingAllowed(true);
        table.setColumnWidth("date", 200);
        table.setColumnWidth("kilometers", 100);
        // table.addListener(this);
        table.setTableFieldFactory(new MyFieldFactory(this));

        loadWorkouts();

        table.setEditable(true);
    }

    /**
     * Loads container to Table
     */
    protected void loadWorkouts() {
        final BeanItemContainer<Workout> cont;
        // Use plain HbnContainer
        cont = new BeanItemContainer<Workout>(Workout.class);
        table.setContainerDataSource(cont);

        // insert some sample data
        Calendar c = Calendar.getInstance();
        c.set(Calendar.MILLISECOND, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MINUTE, 0);

        String[] titles = new String[] { "A short easy one", "intervals",
                "very long", "just shaking legs after work",
                "long one with Paul", "test run" };

        c.add(Calendar.DATE, -1000);

        Random rnd = new Random();

        Workout r;

        for (int i = 0; i < 1000; i++) {
            r = new Workout();
            c.set(Calendar.HOUR_OF_DAY,
                    12 + (rnd.nextInt(11) - rnd.nextInt(11)));
            r.setDate(c.getTime());
            r.setTitle(titles[rnd.nextInt(titles.length)]);
            r.setKilometers(Math.round(rnd.nextFloat() * 30));
            r.setTrainingType("tt");
            c.add(Calendar.DATE, 1);
            cont.addBean(r);
        }
    }

}
