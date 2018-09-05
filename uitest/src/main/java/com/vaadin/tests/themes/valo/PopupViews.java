package com.vaadin.tests.themes.valo;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.PopupView.Content;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

public class PopupViews extends VerticalLayout implements View {
    public PopupViews() {
        setSpacing(false);

        Label h1 = new Label("Popup Views");
        h1.addStyleName(ValoTheme.LABEL_H1);
        addComponent(h1);

        HorizontalLayout row = new HorizontalLayout();
        row.addStyleName(ValoTheme.LAYOUT_HORIZONTAL_WRAPPING);
        addComponent(row);

        PopupView pv = new PopupView(new Content() {
            @Override
            public Component getPopupComponent() {
                return new VerticalLayout() {
                    {
                        setSpacing(false);
                        setWidth("300px");
                        addComponent(new Label(
                                "Fictum,  deserunt mollit anim laborum astutumque! Magna pars studiorum, prodita quaerimus."));
                    }
                };
            }

            @Override
            public String getMinimizedValueAsHTML() {
                return "Click to view";
            }
        });
        row.addComponent(pv);
        pv.setHideOnMouseOut(true);
        pv.setCaption("Hide on mouse-out");

        pv = new PopupView(new Content() {
            int count = 0;

            @Override
            public Component getPopupComponent() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {

                }
                return new VerticalLayout() {
                    {
                        setSpacing(false);
                        addComponent(new Label(
                                "<h3>Thanks for waiting!</h3><p>You've opened this popup <b>"
                                        + ++count + " time"
                                        + (count > 1 ? "s" : " only")
                                        + "</b>.</p>",
                                ContentMode.HTML));
                    }
                };
            }

            @Override
            public String getMinimizedValueAsHTML() {
                return "Show slow loading content";
            }
        });
        row.addComponent(pv);
        pv.setHideOnMouseOut(false);
        pv.setCaption("Hide on click-outside");
    }

    @Override
    public void enter(ViewChangeEvent event) {
        // TODO Auto-generated method stub

    }

}
