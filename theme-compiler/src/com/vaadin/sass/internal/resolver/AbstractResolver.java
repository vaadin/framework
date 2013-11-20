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

package com.vaadin.sass.internal.resolver;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.w3c.css.sac.InputSource;

import com.vaadin.sass.internal.ScssStylesheet;

/**
 * Base class for resolvers. Implements functionality for locating paths which
 * an import can be relative to and helpers for extracting path information from
 * the identifier.
 * 
 * @since 7.2
 * @author Vaadin Ltd
 */
public abstract class AbstractResolver implements ScssStylesheetResolver,
        Serializable {
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.sass.internal.resolver.ScssStylesheetResolver#resolve(java
     * .lang.String)
     */
    @Override
    public InputSource resolve(ScssStylesheet parentStylesheet,
            String identifier) {
        // Remove a possible ".scss" suffix
        identifier = identifier.replaceFirst(".scss$", "");

        List<String> potentialParentPaths = getPotentialParentPaths(
                parentStylesheet, identifier);

        // remove path from identifier as it has already been added to the
        // parent path
        if (identifier.contains("/")) {
            identifier = identifier.substring(identifier.lastIndexOf("/") + 1);
        }

        for (String path : potentialParentPaths) {
            InputSource source = normalizeAndResolve(path + "/" + identifier);

            if (source != null) {
                return source;
            }

            // Try to find partial import (_identifier.scss)
            source = normalizeAndResolve(path + "/_" + identifier);

            if (source != null) {
                return source;
            }

        }

        return normalizeAndResolve(identifier);
    }

    /**
     * Retrieves the parent paths which should be used while resolving relative
     * identifiers. By default uses the parent stylesheet location and a
     * possible absolute path in the identifier.
     * 
     * @param parentStylesheet
     *            The parent stylesheet or null if there is no parent
     * @param identifier
     *            The identifier to be resolved
     * @return a list of paths in which to look for the relative import
     */
    protected List<String> getPotentialParentPaths(
            ScssStylesheet parentStylesheet, String identifier) {
        List<String> potentialParents = new ArrayList<String>();
        if (parentStylesheet != null) {
            potentialParents.add(extractFullPath(
                    parentStylesheet.getDirectory(), identifier));
        }

        // Identifier can be a full path so extract the path part also as a
        // potential parent
        if (identifier.contains("/")) {
            potentialParents.add(extractFullPath("", identifier));
        }

        return potentialParents;

    }

    /**
     * Extracts the full path from the path combined with the identifier
     * 
     * @param path
     *            The base path
     * @param identifier
     *            The identifier which may contain a path part, separated by "/"
     *            from the real identifier
     * @return a normalized version of the path where identifier does not
     *         contain any directory information
     */
    protected String extractFullPath(String path, String identifier) {
        int lastSlashPosition = identifier.lastIndexOf("/");
        if (lastSlashPosition == -1) {
            return path;
        }
        String identifierPath = identifier.substring(0, lastSlashPosition);
        if ("".equals(path)) {
            return identifierPath;
        } else {
            return path + "/" + identifierPath;
        }
    }

    /**
     * Resolves the normalized version of the given identifier
     * 
     * @param identifier
     *            The identifier to resolve
     * @return An input source if the resolver found one or null otherwise
     */
    protected InputSource normalizeAndResolve(String identifier) {
        String normalized = normalize(identifier);
        return resolveNormalized(normalized);
    }

    /**
     * Resolves the identifier after it has been normalized using
     * {@link #normalize(String)}.
     * 
     * @param identifier
     *            The normalized identifier
     * @return an InputSource if the resolver found a source or null otherwise
     */
    protected abstract InputSource resolveNormalized(String identifier);

    /**
     * Normalizes "." and ".." from the path string where parent path segments
     * can be removed. Preserve leading "..". Also ensure / is used instead of \
     * in all places.
     * 
     * @param path
     *            A relative or absolute file path
     * @return The normalized path
     */
    protected String normalize(String path) {

        // Ensure only "/" is used, also in Windows
        path = path.replace(File.separatorChar, '/');

        // Split into segments
        String[] segments = path.split("/");
        Stack<String> result = new Stack<String>();

        // Replace '.' and '..' segments
        for (int i = 0; i < segments.length; i++) {
            if (segments[i].equals(".")) {
                // Segments marked '.' are ignored

            } else if (segments[i].equals("..") && !result.isEmpty()
                    && !result.lastElement().equals("..")) {
                // If segment is ".." then remove the previous iff the previous
                // element is not a ".." and the result stack is not empty
                result.pop();
            } else {
                // Other segments are just added to the stack
                result.push(segments[i]);
            }
        }

        // Reconstruct path
        StringBuilder pathBuilder = new StringBuilder();
        for (int i = 0; i < result.size(); i++) {
            if (i > 0) {
                pathBuilder.append("/");
            }
            pathBuilder.append(result.get(i));
        }
        return pathBuilder.toString();
    }

}
