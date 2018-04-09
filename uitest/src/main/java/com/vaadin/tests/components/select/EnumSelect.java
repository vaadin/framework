package com.vaadin.tests.components.select;

import java.util.Arrays;
import java.util.Locale;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.ComboBox;
import com.vaadin.v7.data.util.converter.StringToEnumConverter;
import com.vaadin.v7.ui.NativeSelect;
import com.vaadin.v7.ui.Tree;

public class EnumSelect extends AbstractTestUIWithLog {

    public enum Constant {
        SOME_VALUE, SOME_OTHER_VALUE, FOO, BAR;
    }

    @Override
    protected void setup(VaadinRequest request) {

        setLocale(new Locale("fi", "FI"));
        ComboBox<Constant> cb = new ComboBox<>(null,
                Arrays.asList(Constant.values()));
        cb.setItemCaptionGenerator(value -> StringToEnumConverter
                .enumToString(value, getLocale()));
        addComponent(cb);

        NativeSelect ns = new NativeSelect();
        for (Constant c : Constant.values()) {
            ns.addItem(c);
        }
        addComponent(ns);

        Tree t = new Tree();
        t.addItem(Constant.SOME_OTHER_VALUE);
        t.addItem(2500.12);
        t.setParent(2500.12, Constant.SOME_OTHER_VALUE);

        addComponent(t);

    }

    @Override
    protected String getTestDescription() {
        return "Test formatting captions with enum converters in selection components";
    }

    @Override
    protected Integer getTicketNumber() {
        return 11433;
    }

}
