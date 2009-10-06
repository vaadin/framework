package com.vaadin.demo.sampler;

import com.vaadin.demo.sampler.gwt.client.ui.VCodeLabel;
import com.vaadin.ui.ClientWidget;
import com.vaadin.ui.Label;

@ClientWidget(VCodeLabel.class)
@SuppressWarnings("serial")
public class CodeLabel extends Label {

    private static final String TAG = "codelabel";

    public CodeLabel() {
        setContentMode(CONTENT_PREFORMATTED);
    }

    public CodeLabel(String content) {
        super(content, CONTENT_PREFORMATTED);
    }

    @Override
    public void setContentMode(int contentMode) {
        if (contentMode != CONTENT_PREFORMATTED) {
            throw new UnsupportedOperationException(
                    "Only preformatted content supported");
        }
        super.setContentMode(CONTENT_PREFORMATTED);
    }

}
