/**
 * Directory wat
 * Companies REST endpoint
 * Copyright (C) 2015 Echinnopsii
 * Author : Sagar Ghuge
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
package net.echinopsii.ariane.community.core.directory.wat.rest;


public class CommonRestResponse {
    Object deserialiedObject;
    String errorMessage;

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Object getDeserialiedObject() {
        return deserialiedObject;
    }

    public void setDeserialiedObject(Object deserialiedObject) {
        this.deserialiedObject = deserialiedObject;
    }
}
