package com.vaadin.ui;

import com.vaadin.terminal.Terminal;

public interface Root extends Component {

    public String getName();

    public Terminal getTerminal();

    public void setTerminal(Terminal terminal);

}
