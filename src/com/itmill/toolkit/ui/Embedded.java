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

package com.itmill.toolkit.ui;

import java.util.Hashtable;
import java.util.Iterator;

import com.itmill.toolkit.terminal.PaintException;
import com.itmill.toolkit.terminal.PaintTarget;
import com.itmill.toolkit.terminal.Resource;
import com.itmill.toolkit.terminal.Sizeable;

/**
 * Component for embedding external objects.
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 3.0
 */
public class Embedded extends AbstractComponent implements Sizeable {

	/**
	 * General object type.
	 */
	public static final int TYPE_OBJECT = 0;

	/**
	 * Image types.
	 */
	public static final int TYPE_IMAGE = 1;

	/**
	 * Browser ("iframe") type.
	 */
	public static final int TYPE_BROWSER = 2;

	/**
	 * Type of the object.
	 */
	private int type = TYPE_OBJECT;

	/**
	 * Source of the embedded object.
	 */
	private Resource source = null;

	/**
	 * Dimensions of the object.
	 */
	private int width = -1;

	private int height = -1;

	private int widthUnits = Sizeable.UNITS_PIXELS;

	private int heightUnits = Sizeable.UNITS_PIXELS;

	/**
	 * Generic object attributes.
	 */
	private String mimeType = null;

	private String standby = null;

	/**
	 * Hash of object parameteres.
	 */
	private final Hashtable parameters = new Hashtable();

	/**
	 * Applet or other client side runnable properties.
	 */
	private String codebase = null;

	private String codetype = null;

	private String classId = null;

	private String archive = null;

	/**
	 * Creates a new empty Embedded object.
	 */
	public Embedded() {
	}

	/**
	 * Creates a new empty Embedded object with caption.
	 * 
	 * @param caption
	 */
	public Embedded(String caption) {
		setCaption(caption);
	}

	/**
	 * Creates a new Embedded object whose contents is loaded from given
	 * resource. The dimensions are assumed if possible. The type is guessed
	 * from resource.
	 * 
	 * @param caption
	 * @param source
	 *            the Source of the embedded object.
	 */
	public Embedded(String caption, Resource source) {
		setCaption(caption);
		setSource(source);
	}

	/**
	 * Gets the component UIDL tag.
	 * 
	 * @return the Component UIDL tag as string.
	 */
	public String getTag() {
		return "embedded";
	}

	/**
	 * Invoked when the component state should be painted.
	 */
	public void paintContent(PaintTarget target) throws PaintException {

		switch (type) {
		case TYPE_IMAGE:
			target.addAttribute("type", "image");
			break;
		case TYPE_BROWSER:
			target.addAttribute("type", "browser");
			break;
		default:
			break;
		}

		if (source != null) {
			target.addAttribute("src", source);
		}

		// Dimensions
		if (width > 0) {
			target.addAttribute("width", "" + width
					+ Sizeable.UNIT_SYMBOLS[this.widthUnits]);
		}
		if (height > 0) {
			target.addAttribute("height", "" + height
					+ Sizeable.UNIT_SYMBOLS[this.heightUnits]);
		}
		if (mimeType != null && !"".equals(mimeType)) {
			target.addAttribute("mimetype", mimeType);
		}
		if (classId != null && !"".equals(classId)) {
			target.addAttribute("classid", classId);
		}
		if (codebase != null && !"".equals(codebase)) {
			target.addAttribute("codebase", codebase);
		}
		if (codetype != null && !"".equals(codetype)) {
			target.addAttribute("codetype", codetype);
		}
		if (standby != null && !"".equals(standby)) {
			target.addAttribute("standby", standby);
		}
		if (archive != null && !"".equals(archive)) {
			target.addAttribute("archive", archive);
		}

		// Params
		for (Iterator i = this.getParameterNames(); i.hasNext();) {
			target.startTag("embeddedparam");
			String key = (String) i.next();
			target.addAttribute("name", key);
			target.addAttribute("value", getParameter(key));
			target.endTag("embeddedparam");
		}
	}

	/**
	 * Sets an object parameter. Parameters are optional information, and they
	 * are passed to the instantiated object. Parameters are are stored as name
	 * value pairs. This overrides the previous value assigned to this
	 * parameter.
	 * 
	 * @param name
	 *            the name of the parameter.
	 * @param value
	 *            the value of the parameter.
	 */
	public void setParameter(String name, String value) {
		parameters.put(name, value);
		requestRepaint();
	}

