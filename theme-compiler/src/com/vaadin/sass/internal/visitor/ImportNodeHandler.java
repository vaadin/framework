/*
 * Copyright 2011 Vaadin Ltd.
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
import com.vaadin.sass.internal.tree.ImportNode;
import com.vaadin.sass.internal.tree.Node;
import com.vaadin.sass.internal.tree.RuleNode;
import com.vaadin.sass.internal.util.StringUtil;

public class ImportNodeHandler {

    public static void traverse(ScssStylesheet node) {
        ArrayList<Node> c = new ArrayList<Node>(node.getChildren());
        for (Node n : c) {
            if (n instanceof ImportNode) {
                ImportNode importNode = (ImportNode) n;
                if (!importNode.isPureCssImport()) {
                    try {
                        StringBuilder filePathBuilder = new StringBuilder(
                                node.getFileName());
                        filePathBuilder.append(File.separatorChar).append(
                                importNode.getUri());
                        if (!filePathBuilder.toString().endsWith(".scss")) {
                            filePathBuilder.append(".scss");
                        }

                        ScssStylesheet imported = ScssStylesheet
                                .get(filePathBuilder.toString());
                        if (imported == null) {
                            imported = ScssStylesheet.get(importNode.getUri());
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

                        Node pre = importNode;
                        for (Node importedChild : new ArrayList<Node>(
                                imported.getChildren())) {
                            node.appendChild(importedChild, pre);
                            pre = importedChild;
                        }
                        node.removeChild(importNode);
                    } catch (CSSException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
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
