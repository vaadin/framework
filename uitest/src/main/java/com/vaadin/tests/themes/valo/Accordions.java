package com.vaadin.tests.themes.valo;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

public class Accordions extends VerticalLayout implements View {
    public Accordions() {
        setSpacing(false);

        Label h1 = new Label("Accordions");
        h1.addStyleName(ValoTheme.LABEL_H1);
        addComponent(h1);

        HorizontalLayout row = new HorizontalLayout();
        row.setWidth("100%");
        addComponent(row);

        row.addComponent(getAccordion("Normal"));

        Accordion ac = getAccordion("Borderless");
        ac.addStyleName(ValoTheme.ACCORDION_BORDERLESS);
        row.addComponent(ac);

    }

    Accordion getAccordion(String caption) {
        TestIcon testIcon = new TestIcon(0);
        Accordion ac = new Accordion();
        ac.setCaption(caption);
        ac.addTab(new VerticalLayout() {
            {
                setMargin(true);
                Label label = new Label(
                        "Fabio vel iudice vincam, sunt in culpa qui officia. Ut enim ad minim veniam, quis nostrud exercitation.");
                label.setWidth("100%");
                addComponent(label);
            }
        }, "First Caption", testIcon.get());
        ac.addTab(new VerticalLayout() {
            {
                setMargin(true);
                Label label = new Label(
                        "Gallia est omnis divisa in partes tres, quarum.");
                label.setWidth("100%");
                addComponent(label);
            }
        }, "Second Caption", testIcon.get());
        ac.addTab(new VerticalLayout() {
            {
                setMargin(true);
                Label label = new Label(
                        "Nihil hic munitissimus habendi senatus locus, nihil horum? Sed haec quis possit intrepidus aestimare tellus.");
                label.setWidth("100%");
                addComponent(label);
            }
        }, "Third Caption", testIcon.get());
        ac.addTab(new VerticalLayout() {
            {
                setMargin(true);
                Label label = new Label(
                        "Inmensae subtilitatis, obscuris et malesuada fames. Quisque ut dolor gravida, placerat libero vel, euismod.");
                label.setWidth("100%");
                addComponent(label);
            }
        }, "Custom Caption Style", testIcon.get()).setStyleName("color1");
        return ac;
    }

    @Override
    public void enter(ViewChangeEvent event) {
        // TODO Auto-generated method stub

    }

}
