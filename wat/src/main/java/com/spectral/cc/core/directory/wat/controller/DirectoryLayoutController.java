/**
 * Directory wat
 * Directories Layout Controller
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

package com.spectral.cc.core.directory.wat.controller;

import org.primefaces.component.layout.LayoutUnit;
import org.primefaces.event.ToggleEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * controller for menu layout toogling <br/>
 * this is a session managed bean
 */
public class DirectoryLayoutController {
    private static final Logger log = LoggerFactory.getLogger(DirectoryLayoutController.class);

    private boolean   collapsed = false;

    public boolean isCollapsed() {
        return collapsed;
    }

    public void setCollapsed(boolean collapsed) {
        this.collapsed = collapsed;
    }

    /**
     * listener onToogle
     *
     * @param event
     */
    public void onToggle(ToggleEvent event) {
        log.debug("ToogleEvent : {} {}", new Object[]{((LayoutUnit)event.getComponent()).getPosition() + " toggled", "Status:" + event.getVisibility().name()});
        if (this.collapsed)
            this.collapsed=false;
        else
            this.collapsed=true;
    }
}