package com.vaadin.tests.layouts;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;

public class VerticalLayoutWithRelativeSizeComponents extends TestBase {

    @Override
    protected String getDescription() {
        return "A undefined wide VerticalLayout containing a 100% wide label, a Button and another identical label. The labels should be as wide as the button (400px) and there should be no extra space between the bottom of the first label and the button.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 2591;
    }

    @Override
    protected void setup() {
        getLayout().setSizeUndefined();
        getMainWindow().getContent().setHeight(null);

        Label l = new Label(
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nunc condimentum urna quis leo. In hac habitasse platea dictumst. Pellentesque tincidunt. Sed libero nisl, faucibus in, laoreet pellentesque, consectetur non, dolor. Sed tortor. Ut pretium sapien. Cras elementum enim non lacus. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Nulla velit est, aliquam a, pellentesque a, iaculis nec, sapien. Ut ultrices ligula vitae nulla. Morbi sem pede, iaculis ac, condimentum a, ornare eget, nisi. Aliquam hendrerit pulvinar massa. Vestibulum pretium purus eu augue. Sed posuere elit ut magna. Cras consequat faucibus nunc. Vestibulum quis diam.");
        Label l2 = new Label(
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nunc condimentum urna quis leo. In hac habitasse platea dictumst. Pellentesque tincidunt. Sed libero nisl, faucibus in, laoreet pellentesque, consectetur non, dolor. Sed tortor. Ut pretium sapien. Cras elementum enim non lacus. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Nulla velit est, aliquam a, pellentesque a, iaculis nec, sapien. Ut ultrices ligula vitae nulla. Morbi sem pede, iaculis ac, condimentum a, ornare eget, nisi. Aliquam hendrerit pulvinar massa. Vestibulum pretium purus eu augue. Sed posuere elit ut magna. Cras consequat faucibus nunc. Vestibulum quis diam.");
        l.setWidth("100%");
        l2.setWidth("100%");

        Button b = new Button("This defines the width");
        b.setWidth("400px");
        addComponent(l);
        addComponent(b);
        addComponent(l2);
    }

}
