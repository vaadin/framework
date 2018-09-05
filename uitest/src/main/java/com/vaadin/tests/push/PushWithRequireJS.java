package com.vaadin.tests.push;

import com.vaadin.annotations.JavaScript;
import com.vaadin.annotations.Push;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;

@Push
// https://cdnjs.cloudflare.com/ajax/libs/require.js/2.1.20/require.min.js
@JavaScript("require.min.js")
public class PushWithRequireJS extends AbstractReindeerTestUI {
    @Override
    protected void setup(VaadinRequest request) {
    }
}
