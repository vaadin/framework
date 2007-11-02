package com.itmill.toolkit.demo;

import com.itmill.toolkit.data.Property.ValueChangeEvent;
import com.itmill.toolkit.data.Property.ValueChangeListener;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.Slider;
import com.itmill.toolkit.ui.Window;

/**
 * The classic "hello, world!" example for IT Mill Toolkit. The class simply
 * implements the abstract {@link com.itmill.toolkit.Application#init() init()}
 * method in which it creates a Window and adds a Label to it.
 * 
 * @author IT Mill Ltd.
 * @see com.itmill.toolkit.Application
 * @see com.itmill.toolkit.ui.Window
 * @see com.itmill.toolkit.ui.Label
 */
public class HelloWorld extends com.itmill.toolkit.Application {

	private Label value = new Label();
	/**
	 * The initialization method that is the only requirement for inheriting the
	 * com.itmill.toolkit.service.Application class. It will be automatically
	 * called by the framework when a user accesses the application.
	 */
	public void init() {

		/*
		 * - Create new window for the application - Give the window a visible
		 * title - Set the window to be the main window of the application
		 */
		Window main = new Window("Hello window");
		setMainWindow(main);
		
		OrderedLayout ol = new OrderedLayout(OrderedLayout.ORIENTATION_HORIZONTAL);

		setTheme("example");
		
		Slider s = new Slider();
		s.setCaption("Volume");
		s.setMax(20);
		s.setMin(12);
		//s.setResolution(2);
		s.setImmediate(true);
		s.setOrientation(Slider.ORIENTATION_VERTICAL);
		//s.setArrows(true);
		s.setSize(200);
		//s.addStyleName(Slider.STYLE_SCROLLBAR);
		
		s.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				value.setValue(event.getProperty().getValue());
			}
			
		});
		
		ol.addComponent(s);
		
		Panel p = new Panel("Volume level");
		p.setHeight(400);
		p.setHeightUnits(Panel.UNITS_PIXELS);
		((OrderedLayout)p.getLayout()).setMargin(false,true,true,false);
		
		p.addComponent(value);
		ol.addComponent(p);
		value.setValue(s.getValue());
		
		ol.setComponentAlignment(s, OrderedLayout.ALIGNMENT_LEFT, OrderedLayout.ALIGNMENT_BOTTOM);
		
		main.setLayout(ol);
	}
}
