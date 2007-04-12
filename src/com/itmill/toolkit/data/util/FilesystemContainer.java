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

package com.itmill.toolkit.data.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Method;

import com.itmill.toolkit.data.Container;
import com.itmill.toolkit.data.Item;
import com.itmill.toolkit.data.Property;
import com.itmill.toolkit.service.FileTypeResolver;
import com.itmill.toolkit.terminal.Resource;

/**
 * A hierarchical container wrapper for a filesystem.
 * 
 * @author IT Mill Ltd.
 * @version @VERSION@
 * @since 3.0
 */
public class FilesystemContainer implements Container.Hierarchical {

	/** 
	 * String identifier of a file's "name" property. 
	 */
	public static String PROPERTY_NAME = "Name";

	/**
	 * String identifier of a file's "size" property. 
	 */
	public static String PROPERTY_SIZE = "Size";

	/** 
	 * String identifier of a file's "icon" property. 
	 */
	public static String PROPERTY_ICON = "Icon";

	/**
	 * String identifier of a file's "last modified" property.
	 */
	public static String PROPERTY_LASTMODIFIED = "Last Modified";

	/**
	 * List of the string identifiers for the available properties. 
	 */
	public static Collection FILE_PROPERTIES;

	private static Method FILEITEM_LASTMODIFIED;

	private static Method FILEITEM_NAME;
	private static Method FILEITEM_ICON;
	private static Method FILEITEM_SIZE;

	static {

		FILE_PROPERTIES = new ArrayList();
		FILE_PROPERTIES.add(PROPERTY_NAME);
		FILE_PROPERTIES.add(PROPERTY_ICON);
		FILE_PROPERTIES.add(PROPERTY_SIZE);
		FILE_PROPERTIES.add(PROPERTY_LASTMODIFIED);
		FILE_PROPERTIES = Collections.unmodifiableCollection(FILE_PROPERTIES);
		try {
			FILEITEM_LASTMODIFIED =
				FileItem.class.getMethod("lastModified", new Class[] {
			});
			FILEITEM_NAME = FileItem.class.getMethod("getName", new Class[] {
			});
			FILEITEM_ICON = FileItem.class.getMethod("getIcon", new Class[] {
			});
			FILEITEM_SIZE = FileItem.class.getMethod("getSize", new Class[] {
			});
		} catch (NoSuchMethodException e) {

		}
	}

	private File[] roots = new File[] {
	};
	private FilenameFilter filter = null;
	private boolean recursive = true;

	/** 
	 * Constructs a new <code>FileSystemContainer</code> with the specified
	 * file as the root of the filesystem. The files are included recursively.
	 * 
	 * @param root the root file for the new file-system container. Null values are ignored.
	 */
	public FilesystemContainer(File root) {
		if (root != null) {
			this.roots = new File[] { root };
		}
	}

	/** 
	 * Constructs a new <code>FileSystemContainer</code> with the specified
	 * file as the root of the filesystem. The files are included recursively.
	 * 
	 * @param root the root file for the new file-system container.
	 * @param recursive should the container recursively contain subdirectories.
	 */
	public FilesystemContainer(File root, boolean recursive) {
		this(root);
		this.setRecursive(recursive);
	}

	/** 
	 * Constructs a new <code>FileSystemContainer</code> with the specified
	 * file as the root of the filesystem.
	 * 
	 * @param root the root file for the new file-system container.
	 * @param extension the Filename extension (w/o separator) to limit the files in container.
	 * @param recursive should the container recursively contain subdirectories.
	 */
	public FilesystemContainer(
		File root,
		String extension,
		boolean recursive) {
		this(root);
		this.setFilter(extension);
		this.setRecursive(recursive);
	}

	/** 
	 * Constructs a new <code>FileSystemContainer</code> with the specified
	 * root and recursivity status.
	 * 
	 * @param root the root file for the new file-system container.
	 * @param filter the Filename filter to limit the files in container.
	 * @param recursive should the container recursively contain subdirectories.
	 */
	public FilesystemContainer(
		File root,
		FilenameFilter filter,
		boolean recursive) {
		this(root);
		this.setFilter(filter);
		this.setRecursive(recursive);
	}

