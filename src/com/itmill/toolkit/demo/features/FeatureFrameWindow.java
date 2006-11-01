/* *************************************************************************
 
                               IT Mill Toolkit 

               Development of Browser User Intarfaces Made Easy

                    Copyright (C) 2000-2006 IT Mill Ltd
                     
   *************************************************************************

   This product is distributed under commercial license that can be found
   from the product package on license/license.txt. Use of this product might 
   require purchasing a commercial license from IT Mill Ltd. For guidelines 
   on usage, see license/licensing-guidelines.html

   *************************************************************************
   
   For more information, contact:
   
   IT Mill Ltd                           phone: +358 2 4802 7180
   Ruukinkatu 2-4                        fax:   +358 2 4802 7181
   20540, Turku                          email:  info@itmill.com
   Finland                               company www: www.itmill.com
   
   Primary source for information and releases: www.itmill.com

   ********************************************************************** */

package com.itmill.toolkit.demo.features;

import java.util.HashMap;
import java.util.List;

import com.itmill.toolkit.ui.*;
import com.itmill.toolkit.ui.Button.ClickEvent;

public class FeatureFrameWindow
	extends Feature
	implements Button.ClickListener {

	private Button addButton = new Button("Add to application", this, "addWin");
	private Button removeButton =
		new Button("Remove from application", this, "delWin");
	private FrameWindow demoWindow;
	private HashMap windowToFramesetMap = new HashMap();
	private int count = 0;

	protected Component getDemoComponent() {
		OrderedLayout l = new OrderedLayout();
		demoWindow = new FrameWindow("Feature Test Window");
		demoWindow.getFrameset().newFrame(
			createFrame(demoWindow.getFrameset()));

		// Example panel
		Panel show = new Panel("Test Window Control");
		((OrderedLayout) show.getLayout()).setOrientation(
			OrderedLayout.ORIENTATION_HORIZONTAL);
		show.addComponent(addButton);
		show.addComponent(removeButton);
		updateWinStatus();
		l.addComponent(show);

		// Properties
		PropertyPanel p = new PropertyPanel(demoWindow);
		p.dependsOn(addButton);
		p.dependsOn(removeButton);
		Form ap =
			p.createBeanPropertySet(
				new String[] { "width", "height", "name", "border", "theme" });
		ap.replaceWithSelect(
			"border",
			new Object[] {
				new Integer(Window.BORDER_DEFAULT),
				new Integer(Window.BORDER_NONE),
				new Integer(Window.BORDER_MINIMAL)},
			new Object[] { "Default", "None", "Minimal" });

		p.addProperties("FrameWindow Properties", ap);
		l.addComponent(p);

		return l;
	}

	protected String getDescriptionXHTML() {
		return "<p>This component implements a window that contains a hierarchical set of frames. "
			+ "Each frame can contain a web-page, window or a set of frames that divides the space "
			+ "horizontally or vertically.</p>";
	}

	protected String getExampleSrc() {
		return "FrameWindow f = new FrameWindow(\"Frame example\");\n"
			+ "f.getFrameset().newFrame(window);\n"
			+ "f.getFrameset().newFrame(resource,\"targetName\");\n";
	}

	protected String getImage() {
		return "framewindow.jpg";
	}

	protected String getTitle() {
		return "FrameWindow";
	}

	public void addWin() {
		getApplication().addWindow(demoWindow);
		updateWinStatus();
	}

	public void delWin() {
		getApplication().removeWindow(demoWindow);
		updateWinStatus();
	}

	private void updateWinStatus() {
		if (demoWindow.getApplication() == null) {
			addButton.setEnabled(true);
			removeButton.setEnabled(false);
		} else {
			addButton.setEnabled(false);
			removeButton.setEnabled(true);
		}
	}

	public void buttonClick(ClickEvent event) {

		if (event.getButton().getCaption().equals("Remove")) {
			Window w = event.getButton().getWindow();
			FrameWindow.Frameset fs =
				(FrameWindow.Frameset) windowToFramesetMap.get(w);
			if (fs == demoWindow.getFrameset() && fs.size() <= 1) {
				// Do not remove the last frame	
			} else if (fs.size() > 1) {
				fs.removeFrame(fs.getFrame(w.getName()));
				windowToFramesetMap.remove(w);
			} else {
				FrameWindow.Frameset p = fs.getParentFrameset();
				if (p != demoWindow.getFrameset() || p.size() > 1)
					p.removeFrame(fs);
				if (p.size() == 0)
					p.newFrame(createFrame(p));
			}
		}

		if (event.getButton().getCaption().equals("Split")) {
			Window w = event.getButton().getWindow();
			FrameWindow.Frameset fs =
				(FrameWindow.Frameset) windowToFramesetMap.get(w);
			int index = 0;
			List l = fs.getFrames();
			while (index < l.size() && fs.getFrame(index).getWindow() != w)
				index++;
			fs.removeFrame(fs.getFrame(w.getName()));
			windowToFramesetMap.remove(w);
			if (index > fs.size())
				index = fs.size();
			fs = fs.newFrameset((Math.random() > 0.5), index);
			for (int i = 2 + (int) (Math.random() * 2.0); i > 0; i--)
				fs.newFrame(createFrame(fs));
		}
	}

	private Window createFrame(FrameWindow.Frameset fs) {
		Window w = new Window();
		w.addComponent(
			new Label("<b>Frame: " + (++count) + "</b>", Label.CONTENT_UIDL));
		w.addComponent(new Button("Split", this));
		w.addComponent(new Button("Remove", this));
		windowToFramesetMap.put(w, fs);
		return w;
	}
}
