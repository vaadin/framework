package com.vaadin.demo.sampler.features.commons;

import com.vaadin.ui.Button;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

public class TooltipsExample extends VerticalLayout {

    private static final String editTxt = "Edit tooltip";
    private static final String applyTxt = "Apply";

    public TooltipsExample() {
        setSpacing(true);

        /* Plain tooltip (description) */
        Button plain = new Button("Mouse over for plain tooltip");
        plain.setStyleName(Button.STYLE_LINK);
        // add the tooltip:
        plain.setDescription("A simple plaintext tooltip");
        addComponent(plain);

        /* Richtext tooltip (description) */
        Button rich = new Button("Mouse over for richtext tooltip");
        rich.setStyleName(Button.STYLE_LINK);
        // add the tooltip:
        rich
                .setDescription("<h2><img src=\"../ITMILL/themes/sampler/icons/comment_yellow.gif\"/>A richtext tooltip</h2>"
                        + "<ul>"
                        + "<li>HTML formatting</li><li>Images<br/>"
                        + "</li><li>etc...</li></ul>");
        addComponent(rich);

        /* Edit */
        final RichTextArea rte = new RichTextArea();
        rte.setValue("Click <b>" + editTxt
                + "</b> to edit this tooltip, then <b>" + applyTxt + "</b>");
        rte.setVisible(false); // hide editor initially
        addComponent(rte);
        Button apply = new Button(editTxt, new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                if (rte.isVisible()) {
                    rte.setVisible(false);
                    event.getButton().setDescription((String) rte.getValue());
                    event.getButton().setCaption(editTxt);
                } else {
                    rte.setVisible(true);
                    event.getButton().setCaption(applyTxt);
                }
            }
        });
        apply.setDescription((String) rte.getValue());
        addComponent(apply);

    }
}