	/** 
	 * Adds new root file directory. 
	 * Adds a file to be included as root file directory in the <code>FilesystemContainer</code>.
	 * @param root the File to be added as root directory. Null values are ignored.
	 */
	public void addRoot(File root) {
		if (root != null) {
			File[] newRoots = new File[this.roots.length + 1];
			for (int i = 0; i < this.roots.length; i++) {
				newRoots[i] = this.roots[i];
			}
			newRoots[this.roots.length] = root;
			this.roots = newRoots;
		}
	}

	/** 
	 * Tests if the specified Item in the container may have children.
	 * Since a <code>FileSystemContainer</code> contains files and
	 * directories, this method returns <code>true</code> for directory
	 * Items only.
	 * @param itemId the id of the item.
	 * @return <code>true</code> if the specified Item is a directory,
	 * <code>false</code> otherwise.
	 */
	public boolean areChildrenAllowed(Object itemId) {
		return itemId instanceof File
			&& ((File) itemId).canRead()
			&& ((File) itemId).isDirectory();
	}

	/* Gets the ID's of all Items who are children of the specified Item.
	 * Don't add a JavaDoc comment here, we use the default documentation
	 * from implemented interface.
	 */
	public Collection getChildren(Object itemId) {
		
		if (!(itemId instanceof File))
			return Collections.unmodifiableCollection(new LinkedList());
		File[] f;
		if (this.filter != null)
			f = ((File) itemId).listFiles(this.filter);
		else
			f = ((File) itemId).listFiles();

		if (f == null)
			return Collections.unmodifiableCollection(new LinkedList());

		List l = Arrays.asList(f);
		Collections.sort(l);

		return Collections.unmodifiableCollection(l);
	}

	/* Gets the parent item of the specified Item.
	 * Don't add a JavaDoc comment here, we use the default documentation
	 * from implemented interface.
	 */
	public Object getParent(Object itemId) {

		if (!(itemId instanceof File))
			return null;
		return ((File) itemId).getParentFile();
	}

	/* Tests if the specified Item has any children.
	 * Don't add a JavaDoc comment here, we use the default documentation
	 * from implemented interface.
	 */
	public boolean hasChildren(Object itemId) {

		if (!(itemId instanceof File))
			return false;
		String[] l;
		if (this.filter != null)
			l = ((File) itemId).list(this.filter);
		else
			l = ((File) itemId).list();
		return (l != null) && (l.length > 0);
	}

	/* Tests if the specified Item is the root of the filesystem.
	 * Don't add a JavaDoc comment here, we use the default documentation
	 * from implemented interface.
	 */
	public boolean isRoot(Object itemId) {

		if (!(itemId instanceof File))
			return false;
		for (int i = 0; i < roots.length; i++) {
			if (roots[i].equals((File) itemId))
				return true;
		}
		return false;
	}

	/* Gets the ID's of all root Items in the container.
	 * Don't add a JavaDoc comment here, we use the default documentation
	 * from implemented interface.
	 */
	public Collection rootItemIds() {

		File[] f;

		// in single root case we use children
		if (roots.length == 1) {
			if (this.filter != null)
				f = roots[0].listFiles(this.filter);
			else
				f = roots[0].listFiles();
		} else {
			f = this.roots;
		}

		if (f == null)
			return Collections.unmodifiableCollection(new LinkedList());

		List l = Arrays.asList(f);
		Collections.sort(l);

		return Collections.unmodifiableCollection(l);
	}

	/** 
	 * Returns <code>false</code> when conversion from files to directories is not
	 * supported.
	 * @param itemId 
	 * 				the ID of the item.
	 * @param areChildrenAllowed 
	 * 				the boolean value specifying if the Item can have children or not. 
	 * @return <code>true</code> if the operaton is successful otherwise 
	 * <code>false</code>.
	 * @throws UnsupportedOperationException
	 * 									if the setChildrenAllowed is not supported.
	 */
	public boolean setChildrenAllowed(
		Object itemId,
		boolean areChildrenAllowed)
		throws UnsupportedOperationException {

		throw new UnsupportedOperationException("Conversion file to/from directory is not supported");
	}

	/** 
	 * Returns <code>false</code> when moving files around in the filesystem is not
	 * supported.
	 * @param itemId the ID of the item.
	 * @param newParentId the ID of the Item that's to be the new parent
	 * of the Item identified with itemId.
	 * @return <code>true</code> if the operation is successful otherwise
	 * <code>false</code>.
	 * @throws UnsupportedOperationException
	 * 										if the setParent is not supported.
	 */
	public boolean setParent(Object itemId, Object newParentId)
		throws UnsupportedOperationException {

		throw new UnsupportedOperationException("File moving is not supported");
	}

