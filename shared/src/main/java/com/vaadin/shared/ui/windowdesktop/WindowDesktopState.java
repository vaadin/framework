/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vaadin.shared.ui.windowdesktop;

import com.vaadin.shared.ui.panel.PanelState;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Marcelo D. Ré {@literal <marcelo.re@gmail.com>}
 */
public class WindowDesktopState extends PanelState {
    private final static Logger LOGGER = Logger.getLogger(WindowDesktopState.class .getName());
    static {
        LOGGER.setLevel(Level.INFO);
    }
}
