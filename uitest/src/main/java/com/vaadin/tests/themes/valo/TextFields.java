package com.vaadin.tests.themes.valo;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.UserError;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

public class TextFields extends VerticalLayout implements View {
    private TestIcon testIcon = new TestIcon(140);

    public TextFields() {
        setSpacing(false);

        Label h1 = new Label("Text Fields");
        h1.addStyleName(ValoTheme.LABEL_H1);
        addComponent(h1);

        HorizontalLayout row = new HorizontalLayout();
        row.addStyleName(ValoTheme.LAYOUT_HORIZONTAL_WRAPPING);
        addComponent(row);

        TextField tf = new TextField("Normal");
        tf.setPlaceholder("First name");
        tf.setIcon(testIcon.get());
        row.addComponent(tf);

        tf = new TextField("Custom color");
        tf.setPlaceholder("Email");
        tf.addStyleName("color1");
        row.addComponent(tf);

        tf = new TextField("User Color");
        tf.setPlaceholder("Gender");
        tf.addStyleName("color2");
        row.addComponent(tf);

        tf = new TextField("Themed");
        tf.setPlaceholder("Age");
        tf.addStyleName("color3");
        row.addComponent(tf);

        tf = new TextField("Error");
        tf.setValue("Something’s wrong");
        tf.setComponentError(new UserError("Fix it, now!"));
        row.addComponent(tf);

        tf = new TextField("Error, borderless");
        tf.setValue("Something’s wrong");
        tf.setComponentError(new UserError("Fix it, now!"));
        tf.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
        row.addComponent(tf);

        tf = new TextField("Read-only");
        tf.setPlaceholder("Nationality");
        tf.setValue("Finnish");
        tf.setReadOnly(true);
        row.addComponent(tf);

        tf = new TextField("Small");
        tf.setValue("Field value");
        tf.addStyleName(ValoTheme.TEXTFIELD_SMALL);
        row.addComponent(tf);

        tf = new TextField("Large");
        tf.setValue("Field value");
        tf.addStyleName(ValoTheme.TEXTFIELD_LARGE);
        tf.setIcon(testIcon.get(true));
        row.addComponent(tf);

        tf = new TextField("Icon inside");
        tf.setPlaceholder("Ooh, an icon");
        tf.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);
        tf.setIcon(testIcon.get());
        row.addComponent(tf);

