/**
 * 
 */
package com.itmill.toolkit.demo.featurebrowser;

import com.itmill.toolkit.ui.CustomComponent;
import com.itmill.toolkit.ui.OrderedLayout;

/**
 * @author marc
 * 
 */
public class WindowingExample extends CustomComponent {

    public static final String txt = "There are two main types of windows:";

    /*
     * application-level windows, and
     * 
     */

    public WindowingExample() {
        OrderedLayout main = new OrderedLayout();
        setCompositionRoot(main);

    }

}