	/* Tests if the filesystem contains the specified Item.
	 * Don't add a JavaDoc comment here, we use the default documentation
	 * from implemented interface.
	 */
	public boolean containsId(Object itemId) {

		if (!(itemId instanceof File))
			return false;
		boolean val = false;

		// Try to match all roots
		for (int i = 0; i < roots.length; i++) {
			try {
				val
					|= ((File) itemId).getCanonicalPath().startsWith(
						roots[i].getCanonicalPath());
			} catch (IOException e) {
				// Exception ignored				
			}

		}
		if (val && this.filter != null)
			val
				&= this.filter.accept(
					((File) itemId).getParentFile(),
					((File) itemId).getName());
		return val;
	}

	/* Gets the specified Item from the filesystem.
	 * Don't add a JavaDoc comment here, we use the default documentation
	 * from implemented interface.
	 */
	public Item getItem(Object itemId) {

		if (!(itemId instanceof File))
			return null;
		return new FileItem((File) itemId);
	}

	/** 
	 * Internal recursive method to add the files under the specified
	 * directory to the collection.
	 * 
	 * @param col the collection where the found items are added
	 * @param f the root file where to start adding files
	 */
	private void addItemIds(Collection col, File f) {
		File[] l;
		if (this.filter != null)
			l = f.listFiles(this.filter);
		else
			l = f.listFiles();
		List ll = Arrays.asList(l);
		Collections.sort(ll);

		for (Iterator i = ll.iterator();i.hasNext();) {
			File lf = (File)i.next();
			if (lf.isDirectory())
				addItemIds(col, lf);
			else
				col.add(lf);
		}
	}

	/* Gets the IDs of Items in the filesystem.
	 * Don't add a JavaDoc comment here, we use the default documentation
	 * from implemented interface.
	 */
	public Collection getItemIds() {

		

		if (recursive) {
			Collection col = new ArrayList();
			for (int i = 0; i < roots.length; i++) {
				addItemIds(col, roots[i]);
			}
			return Collections.unmodifiableCollection(col);
		} else {
			File[] f;
			if (roots.length == 1) {
				if (this.filter != null)
					f = roots[0].listFiles(this.filter);
				else
					f = roots[0].listFiles();
			} else {
				f = roots;
			}

			if (f == null)
				return Collections.unmodifiableCollection(new LinkedList());
				
			List l = Arrays.asList(f);
			Collections.sort(l);
			return Collections.unmodifiableCollection(l);
		}
		
	}

	/** 
	 * Gets the specified property of the specified file Item. The
	 * available file properties are "Name", "Size" and "Last Modified".
	 * If propertyId is not one of those, <code>null</code> is
	 * returned.
	 * 
	 * @param itemId the ID of the file whose property is requested.
	 * @param propertyId the property's ID.
	 * @return the requested property's value, or <code>null</code>
	 */
	public Property getContainerProperty(Object itemId, Object propertyId) {

		if (!(itemId instanceof File))
			return null;

		if (propertyId.equals(PROPERTY_NAME))
			return new MethodProperty(
				getType(propertyId),
				new FileItem((File) itemId),
				FILEITEM_NAME,
				null);

		if (propertyId.equals(PROPERTY_ICON))
			return new MethodProperty(
				getType(propertyId),
				new FileItem((File) itemId),
				FILEITEM_ICON,
				null);

		if (propertyId.equals(PROPERTY_SIZE))
			return new MethodProperty(
				getType(propertyId),
				new FileItem((File) itemId),
				FILEITEM_SIZE,
				null);

		if (propertyId.equals(PROPERTY_LASTMODIFIED))
			return new MethodProperty(
				getType(propertyId),
				new FileItem((File) itemId),
				FILEITEM_LASTMODIFIED,
				null);

		return null;
	}

	/** 
	 * Gets the collection of available file properties.
	 * 
	 * @return Unmodifiable collection containing all available file
	 * properties.
	 */
	public Collection getContainerPropertyIds() {
		return FILE_PROPERTIES;
	}

