package com.itmill.toolkit.tests.components.combobox;

import java.util.Map;

import com.itmill.toolkit.tests.components.TestBase;
import com.itmill.toolkit.ui.ComboBox;
import com.itmill.toolkit.ui.PopupView;

public class ComboBoxInPopupView extends TestBase {

    @Override
    protected Integer getTicketNumber() {
        return 2508;
    }

    @Override
    protected String getDescription() {
        return "Testcase for ComboBox in PopupView. Make the window narrower than the popup: the focused (2) one wraps button to second row AND seems narrower than (1), the unfocused one (1) works as expected.";
    }

    @Override
    protected void setup() {
        final ComboBox cb1 = new ComboBox();
        cb1.setWidth("260px");
        // cb.focus();
        PopupView pv1 = new PopupView("<u>1. expected (click)</u>", cb1);
        getLayout().addComponent(pv1);

        final ComboBox cb2 = new ComboBox();
        cb2.setWidth("260px");
        PopupView pv2 = new PopupView("<u>2. focused (click)</u>", cb2) {
            public void changeVariables(Object source, Map variables) {
                super.changeVariables(source, variables);
                cb2.focus();
            }

        };
        getLayout().addComponent(pv2);

    }

}
