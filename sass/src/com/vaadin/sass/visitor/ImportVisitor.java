/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.sass.visitor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.LexicalUnit;

import com.vaadin.sass.ScssStylesheet;
import com.vaadin.sass.parser.LexicalUnitImpl;
import com.vaadin.sass.tree.ImportNode;
import com.vaadin.sass.tree.Node;
import com.vaadin.sass.tree.RuleNode;
import com.vaadin.sass.util.StringUtil;

public class ImportVisitor implements Visitor {

    @Override
    public void traverse(Node node) {
        for (Node child : new ArrayList<Node>(node.getChildren())) {
            if (child instanceof ImportNode) {
                ImportNode importNode = (ImportNode) child;
                if (!importNode.isPureCssImport()) {
                    StringBuilder filePathBuilder = new StringBuilder(
                            node.getFileName());
                    filePathBuilder.append(File.separatorChar).append(
                            importNode.getUri());
                    if (!filePathBuilder.toString().endsWith(".scss")) {
                        filePathBuilder.append(".scss");
                    }
                    try {
                        ScssStylesheet imported = ScssStylesheet.get(new File(
                                filePathBuilder.toString()));
                        traverse(imported);
                        String prefix = getUrlPrefix(importNode.getUri());
                        if (prefix != null) {
                            updateUrlInImportedSheet(imported, prefix);
                        }
                        Node pre = importNode;
                        for (Node importedChild : imported.getChildren()) {
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

    private String getUrlPrefix(String url) {
        if (url == null) {
            return null;
        }
        int pos = url.lastIndexOf('/');
        if (pos == -1) {
            return null;
        }
        return url.substring(0, pos + 1);
    }

    private void updateUrlInImportedSheet(Node node, String prefix) {
        for (Node child : node.getChildren()) {
            if (child instanceof RuleNode) {
                LexicalUnit value = ((RuleNode) child).getValue();
                while (value != null) {
                    if (value.getLexicalUnitType() == LexicalUnit.SAC_URI) {
                        String path = value.getStringValue();
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
