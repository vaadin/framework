package com.itmill.toolkit.demo.featurebrowser;

import com.itmill.toolkit.terminal.PaintException;
import com.itmill.toolkit.terminal.PaintTarget;
import com.itmill.toolkit.ui.CustomComponent;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Layout;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.TabSheet;

/**
 * This example is a (simple) demonstration of client-side caching. The content
 * in one tab is intentionally made very slow to produce server-side. When the
 * user changes to this tab for the first time, there will be a 3 second wait
 * before the content shows up, but the second time it shows up immediately
 * since the content has not changed and is cached client-side.
 * 
 * @author IT Mill Ltd.
 */
public class ClientCachingExample extends CustomComponent {

    private static final String msg = "This example is a (simple) demonstration of client-side caching."
            + " The content in one tab is intentionally made very slow to"
            + " 'produce' server-side. When you changes to this tab for the"
            + " first time, there will be a 3 second wait before the content"
            + " shows up, but the second time it shows up immediately since the"
            + " content has not changed and is cached client-side.";

    public ClientCachingExample() {

        OrderedLayout main = new OrderedLayout();
        main.setMargin(true);
        setCompositionRoot(main);

        main.addComponent(new Label(msg));

        TabSheet ts = new TabSheet();
        main.addComponent(ts);

        Layout layout = new OrderedLayout();
        layout.setMargin(true);
        Label l = new Label("This is a normal label, quick to render.");
        l.setCaption("A normal label");
        layout.addComponent(l);

        ts.addTab(layout, "Normal", null);

        layout = new OrderedLayout();
        layout.setMargin(true);
        l = new Label("Slow label - until cached client side.") {
            public void paintContent(PaintTarget target) throws PaintException {
                try {
                    Thread.sleep(3000);
                } catch (Exception e) {
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
