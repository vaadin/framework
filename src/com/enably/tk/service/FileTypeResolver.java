/* *************************************************************************
 
                               Enably Toolkit 

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
   20540, Turku                          email:  info@enably.com
   Finland                               company www: www.enably.com
   
   Primary source for information and releases: www.enably.com

   ********************************************************************** */

package com.enably.tk.service;

import java.io.File;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;
import java.util.StringTokenizer;

import com.enably.tk.terminal.Resource;
import com.enably.tk.terminal.ThemeResource;

/** Utility class that can figure out mime-types and icons related to files.
 * Note that the icons are associated purely to mime-types, so a file
 * may not have a custom icon accessible with this class.
 * 
 * @author IT Mill Ltd.
 * @version @VERSION@
 * @since 3.0
 */
public class FileTypeResolver {

	/** Default icon given if no icon is specified for a mime-type. */
	static public Resource DEFAULT_ICON = new ThemeResource("icon/files/file.gif");
	
	/** Default mime-type. */
	static public  String DEFAULT_MIME_TYPE = "application/octet-stream";

	/** Initial file extension to mime-type mapping. */
	static private String initialExtToMIMEMap =
		"application/cu-seeme                            csm cu,"
			+ "application/dsptype                             tsp,"
			+ "application/futuresplash                        spl,"
			+ "application/mac-binhex40                        hqx,"
			+ "application/msaccess                            mdb,"
			+ "application/msword                              doc dot,"
			+ "application/octet-stream                        bin,"
			+ "application/oda                                 oda,"
			+ "application/pdf                                 pdf,"
			+ "application/pgp-signature                       pgp,"
			+ "application/postscript                          ps ai eps,"
			+ "application/rtf                                 rtf,"
			+ "application/vnd.ms-excel                        xls xlb,"
			+ "application/vnd.ms-powerpoint                   ppt pps pot,"
			+ "application/vnd.wap.wmlc                        wmlc,"
			+ "application/vnd.wap.wmlscriptc                  wmlsc,"
			+ "application/wordperfect5.1                      wp5,"
			+ "application/zip                                 zip,"
			+ "application/x-123                               wk,"
			+ "application/x-bcpio                             bcpio,"
			+ "application/x-chess-pgn                         pgn,"
			+ "application/x-cpio                              cpio,"
			+ "application/x-debian-package                    deb,"
			+ "application/x-director                          dcr dir dxr,"
			+ "application/x-dms                               dms,"
			+ "application/x-dvi                               dvi,"
			+ "application/x-xfig                              fig,"
			+ "application/x-font                              pfa pfb gsf pcf pcf.Z,"
			+ "application/x-gnumeric                          gnumeric,"
			+ "application/x-gtar                              gtar tgz taz,"
			+ "application/x-hdf                               hdf,"
			+ "application/x-httpd-php                         phtml pht php,"
			+ "application/x-httpd-php3                        php3,"
			+ "application/x-httpd-php3-source                 phps,"
			+ "application/x-httpd-php3-preprocessed           php3p,"
			+ "application/x-httpd-php4                        php4,"
			+ "application/x-ica                               ica,"
			+ "application/x-java-archive                      jar,"
			+ "application/x-java-serialized-object            ser,"
			+ "application/x-java-vm                           class,"
			+ "application/x-javascript                        js,"
			+ "application/x-kchart                            chrt,"
			+ "application/x-killustrator                      kil,"
			+ "application/x-kpresenter                        kpr kpt,"
			+ "application/x-kspread                           ksp,"
			+ "application/x-kword                             kwd kwt,"
			+ "application/x-latex                             latex,"
			+ "application/x-lha                               lha,"
			+ "application/x-lzh                               lzh,"
			+ "application/x-lzx                               lzx,"
			+ "application/x-maker                             frm maker frame fm fb book fbdoc,"
			+ "application/x-mif                               mif,"
			+ "application/x-msdos-program                     com exe bat dll,"
			+ "application/x-msi                               msi,"
			+ "application/x-netcdf                            nc cdf,"
			+ "application/x-ns-proxy-autoconfig               pac,"
			+ "application/x-object                            o,"
			+ "application/x-ogg                               ogg,"
			+ "application/x-oz-application                    oza,"
			+ "application/x-perl                              pl pm,"
			+ "application/x-pkcs7-crl                         crl,"
			+ "application/x-redhat-package-manager            rpm,"
			+ "application/x-shar                              shar,"
			+ "application/x-shockwave-flash                   swf swfl,"
			+ "application/x-star-office                       sdd sda,"
			+ "application/x-stuffit                           sit,"
			+ "application/x-sv4cpio                           sv4cpio,"
			+ "application/x-sv4crc                            sv4crc,"
			+ "application/x-tar                               tar,"
			+ "application/x-tex-gf                            gf,"
			+ "application/x-tex-pk                            pk PK,"
			+ "application/x-texinfo                           texinfo texi,"
			+ "application/x-trash                             ~ % bak old sik,"
			+ "application/x-troff                             t tr roff,"
			+ "application/x-troff-man                         man,"
			+ "application/x-troff-me                          me,"
			+ "application/x-troff-ms                          ms,"
			+ "application/x-ustar                             ustar,"
			+ "application/x-wais-source                       src,"
			+ "application/x-wingz                             wz,"
			+ "application/x-x509-ca-cert                      crt,"
			+ "audio/basic                                     au snd,"
			+ "audio/midi                                      mid midi,"
			+ "audio/mpeg                                      mpga mpega mp2 mp3,"
			+ "audio/mpegurl                                   m3u,"
			+ "audio/prs.sid                                   sid,"
			+ "audio/x-aiff                                    aif aiff aifc,"
			+ "audio/x-gsm                                     gsm,"
			+ "audio/x-pn-realaudio                            ra rm ram,"
			+ "audio/x-scpls                                   pls,"
			+ "audio/x-wav                                     wav,"
			+ "image/bitmap                                    bmp,"
			+ "image/gif                                       gif,"
			+ "image/ief                                       ief,"
			+ "image/jpeg                                      jpeg jpg jpe,"
			+ "image/pcx                                       pcx,"
			+ "image/png                                       png,"
			+ "image/tiff                                      tiff tif,"
			+ "image/vnd.wap.wbmp                              wbmp,"
			+ "image/x-cmu-raster                              ras,"
			+ "image/x-coreldraw                               cdr,"
			+ "image/x-coreldrawpattern                        pat,"
			+ "image/x-coreldrawtemplate                       cdt,"
			+ "image/x-corelphotopaint                         cpt,"
			+ "image/x-jng                                     jng,"
			+ "image/x-portable-anymap                         pnm,"
			+ "image/x-portable-bitmap                         pbm,"
			+ "image/x-portable-graymap                        pgm,"
			+ "image/x-portable-pixmap                         ppm,"
			+ "image/x-rgb                                     rgb,"
			+ "image/x-xbitmap                                 xbm,"
			+ "image/x-xpixmap                                 xpm,"
			+ "image/x-xwindowdump                             xwd,"
			+ "text/comma-separated-values                     csv,"
			+ "text/css                                        css,"
			+ "text/html                                       htm html xhtml,"
			+ "text/mathml                                     mml,"
			+ "text/plain                                      txt text diff,"
			+ "text/richtext                                   rtx,"
			+ "text/tab-separated-values                       tsv,"
			+ "text/vnd.wap.wml                                wml,"
			+ "text/vnd.wap.wmlscript                          wmls,"
			+ "text/xml                                        xml,"
			+ "text/x-c++hdr                                   h++ hpp hxx hh,"
			+ "text/x-c++src                                   c++ cpp cxx cc,"
			+ "text/x-chdr                                     h,"
			+ "text/x-csh                                      csh,"
			+ "text/x-csrc                                     c,"
			+ "text/x-java                                     java,"
			+ "text/x-moc                                      moc,"
			+ "text/x-pascal                                   p pas,"
			+ "text/x-setext                                   etx,"
			+ "text/x-sh                                       sh,"
			+ "text/x-tcl                                      tcl tk,"
			+ "text/x-tex                                      tex ltx sty cls,"
			+ "text/x-vcalendar                                vcs,"
			+ "text/x-vcard                                    vcf,"
			+ "video/dl                                        dl,"
			+ "video/fli                                       fli,"
			+ "video/gl                                        gl,"
			+ "video/mpeg                                      mpeg mpg mpe,"
			+ "video/quicktime                                 qt mov,"
			+ "video/x-mng                                     mng,"
			+ "video/x-ms-asf                                  asf asx,"
			+ "video/x-msvideo                                 avi,"
			+ "video/x-sgi-movie                               movie,"
			+ "x-world/x-vrml                                  vrm vrml wrl";