        tf = new TextField("Large, Icon inside");
        tf.setPlaceholder("Ooh, an icon");
        tf.addStyleName(ValoTheme.TEXTFIELD_LARGE);
        tf.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);
        tf.setIcon(testIcon.get());
        row.addComponent(tf);

        tf = new TextField("Small, Icon inside");
        tf.setPlaceholder("Ooh, an icon");
        tf.addStyleName(ValoTheme.TEXTFIELD_SMALL);
        tf.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);
        tf.setIcon(testIcon.get());
        row.addComponent(tf);

        tf = new TextField("16px supported by default");
        tf.setPlaceholder("Image icon");
        tf.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);
        tf.setIcon(testIcon.get(true, 16));
        row.addComponent(tf);

        tf = new TextField();
        tf.setValue("Font, no caption");
        tf.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);
        tf.setIcon(testIcon.get());
        row.addComponent(tf);

        tf = new TextField();
        tf.setValue("Image, no caption");
        tf.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);
        tf.setIcon(testIcon.get(true, 16));
        row.addComponent(tf);

        CssLayout group = new CssLayout();
        group.addStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
        row.addComponent(group);

        tf = new TextField();
        tf.setPlaceholder("Grouped with a button");
        tf.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);
        tf.setIcon(testIcon.get());
        tf.setWidth("260px");
        group.addComponent(tf);

        Button button = new Button("Do It");
        // button.addStyleName(ValoTheme.BUTTON_PRIMARY);
        group.addComponent(button);

        tf = new TextField("Borderless");
        tf.setPlaceholder("Write here…");
        tf.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);
        tf.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
        tf.setIcon(testIcon.get());
        row.addComponent(tf);

        tf = new TextField("Right-aligned");
        tf.setValue("1,234");
        tf.addStyleName(ValoTheme.TEXTFIELD_ALIGN_RIGHT);
        row.addComponent(tf);

        tf = new TextField("Centered");
        tf.setPlaceholder("Guess what?");
        tf.addStyleName(ValoTheme.TEXTFIELD_ALIGN_CENTER);
        row.addComponent(tf);

        PasswordField pwf = new PasswordField("Password");
        pwf.setPlaceholder("Secret words");
        pwf.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);
        pwf.setIcon(FontAwesome.LOCK);
        row.addComponent(pwf);

        pwf = new PasswordField("Password, right-aligned");
        pwf.setPlaceholder("Secret words");
        pwf.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);
        pwf.addStyleName(ValoTheme.TEXTFIELD_ALIGN_RIGHT);
        pwf.setIcon(FontAwesome.LOCK);
        row.addComponent(pwf);

        pwf = new PasswordField("Password, centered");
        pwf.setPlaceholder("Secret words");
        pwf.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);
        pwf.addStyleName(ValoTheme.TEXTFIELD_ALIGN_CENTER);
        pwf.setIcon(FontAwesome.LOCK);
        row.addComponent(pwf);

        tf = new TextField("Tiny");
        tf.setValue("Field value");
        tf.addStyleName(ValoTheme.TEXTFIELD_TINY);
        row.addComponent(tf);

        tf = new TextField("Huge");
        tf.setValue("Field value");
        tf.addStyleName(ValoTheme.TEXTFIELD_HUGE);
        row.addComponent(tf);

        h1 = new Label("Text Areas");
        h1.addStyleName(ValoTheme.LABEL_H1);
        addComponent(h1);

        row = new HorizontalLayout();
        row.addStyleName(ValoTheme.LAYOUT_HORIZONTAL_WRAPPING);
        addComponent(row);

        TextArea ta = new TextArea("Normal");
        ta.setPlaceholder("Write your comment…");
        row.addComponent(ta);

        ta = new TextArea("Inline icon");
        ta.setPlaceholder("Inline icon not really working");
        ta.addStyleName("inline-icon");
        ta.setIcon(testIcon.get());
        row.addComponent(ta);

        ta = new TextArea("Custom color");
        ta.addStyleName("color1");
        ta.setPlaceholder("Write your comment…");
        row.addComponent(ta);

        ta = new TextArea("Custom color, read-only");
        ta.addStyleName("color2");
        ta.setValue("Field value, spanning multiple lines of text");
        ta.setReadOnly(true);
        row.addComponent(ta);

        ta = new TextArea("Custom color");
        ta.addStyleName("color3");
        ta.setValue("Field value, spanning multiple lines of text");
        row.addComponent(ta);

        ta = new TextArea("Small");
        ta.addStyleName(ValoTheme.TEXTAREA_SMALL);
        ta.setPlaceholder("Write your comment…");
        row.addComponent(ta);

        ta = new TextArea("Large");
        ta.addStyleName(ValoTheme.TEXTAREA_LARGE);
        ta.setPlaceholder("Write your comment…");
        row.addComponent(ta);

        ta = new TextArea("Borderless");
        ta.addStyleName(ValoTheme.TEXTAREA_BORDERLESS);
        ta.setPlaceholder("Write your comment…");
        row.addComponent(ta);

        ta = new TextArea("Right-aligned");
        ta.addStyleName(ValoTheme.TEXTAREA_ALIGN_RIGHT);
        ta.setValue("Field value, spanning multiple lines of text");
        row.addComponent(ta);

        ta = new TextArea("Centered");
        ta.addStyleName(ValoTheme.TEXTAREA_ALIGN_CENTER);
        ta.setValue("Field value, spanning multiple lines of text");
        row.addComponent(ta);

        ta = new TextArea("Tiny");
        ta.addStyleName(ValoTheme.TEXTAREA_TINY);
        ta.setPlaceholder("Write your comment…");
        row.addComponent(ta);

        ta = new TextArea("Huge");
        ta.addStyleName(ValoTheme.TEXTAREA_HUGE);
        ta.setPlaceholder("Write your comment…");
        row.addComponent(ta);

        RichTextArea rta = new RichTextArea();
        rta.setValue("<b>Some</b> <i>rich</i> content");
        row.addComponent(rta);

        rta = new RichTextArea("Read-only");
        rta.setValue("<b>Some</b> <i>rich</i> content");
        rta.setReadOnly(true);
        row.addComponent(rta);
    }

    @Override
    public void enter(ViewChangeEvent event) {
        // TODO Auto-generated method stub

    }

}
