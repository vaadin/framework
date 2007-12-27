/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.ui;

import java.util.Hashtable;
import java.util.Iterator;

import com.itmill.toolkit.terminal.HasSize;
import com.itmill.toolkit.terminal.PaintException;
import com.itmill.toolkit.terminal.PaintTarget;
import com.itmill.toolkit.terminal.Resource;
import com.itmill.toolkit.terminal.Size;

/**
 * Component for embedding external objects.
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 3.0
 */
public class Embedded extends AbstractComponent implements HasSize {

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

    private Size size = new Size(this);

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
     *                the Source of the embedded object.
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

        size.paint(target);

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
        for (final Iterator i = getParameterNames(); i.hasNext();) {
            target.startTag("embeddedparam");
            final String key = (String) i.next();
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
     *                the name of the parameter.
     * @param value
     *                the value of the parameter.
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
     *                the name of the parameter to remove.
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
     *                the codebase to set.
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
     *                the codetype to set.
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
     *                the mimeType to set.
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
     *                the standby to set.
     */
    public void setStandby(String standby) {
        if (standby != this.standby
                || (standby != null && !standby.equals(this.standby))) {
            this.standby = standby;
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
     *                the classId to set.
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
     *                the source to set.
     */
    public void setSource(Resource source) {
        if (source != null && !source.equals(this.source)) {
            this.source = source;
            final String mt = source.getMIMEType();
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
     *                the type to set.
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
     *                the archive string to set.
     */
    public void setArchive(String archive) {
        if (archive != this.archive
                || (archive != null && !archive.equals(this.archive))) {
            this.archive = archive;
            requestRepaint();
        }
    }

    public Size getSize() {
        return size;
    }

    /* Compatibility methods for previous Sizeable interface */

    /**
     * @deprecated use Size object instead (getSize().setWidth()).
     */
    public void setWidth(int width) {
        size.setWidth(width);
    }

    /**
     * @deprecated use Size object instead (getSize().setWidthUnits()).
     */
    public void setWidthUnits(int unit) {
        size.setWidthUnits(unit);
    }

    /**
     * @deprecated use Size object instead (getSize().setHeight()).
     */
    public void setHeight(int height) {
        size.setHeight(height);
    }

    /**
     * @deprecated use Size object instead (getSize().setHeightUnits()).
     */
    public void setHeightUnits(int unit) {
        size.setHeightUnits(unit);
    }

    /**
     * @deprecated use Size object instead (getSize().getWidth()).
     */
    public int getWidth() {
        return size.getWidth();
    }

    /**
     * @deprecated use Size object instead (getSize().getWidthUnits()).
     */
    public int getWidthUnits() {
        return size.getWidthUnits();
    }

    /**
     * @deprecated use Size object instead (getSize().getHeight()).
     */
    public int getHeight() {
        return size.getHeight();
    }

    /**
     * @deprecated use Size object instead (getSize().getHeightUnits()).
     */
    public int getHeightUnits() {
        return size.getHeightUnits();
    }

}