	/** File extension to MIME type mapping. */
	static private Hashtable extToMIMEMap = new Hashtable();

	/** MIME type to Icon mapping. */
	static private Hashtable MIMEToIconMap = new Hashtable();

	static {
		
		// Initialize extension to MIME map
		StringTokenizer lines = new StringTokenizer(initialExtToMIMEMap,",");
		while (lines.hasMoreTokens()) {
			String line = lines.nextToken();
			StringTokenizer exts = new StringTokenizer(line);
			String type = exts.nextToken();
			while (exts.hasMoreTokens()) {
				String ext = exts.nextToken();
				addExtension(ext, type);
			}
		}
		
		// Initialize Icons
		addIcon("inode/drive", new ThemeResource("icon/files/drive.gif"));
		addIcon("inode/directory", new ThemeResource("icon/files/folder.gif"));
	}

	/** Gets the mime-type of a file. Currently the mime-type is resolved
	 * based only on the file name extension.
	 * 
	 * @param fileName name of the file whose mime-type is requested
	 * @return mime-type <code>String</code> for the given filename
	 */
	public static String getMIMEType(String fileName) {

		// Check for nulls
		if (fileName == null) 
			throw new NullPointerException("Filename can not be null");

		// Calculate the extension of the file
		int dotIndex = fileName.indexOf(".");
		while (dotIndex >= 0 && fileName.indexOf(".",dotIndex+1) >= 0) 
			dotIndex = fileName.indexOf(".",dotIndex+1);
		dotIndex++;
	
		if (fileName.length() > dotIndex) {
			String ext = fileName.substring(dotIndex);

			// Return type from extension map, if found
			String type = (String) extToMIMEMap.get(ext);
			if (type != null) return type;
		}

		return DEFAULT_MIME_TYPE;
	}

