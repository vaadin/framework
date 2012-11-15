package com.vaadin.shared.ui.datefield;

public class PopupDateFieldState extends TextualDateFieldState {
    {
        primaryStyleName = "v-datefield";
    }

    private boolean textFieldEnabled = true;

    public boolean isTextFieldEnabled() {
        return textFieldEnabled;
    }

    public void setTextFieldEnabled(boolean textFieldEnabled) {
        this.textFieldEnabled = textFieldEnabled;
    }

}
