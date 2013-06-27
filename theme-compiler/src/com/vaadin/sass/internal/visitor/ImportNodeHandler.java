/*
 * Copyright 2000-2013 Vaadin Ltd.
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

package com.vaadin.sass.internal.visitor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.LexicalUnit;

import com.vaadin.sass.internal.ScssStylesheet;
import com.vaadin.sass.internal.parser.LexicalUnitImpl;
import com.vaadin.sass.internal.parser.ParseException;
import com.vaadin.sass.internal.tree.ImportNode;
import com.vaadin.sass.internal.tree.Node;
import com.vaadin.sass.internal.tree.RuleNode;
import com.vaadin.sass.internal.util.StringUtil;

public class ImportNodeHandler {

    public static void traverse(Node node) {
        ScssStylesheet styleSheet = null;
        if (node instanceof ScssStylesheet) {
            styleSheet = (ScssStylesheet) node;
        } else {
            // iterate to parents of node, find ScssStylesheet
            Node parent = node.getParentNode();
            while (parent != null && !(parent instanceof ScssStylesheet)) {
                parent = parent.getParentNode();
            }
            if (parent instanceof ScssStylesheet) {
                styleSheet = (ScssStylesheet) parent;
            }
        }
        if (styleSheet == null) {
            throw new ParseException("Nested import in an invalid context");
        }
        ArrayList<Node> c = new ArrayList<Node>(node.getChildren());
        for (Node n : c) {
            if (n instanceof ImportNode) {
                ImportNode importNode = (ImportNode) n;
                if (!importNode.isPureCssImport()) {
                    try {
                        StringBuilder filePathBuilder = new StringBuilder(
                                stylesheet.getFileName());
                        StringBuilder filePathBuilder2 = new StringBuilder(
                                stylesheet.getFileName());
                        filePathBuilder.append(File.separatorChar).append(
                                importNode.getUri());
                        if (!filePathBuilder.toString().endsWith(".scss")) {
                            filePathBuilder.append(".scss");
                        }

                        if (importNode.getUri().contains(String.valueOf(File.separatorChar))) {
                            filePathBuilder2.append(File.separatorChar);
                            int index = importNode.getUri().lastIndexOf(File.separatorChar);
                            filePathBuilder2.append(importNode.getUri().substring(0, index + 1));
                            filePathBuilder2.append(new String("_"));
                            filePathBuilder2.append(importNode.getUri().substring((index + 1), importNode.getUri().length()));
                        }
                        else {
                            filePathBuilder2.append(File.separatorChar);
                            filePathBuilder2.append(new String("_"));
                            filePathBuilder2.append(importNode.getUri());
                        }
                        if (!filePathBuilder2.toString().endsWith(".scss")) {
                            filePathBuilder2.append(".scss");
                        }

                        // set parent's charset to imported node.
                        ScssStylesheet imported = ScssStylesheet.get(
                                filePathBuilder.toString(), styleSheet.getCharset());
                        if (imported == null) {
                            imported = ScssStylesheet.get(
                                    filePathBuilder2.toString(), styleSheet.getCharset());
                        }
                        if (imported == null) {
                            imported = ScssStylesheet.get(importNode.getUri());
                        }
                        if (imported == null) {
                            ArrayList<String> importPaths = styleSheet.getImportPaths();
                            int i = 0;
                            while (i < importPaths.size() && (imported == null) ) {
                                String item = importPaths.get(i).concat(
                                        String.valueOf((File.separatorChar)).concat(
                                        importNode.getUri()));
                                String item2 = null;
                                if (importNode.getUri().contains(String.valueOf(File.separatorChar))) {
                                    StringBuilder filePath = new StringBuilder();
                                    filePath.append(importPaths.get(i));
                                    filePath.append(File.separatorChar);
                                    int index = importNode.getUri().lastIndexOf(File.separatorChar);
                                    filePath.append(importNode.getUri().substring(0, index + 1));
                                    filePath.append(new String("_"));
                                    filePath.append(importNode.getUri().substring((index + 1), importNode.getUri().length()));
                                    item2 = filePath.toString();
                                }
                                else {
                                    item2 = importPaths.get(i).concat(
                                            String.valueOf(File.separatorChar).concat(
                                            (new String("_")).concat(
                                            importNode.getUri())));
                                }

                                imported = ScssStylesheet.get(
                                        item, styleSheet.getCharset());
                                if (imported == null) {
                                    imported = ScssStylesheet.get(
                                            item.concat(".scss"), styleSheet.getCharset());
                                }
                                if (imported == null) {
                                    imported = ScssStylesheet.get(
                                            item2, styleSheet.getCharset());
                                }
                                if (imported == null) {
                                    imported = ScssStylesheet.get(
                                            item2.concat(".scss"), styleSheet.getCharset());
                                }
                                i++;
                            }
                        }
                        if (imported == null) {
                            throw new FileNotFoundException(importNode.getUri()
                                    + " (parent: "
                                    + ScssStylesheet.get().getFileName() + ")");
                        }

                        traverse(imported);

                        String prefix = getUrlPrefix(importNode.getUri());
                        if (prefix != null) {
                            updateUrlInImportedSheet(imported, prefix);
                        }

                        node.appendChildrenAfter(
                                new ArrayList<Node>(imported.getChildren()),
                                importNode);
                        node.removeChild(importNode);
                    } catch (CSSException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (styleSheet != node) {
                        throw new ParseException(
                                "CSS imports can only be used at the top level, not as nested imports. Within style rules, use SCSS imports.");
                    }
                }
            }
        }
    }

    private static String getUrlPrefix(String url) {
        if (url == null) {
            return null;
        }
        int pos = url.lastIndexOf('/');
        if (pos == -1) {
            return null;
        }
        return url.substring(0, pos + 1);
    }

    private static void updateUrlInImportedSheet(Node node, String prefix) {
        for (Node child : node.getChildren()) {
            if (child instanceof RuleNode) {
                LexicalUnit value = ((RuleNode) child).getValue();
                while (value != null) {
                    if (value.getLexicalUnitType() == LexicalUnit.SAC_URI) {
                        String path = value.getStringValue()
                                .replaceAll("^\"|\"$", "")
                                .replaceAll("^'|'$", "");
                        if (!path.startsWith("/") && !path.contains(":")) {
                            path = prefix + path;
                            path = StringUtil.cleanPath(path);
                            ((LexicalUnitImpl) value).setStringValue(path);
                        }
                    }
                    value = value.getNextLexicalUnit();
                }

            }
            updateUrlInImportedSheet(child, prefix);
        }
    }
}