	/** 
	 * Gets the specified property's data type. "Name" is a
	 * <code>String</code>, "Size" is a <code>Long</code>, "Last Modified"
	 * is a <code>Date</code>. If propertyId is not one of
	 * those, <code>null</code> is returned.
	 * 
	 * @param propertyId the ID of the property whose type is requested.
	 * @return data type of the requested property, or <code>null</code>
	 */
	public Class getType(Object propertyId) {

		if (propertyId.equals(PROPERTY_NAME))
			return String.class;
		if (propertyId.equals(PROPERTY_ICON))
			return Resource.class;
		if (propertyId.equals(PROPERTY_SIZE))
			return Long.class;
		if (propertyId.equals(PROPERTY_LASTMODIFIED))
			return Date.class;
		return null;
	}

	/** 
	 * Internal method to recursively calculate the number of files under
	 * a root directory.
	 * 
	 * @param f the root to start counting from.
	 */
	private int getFileCounts(File f) {
		File[] l;
		if (this.filter != null)
			l = f.listFiles(this.filter);
		else
			l = f.listFiles();

		if (l == null)
			return 0;
		int ret = l.length;
		for (int i = 0; i < l.length; i++) {
			if (l[i].isDirectory())
				ret += getFileCounts(l[i]);
		}
		return ret;
	}

	/** 
	 * Gets the number of Items in the container. In effect, this is the
	 * combined amount of files and directories.
	 * 
	 * @return Number of Items in the container.
	 */
	public int size() {

		if (recursive) {
			int counts = 0;
			for (int i = 0; i < roots.length; i++) {
				counts += getFileCounts(this.roots[i]);
			}
			return counts;
		} else {
			File[] f;
			if (roots.length == 1) {
				if (this.filter != null)
					f = roots[0].listFiles(this.filter);
				else
					f = roots[0].listFiles();
			} else {
				f = roots;
			}

			if (f == null)
				return 0;
			return f.length;
		}
	}

	/** 
	 * A Item wrapper for files in a filesystem.
	 * @author IT Mill Ltd.
	 * @version @VERSION@
	 * @since 3.0
	 */
	public class FileItem implements Item {

		/** 
		 * The wrapped file. 
		 */
		private File file;

		/** 
		 * Constructs a FileItem from a existing file. 
		 */
		private FileItem(File file) {
			this.file = file;
		}

		/* Gets the specified property of this file.
		 * Don't add a JavaDoc comment here, we use the default documentation
		 * from implemented interface.
		 */
		public Property getItemProperty(Object id) {
			return FilesystemContainer.this.getContainerProperty(file, id);
		}

		/* Gets the IDs of all properties available for this item
		 * Don't add a JavaDoc comment here, we use the default documentation
		 * from implemented interface.
		 */
		public Collection getItemPropertyIds() {
			return FilesystemContainer.this.getContainerPropertyIds();
		}

		/**
		 * Calculates a integer hash-code for the Property that's unique
		 * inside the Item containing the Property. Two different Properties
		 * inside the same Item contained in the same list always have
		 * different hash-codes, though Properties in different Items may
		 * have identical hash-codes.
		 * 
		 * @return A locally unique hash-code as integer
		 */
		public int hashCode() {
			return file.hashCode() ^ FilesystemContainer.this.hashCode();
		}

		/**
		 * Tests if the given object is the same as the this object.
		 * Two Properties got from an Item with the same ID are equal.
		 * 
		 * @param obj an object to compare with this object.
		 * @return <code>true</code> if the given object is the same as
		 * this object, <code>false</code> if not
		 */
		public boolean equals(Object obj) {
			if (obj == null || !(obj instanceof FileItem))
				return false;
			FileItem fi = (FileItem) obj;
			return fi.getHost() == getHost() && fi.file.equals(file);
		}
		/**
		 * Gets the host of this file.
		 */
		private FilesystemContainer getHost() {
			return FilesystemContainer.this;
		}
		/**
		 * Gets the last modified date of this file.
		 * @return Date
		 */
		public Date lastModified() {
			return new Date(this.file.lastModified());
		}
		/**
		 * Gets the name of this file.
		 * @return file name of this file.
		 */
		public String getName() {
			return this.file.getName();
		}
		/**
		 * Gets the icon of this file.
		 * @return the icon of this file.
		 */
		public Resource getIcon() {
			return FileTypeResolver.getIcon(this.file);
		}
		/**
		 * Gets the size of this file.
		 * @return size
		 */
		public long getSize() {
			if (this.file.isDirectory())
				return 0;
			return this.file.length();
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			if ("".equals(file.getName()))
				return file.getAbsolutePath();
			return file.getName();
		}

