package com.vaadin.tests.applicationcontext;

import com.vaadin.ui.UI;

public abstract class UIRunSafelyThread extends Thread {
    private UI ui;

    public UIRunSafelyThread(UI ui) {
        this.ui = ui;
    }

    @Override
    public void run() {
        ui.accessSynchronously(new Runnable() {

            @Override
            public void run() {
                runSafely();
            }
        });
    }

    protected abstract void runSafely();
}
