package com.vaadin.tests.components.uitest;

import java.util.Arrays;

import com.vaadin.data.HasValue.ValueChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.tests.util.TestUtils;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.v7.ui.TextField;

@SuppressWarnings("deprecation")
public class ThemeTestUI extends AbstractReindeerTestUI {

    private TextField customStyle;
    private Button setStyleName;
    private CheckBox readOnly;
    private ComboBox<String> bgColor;
    private TestSampler sampler;
    private String customStyleName = null;

    @Override
    protected void setup(VaadinRequest request) {
        getLayout().setSpacing(true);

        createCustomStyleStringField();

        HorizontalLayout selectors = new HorizontalLayout();

        selectors.addComponent(customStyle);
        selectors.addComponent(setStyleName);
        if (showAdditionalControlFields()) {
            selectors.addComponent(readOnly);
            selectors.setComponentAlignment(readOnly, Alignment.MIDDLE_LEFT);
            selectors.addComponent(bgColor);
        }

        addComponent(selectors);

        sampler = new TestSampler();
        if (showAdditionalControlFields()) {
            for (ValueChangeListener<Boolean> listener : sampler
                    .getReadOnlyChangeListeners()) {
                readOnly.addValueChangeListener(listener);
            }
        }
        addComponent(sampler);

        if (showAdditionalControlFields()) {
            TestUtils.injectCSS(getLayout().getUI(),
                    "body .v-app .yellow {background-color: yellow;}");
        }
    }

    protected boolean showAdditionalControlFields() {
        return false;
    }

    private void createCustomStyleStringField() {
        customStyle = new TextField();
        customStyle.setId("customstyle");
        setStyleName = new Button("Set stylename",
                event -> onCustomStyleNameChanged(customStyle.getValue()));
        setStyleName.setId("setcuststyle");

        if (showAdditionalControlFields()) {
            readOnly = new CheckBox("Set read-only");
            bgColor = new ComboBox<>(null, Arrays.asList(
                    "Default sampler background", "Yellow sampler background"));
            bgColor.setValue("Default sampler background");
            bgColor.setEmptySelectionAllowed(false);
            bgColor.setWidth("270px");
            bgColor.addValueChangeListener(event -> {
                if ("Yellow sampler background".equals(bgColor.getValue())) {
                    addStyleName("yellow");
                } else {
                    removeStyleName("yellow");
                }
            });
        }
    }

    private void onCustomStyleNameChanged(String newStyleName) {
        sampler.setCustomStyleNameToComponents(customStyleName, newStyleName);
        customStyleName = newStyleName;
    }

    @Override
    protected String getTestDescription() {
        return "Test Sampler application with support for changing themes and stylenames.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 8031;
    }

}
