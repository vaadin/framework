package com.vaadin.tests.dnd;

import java.util.stream.Stream;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.Page;
import com.vaadin.server.Page.Styles;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.tests.components.uitest.TestSampler;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.dnd.DragSourceExtension;

@Theme("valo")
@Widgetset("com.vaadin.DefaultWidgetSet")
public class DragImage extends AbstractTestUIWithLog {

    private final String[] positions = { "default", "relative", "absolute",
            "static", "fixed", "sticky", "inherit", "initial", "unset" };

    @Override
    protected void setup(VaadinRequest request) {
        setMobileHtml5DndEnabled(true);

        HorizontalLayout layout1 = new HorizontalLayout();
        layout1.setCaption("No custom drag image");
        Styles styles = Page.getCurrent().getStyles();

        Stream.of(positions).forEach(position -> {
            Label label = new Label(position);
            label.addStyleName(position);
            new DragSourceExtension<>(label);

            layout1.addComponents(label, new Label("spacer"));

            styles.add(new StringBuilder(".").append(position)
                    .append(" { position: ").append(position).append(";}")
                    .toString());
        });

        HorizontalLayout layout2 = new HorizontalLayout();
        layout2.setCaption("Custom drag image");
        Stream.of(positions).forEach(position -> {
            Label label = new Label(position);
            label.addStyleName(position);
            new DragSourceExtension<>(label)
                    .setDragImage(new ThemeResource(TestSampler.ICON_URL));
            layout2.addComponents(label, new Label("spacer"));

            styles.add(new StringBuilder(".").append(position)
                    .append(" { position: ").append(position).append(";}")
                    .toString());
        });

        // #9261 grid row like element that safari can't show the drag image for
        String css = ".absolute-pos { position: absolute; top:0; }";
        Label gridRowLikeLabel = new Label(css);
        gridRowLikeLabel.addStyleName("absolute-pos");
        new DragSourceExtension(gridRowLikeLabel);
        styles.add(css);

        VerticalLayout layout = new VerticalLayout();
        CheckBox transitionCheckBox = new CheckBox("Transition layout", false);
        transitionCheckBox.addValueChangeListener(event -> {
            if (event.getValue()) {
                layout.addStyleName("transitioned");
            } else {
                layout.removeStyleName("transitioned");
            }
        });

        layout.addComponents(transitionCheckBox, layout1, layout2,
                gridRowLikeLabel);
        addComponent(layout);
        layout.addStyleName("transitioned");
        getPage().getStyles()
                .add(".transitioned {transform: translateX(50px);}");
    }

}
