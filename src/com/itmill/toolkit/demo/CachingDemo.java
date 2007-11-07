package com.itmill.toolkit.demo;

import com.itmill.toolkit.terminal.PaintException;
import com.itmill.toolkit.terminal.PaintTarget;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Layout;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.TabSheet;
import com.itmill.toolkit.ui.Window;

/**
 * This example is a (simple) demonstration of client-side caching. The content
 * in one tab is intentionally made very slow to produce server-side. When the
 * user changes to this tab for the first time, there will be a 3 second wait
 * before the content shows up, but the second time it shows up immediately
 * since the content has not changed and is cached client-side.
 * 
 * @author IT Mill Ltd.
 */
public class CachingDemo extends com.itmill.toolkit.Application {

	public void init() {

		Window main = new Window("Client-side caching example");
		setMainWindow(main);

		setTheme("example");

		TabSheet ts = new TabSheet();
		main.addComponent(ts);

		Layout layout = new OrderedLayout();
		layout.setMargin(true);
		Label l = new Label(
				"This is a normal label, quick to render.<br/>The second tab will be slow to render the first time, after that it will be as quick as this one.");
		l.setCaption("A normal label");
		l.setContentMode(Label.CONTENT_XHTML);
		layout.addComponent(l);

		ts.addTab(layout, "Normal", null);

		layout = new OrderedLayout();
		layout.setMargin(true);
		l = new Label(
				"The first time you change to this tab, this label is very slow to produce (server-side).<br/> However, it will seem fast the second time you change to this tab, because it has not changed and is cached client-side.") {
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
		l.setContentMode(Label.CONTENT_XHTML);
		layout.addComponent(l);
		ts.addTab(layout, "Slow", null);

	}

}
