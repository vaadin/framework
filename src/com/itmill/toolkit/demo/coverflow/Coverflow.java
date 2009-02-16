/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.demo.coverflow;

import com.itmill.toolkit.terminal.PaintException;
import com.itmill.toolkit.terminal.PaintTarget;
import com.itmill.toolkit.ui.AbstractSelect;

public class Coverflow extends AbstractSelect {

	private String backgroundGradientStart = "FFFFFF";
	private String backgroundGradientEnd = "EEEEEE";
	private boolean scrollbarVisibility = true;
	
    public String getTag() {
            return "cover";
    }
            
    /**
     * Paints the uidl
     * @param PaintTarget target
     * @throws PaintException
     */
    public void paintContent(PaintTarget target) throws PaintException {
        // Superclass writes any common attributes in the paint target.
        super.paintContent(target);
        
        target.addAttribute("backgroundGradientStart", backgroundGradientStart);
        target.addAttribute("backgroundGradientEnd", backgroundGradientEnd);
        target.addAttribute("scrollbarVisibility", scrollbarVisibility);
    }
    
    /**
     * The user can specify a background gradient for the coverflow. The input values
     * are RGB values for the start and end gradients.
     * @param int startR
     * @param int startG
     * @param int startB
     * @param int endR
     * @param int endG
     * @param int endB
     */
    public void setBackgroundColor(int startR, int startG, int startB, int endR, int endG, int endB) {
    	backgroundGradientStart = "";
    	backgroundGradientEnd = "";
    	
    	// Convert all integers to hexadecimal format and make sure they are two characters long (in
    	// other words, add a zero infront if the value is less than 16 => 0x0F
    	if(startR < 16)
    		backgroundGradientStart += "0";    	
    	backgroundGradientStart += Integer.toHexString(Math.max(Math.min(startR,255),0));
    	
    	if(startG < 16)
    		backgroundGradientStart += "0";
    	backgroundGradientStart += Integer.toHexString(Math.max(Math.min(startG,255),0));
    	
    	if(startB < 16)
    		backgroundGradientStart += "0";
    	backgroundGradientStart += Integer.toHexString(Math.max(Math.min(startB,255),0));
    	
    	if(endR < 16)
    		backgroundGradientEnd += "0"; 
    	backgroundGradientEnd += Integer.toHexString(Math.max(Math.min(endR,255),0));
    	
    	if(endG < 16)
    		backgroundGradientEnd += "0";
    	backgroundGradientEnd += Integer.toHexString(Math.max(Math.min(endG,255),0));
    	
    	if(endB < 16)
    		backgroundGradientEnd += "0";
    	backgroundGradientEnd += Integer.toHexString(Math.max(Math.min(endB,255),0));
    	
    	this.requestRepaint();
    }
    
    /**
     * The user can toggle the visibility of the scrollbar
     * @param boolean visible
     */
    public void setScrollbarVisibility(boolean visible) {
    	if(scrollbarVisibility != visible) {
    		scrollbarVisibility = visible;    	
    		this.requestRepaint();
    	}
    }
	
}
