package com.vaadin.tests.validation;

import java.util.Date;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.validator.AbstractValidator;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Select;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

public class EmptyFieldErrorIndicators extends TestBase {

    @Override
    protected void setup() {
        getLayout().setSizeFull();

        HorizontalLayout hl = new HorizontalLayout();
        hl.setSizeFull();
        hl.setSpacing(true);

        ComponentContainer part1 = createPart(
                "Empty required fields validation", true, false);
        part1.setDebugId("emptyFieldPart");
        hl.addComponent(part1);

        ComponentContainer part2 = createPart(
                "Empty required fields with failing validator", true, true);
        part1.setDebugId("validatedFieldPart");
        hl.addComponent(part2);

        Panel panel = new Panel();
        panel.setSizeFull();
        panel.setStyleName(Reindeer.PANEL_LIGHT);
        panel.addComponent(hl);
        panel.setScrollable(true);
        addComponent(panel);
    }

    private ComponentContainer createPart(String caption, boolean required,
            boolean failValidator) {
        VerticalLayout part = new VerticalLayout();
        part.setMargin(true);

        final Form form = createForm(required, failValidator);
        part.addComponent(form);

        Button validate = new Button("Validate fields");
        validate.addListener(new ClickListener() {
            public void buttonClick(ClickEvent event) {
                try {
                    form.validate();
                } catch (InvalidValueException e) {
                }
            }
        });
        part.addComponent(validate);

        Panel panel = new Panel(caption, part);
        panel.setHeight("100%");
        return panel;
    }

    private Form createForm(final boolean required, final boolean failValidator) {
        // hand-crafted form, not using form field factory
        final Form form = new Form() {
            @Override
            public void addField(Object propertyId, Field field) {
                super.addField(propertyId, field);
                field.setRequired(required);
                field.setRequiredError("Missing required value!");
                if (failValidator && !(field instanceof Button)) {
                    field.addValidator(new AbstractValidator("Validation error") {
                        public boolean isValid(Object value) {
                            return false;
                        }
                    });
                }
            }
        };

        form.addField("Field", new TextField("Text"));
        form.addField("Date", new DateField("Date"));
        // not good for automated testing with screenshots when null
        // form.addField("Inline Date", new InlineDateField("Date"));
        // same as basic DateField
        // form.addField("Popup Date", new PopupDateField("Date"));
        Button setDateButton = new Button("Set date");
        form.addField("Set Date", setDateButton);
        setDateButton.addListener(new ClickListener() {
            public void buttonClick(ClickEvent event) {
                form.getField("Date").setValue(new Date(0));
            }
        });

        NativeSelect nativeSelect = new NativeSelect("NativeSelect");
        form.addField("Native Select", nativeSelect);
        nativeSelect.addItem("Value 1");

        // in #4103, the Select component was behaving differently from others
        form.addField("Select", new Select("Select"));

        Select select2 = new Select("Select 2");
        select2.addItem("Value 1");
        form.addField("Select 2", select2);

        OptionGroup optionGroup = new OptionGroup("OptionGroup");
        optionGroup.setMultiSelect(false);
        optionGroup.addItem("Option 1");
        optionGroup.addItem("Option 2");
        form.addField("Option Group 1", optionGroup);

        OptionGroup optionGroup2 = new OptionGroup("OptionGroup");
        optionGroup2.setMultiSelect(true);
        optionGroup2.addItem("Option 1");
        optionGroup2.addItem("Option 2");
        form.addField("Option Group 2", optionGroup2);

        // TODO could add more different fields

        return form;
    }

    @Override
    protected String getDescription() {
        return "Fields on a form should not show the error indicator if required and empty";
    }

    @Override
    protected Integer getTicketNumber() {
        return 4013;
    }

}
