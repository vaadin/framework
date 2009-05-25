/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.demo.featurebrowser;

import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;

/**
 * This example is a (simple) demonstration of client-side caching. The content
 * in one tab is intentionally made very slow to produce server-side. When the
 * user changes to this tab for the first time, there will be a 3 second wait
 * before the content shows up, but the second time it shows up immediately
 * since the content has not changed and is cached client-side.
 * 
 * @author IT Mill Ltd.
 */
@SuppressWarnings("serial")
public class ClientCachingExample extends CustomComponent {

    private static final String msg = "This example is a (simple) demonstration of client-side caching."
            + " The content in one tab is intentionally made very slow to"
            + " 'produce' server-side. When you changes to this tab for the"
            + " first time, there will be a 3 second wait before the content"
            + " shows up, but the second time it shows up immediately since the"
            + " content has not changed and is cached client-side.";

    public ClientCachingExample() {

        final VerticalLayout main = new VerticalLayout();
        main.setMargin(true);
        setCompositionRoot(main);

        main.addComponent(new Label(msg));

        final TabSheet ts = new TabSheet();
        main.addComponent(ts);

        Layout layout = new VerticalLayout();
        layout.setMargin(true);
        Label l = new Label("This is a normal label, quick to render.");
        l.setCaption("A normal label");
        layout.addComponent(l);

        ts.addTab(layout, "Normal", null);

        layout = new VerticalLayout();
        layout.setMargin(true);
        l = new Label("Slow label - until cached client side.") {
            @Override
            public void paintContent(PaintTarget target) throws PaintException {
                try {
                    Thread.sleep(3000);
                } catch (final Exception e) {
                    // IGNORED
                }
                super.paintContent(target);
            }

        };
        l.setCaption("A slow label");
        layout.addComponent(l);
        ts.addTab(layout, "Slow", null);

    }
}
