package com.vaadin.tests.components.popupview;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.LoremIpsum;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;

public class PopupViewOffScreen extends TestBase {

    private List<PopupView> popupViews = new ArrayList<PopupView>();

    @Override
    protected String getDescription() {
        return "A popupview should be displayed on screen. If the popup position is close to a border it should be moved so it is still on screen.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3099;
    }

    @Override
    protected void setup() {
        GridLayout gl = new GridLayout(3, 3);
        gl.setSizeFull();
        Label expander = new Label();
        gl.addComponent(expander, 1, 1);
        gl.setColumnExpandRatio(1, 1);
        gl.setRowExpandRatio(1, 1);

        Button showall = new Button("Popup all", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                for (PopupView pv : popupViews) {
                    pv.setPopupVisible(true);
                }
            }
        });
        gl.addComponent(showall, 1, 0);
        gl.addComponent(createPopupView("red"), 0, 0);
        gl.addComponent(createPopupView("green"), 2, 0);
        gl.addComponent(createPopupView("blue"), 0, 2);
        gl.addComponent(createPopupView("yellow"), 2, 2);

        addComponent(gl);
        gl.getParent().setSizeFull();
    }

    private Component createPopupView(String bg) {
        VerticalLayout vl = new VerticalLayout();
        vl.setSpacing(false);
        vl.setMargin(false);
        vl.setSizeFull();
        vl.addStyleName(BaseTheme.CLIP);

        Panel p = new Panel(vl);
        p.setWidth("400px");
        p.setHeight("400px");

        Label l = new Label(
                "<div style='width: 100%; height: 100%; background: " + bg
                        + "'>" + LoremIpsum.get(2000) + "</div>",
                ContentMode.HTML);
        l.setSizeFull();
        vl.addComponent(l);
        PopupView pv = new PopupView("Click here to popup", p);

        popupViews.add(pv);
        return pv;
    }
}
