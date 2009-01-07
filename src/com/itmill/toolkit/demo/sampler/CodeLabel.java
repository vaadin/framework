package com.itmill.toolkit.demo.sampler;

import com.itmill.toolkit.ui.Label;

public class CodeLabel extends Label {

    private static final String TAG = "codelabel";

    public CodeLabel() {
        super.setContentMode(CONTENT_PREFORMATTED);
    }

    public CodeLabel(String content) {
        super(content, CONTENT_PREFORMATTED);
    }

    @Override
    public String getTag() {
        return TAG;
    }

    @Override
    public void setContentMode(int contentMode) {
        if (contentMode != Label.CONTENT_PREFORMATTED) {
            throw new UnsupportedOperationException(
                    "Only preformatted content supported");
        }
    }

}
