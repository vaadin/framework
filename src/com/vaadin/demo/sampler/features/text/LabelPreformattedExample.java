package com.vaadin.demo.sampler.features.text;

import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class LabelPreformattedExample extends VerticalLayout {

    public LabelPreformattedExample() {
        setSpacing(true);

        Label preformattedText = new Label(
                "This is an example of a Label component.\n"
                        + "\nThe content mode of this label is set"
                        + "\nto CONTENT_PREFORMATTED. This means"
                        + "\nthat it will display the content text"
                        + "\nusing a fixed-width font. You also have"
                        + "\nto insert the line breaks yourself.\n"
                        + "\n\tHTML and XML special characters"
                        + "\n\t(<,>,&) are escaped properly to"
                        + "\n\tallow displaying them.");
        preformattedText.setContentMode(Label.CONTENT_PREFORMATTED);

        addComponent(preformattedText);
    }
}
