/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.ui;

import com.vaadin.data.Property;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.terminal.gwt.client.ui.VTextField;
import com.vaadin.ui.ClientWidget.LoadStyle;

/**
 * <p>
 * A text editor component that can be bound to any bindable Property. The text
 * editor supports both multiline and single line modes, default is one-line
 * mode.
 * </p>
 * 
 * <p>
 * Since <code>TextField</code> extends <code>AbstractField</code> it implements
 * the {@link com.vaadin.data.Buffered} interface. A <code>TextField</code> is
 * in write-through mode by default, so
 * {@link com.vaadin.ui.AbstractField#setWriteThrough(boolean)} must be called
 * to enable buffering.
 * </p>
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 3.0
 */
@SuppressWarnings("serial")
@ClientWidget(value = VTextField.class, loadStyle = LoadStyle.EAGER)
public class TextField extends AbstractTextField {

    /**
     * Tells if input is used to enter sensitive information that is not echoed
     * to display. Typically passwords.
     */
    @Deprecated
    private boolean secret = false;

    /**
     * Number of visible rows in a multiline TextField. Value 0 implies a
     * single-line text-editor.
     */
    @Deprecated
    private int rows = 0;

    /**
     * Tells if word-wrapping should be used in multiline mode.
     */
    @Deprecated
    private boolean wordwrap = true;

    /**
     * Constructs an empty <code>TextField</code> with no caption.
     */
    public TextField() {
        setValue("");
    }

    /**
     * Constructs an empty <code>TextField</code> with given caption.
     * 
     * @param caption
     *            the caption <code>String</code> for the editor.
     */
    public TextField(String caption) {
        this();
        setCaption(caption);
    }

    /**
     * Constructs a new <code>TextField</code> that's bound to the specified
     * <code>Property</code> and has no caption.
     * 
     * @param dataSource
     *            the Property to be edited with this editor.
     */
    public TextField(Property dataSource) {
        setPropertyDataSource(dataSource);
    }

    /**
     * Constructs a new <code>TextField</code> that's bound to the specified
     * <code>Property</code> and has the given caption <code>String</code>.
     * 
     * @param caption
     *            the caption <code>String</code> for the editor.
     * @param dataSource
     *            the Property to be edited with this editor.
     */
    public TextField(String caption, Property dataSource) {
        this(dataSource);
        setCaption(caption);
    }

    /**
     * Constructs a new <code>TextField</code> with the given caption and
     * initial text contents. The editor constructed this way will not be bound
     * to a Property unless
     * {@link com.vaadin.data.Property.Viewer#setPropertyDataSource(Property)}
     * is called to bind it.
     * 
     * @param caption
     *            the caption <code>String</code> for the editor.
     * @param text
     *            the initial text content of the editor.
     */
    public TextField(String caption, String value) {
        setValue(value);
        setCaption(caption);
    }

    /**
     * Gets the secret property. If a field is used to enter secret information
     * the information is not echoed to display.
     * 
     * @return <code>true</code> if the field is used to enter secret
     *         information, <code>false</code> otherwise.
     * 
     * @deprecated Starting from 6.5 use {@link PasswordField} instead
     */
    @Deprecated
    public boolean isSecret() {
        return secret;
    }

    /**
     * Sets the secret property on and off. If a field is used to enter secret
     * information the information is not echoed to display.
     * 
     * @param secret
     *            the value specifying if the field is used to enter secret
     *            information.
     * @deprecated Starting from 6.5 use {@link PasswordField} instead
     */
    @Deprecated
    public void setSecret(boolean secret) {
        if (this.secret != secret) {
            this.secret = secret;
            requestRepaint();
        }
    }

    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        if (isSecret()) {
            target.addAttribute("secret", true);
        }

        final int rows = getRows();
        if (rows != 0) {
            target.addAttribute("rows", String.valueOf(rows));
            target.addAttribute("multiline", true);

            // Optimization: the default true is assumed if not painted
            if (!isWordwrap()) {
                target.addAttribute("wordwrap", false);
            }
        }

