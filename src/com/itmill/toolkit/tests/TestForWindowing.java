package com.itmill.toolkit.tests;

import java.net.MalformedURLException;
import java.net.URL;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;

public class TestForWindowing extends Application {

	Window main = new Window("Windowing test");
	
	public void init() {

		setMainWindow(main);
		
		main.addComponent(new Button("Add new subwindow", new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				final Window w = new Window("Subwindow " + System.currentTimeMillis());
				main.addWindow(w);
				
				Button closebutton = new Button("Close " + w.getCaption(), new Button.ClickListener() {
					public void buttonClick(ClickEvent event) {
						main.removeWindow(w);
					}
				
				});
				w.addComponent(closebutton);
			}
		}));
		
		main.addComponent( new Button("Open a currently uncreated application level window", new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				try {
					main.open(
							new com.itmill.toolkit.terminal.ExternalResource(new URL(getURL(),"mainwin-" + System.currentTimeMillis() + "/")), 
							null);
				} catch (MalformedURLException e) {
				}
			}
		}));
	
	}

	public Window getWindow(String name) {
		
		Window w = super.getWindow(name);
		if (w != null) return w;

		if (name != null && name.startsWith("mainwin-")) {
			String postfix = name.substring("mainwin-".length());
			final Window ww=new Window("Window: " + postfix);
			ww.setName(name);
			ww.addComponent(new Label("This is a application-level window opened with name: " + name));
			ww.addComponent(new Button("Click me", new Button.ClickListener() {
				int state = 0;
				public void buttonClick(ClickEvent event) {
					ww.addComponent(new Label("Button clicked " + (++state) + " times"));
				}
			}));
			addWindow(ww);
			return ww;
		}
		
		return null;
	}
	
}
