/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal;

import java.io.Serializable;
import java.util.Map;

/**
 * <p>
 * Listener interface for UI variable changes. The user communicates with the
 * application using the so-called <i>variables</i>. When the user makes a
 * change using the UI the terminal trasmits the changed variables to the
 * application, and the components owning those variables may then process those
 * changes.
 * </p>
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 3.0
 */
public interface VariableOwner extends Serializable {

    /**
     * Called when one or more variables handled by the implementing class are
     * changed.
     * 
     * @param source
     *            the Source of the variable change. This is the origin of the
     *            event. For example in Web Adapter this is the request.
     * @param variables
     *            the Mapping from variable names to new variable values.
     */
    public void changeVariables(Object source, Map variables);

    /**
     * <p>
     * Tests if the variable owner is enabled or not. The terminal should not
     * send any variable changes to disabled variable owners.
     * </p>
     * 
     * @return <code>true</code> if the variable owner is enabled,
     *         <code>false</code> if not
     */
    public boolean isEnabled();

    /**
     * <p>
     * Tests if the variable owner is in immediate mode or not. Being in
     * immediate mode means that all variable changes are required to be sent
     * back from the terminal immediately when they occur.
     * </p>
     * 
     * <p>
     * <strong>Note:</strong> <code>VariableOwner</code> does not include a set-
     * method for the immediateness property. This is because not all
     * VariableOwners wish to offer the functionality. Such VariableOwners are
     * never in the immediate mode, thus they always return <code>false</code>
     * in {@link #isImmediate()}.
     * </p>
     * 
     * @return <code>true</code> if the component is in immediate mode,
     *         <code>false</code> if not.
     */
    public boolean isImmediate();

    /**
     * VariableOwner error event.
     */
    public interface ErrorEvent extends Terminal.ErrorEvent {

        /**
         * Gets the source VariableOwner.
         * 
         * @return the variable owner.
         */
        public VariableOwner getVariableOwner();

    }
}
