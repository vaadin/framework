/*
 * Copyright 2000-2014 Vaadin Ltd.
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
package com.vaadin.sass.linker;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.w3c.css.sac.CSSException;

import com.google.gwt.core.ext.LinkerContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.linker.AbstractLinker;
import com.google.gwt.core.ext.linker.ArtifactSet;
import com.google.gwt.core.ext.linker.EmittedArtifact;
import com.google.gwt.core.ext.linker.LinkerOrder;
import com.google.gwt.core.ext.linker.LinkerOrder.Order;
import com.google.gwt.core.ext.linker.Shardable;
import com.vaadin.sass.internal.ScssStylesheet;

/**
 * Pre-linker that checks for the existence of SASS files in public folders,
 * compiles them to CSS files with the SassCompiler from Vaadin and adds the CSS
 * back into the artifact.
 * 
 */
@LinkerOrder(Order.PRE)
@Shardable
public class SassLinker extends AbstractLinker {

    @Override
    public String getDescription() {
        return "Compiling SCSS files in public folders to standard CSS";
    }

    @Override
    public ArtifactSet link(TreeLogger logger, LinkerContext context,
            ArtifactSet artifacts, boolean onePermutation)
            throws UnableToCompleteException {

        if (!onePermutation) {
            // The artifact to return
            ArtifactSet toReturn = new ArtifactSet(artifacts);

            // The temporary scss files provided from the artefacts
            List<FileInfo> scssFiles = new ArrayList<FileInfo>();

            // The public files are provided as inputstream, but the compiler
            // needs real files, as they can contain references to other
            // files. They will be stored here, with their relative paths intact
            String tempFolderName = new Date().getTime() + File.separator;
            File tempFolder = createTempDir(tempFolderName);

            // Can't search here specifically for public resources, as the type
            // is different during compilation. This means we have to loop
            // through all the artifacts
            for (EmittedArtifact resource : artifacts
                    .find(EmittedArtifact.class)) {

                // Create the temporary files.
                String partialPath = resource.getPartialPath();
                if (partialPath.endsWith(".scss")) {
                    // In my opinion, the SCSS file does not need to be
                    // output to the web content folder, as they can't
                    // be used there
                    toReturn.remove(resource);

                    String fileName = partialPath;
                    File path = tempFolder;

                    int separatorIndex = fileName.lastIndexOf(File.separator);
                    if (-1 != separatorIndex) {
                        fileName = fileName.substring(separatorIndex + 1);

                        String filePath = partialPath.substring(0,
                                separatorIndex);
                        path = createTempDir(tempFolderName + filePath);
                    }

                    File tempfile = new File(path, fileName);
                    try {
                        boolean fileCreated = tempfile.createNewFile();
                        if (fileCreated) {

                            // write the received inputstream to the temp file
                            writeFromInputStream(resource.getContents(logger),
                                    tempfile);

                            // Store the file info for the compilation
                            scssFiles.add(new FileInfo(tempfile, partialPath));
                        } else {
                            logger.log(TreeLogger.WARN, "Duplicate file "
                                    + tempfile.getPath());
                        }
                    } catch (IOException e) {
                        logger.log(TreeLogger.ERROR,
                                "Could not write temporary file " + fileName, e);
                    }
                }
            }

            // Compile the files and store them in the artifact
            logger.log(TreeLogger.INFO, "Processing " + scssFiles.size()
                    + " Sass file(s)");
            for (FileInfo fileInfo : scssFiles) {
                logger.log(TreeLogger.INFO, "   " + fileInfo.originalScssPath
                        + " -> " + fileInfo.getOriginalCssPath());

                try {
                    ScssStylesheet scss = ScssStylesheet.get(fileInfo
                            .getAbsolutePath());
                    if (!fileInfo.isMixin()) {
                        scss.compile();
                        InputStream is = new ByteArrayInputStream(scss
                                .printState().getBytes());

                        toReturn.add(this.emitInputStream(logger, is,
                                fileInfo.getOriginalCssPath()));
                    }

                    fileInfo.getFile().delete();
                } catch (CSSException e) {
                    logger.log(TreeLogger.ERROR, "SCSS compilation failed for "
                            + fileInfo.getOriginalCssPath(), e);
                } catch (IOException e) {
                    logger.log(
                            TreeLogger.ERROR,
                            "Could not write CSS file for "
                                    + fileInfo.getOriginalCssPath(), e);
                } catch (Exception e) {
                    logger.log(TreeLogger.ERROR, "SCSS compilation failed for "
                            + fileInfo.getOriginalCssPath(), e);
                }
            }

            return toReturn;
        }

        return artifacts;
    }

    /**
     * Writes the contents of an InputStream out to a file.
     * 
     * @param contents
     * @param tempfile
     * @throws IOException
     */
    private void writeFromInputStream(InputStream contents, File tempfile)
            throws IOException {
        // write the inputStream to a FileOutputStream
        OutputStream out = new FileOutputStream(tempfile);

        int read = 0;
        byte[] bytes = new byte[1024];

        while ((read = contents.read(bytes)) != -1) {
            out.write(bytes, 0, read);
        }

        contents.close();
        out.flush();
        out.close();
    }

    /**
     * Create folder in temporary space on disk.
     * 
     * @param partialPath
     * @return
     */
    private File createTempDir(String partialPath) {
        String baseTempPath = System.getProperty("java.io.tmpdir");

        File tempDir = new File(baseTempPath + File.separator + partialPath);
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }

        tempDir.deleteOnExit();
        return tempDir;
    }

    /**
     * Temporal storage for file info from Artifact.
     */
    private class FileInfo {
        private String originalScssPath;
        private File file;

        public FileInfo(File file, String originalScssPath) {
            this.file = file;
            this.originalScssPath = originalScssPath;
        }

        public boolean isMixin() {
            return file.getName().startsWith("_");
        }

        public String getAbsolutePath() {
            return file.getAbsolutePath();
        }

        public String getOriginalCssPath() {
            return originalScssPath.substring(0, originalScssPath.length() - 5)
                    + ".css";
        }

        public File getFile() {
            return file;
        }
    }
}