	/**
	 * Gets the value of an object parameter. Parameters are optional
	 * information, and they are passed to the instantiated object. Parameters
	 * are are stored as name value pairs.
	 * 
	 * @return the Value of parameter or null if not found.
	 */
	public String getParameter(String name) {
		return (String) parameters.get(name);
	}

	/**
	 * Removes an object parameter from the list.
	 * 
	 * @param name
	 *            the name of the parameter to remove.
	 */
	public void removeParameter(String name) {
		parameters.remove(name);
		requestRepaint();
	}

	/**
	 * Gets the embedded object parameter names.
	 * 
	 * @return the Iterator of parameters names.
	 */
	public Iterator getParameterNames() {
		return parameters.keySet().iterator();
	}

	/**
	 * Gets the codebase, the root-path used to access resources with relative
	 * paths.
	 * 
	 * @return the code base.
	 */
	public String getCodebase() {
		return codebase;
	}

	/**
	 * Gets the MIME-Type of the code.
	 * 
	 * @return the MIME-Type of the code.
	 */
	public String getCodetype() {
		return codetype;
	}

	/**
	 * Gets the MIME-Type of the object.
	 * 
	 * @return the MIME-Type of the object.
	 */
	public String getMimeType() {
		return mimeType;
	}

	/**
	 * Gets the standby text displayed when the object is loading.
	 * 
	 * @return the standby text.
	 */
	public String getStandby() {
		return standby;
	}

	/**
	 * Sets the codebase, the root-path used to access resources with relative
	 * paths.
	 * 
	 * @param codebase
	 *            the codebase to set.
	 */
	public void setCodebase(String codebase) {
		if (codebase != this.codebase
				|| (codebase != null && !codebase.equals(this.codebase))) {
			this.codebase = codebase;
			requestRepaint();
		}
	}

	/**
	 * Sets the codetype, the MIME-Type of the code.
	 * 
	 * @param codetype
	 *            the codetype to set.
	 */
	public void setCodetype(String codetype) {
		if (codetype != this.codetype
				|| (codetype != null && !codetype.equals(this.codetype))) {
			this.codetype = codetype;
			requestRepaint();
		}
	}

	/**
	 * Sets the mimeType, the MIME-Type of the object.
	 * 
	 * @param mimeType
	 *            the mimeType to set.
	 */
	public void setMimeType(String mimeType) {
		if (mimeType != this.mimeType
				|| (mimeType != null && !mimeType.equals(this.mimeType))) {
			this.mimeType = mimeType;
			requestRepaint();
		}
	}

	/**
	 * Sets the standby, the text to display while loading the object.
	 * 
	 * @param standby
	 *            the standby to set.
	 */
	public void setStandby(String standby) {
		if (standby != this.standby
				|| (standby != null && !standby.equals(this.standby))) {
			this.standby = standby;
			requestRepaint();
		}
	}

	/**
	 * Returns the visual height of the object. Default height is -1, which is
	 * interpreted as "unspecified".
	 * 
	 * @return the height in units specified by heightUnits property.
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Returns the visual width of the object. Default width is -1, which is
	 * interpreted as "unspecified".
	 * 
	 * @return the width in units specified by widthUnits property.
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Sets the visual height of the object. Default height is -1, which is
	 * interpreted as "unspecified".
	 * 
	 * @param height
	 *            the height in units specified by heightUnits property.
	 */
	public void setHeight(int height) {
		if (this.height != height) {
			this.height = height;
			requestRepaint();
		}
	}

	/**
	 * Sets the visual width of the object. Default width is -1, which is
	 * interpreted as "unspecified".
	 * 
	 * @param width
	 *            the width in units specified by widthUnits property.
	 */
	public void setWidth(int width) {
		if (this.width != width) {
			this.width = width;
			requestRepaint();
		}
	}

	/**
	 * Gets the classId attribute.
	 * 
	 * @return the class id.
	 */
	public String getClassId() {
		return classId;
	}