	/** Gets the descriptive icon representing file, based on the filename.
	 * First the mime-type for the given filename is resolved, and then the
	 * corresponding icon is fetched from the internal icon storage. If it
	 * is not found the default icon is returned.
	 * 
	 * @param fileName name of the file whose icon is requested
	 * @return the icon corresponding to the given file
	 */
	public static Resource getIcon(String fileName) {

		String mimeType = getMIMEType(fileName);
		Resource icon = (Resource) MIMEToIconMap.get(mimeType);
		if (icon != null) return icon;

		// If nothing is known about the file-type, general file
		// icon is used		
		return DEFAULT_ICON;
	}

	/** Gets the descriptive icon representing a file. First the mime-type
	 * for the given file name is resolved, and then the corresponding
	 * icon is fetched from the internal icon storage. If it is not found
	 * the default icon is returned.
	 * 
	 * @param file the file whose icon is requested
	 * @return the icon corresponding to the given file
	 */
	public static Resource getIcon(File file) {

		String mimeType = getMIMEType(file);
		Resource icon = (Resource) MIMEToIconMap.get(mimeType);
		if (icon != null) return icon;

		// If nothing is known about the file-type, general file
		// icon is used		
		return DEFAULT_ICON;
	}

	/** Gets the mime-type for a file. Currently the returned file type is
	 * resolved by the filename extension only.
	 * 
	 * @param file the file whose mime-type is requested
	 * @return the files mime-type <code>String</code>
	 */
	public static String getMIMEType(File file) {

		// Check for nulls
		if (file == null) 
			throw new NullPointerException("File can not be null");

		// Drives
		if (file.getParentFile() == null) return "inode/drive";

		// Directories
		if (file.isDirectory()) return "inode/directory";

		// Return type from extension
		return getMIMEType(file.getName());
	}

	/** Adds a mime-type mapping for the given filename extension. If
	 * the extension is already in the internal mapping it is overwritten.
	 * 
	 * @param extension the filename extension to be associated with
	 * <code>MIMEType</code>
	 * @param MIMEType the new mime-type for <code>extension</code>
	 */
	public static void addExtension(String extension, String MIMEType) {
		extToMIMEMap.put(extension, MIMEType);
	}

	/** Adds a icon for the given mime-type. If the mime-type also has a
	 * corresponding icon, it is replaced with the new icon.
	 * 
	 * @param MIMEType the mime-type whose icon is to be changed
	 * @param icon the new icon to be associated with <code>MIMEType</code>
	 */
	public static void addIcon(String MIMEType, Resource icon) {
		MIMEToIconMap.put(MIMEType, icon);
	}
	
	/** Gets the internal file extension to mime-type mapping.
	 * 
	 * @return unmodifiable map containing the current file extension to
	 * mime-type mapping
	 */
	public static Map getExtensionToMIMETypeMapping() {
		return Collections.unmodifiableMap(extToMIMEMap);
	}

	/** Gets the internal mime-type to icon mapping.
	 * 
	 * @return unmodifiable map containing the current mime-type to icon
	 * mapping
	 */
	public static Map getMIMETypeToIconMapping() {
		return Collections.unmodifiableMap(MIMEToIconMap);
	}
}
