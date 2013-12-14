/**
 * Directory Main
 * PersistenceConsumer
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

package com.spectral.cc.core.directory.main.runtime;

import com.spectral.cc.core.directory.main.model.technical.network.LanType;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TXPersistenceConsumer implements Runnable {

    private static final String DIRECTORY_TXPERSISTENCE_CONSUMER_SERVICE_NAME = "Directory TX Persistence Consumer";
    private static final Logger log = LoggerFactory.getLogger(TXPersistenceConsumer.class);

    private BundleContext  context       = null;
    private ServiceTracker utxSceTracker = null;

    private static EntityManagerFactory sharedEMF = null;
    private static EntityManager        sharedEM = null;
    private static UserTransaction      sharedUX = null;

    private static ArrayList<EntityManager>        emPool     = new ArrayList<>();
    private static HashMap<EntityManager, Boolean> lockEMPool = new HashMap<>();

    public TXPersistenceConsumer setContext(BundleContext context) {
        this.context = context;
        return this;
    }

    public synchronized static EntityManagerFactory getSharedEMF() {
        return sharedEMF;
    }

    public synchronized static EntityManager getSharedEM() {
        return sharedEM;
    }

    public synchronized static UserTransaction getSharedUX() {
        return sharedUX;
    }

    public synchronized static EntityManager getLockedEM() {
        for (EntityManager em : emPool) {
            if (!lockEMPool.get(em)) {
                lockEMPool.put(em, true);
                return em;
            }
        }
        EntityManager ret = sharedEMF.createEntityManager();
        emPool.add(ret);
        lockEMPool.put(ret, true);
        return ret;
    }

    public synchronized static void unlockEM(EntityManager em) {
        lockEMPool.put(em,false);
    }

    public static void close() {
        if (sharedEM !=null) sharedEM.close();
        if (sharedEMF !=null) sharedEMF.close();
    }

    @Override
    public void run() {
        log.debug("{} is getting user transaction service", new Object[]{DIRECTORY_TXPERSISTENCE_CONSUMER_SERVICE_NAME});
        utxSceTracker = new ServiceTracker(context, UserTransaction.class.getName(), null);
        if (utxSceTracker!=null) {
            utxSceTracker.open();
            try {
                sharedUX = (UserTransaction) utxSceTracker.waitForService(60000);
            } catch (InterruptedException e) {
                log.error(DIRECTORY_TXPERSISTENCE_CONSUMER_SERVICE_NAME + " failed to get UserTransaction service !!!");
            }
        } else {
            log.error(DIRECTORY_TXPERSISTENCE_CONSUMER_SERVICE_NAME + " failed to init UserTransaction ServiceTracker !!!");
        }

        log.debug("{} is getting persistence entity manager factory service", new Object[]{DIRECTORY_TXPERSISTENCE_CONSUMER_SERVICE_NAME});
        ServiceReference sref = context.getServiceReference(EntityManagerFactory.class.getName());
        while (sref==null)
            try {
                Thread.sleep(10);
                sref = context.getServiceReference(EntityManagerFactory.class.getName());
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        sharedEMF = (EntityManagerFactory)context.getService(sref);
        log.debug("{} is creating persistence entity manager", new Object[]{DIRECTORY_TXPERSISTENCE_CONSUMER_SERVICE_NAME});
        sharedEM = sharedEMF.createEntityManager();

        List<LanType> lanTypes = new ArrayList<LanType>();

        LanType wanT = new LanType().setNameR("WAN").setDescriptionR("WAN type Lan"); lanTypes.add(wanT);
        LanType manT = new LanType().setNameR("MAN").setDescriptionR("MAN type Lan"); lanTypes.add(manT);
        LanType lanT = new LanType().setNameR("LAN").setDescriptionR("LAN type Lan"); lanTypes.add(lanT);

        try {
            TXPersistenceConsumer.getSharedUX().begin();
            TXPersistenceConsumer.getSharedEM().joinTransaction();
            for (LanType ltype : lanTypes)
                TXPersistenceConsumer.getSharedEM().persist(ltype);
            TXPersistenceConsumer.getSharedUX().commit();
        } catch (Exception E) {
            log.error("Fail to init lantype db!");
        }

        /*
        List<Datacenter> datacenterList = new ArrayList<Datacenter>();

        datacenterList.add(new Datacenter().setNameR("Paris DC 1").setAddressR("address1").setTownR("Paris").setZipCodeR((long)75001).
                                                                                                                                             setCountryR("France").setDescriptionR("Paris DC1").setGpsLatitudeR(0).setGpsLongitudeR(0));
        datacenterList.add(new Datacenter().setNameR("Paris DC 2").setAddressR("address2").setTownR("Paris").setZipCodeR((long)75002).
                                                                                                                                             setCountryR("France").setDescriptionR("Paris DC2").setGpsLatitudeR(0).setGpsLongitudeR(0));
        datacenterList.add(new Datacenter().setNameR("Paris DC 3").setAddressR("address3").setTownR("Paris").setZipCodeR((long)75003).
                                                                                                                                             setCountryR("France").setDescriptionR("Paris DC3").setGpsLatitudeR(0).setGpsLongitudeR(0));
        datacenterList.add(new Datacenter().setNameR("Paris DC 4").setAddressR("address4").setTownR("Paris").setZipCodeR((long)75004).
                                                                                                                                             setCountryR("France").setDescriptionR("Paris DC4").setGpsLatitudeR(0).setGpsLongitudeR(0));
        datacenterList.add(new Datacenter().setNameR("Paris DC 5").setAddressR("address5").setTownR("Paris").setZipCodeR((long)75005).
                                                                                                                                             setCountryR("France").setDescriptionR("Paris DC5").setGpsLatitudeR(0).setGpsLongitudeR(0));
        datacenterList.add(new Datacenter().setNameR("Paris DC 6").setAddressR("address6").setTownR("Paris").setZipCodeR((long)75006).
                                                                                                                                             setCountryR("France").setDescriptionR("Paris DC6").setGpsLatitudeR(0).setGpsLongitudeR(0));
        datacenterList.add(new Datacenter().setNameR("Paris DC 7").setAddressR("address7").setTownR("Paris").setZipCodeR((long)75007).
                                                                                                                                             setCountryR("France").setDescriptionR("Paris DC7").setGpsLatitudeR(0).setGpsLongitudeR(0));
        datacenterList.add(new Datacenter().setNameR("Paris DC 8").setAddressR("address8").setTownR("Paris").setZipCodeR((long)75008).
                                                                                                                                             setCountryR("France").setDescriptionR("Paris DC8").setGpsLatitudeR(0).setGpsLongitudeR(0));
        datacenterList.add(new Datacenter().setNameR("Paris DC 9").setAddressR("address9").setTownR("Paris").setZipCodeR((long)75009).
                                                                                                                                             setCountryR("France").setDescriptionR("Paris DC9").setGpsLatitudeR(0).setGpsLongitudeR(0));

        datacenterList.add(new Datacenter().setNameR("New York DC 1").setAddressR("address1").setTownR("New York").setZipCodeR((long)875001).
                                                                                                                                                    setCountryR("USA").setDescriptionR("New York DC1").setGpsLatitudeR(0).setGpsLongitudeR(0));
        datacenterList.add(new Datacenter().setNameR("New York DC 2").setAddressR("address2").setTownR("New York").setZipCodeR((long)875002).
                                                                                                                                                    setCountryR("USA").setDescriptionR("New York DC2").setGpsLatitudeR(0).setGpsLongitudeR(0));
        datacenterList.add(new Datacenter().setNameR("New York DC 3").setAddressR("address3").setTownR("New York").setZipCodeR((long)875003).
                                                                                                                                                    setCountryR("USA").setDescriptionR("New York DC3").setGpsLatitudeR(0).setGpsLongitudeR(0));
        datacenterList.add(new Datacenter().setNameR("New York DC 4").setAddressR("address4").setTownR("New York").setZipCodeR((long)875004).
                                                                                                                                                    setCountryR("USA").setDescriptionR("New York DC4").setGpsLatitudeR(0).setGpsLongitudeR(0));
        datacenterList.add(new Datacenter().setNameR("New York DC 5").setAddressR("address5").setTownR("New York").setZipCodeR((long)875005).
                                                                                                                                                    setCountryR("USA").setDescriptionR("New York DC5").setGpsLatitudeR(0).setGpsLongitudeR(0));
        datacenterList.add(new Datacenter().setNameR("New York DC 6").setAddressR("address6").setTownR("New York").setZipCodeR((long)875006).
                                                                                                                                                    setCountryR("USA").setDescriptionR("New York DC6").setGpsLatitudeR(0).setGpsLongitudeR(0));
        datacenterList.add(new Datacenter().setNameR("New York DC 7").setAddressR("address7").setTownR("New York").setZipCodeR((long)875007).
                                                                                                                                                    setCountryR("USA").setDescriptionR("New York DC7").setGpsLatitudeR(0).setGpsLongitudeR(0));
        datacenterList.add(new Datacenter().setNameR("New York DC 8").setAddressR("address8").setTownR("New York").setZipCodeR((long)875008).
                                                                                                                                                    setCountryR("USA").setDescriptionR("New York DC8").setGpsLatitudeR(0).setGpsLongitudeR(0));
        datacenterList.add(new Datacenter().setNameR("New York DC 9").setAddressR("address9").setTownR("New York").setZipCodeR((long)875009).
                                                                                                                                                    setCountryR("USA").setDescriptionR("New York DC9").setGpsLatitudeR(0).setGpsLongitudeR(0));

        datacenterList.add(new Datacenter().setNameR("Tokyo DC 1").setAddressR("address1").setTownR("Tokyo").setZipCodeR((long)106781).
                                                                                                                                              setCountryR("Japan").setDescriptionR("Tokyo DC1").setGpsLatitudeR(0).setGpsLongitudeR(0));
        datacenterList.add(new Datacenter().setNameR("Tokyo DC 2").setAddressR("address2").setTownR("Tokyo").setZipCodeR((long)106782).
                                                                                                                                              setCountryR("Japan").setDescriptionR("Tokyo DC2").setGpsLatitudeR(0).setGpsLongitudeR(0));
        datacenterList.add(new Datacenter().setNameR("Tokyo DC 3").setAddressR("address3").setTownR("Tokyo").setZipCodeR((long)106783).
                                                                                                                                              setCountryR("Japan").setDescriptionR("Tokyo DC3").setGpsLatitudeR(0).setGpsLongitudeR(0));
        datacenterList.add(new Datacenter().setNameR("Tokyo DC 4").setAddressR("address4").setTownR("Tokyo").setZipCodeR((long)106784).
                                                                                                                                              setCountryR("Japan").setDescriptionR("Tokyo DC4").setGpsLatitudeR(0).setGpsLongitudeR(0));
        datacenterList.add(new Datacenter().setNameR("Tokyo DC 5").setAddressR("address5").setTownR("Tokyo").setZipCodeR((long)106785).
                                                                                                                                              setCountryR("Japan").setDescriptionR("Tokyo DC5").setGpsLatitudeR(0).setGpsLongitudeR(0));
        datacenterList.add(new Datacenter().setNameR("Tokyo DC 6").setAddressR("address6").setTownR("Tokyo").setZipCodeR((long)106786).
                                                                                                                                              setCountryR("Japan").setDescriptionR("Tokyo DC6").setGpsLatitudeR(0).setGpsLongitudeR(0));
        datacenterList.add(new Datacenter().setNameR("Tokyo DC 7").setAddressR("address7").setTownR("Tokyo").setZipCodeR((long)106787).
                                                                                                                                              setCountryR("Japan").setDescriptionR("Tokyo DC7").setGpsLatitudeR(0).setGpsLongitudeR(0));
        datacenterList.add(new Datacenter().setNameR("Tokyo DC 8").setAddressR("address8").setTownR("Tokyo").setZipCodeR((long)106788).
                                                                                                                                              setCountryR("Japan").setDescriptionR("Tokyo DC8").setGpsLatitudeR(0).setGpsLongitudeR(0));
        datacenterList.add(new Datacenter().setNameR("Tokyo DC 9").setAddressR("address9").setTownR("Tokyo").setZipCodeR((long)106789).
                                                                                                                                              setCountryR("Japan").setDescriptionR("Tokyo DC9").setGpsLatitudeR(0).setGpsLongitudeR(0));

        datacenterList.add(new Datacenter().setNameR("Hong Kong DC 1").setAddressR("address1").setTownR("Hong Kong").setZipCodeR((long)342591).
                                                                                                                                                      setCountryR("China").setDescriptionR("Hong Kong DC1").setGpsLatitudeR(0).setGpsLongitudeR(0));
        datacenterList.add(new Datacenter().setNameR("Hong Kong DC 2").setAddressR("address2").setTownR("Hong Kong").setZipCodeR((long)342592).
                                                                                                                                                      setCountryR("China").setDescriptionR("Hong Kong DC2").setGpsLatitudeR(0).setGpsLongitudeR(0));
        datacenterList.add(new Datacenter().setNameR("Hong Kong DC 3").setAddressR("address3").setTownR("Hong Kong").setZipCodeR((long)342593).
                                                                                                                                                      setCountryR("China").setDescriptionR("Hong Kong DC3").setGpsLatitudeR(0).setGpsLongitudeR(0));
        datacenterList.add(new Datacenter().setNameR("Hong Kong DC 4").setAddressR("address4").setTownR("Hong Kong").setZipCodeR((long)342594).
                                                                                                                                                      setCountryR("China").setDescriptionR("Hong Kong DC4").setGpsLatitudeR(0).setGpsLongitudeR(0));
        datacenterList.add(new Datacenter().setNameR("Hong Kong DC 5").setAddressR("address5").setTownR("Hong Kong").setZipCodeR((long)342595).
                                                                                                                                                      setCountryR("China").setDescriptionR("Hong Kong DC5").setGpsLatitudeR(0).setGpsLongitudeR(0));
        datacenterList.add(new Datacenter().setNameR("Hong Kong DC 6").setAddressR("address6").setTownR("Hong Kong").setZipCodeR((long)342596).
                                                                                                                                                      setCountryR("China").setDescriptionR("Hong Kong DC6").setGpsLatitudeR(0).setGpsLongitudeR(0));
        datacenterList.add(new Datacenter().setNameR("Hong Kong DC 7").setAddressR("address7").setTownR("Hong Kong").setZipCodeR((long)342597).
                                                                                                                                                      setCountryR("China").setDescriptionR("Hong Kong DC7").setGpsLatitudeR(0).setGpsLongitudeR(0));
        datacenterList.add(new Datacenter().setNameR("Hong Kong DC 8").setAddressR("address8").setTownR("Hong Kong").setZipCodeR((long)342598).
                                                                                                                                                      setCountryR("China").setDescriptionR("Hong Kong DC8").setGpsLatitudeR(0).setGpsLongitudeR(0));
        datacenterList.add(new Datacenter().setNameR("Hong Kong DC 9").setAddressR("address9").setTownR("Hong Kong").setZipCodeR((long)106789).
                                                                                                                                                      setCountryR("China").setDescriptionR("Hong Kong DC9").setGpsLatitudeR(0).setGpsLongitudeR(0));

        try {
            TXPersistenceConsumer.getSharedUX().begin();
            TXPersistenceConsumer.getSharedEM().joinTransaction();
            for (Datacenter dc : datacenterList)
                TXPersistenceConsumer.getSharedEM().persist(dc);
            TXPersistenceConsumer.getSharedEM().flush();
            TXPersistenceConsumer.getSharedUX().commit();
        } catch (Exception E) {
            log.error("Failed to init datacenter test db!");
        }

        List<MulticastArea> multicastAreaList = new ArrayList<MulticastArea>();
        MulticastArea ma1 = new MulticastArea().setNameR("Moulticast01").setDescriptionR("Lobe temporal droit"); multicastAreaList.add(ma1);
        MulticastArea ma2 = new MulticastArea().setNameR("Moulticast02").setDescriptionR("Lobe temporal gauche"); multicastAreaList.add(ma2);

        try {
            TXPersistenceConsumer.getSharedUX().begin();
            TXPersistenceConsumer.getSharedEM().joinTransaction();
            for (MulticastArea marea : multicastAreaList)
                TXPersistenceConsumer.getSharedEM().persist(marea);
            TXPersistenceConsumer.getSharedEM().flush();
            TXPersistenceConsumer.getSharedUX().commit();
        } catch (Exception E) {
            log.error("Fail to init multicast area test db!");
        }

        List<Lan>     lans     = new ArrayList<Lan>();

        lans.add(new Lan().setNameR("WAN01").setDescriptionR("A WAN Lan").setSubnetIPR("192.168.31.0").setSubnetMaskR("255.255.255.0").setTypeR(wanT));
        lans.add(new Lan().setNameR("WAN02").setDescriptionR("A WAN Lan").setSubnetIPR("192.168.32.0").setSubnetMaskR("255.255.255.0").setTypeR(wanT));
        lans.add(new Lan().setNameR("WAN03").setDescriptionR("A WAN Lan").setSubnetIPR("192.168.33.0").setSubnetMaskR("255.255.255.0").setTypeR(wanT));
        lans.add(new Lan().setNameR("WAN04").setDescriptionR("A WAN Lan").setSubnetIPR("192.168.34.0").setSubnetMaskR("255.255.255.0").setTypeR(wanT));
        lans.add(new Lan().setNameR("WAN05").setDescriptionR("A WAN Lan").setSubnetIPR("192.168.35.0").setSubnetMaskR("255.255.255.0").setTypeR(wanT));
        lans.add(new Lan().setNameR("WAN06").setDescriptionR("A WAN Lan").setSubnetIPR("192.168.36.0").setSubnetMaskR("255.255.255.0").setTypeR(wanT));
        lans.add(new Lan().setNameR("WAN07").setDescriptionR("A WAN Lan").setSubnetIPR("192.168.37.0").setSubnetMaskR("255.255.255.0").setTypeR(wanT));
        lans.add(new Lan().setNameR("WAN08").setDescriptionR("A WAN Lan").setSubnetIPR("192.168.38.0").setSubnetMaskR("255.255.255.0").setTypeR(wanT));
        lans.add(new Lan().setNameR("WAN09").setDescriptionR("A WAN Lan").setSubnetIPR("192.168.39.0").setSubnetMaskR("255.255.255.0").setTypeR(wanT));
        lans.add(new Lan().setNameR("WAN10").setDescriptionR("A WAN Lan").setSubnetIPR("192.168.40.0").setSubnetMaskR("255.255.255.0").setTypeR(wanT));

        lans.add(new Lan().setNameR("MAN01").setDescriptionR("A MAN Lan").setSubnetIPR("192.168.41.0").setSubnetMaskR("255.255.255.0").setTypeR(manT));
        lans.add(new Lan().setNameR("MAN02").setDescriptionR("A MAN Lan").setSubnetIPR("192.168.42.0").setSubnetMaskR("255.255.255.0").setTypeR(manT));
        lans.add(new Lan().setNameR("MAN03").setDescriptionR("A MAN Lan").setSubnetIPR("192.168.43.0").setSubnetMaskR("255.255.255.0").setTypeR(manT));
        lans.add(new Lan().setNameR("MAN04").setDescriptionR("A MAN Lan").setSubnetIPR("192.168.44.0").setSubnetMaskR("255.255.255.0").setTypeR(manT));
        lans.add(new Lan().setNameR("MAN05").setDescriptionR("A MAN Lan").setSubnetIPR("192.168.45.0").setSubnetMaskR("255.255.255.0").setTypeR(manT));
        lans.add(new Lan().setNameR("MAN06").setDescriptionR("A MAN Lan").setSubnetIPR("192.168.46.0").setSubnetMaskR("255.255.255.0").setTypeR(manT));
        lans.add(new Lan().setNameR("MAN07").setDescriptionR("A MAN Lan").setSubnetIPR("192.168.47.0").setSubnetMaskR("255.255.255.0").setTypeR(manT));
        lans.add(new Lan().setNameR("MAN08").setDescriptionR("A MAN Lan").setSubnetIPR("192.168.48.0").setSubnetMaskR("255.255.255.0").setTypeR(manT));
        lans.add(new Lan().setNameR("MAN09").setDescriptionR("A MAN Lan").setSubnetIPR("192.168.49.0").setSubnetMaskR("255.255.255.0").setTypeR(manT));
        lans.add(new Lan().setNameR("MAN10").setDescriptionR("A MAN Lan").setSubnetIPR("192.168.50.0").setSubnetMaskR("255.255.255.0").setTypeR(manT));

        lans.add(new Lan().setNameR("LAN01").setDescriptionR("A LAN Lan").setSubnetIPR("192.168.41.0").setSubnetMaskR("255.255.255.0").setTypeR(lanT).setMareaR(ma1));
        lans.add(new Lan().setNameR("LAN02").setDescriptionR("A LAN Lan").setSubnetIPR("192.168.42.0").setSubnetMaskR("255.255.255.0").setTypeR(lanT).setMareaR(ma1));
        lans.add(new Lan().setNameR("LAN03").setDescriptionR("A LAN Lan").setSubnetIPR("192.168.43.0").setSubnetMaskR("255.255.255.0").setTypeR(lanT).setMareaR(ma1));
        lans.add(new Lan().setNameR("LAN04").setDescriptionR("A LAN Lan").setSubnetIPR("192.168.44.0").setSubnetMaskR("255.255.255.0").setTypeR(lanT).setMareaR(ma1));
        lans.add(new Lan().setNameR("LAN05").setDescriptionR("A LAN Lan").setSubnetIPR("192.168.45.0").setSubnetMaskR("255.255.255.0").setTypeR(lanT).setMareaR(ma1));
        lans.add(new Lan().setNameR("LAN06").setDescriptionR("A LAN Lan").setSubnetIPR("192.168.46.0").setSubnetMaskR("255.255.255.0").setTypeR(lanT).setMareaR(ma2));
        lans.add(new Lan().setNameR("LAN07").setDescriptionR("A LAN Lan").setSubnetIPR("192.168.47.0").setSubnetMaskR("255.255.255.0").setTypeR(lanT).setMareaR(ma2));
        lans.add(new Lan().setNameR("LAN08").setDescriptionR("A LAN Lan").setSubnetIPR("192.168.48.0").setSubnetMaskR("255.255.255.0").setTypeR(lanT).setMareaR(ma2));
        lans.add(new Lan().setNameR("LAN09").setDescriptionR("A LAN Lan").setSubnetIPR("192.168.49.0").setSubnetMaskR("255.255.255.0").setTypeR(lanT).setMareaR(ma2));
        lans.add(new Lan().setNameR("LAN10").setDescriptionR("A LAN Lan").setSubnetIPR("192.168.50.0").setSubnetMaskR("255.255.255.0").setTypeR(lanT).setMareaR(ma2));

        try {
            TXPersistenceConsumer.getSharedUX().begin();
            TXPersistenceConsumer.getSharedEM().joinTransaction();
            for (LanType ltype : lanTypes)
                TXPersistenceConsumer.getSharedEM().persist(ltype);
            TXPersistenceConsumer.getSharedEM().flush();
            TXPersistenceConsumer.getSharedUX().commit();

            TXPersistenceConsumer.getSharedUX().begin();
            TXPersistenceConsumer.getSharedEM().joinTransaction();
            for (Lan lan : lans) {
                TXPersistenceConsumer.getSharedEM().persist(lan);
                if (lan.getType()!=null) {
                    lan.getType().getLans().add(lan);
                    if (lan.getType().getId()==null)
                        TXPersistenceConsumer.getSharedEM().persist(lan.getType());
                    else
                        TXPersistenceConsumer.getSharedEM().merge(lan.getType());
                }
                if (lan.getMarea()!=null) {
                    lan.getMarea().getLans().add(lan);
                    if (lan.getType().getId()==null)
                        TXPersistenceConsumer.getSharedEM().persist(lan.getMarea());
                    else
                        TXPersistenceConsumer.getSharedEM().merge(lan.getMarea());
                }
            }
            TXPersistenceConsumer.getSharedEM().flush();
            TXPersistenceConsumer.getSharedUX().commit();

        } catch (Exception E) {
            log.error("Fail to init lan test db!");
        } */
    }
}