	/**
	 * Sets the classId attribute.
	 * 
	 * @param classId
	 *            the classId to set.
	 */
	public void setClassId(String classId) {
		if (classId != this.classId
				|| (classId != null && !classId.equals(classId))) {
			this.classId = classId;
			requestRepaint();
		}
	}

	/**
	 * Gets the resource contained in the embedded object.
	 * 
	 * @return the Resource
	 */
	public Resource getSource() {
		return source;
	}

	/**
	 * Gets the type of the embedded object.
	 * <p>
	 * This can be one of the following:
	 * <ul>
	 * <li>TYPE_OBJECT <i>(This is the default)</i>
	 * <li>TYPE_IMAGE
	 * </ul>
	 * </p>
	 * 
	 * @return the type.
	 */
	public int getType() {
		return type;
	}

	/**
	 * Sets the object source resource. The dimensions are assumed if possible.
	 * The type is guessed from resource.
	 * 
	 * @param source
	 *            the source to set.
	 */
	public void setSource(Resource source) {
		if (source != null && !source.equals(this.source)) {
			this.source = source;
			String mt = source.getMIMEType();
			if ((mt.substring(0, mt.indexOf("/")).equalsIgnoreCase("image"))) {
				type = TYPE_IMAGE;
			} else {
				// Keep previous type
			}
			requestRepaint();
		}
	}

	/**
	 * Sets the object type.
	 * <p>
	 * This can be one of the following:
	 * <ul>
	 * <li>TYPE_OBJECT <i>(This is the default)</i>
	 * <li>TYPE_IMAGE
	 * </ul>
	 * </p>
	 * 
	 * @param type
	 *            the type to set.
	 */
	public void setType(int type) {
		if (type != TYPE_OBJECT && type != TYPE_IMAGE && type != TYPE_BROWSER) {
			throw new IllegalArgumentException("Unsupported type");
		}
		if (type != this.type) {
			this.type = type;
			requestRepaint();
		}
	}

	/**
	 * Gets the archive attribute.
	 * 
	 * @return the archive attribute.
	 */
	public String getArchive() {
		return archive;
	}

	/**
	 * Sets the archive attribute.
	 * 
	 * @param archive
	 *            the archive string to set.
	 */
	public void setArchive(String archive) {
		if (archive != this.archive
				|| (archive != null && !archive.equals(this.archive))) {
			this.archive = archive;
			requestRepaint();
		}
	}

	/**
	 * Gets the height property units. Default units are
	 * <code>Sizeable.UNITS_PIXELS</code>.
	 * 
	 * @see com.itmill.toolkit.terminal.Sizeable#getHeightUnits()
	 */
	public int getHeightUnits() {
		return this.heightUnits;
	}

	/**
	 * Gets the width property units. Default units are
	 * <code>Sizeable.UNITS_PIXELS</code>.
	 * 
	 * @see com.itmill.toolkit.terminal.Sizeable#getWidthUnits()
	 */
	public int getWidthUnits() {
		return this.widthUnits;
	}

	/**
	 * Sets the height property units.
	 * 
	 * @see com.itmill.toolkit.terminal.Sizeable#setHeightUnits(int)
	 */
	public void setHeightUnits(int units) {
		if (units >= 0 && units <= Sizeable.UNITS_PERCENTAGE
				&& this.heightUnits != units) {
			this.heightUnits = units;
			requestRepaint();
		}
	}

	/**
	 * Sets the width property units.
	 * 
	 * @see com.itmill.toolkit.terminal.Sizeable#setWidthUnits(int)
	 */
	public void setWidthUnits(int units) {
		if (units >= 0 && units <= Sizeable.UNITS_PERCENTAGE
				&& this.widthUnits != units) {
			this.widthUnits = units;
			requestRepaint();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.itmill.toolkit.terminal.Sizeable#setSizeFull()
	 */
	public void setSizeFull() {
		setWidth(100);
		setHeight(100);
		setWidthUnits(UNITS_PERCENTAGE);
		setHeightUnits(UNITS_PERCENTAGE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.itmill.toolkit.terminal.Sizeable#setSizeUndefined()
	 */
	public void setSizeUndefined() {
		setWidth(-1);
		setHeight(-1);
		setWidthUnits(UNITS_PIXELS);
		setHeightUnits(UNITS_PIXELS);
	}

}