		/** 
		 * Filesystem container does not support adding new properties.
		 * @see com.itmill.toolkit.data.Item#addItemProperty(Object, Property)
		 */
		public boolean addItemProperty(Object id, Property property)
			throws UnsupportedOperationException {
			throw new UnsupportedOperationException(
				"Filesystem container "
					+ "does not support adding new properties");
		}

		/**
		 * Filesystem container does not support removing properties.
		 * @see com.itmill.toolkit.data.Item#removeItemProperty(Object)
		 */
		public boolean removeItemProperty(Object id)
			throws UnsupportedOperationException {
			throw new UnsupportedOperationException("Filesystem container does not support property removal");
		}

	}

	/** 
	 * Generic file extension filter for displaying only files having certain extension.
	 * @author IT Mill Ltd.
	 * @version @VERSION@
	 * @since 3.0
	 */
	public class FileExtensionFilter implements FilenameFilter {

		private String filter;

		/** 
		 * Constructs new FileExtensionFilter using given extension.
		 * @param fileExtension the File extension without the separator (dot).
		 */
		public FileExtensionFilter(String fileExtension) {
			this.filter = "." + fileExtension;
		}

		/** 
		 * Allows only files with the extension and directories.
		 * @see java.io.FilenameFilter#accept(File, String)
		 */
		public boolean accept(File dir, String name) {
			if (name.endsWith(filter))
				return true;
			return new File(dir, name).isDirectory();
		}

	}
	/**
	 * Returns the file filter used to limit the files in this container.
	 * @return Used filter instance or null if no filter is assigned.
	 */
	public FilenameFilter getFilter() {
		return filter;
	}

	/**
	 * Sets the file filter used to limit the files in this container.
	 * @param filter The filter to set. <code>null</code> disables filtering.
	 */
	public void setFilter(FilenameFilter filter) {
		this.filter = filter;
	}

	/**
	 * Sets the file filter used to limit the files in this container.
	 * @param extension the Filename extension (w/o separator) to limit the files in container.
	 */
	public void setFilter(String extension) {
		this.filter = new FileExtensionFilter(extension);
	}

	/**
	 * Is this container recursive filesystem.
	 * @return <code>true</code> if container is recursive, <code>false</code> otherwise.
	 */
	public boolean isRecursive() {
		return recursive;
	}

	/**
	 * Sets the container recursive property.
	 * Set this to false to limit the files directly under the root file.
	 * <p>
	 * Note : This is meaningful only if the root really is a directory.
	 * </p>
	 * @param recursive the  New value for recursive property.
	 */
	public void setRecursive(boolean recursive) {
		this.recursive = recursive;
	}

	/**
	 * @see com.itmill.toolkit.data.Container#addContainerProperty(Object, Class, Object)
	 */
	public boolean addContainerProperty(
		Object propertyId,
		Class type,
		Object defaultValue)
		throws UnsupportedOperationException {
		throw new UnsupportedOperationException("File system container does not support this operation");
	}

	/**
	 * @see com.itmill.toolkit.data.Container#addItem()
	 */
	public Object addItem() throws UnsupportedOperationException {
		throw new UnsupportedOperationException("File system container does not support this operation");
	}

	/**
	 * @see com.itmill.toolkit.data.Container#addItem(Object)
	 */
	public Item addItem(Object itemId) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("File system container does not support this operation");
	}

	/**
	 * @see com.itmill.toolkit.data.Container#removeAllItems()
	 */
	public boolean removeAllItems() throws UnsupportedOperationException {
		throw new UnsupportedOperationException("File system container does not support this operation");
	}

	/**
	 * @see com.itmill.toolkit.data.Container#removeItem(Object)
	 */
	public boolean removeItem(Object itemId)
		throws UnsupportedOperationException {
		throw new UnsupportedOperationException("File system container does not support this operation");
	}

	/**
	 * @see com.itmill.toolkit.data.Container#removeContainerProperty(Object)
	 */
	public boolean removeContainerProperty(Object propertyId)
		throws UnsupportedOperationException {
		throw new UnsupportedOperationException("File system container does not support this operation");
	}
}
