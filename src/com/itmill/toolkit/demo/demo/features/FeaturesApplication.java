/* *************************************************************************
 
 IT Mill Toolkit 

 Development of Browser User Interfaces Made Easy

 Copyright (C) 2000-2006 IT Mill Ltd
 
 *************************************************************************

 This product is distributed under commercial license that can be found
 from the product package on license.pdf. Use of this product might 
 require purchasing a commercial license from IT Mill Ltd. For guidelines 
 on usage, see licensing-guidelines.html

 *************************************************************************
 
 For more information, contact:
 
 IT Mill Ltd                           phone: +358 2 4802 7180
 Ruukinkatu 2-4                        fax:   +358 2 4802 7181
 20540, Turku                          email:  info@itmill.com
 Finland                               company www: www.itmill.com
 
 Primary source for information and releases: www.itmill.com

 ********************************************************************** */

package com.itmill.toolkit.demo.features;

import com.itmill.toolkit.ui.*;

public class FeaturesApplication extends com.itmill.toolkit.Application {

	public void init() {
		Window main = new Window("IT Mill Toolkit Features Tour");
		setMainWindow(main);
		main.addComponent(new FeatureBrowser());
	}

	/**
	 * ErrorEvents are printed to default error stream and not in GUI.
	 */
	public void terminalError(
			com.itmill.toolkit.terminal.Terminal.ErrorEvent event) {
		Throwable e = event.getThrowable();
		e.printStackTrace();
	}

}
