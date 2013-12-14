/**
 * Directory Main bundle
 * Directories Environment Create controller
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

package com.spectral.cc.core.directory.main.controller.organisational.environment;

import com.spectral.cc.core.directory.main.model.organisational.Environment;
import com.spectral.cc.core.directory.main.runtime.TXPersistenceConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.transaction.*;
import java.io.Serializable;

@ManagedBean
@RequestScoped
public class EnvironmentNewController implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(EnvironmentNewController.class);

    private String name;
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void save() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        log.debug("Save new Environment {} !", new Object[]{name});
        Environment environment = new Environment();
        environment.setName(name);
        environment.setDescription(description);
        try {
            TXPersistenceConsumer.getSharedUX().begin();
            TXPersistenceConsumer.getSharedEM().joinTransaction();
            TXPersistenceConsumer.getSharedEM().persist(environment);
            TXPersistenceConsumer.getSharedUX().commit();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                       "Environment created successfully !",
                                                       "Environment name : " + environment.getName());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                       "Throwable raised while creating environment " + environment.getName() + " !",
                                                       "Throwable message : " + t.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);

            FacesMessage msg2;
            int txStatus = TXPersistenceConsumer.getSharedUX().getStatus();
            switch(txStatus) {
                case Status.STATUS_NO_TRANSACTION:
                    msg2 = new FacesMessage(FacesMessage.SEVERITY_WARN,
                                                   "Operation canceled !",
                                                   "Operation : environment " + environment.getName() + " creation.");
                    break;
                case Status.STATUS_MARKED_ROLLBACK:
                    try {
                        log.debug("Rollback operation !");
                        TXPersistenceConsumer.getSharedUX().rollback();
                        msg2 = new FacesMessage(FacesMessage.SEVERITY_WARN,
                                                       "Operation rollbacked !",
                                                       "Operation : environment " + environment.getName() + " creation.");
                        FacesContext.getCurrentInstance().addMessage(null, msg2);
                    } catch (SystemException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        msg2 = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                       "Error while rollbacking operation !",
                                                       "Operation : environment " + environment.getName() + " creation.");
                        FacesContext.getCurrentInstance().addMessage(null, msg2);
                    }
                    break;
                default:
                    msg2 = new FacesMessage(FacesMessage.SEVERITY_WARN,
                                                   "Operation canceled ! ("+txStatus+")",
                                                   "Operation : environment " + environment.getName() + " creation.");
                    break;
            }
            FacesContext.getCurrentInstance().addMessage(null, msg2);
        }
    }
}