package com.itmill.toolkit.demo.sampler.features.commons;

import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.RichTextArea;
import com.itmill.toolkit.ui.VerticalLayout;
import com.itmill.toolkit.ui.Button.ClickEvent;

public class TooltipsExample extends VerticalLayout {

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
        rte
                .setValue("Click <b>Edit my tooltip</b> to edit this tooltip, then <b>Apply</b>");
        rte.setVisible(false); // hide editor initially
        addComponent(rte);
        Button apply = new Button("Edit tooltip", new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                if (rte.isVisible()) {
                    rte.setVisible(false);
                    event.getButton().setDescription((String) rte.getValue());
                    event.getButton().setCaption("Edit tooltip");
                } else {
                    rte.setVisible(true);
                    event.getButton().setCaption("Apply");
                }
            }
        });
        apply.setDescription((String) rte.getValue());
        addComponent(apply);

    }
}
