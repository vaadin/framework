package com.vaadin.tests.themes.valo;

import com.vaadin.data.HasValue;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

public class Tabsheets extends VerticalLayout implements View {

    TabSheet tabs;

    public Tabsheets() {
        setSpacing(false);

        Label h1 = new Label("Tabs");
        h1.addStyleName(ValoTheme.LABEL_H1);
        addComponent(h1);

        HorizontalLayout wrap = new HorizontalLayout();
        wrap.addStyleName(ValoTheme.LAYOUT_HORIZONTAL_WRAPPING);
        addComponent(wrap);

        final CheckBox closable = new CheckBox("Closable");
        wrap.addComponent(closable);

        final CheckBox overflow = new CheckBox("Overflow");
        wrap.addComponent(overflow);

        final CheckBox caption = new CheckBox("Captions", true);
        wrap.addComponent(caption);

        final CheckBox icon = new CheckBox("Icons");
        wrap.addComponent(icon);

        final CheckBox disable = new CheckBox("Disable tabs");
        wrap.addComponent(disable);

        Label h3 = new Label("Additional Styles");
        h3.addStyleName(ValoTheme.LABEL_H3);
        addComponent(h3);

        wrap = new HorizontalLayout();
        wrap.addStyleName(ValoTheme.LAYOUT_HORIZONTAL_WRAPPING);
        wrap.setMargin(new MarginInfo(false, false, true, false));
        addComponent(wrap);

        final CheckBox framed = new CheckBox("Framed", true);
        wrap.addComponent(framed);

        final CheckBox centered = new CheckBox("Centered tabs");
        wrap.addComponent(centered);

        final CheckBox rightAlign = new CheckBox("Right-aligned tabs");
        wrap.addComponent(rightAlign);

        final CheckBox equal = new CheckBox("Equal-width tabs");
        wrap.addComponent(equal);

        final CheckBox padded = new CheckBox("Padded tabbar");
        wrap.addComponent(padded);

        final CheckBox compact = new CheckBox("Compact");
        wrap.addComponent(compact);

        final CheckBox iconsOnTop = new CheckBox("Icons on top");
        wrap.addComponent(iconsOnTop);

        final CheckBox selectedOnly = new CheckBox("Selected tab closable");
        wrap.addComponent(selectedOnly);

        HasValue.ValueChangeListener<Boolean> update = event -> {
            String style = framed.getValue() ? "framed " : "";
            style += centered.getValue() ? " centered-tabs" : "";
            style += rightAlign.getValue() ? " right-aligned-tabs" : "";
            style += equal.getValue() ? " equal-width-tabs" : "";
            style += padded.getValue() ? " padded-tabbar" : "";
            style += compact.getValue() ? " compact-tabbar" : "";
            style += iconsOnTop.getValue() ? " icons-on-top" : "";
            style += selectedOnly.getValue() ? " only-selected-closable" : "";

            if (tabs != null) {
                removeComponent(tabs);
            }
            tabs = getTabSheet(caption.getValue(), style.trim(),
                    closable.getValue(), overflow.getValue(), icon.getValue(),
                    disable.getValue());
            addComponent(tabs);
        };
        closable.addValueChangeListener(update);
        overflow.addValueChangeListener(update);
        caption.addValueChangeListener(update);
        icon.addValueChangeListener(update);
        disable.addValueChangeListener(update);
        framed.addValueChangeListener(update);
        centered.addValueChangeListener(update);
        rightAlign.addValueChangeListener(update);
        equal.addValueChangeListener(update);
        padded.addValueChangeListener(update);
        compact.addValueChangeListener(update);
        iconsOnTop.addValueChangeListener(update);
        selectedOnly.addValueChangeListener(update);

        // Generate initial view
        icon.setValue(true);
    }

    static TabSheet getTabSheet(boolean caption, String style, boolean closable,
            boolean scrolling, boolean icon, boolean disable) {
        TestIcon testIcon = new TestIcon(60);

        TabSheet ts = new TabSheet();
        ts.addStyleName(style);
        StringGenerator sg = new StringGenerator();

        for (int i = 1; i <= (scrolling ? 10 : 3); i++) {
            String tabcaption = caption
                    ? sg.nextString(true) + " " + sg.nextString(false)
                    : null;

            VerticalLayout content = new VerticalLayout();
            content.addComponent(new Label("Content for tab " + i));
            if (i == 2) {
                content.addComponent(new Label(
                        "Excepteur sint obcaecat cupiditat non proident culpa. Magna pars studiorum, prodita quaerimus."));
            }
            Tab t = ts.addTab(content, tabcaption);
            t.setClosable(closable);
            t.setEnabled(!disable);

            // First tab is always enabled
            if (i == 1) {
                t.setEnabled(true);
            }

            if (icon) {
                t.setIcon(testIcon.get(false));
            }
        }

        ts.addSelectedTabChangeListener(event -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        return ts;
    }

    @Override
    public void enter(ViewChangeEvent event) {
        // TODO Auto-generated method stub

    }

}
