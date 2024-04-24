/*
 * Copyright 2000-2022 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.vaadin.util;

import java.io.File;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;

/**
 * Utility class that can figure out mime-types and icons related to files.
 * <p>
 * Note : The icons are associated purely to mime-types, so a file may not have
 * a custom icon accessible with this class.
 * </p>
 *
 * @author Vaadin Ltd.
 * @since 3.0
 */
public class FileTypeResolver {

    /**
     * Default icon given if no icon is specified for a mime-type.
     */
    public static Resource DEFAULT_ICON = new ThemeResource(
            "../runo/icons/16/document.png");

    /**
     * Default mime-type.
     */
    public static String DEFAULT_MIME_TYPE = "application/octet-stream";

    /**
     * Initial MIME-type to file extension mapping where the MIME type starts
     * with {@code application/vnd}. Separate from the main mapping in order to
     * limit the amount of long lines for the sake of readability.
     */
    private static final String INITIAL_APPLICATION_VND_MIME_TO_EXT_MAPPING = ""
            + "application/vnd.ms-access                                                    mdb,"
            + "application/vnd.ms-excel                                                     xls xlb,"
            + "application/vnd.ms-excel.addin.macroEnabled.12                               xlam,"
            + "application/vnd.ms-excel.sheet.binary.macroEnabled.12                        xlsb,"
            + "application/vnd.ms-excel.sheet.macroEnabled.12                               xlsm,"
            + "application/vnd.ms-excel.template.macroEnabled.12                            xltm,"
            + "application/vnd.ms-powerpoint                                                ppt pps pot,"
            + "application/vnd.ms-powerpoint.addin.macroEnabled.12                          ppam,"
            + "application/vnd.ms-powerpoint.presentation.macroEnabled.12                   pptm,"
            + "application/vnd.ms-powerpoint.slideshow.macroEnabled.12                      ppsm,"
            + "application/vnd.ms-powerpoint.template.macroEnabled.12                       potm,"
            + "application/vnd.ms-word.document.macroEnabled.12                             docm,"
            + "application/vnd.ms-word.template.macroEnabled.12                             dotm,"
            + "application/vnd.openxmlformats-officedocument.presentationml.presentation    pptx,"
            + "application/vnd.openxmlformats-officedocument.presentationml.slideshow       ppsx,"
            + "application/vnd.openxmlformats-officedocument.presentationml.template        potx,"
            + "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet            xlsx,"
            + "application/vnd.openxmlformats-officedocument.spreadsheetml.template         xltx,"
            + "application/vnd.openxmlformats-officedocument.wordprocessingml.document      docx,"
            + "application/vnd.openxmlformats-officedocument.wordprocessingml.template      dotx,"
            + "application/vnd.wap.wmlc                                                     wmlc,"
            + "application/vnd.wap.wmlscriptc                                               wmlsc,";

    /**
     * Initial MIME-type to file extension mapping that will get parsed into a
     * file extension to MIME-type map.
     */
    private static final String INITIAL_MIME_TO_EXT_MAPPING = ""
            + "application/cu-seeme                            csm cu,"
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
            + INITIAL_APPLICATION_VND_MIME_TO_EXT_MAPPING
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
            + "audio/mp4                                       m4a,"
            + "audio/mpeg                                      mpga mpega mp2 mp3,"
            + "audio/mpegurl                                   m3u,"
            + "audio/ogg                                       ogg,"
            + "audio/prs.sid                                   sid,"
            + "audio/x-aac                                     aac,"
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
            + "image/svg+xml                                   svg svgz,"
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
            + "video/mp4                                       mp4,"
            + "video/mpeg                                      mpeg mpg mpe,"
            + "video/ogg                                       ogv,"
            + "video/quicktime                                 qt mov,"
            + "video/x-mng                                     mng,"
            + "video/x-ms-asf                                  asf asx,"
            + "video/x-msvideo                                 avi,"
            + "video/x-sgi-movie                               movie,"
            + "x-world/x-vrml                                  vrm vrml wrl";

    /**
     * File extension to MIME type mapping. All extensions are in lower case.
     */
    private static final Map<String, String> EXT_TO_MIME_MAP = new ConcurrentHashMap<>();

    static {

        // Initialize extension to MIME map
        final StringTokenizer lines = new StringTokenizer(
                INITIAL_MIME_TO_EXT_MAPPING, ",");
        while (lines.hasMoreTokens()) {
            final String line = lines.nextToken();
            final StringTokenizer exts = new StringTokenizer(line);
            final String type = exts.nextToken();
            while (exts.hasMoreTokens()) {
                final String ext = exts.nextToken();
                addExtension(ext, type);
            }
        }

    }

    /**
     * Gets the mime-type of a file. Currently the mime-type is resolved based
     * only on the file name extension.
     *
     * @param fileName
     *            the name of the file whose mime-type is requested.
     * @return mime-type <code>String</code> for the given filename
     */
    public static String getMIMEType(String fileName) {

        // Checks for nulls
        if (fileName == null) {
            throw new NullPointerException("Filename can not be null");
        }

        // Calculates the extension of the file
        int dotIndex = fileName.indexOf('.');
        while (dotIndex >= 0 && fileName.indexOf('.', dotIndex + 1) >= 0) {
            dotIndex = fileName.indexOf('.', dotIndex + 1);
        }
        dotIndex++;

        if (fileName.length() > dotIndex) {
            String ext = fileName.substring(dotIndex);

            // Ignore any query parameters
            int queryStringStart = ext.indexOf('?');
            if (queryStringStart > 0) {
                ext = ext.substring(0, queryStringStart);
            }

            // Return type from extension map, if found
            final String type = EXT_TO_MIME_MAP
                    .get(ext.toLowerCase(Locale.ROOT));
            if (type != null) {
                return type;
            }
        }

        return DEFAULT_MIME_TYPE;
    }

    /**
     * Gets the mime-type for a file. Currently the returned file type is
     * resolved by the filename extension only.
     *
     * @param file
     *            the file whose mime-type is requested.
     * @return the files mime-type <code>String</code>
     */
    public static String getMIMEType(File file) {

        // Checks for nulls
        if (file == null) {
            throw new NullPointerException("File can not be null");
        }

        // Directories
        if (file.isDirectory()) {
            // Drives
            if (file.getParentFile() == null) {
                return "inode/drive";
            } else {
                return "inode/directory";
            }
        }

        // Return type from extension
        return getMIMEType(file.getName());
    }

    /**
     * Adds a mime-type mapping for the given filename extension. If the
     * extension is already in the internal mapping it is overwritten.
     *
     * @param extension
     *            the filename extension to be associated with
     *            <code>MIMEType</code>.
     * @param mimeType
     *            the new mime-type for <code>extension</code>.
     */
    public static void addExtension(String extension, String mimeType) {
        EXT_TO_MIME_MAP.put(extension.toLowerCase(Locale.ROOT), mimeType);
    }

    /**
     * Gets the internal file extension to mime-type mapping.
     *
     * @return unmodifiable map containing the current file extension to
     *         mime-type mapping
     */
    public static Map<String, String> getExtensionToMIMETypeMapping() {
        return Collections.unmodifiableMap(EXT_TO_MIME_MAP);
    }

    protected FileTypeResolver() {
    }
}
