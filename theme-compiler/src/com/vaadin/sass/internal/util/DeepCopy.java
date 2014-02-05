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

package com.vaadin.sass.internal.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility for making deep copies (vs. clone()'s shallow copies) of objects.
 * Objects are first serialized and then deserialized. Error checking is fairly
 * minimal in this implementation. If an object is encountered that cannot be
 * serialized (or that references an object that cannot be serialized) an error
 * is printed to the logger and null is returned. Depending on your specific
 * application, it might make more sense to have copy(...) re-throw the
 * exception.
 */
public class DeepCopy {

    /**
     * Returns a copy of the object, or null if the object cannot be serialized.
     */
    public static Object copy(Object orig) {

        Object obj = null;
        if (!(orig instanceof Clonable)) {
            try {
                // Write the object out to a byte array
                FastByteArrayOutputStream fbos = new FastByteArrayOutputStream();
                ObjectOutputStream out = new ObjectOutputStream(fbos);
                out.writeObject(orig);
                out.flush();
                out.close();

                // Retrieve an input stream from the byte array and read
                // a copy of the object back in.
                ObjectInputStream in = new ObjectInputStream(
                        fbos.getInputStream());
                obj = in.readObject();
                in.close();
            } catch (IOException e) {
                log(e);
            } catch (ClassNotFoundException cnfe) {
                log(cnfe);
            }
            return obj;
        } else {
            try {
                obj = ((Clonable) orig).clone();
            } catch (ClassCastException e2) {
                // Can't clone, return obj as null
            } catch (CloneNotSupportedException e2) {
                // Can't clone, return obj as null
            }
            return obj;
        }
    }

    public static <T> Collection<T> copy(Collection<T> objects) {
        List<T> copies = new LinkedList<T>();
        for (T object : objects) {
            copies.add((T) copy(object));
        }
        return copies;
    }

    private static void log(Throwable e) {
        Logger.getLogger(DeepCopy.class.getName()).log(Level.SEVERE, null, e);
    }
}