        super.paintContent(target);

    }

    /**
     * Gets the number of rows in the editor. If the number of rows is set to 0,
     * the actual number of displayed rows is determined implicitly by the
     * adapter.
     * 
     * @return number of explicitly set rows.
     * @deprecated use {@link TextArea} component and the same method there.
     *             This method will be removed from TextField that is to be used
     *             for one line text input only in the next versions.
     * 
     */
    @Deprecated
    public int getRows() {
        return rows;
    }

    /**
     * Sets the number of rows in the editor.
     * 
     * @param rows
     *            the number of rows for this editor.
     * 
     * @deprecated in 6.5 use {@link TextArea} component and the same method
     *             there. This method will be removed from TextField that is to
     *             be used for one line text input only in the next versions.
     */
    @Deprecated
    public void setRows(int rows) {
        if (rows < 0) {
            rows = 0;
        }
        if (this.rows != rows) {
            this.rows = rows;
            requestRepaint();
        }
    }

    /**
     * Tests if the editor is in word-wrap mode.
     * 
     * @return <code>true</code> if the component is in the word-wrap mode,
     *         <code>false</code> if not.
     * @deprecated in 6.5 use {@link TextArea} component and the same method
     *             there. This method will be removed from TextField that is to
     *             be used for one line text input only in the next versions.
     */
    @Deprecated
    public boolean isWordwrap() {
        return wordwrap;
    }

    /**
     * Sets the editor's word-wrap mode on or off.
     * 
     * @param wordwrap
     *            the boolean value specifying if the editor should be in
     *            word-wrap mode after the call or not.
     * 
     * @deprecated in 6.5 use {@link TextArea} component and the same method
     *             there. This method will be removed from TextField that is to
     *             be used for one line text input only in the next versions.
     */
    @Deprecated
    public void setWordwrap(boolean wordwrap) {
        if (this.wordwrap != wordwrap) {
            this.wordwrap = wordwrap;
            requestRepaint();
        }
    }

    /**
     * Sets the height of the {@link TextField} instance.
     * 
     * <p>
     * Setting height for {@link TextField} also has a side-effect that puts
     * {@link TextField} into multiline mode (aka "textarea"). Multiline mode
     * can also be achieved by calling {@link #setRows(int)}. The height value
     * overrides the number of rows set by {@link #setRows(int)}.
     * <p>
     * If you want to set height of single line {@link TextField}, call
     * {@link #setRows(int)} with value 0 after setting the height. Setting rows
     * to 0 resets the side-effect.
     * <p>
     * Starting from 6.5 you should use {@link TextArea} instead of
     * {@link TextField} for multiline text input.
     * 
     * 
     * @see com.vaadin.ui.AbstractComponent#setHeight(float, int)
     */
    @Override
    public void setHeight(float height, int unit) {
        super.setHeight(height, unit);
        if (height > 1 && getClass() == TextField.class) {
            /*
             * In html based terminals we most commonly want to make component
             * to be textarea if height is defined. Setting row field above 0
             * will render component as textarea.
             */

            setRows(2);
        }
    }

    /**
     * Sets the height of the {@link TextField} instance.
     * 
     * <p>
     * Setting height for {@link TextField} also has a side-effect that puts
     * {@link TextField} into multiline mode (aka "textarea"). Multiline mode
     * can also be achieved by calling {@link #setRows(int)}. The height value
     * overrides the number of rows set by {@link #setRows(int)}.
     * <p>
     * If you want to set height of single line {@link TextField}, call
     * {@link #setRows(int)} with value 0 after setting the height. Setting rows
     * to 0 resets the side-effect.
     * 
     * @see com.vaadin.ui.AbstractComponent#setHeight(java.lang.String)
     */
    @Override
    public void setHeight(String height) {
        // will call setHeight(float, int) the actually does the magic. Method
        // is overridden just to document side-effects.
        super.setHeight(height);
    }

}
