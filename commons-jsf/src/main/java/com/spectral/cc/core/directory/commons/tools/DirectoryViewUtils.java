/**
 * Directory Commons JSF bundle
 * Directories View Utils
 * Copyright (C) 2013 Mathilde Ffrench
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.spectral.cc.core.directory.commons.tools;

import javax.persistence.Id;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Utilities for working with Java Server Faces views.
 */
public final class DirectoryViewUtils {

    public static <T> List<T> asList(Collection<T> collection){
        if (collection == null) {
            return null;
        }
        return new ArrayList<T>(collection);
    }

    public static String display(Object object) {

        if (object == null) {
            return null;
        }

        try {
            // Invoke toString if declared in the class. If not found, the NoSuchMethodException is caught and handled
            object.getClass().getDeclaredMethod("toString");
            return object.toString();
        } catch (NoSuchMethodException noMethodEx) {
            try {
                for (Field field : object.getClass().getDeclaredFields()) {
                    // Find the primary key field and display it
                    if (field.getAnnotation(Id.class) != null) {
                        // Find a matching getter and invoke it to display the key
                        for (Method method : object.getClass().getDeclaredMethods()) {
                            if (method.equals(new PropertyDescriptor(field.getName(), object.getClass()).getReadMethod())) {
                                return method.invoke(object).toString();
                            }
                        }
                    }
                }
                for (Method method : object.getClass().getDeclaredMethods()) {
                    // Find the primary key as a property instead of a field, and display it
                    if (method.getAnnotation(Id.class) != null) {
                        return method.invoke(object).toString();
                    }
                }
            }
            catch (Exception ex) {
                // Unlikely, but abort and stop view generation if any exception is thrown
                throw new RuntimeException(ex);
            }
        }

        return null;
    }

    private DirectoryViewUtils() {
        // Can never be called
    }
}
