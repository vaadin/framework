package com.vaadin.tests.components.datefield;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.LegacyDateField;
import com.vaadin.v7.ui.LegacyInlineDateField;

public class LegacyDateFieldRanges extends AbstractTestUI {

    @Override
    protected Integer getTicketNumber() {
        return 6241;
    }

    private Label label = new Label();
    private NativeSelect resoSelect = new NativeSelect("Resolution");
    private LegacyDateField fromRange = new LegacyDateField("Range start");
    private LegacyDateField toRange = new LegacyDateField("Range end");
    private LegacyDateField valueDF = new LegacyDateField("Value");
    private CheckBox immediateCB = new CheckBox("Immediate");
    private Button recreate = new Button("Recreate static datefields");
    private Button clearRangeButton = new Button("Clear range");

    private GridLayout currentStaticContainer;

    private LegacyDateField inlineDynamicDateField;
    private LegacyDateField dynamicDateField;

    private Calendar createCalendar() {
        Calendar c = Calendar.getInstance();
        c.set(2013, 3, 26, 6, 1, 12);
        return c;
    }

    private Date newDate() {
        return createCalendar().getTime();
    }

    private void initializeControlFields() {
        resoSelect.addItem(Resolution.MINUTE);
        resoSelect.addItem(Resolution.SECOND);
        resoSelect.addItem(Resolution.HOUR);
        resoSelect.addItem(Resolution.DAY);
        resoSelect.addItem(Resolution.MONTH);
        resoSelect.addItem(Resolution.YEAR);
        resoSelect.setImmediate(true);
        resoSelect.setValue(Resolution.DAY);
        resoSelect.addValueChangeListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {

                Resolution r = (Resolution) resoSelect.getValue();
                inlineDynamicDateField.setResolution(r);
                dynamicDateField.setResolution(r);

            }
        });

        fromRange.setValue(null);
        fromRange.setImmediate(true);
        fromRange.addValueChangeListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {

                inlineDynamicDateField.setRangeStart(fromRange.getValue());
                dynamicDateField.setRangeStart(fromRange.getValue());

            }
        });

        toRange.setValue(null);
        toRange.setImmediate(true);
        toRange.addValueChangeListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {

                inlineDynamicDateField.setRangeEnd(toRange.getValue());
                dynamicDateField.setRangeEnd(toRange.getValue());

            }
        });

        valueDF.setValue(null);
        valueDF.setImmediate(true);
        valueDF.addValueChangeListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {

                inlineDynamicDateField.setValue(valueDF.getValue());
                dynamicDateField.setValue(valueDF.getValue());

            }
        });

        immediateCB.setValue(true);
        immediateCB.setImmediate(true);
        immediateCB.addValueChangeListener(event -> {
            inlineDynamicDateField.setImmediate(immediateCB.getValue());
            dynamicDateField.setImmediate(immediateCB.getValue());
        });

        recreate.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                GridLayout newContainer = createStaticFields();
                replaceComponent(currentStaticContainer, newContainer);
                currentStaticContainer = newContainer;
            }
        });

        clearRangeButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                fromRange.setValue(null);
                toRange.setValue(null);
            }
        });

        Calendar startCal = createCalendar();
        Calendar endCal = createCalendar();
        endCal.add(Calendar.DATE, 30);

        dynamicDateField = createDateField(startCal.getTime(), endCal.getTime(),
                null, Resolution.DAY, false);
        inlineDynamicDateField = createDateField(startCal.getTime(),
                endCal.getTime(), null, Resolution.DAY, true);

        resoSelect.setId("resoSelect");
        fromRange.setId("fromRange");
        toRange.setId("toRange");
        valueDF.setId("valueDF");
        immediateCB.setId("immediateCB");
        recreate.setId("recreate");
        clearRangeButton.setId("clearRangeButton");
        dynamicDateField.setId("dynamicDateField");
        inlineDynamicDateField.setId("inlineDynamicDateField");

    }

    @Override
    protected void setup(VaadinRequest request) {
        setLocale(new Locale("en", "US"));
        getLayout().setWidth(100, Unit.PERCENTAGE);
        getLayout().setHeight(null);
        getLayout().setMargin(new MarginInfo(true, false, false, false));
        getLayout().setSpacing(true);

        initializeControlFields();

        GridLayout gl = new GridLayout(2, 2);
        gl.setSpacing(true);

        gl.addComponent(dynamicDateField);
        gl.addComponent(inlineDynamicDateField);

        HorizontalLayout hl = new HorizontalLayout();
        hl.setSpacing(true);
        hl.addComponent(resoSelect);
        hl.addComponent(fromRange);
        hl.addComponent(toRange);
        hl.addComponent(valueDF);
        hl.addComponent(immediateCB);
        hl.addComponent(recreate);
        hl.addComponent(clearRangeButton);
        addComponent(hl);
        addComponent(new Label("Dynamic DateFields"));
        addComponent(gl);
        currentStaticContainer = createStaticFields();
        addComponent(new Label("Static DateFields"));
        addComponent(currentStaticContainer);

        addComponent(label);

    }

    private GridLayout createStaticFields() {
        Calendar startCal = createCalendar();
        Calendar endCal = createCalendar();
        endCal.add(Calendar.DATE, 30);
        GridLayout gl = new GridLayout(2, 2);
        gl.setSpacing(true);
        LegacyDateField df = createDateField(startCal.getTime(),
                endCal.getTime(), null, Resolution.DAY, false);
        gl.addComponent(df);
        LegacyDateField inline = createDateField(startCal.getTime(),
                endCal.getTime(), null, Resolution.DAY, true);
        gl.addComponent(inline);
        inline.setId("staticInline");
        VerticalLayout vl = new VerticalLayout();

        return gl;
    }

    private LegacyDateField createDateField(Date rangeStart, Date rangeEnd,
            Date value, Resolution resolution, boolean inline) {

        LegacyDateField df = null;

        if (inline) {
            df = new LegacyInlineDateField();
        } else {
            df = new LegacyDateField();
        }

        final LegacyDateField gg = df;
        updateValuesForDateField(df);

        df.addValueChangeListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                label.setValue((gg.getValue() == null ? "Nothing"
                        : gg.getValue().toString()) + " selected. isValid: "
                        + gg.isValid());
            }
        });
        return df;
    }

    @Override
    protected String getTestDescription() {
        return "Not defined yet";

    }

    private void updateValuesForDateField(LegacyDateField df) {
        Date fromVal = fromRange.getValue();
        Date toVal = toRange.getValue();
        Date value = valueDF.getValue();
        Resolution r = (Resolution) resoSelect.getValue();
        boolean immediate = immediateCB.getValue();

        df.setValue(value);
        df.setResolution(r);
        df.setRangeStart(fromVal);
        df.setRangeEnd(toVal);
        df.setImmediate(immediate);

    }

}
