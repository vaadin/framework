package com.vaadin.tests.themes.valo;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

public class Labels extends VerticalLayout implements View {
    public Labels() {
        setSpacing(false);

        Label h1 = new Label("Labels");
        h1.addStyleName(ValoTheme.LABEL_H1);
        addComponent(h1);

        HorizontalLayout split = new HorizontalLayout();
        split.setWidth("100%");
        split.setSpacing(false);
        addComponent(split);

        VerticalLayout left = new VerticalLayout();
        left.setSpacing(false);
        left.setMargin(new MarginInfo(false, true, false, false));
        split.addComponent(left);

        Label huge = new Label("Huge type for display text.");
        huge.setWidth("100%");
        huge.addStyleName(ValoTheme.LABEL_HUGE);
        left.addComponent(huge);

        Label large = new Label(
                "Large type for introductory text. Etiam at risus et justo dignissim congue. Donec congue lacinia dui, a porttitor lectus condimentum laoreet. Nunc eu.");
        large.setWidth("100%");
        large.addStyleName(ValoTheme.LABEL_LARGE);
        left.addComponent(large);

        Label h2 = new Label("Subtitle");
        h2.addStyleName(ValoTheme.LABEL_H2);
        left.addComponent(h2);

        Label normal = new Label(
                "Normal type for plain text, with a <a href=\"https://vaadin.com\">regular link</a>. Etiam at risus et justo dignissim congue. Donec congue lacinia dui, a porttitor lectus condimentum laoreet. Nunc eu.",
                ContentMode.HTML);
        normal.setWidth("100%");
        left.addComponent(normal);

        Label h3 = new Label("Small Title");
        h3.addStyleName(ValoTheme.LABEL_H3);
        left.addComponent(h3);

        Label small = new Label(
                "Small type for additional text. Etiam at risus et justo dignissim congue. Donec congue lacinia dui, a porttitor lectus condimentum laoreet. Nunc eu.");
        small.setWidth("100%");
        small.addStyleName(ValoTheme.LABEL_SMALL);
        left.addComponent(small);

        Label tiny = new Label("Tiny type for minor text.");
        tiny.setWidth("100%");
        tiny.addStyleName(ValoTheme.LABEL_TINY);
        left.addComponent(tiny);

        Label h4 = new Label("Section Title");
        h4.addStyleName(ValoTheme.LABEL_H4);
        left.addComponent(h4);

        normal = new Label(
                "Normal type for plain text. Etiam at risus et justo dignissim congue. Donec congue lacinia dui, a porttitor lectus condimentum laoreet. Nunc eu.");
        normal.setWidth("100%");
        left.addComponent(normal);

        Panel p = new Panel("Additional Label Styles");
        split.addComponent(p);

        VerticalLayout right = new VerticalLayout();
        p.setContent(right);

        Label label = new Label(
                "Bold type for prominent text. Etiam at risus et justo dignissim congue. Donec congue lacinia dui, a porttitor lectus condimentum laoreet. Nunc eu.");
        label.setWidth("100%");
        label.addStyleName(ValoTheme.LABEL_BOLD);
        right.addComponent(label);

        label = new Label(
                "Light type for subtle text. Etiam at risus et justo dignissim congue. Donec congue lacinia dui, a porttitor lectus condimentum laoreet. Nunc eu.");
        label.setWidth("100%");
        label.addStyleName(ValoTheme.LABEL_LIGHT);
        right.addComponent(label);

        label = new Label(
                "Colored type for highlighted text. Etiam at risus et justo dignissim congue. Donec congue lacinia dui, a porttitor lectus condimentum laoreet. Nunc eu.");
        label.setWidth("100%");
        label.addStyleName(ValoTheme.LABEL_COLORED);
        right.addComponent(label);

        label = new Label("A label for success");
        label.setWidth("100%");
        label.addStyleName(ValoTheme.LABEL_SUCCESS);
        right.addComponent(label);

        label = new Label("A label for failure");
        label.setWidth("100%");
        label.addStyleName(ValoTheme.LABEL_FAILURE);
        right.addComponent(label);

    }

    @Override
    public void enter(ViewChangeEvent event) {
        // TODO Auto-generated method stub

    }
}
