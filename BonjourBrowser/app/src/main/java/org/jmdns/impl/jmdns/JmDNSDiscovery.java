/*
 *  Copyright: 2007 Kiva Systems, Inc.
 *
 * Licensed under Apache License version 2.0
 *
 *  $Id: JmDNSDiscovery.java 16098 2008-01-04 00:15:52Z jpollak $
 */

package org.jmdns.impl.jmdns;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.logging.Logger;

import javax.jmdns.*;

import org.jmdns.api.*;


public class JmDNSDiscovery implements IDiscoveryRegistry {
    private static final Logger log = Logger.getLogger(JmDNSDiscovery.class.getCanonicalName());

    private static JmDNS jmdns = null;
    
    private List<InternalJmDNSListener> serviceListeners = new LinkedList<InternalJmDNSListener>();

    public JmDNSDiscovery() {
    }

    public void init(String broadcastAddress) throws DiscoveryException {
        InetAddress addr = null;

        if (broadcastAddress != null) {
            try {
                addr = InetAddress.getByName(broadcastAddress);
            } catch (UnknownHostException e) {
                log.warning("Unknown host, trying to continue: " + e);
            }
        }

        if (jmdns == null) {
            try {
                if (addr != null) {
                    log.info("Starting jmdns on " + addr);
                    jmdns = new JmDNS(addr);
                } else {
                    jmdns = new JmDNS();
                }

                log.info("JmDNS is bound on " + jmdns.getInterface());
                
            } catch (SocketException e) {
                throw new DiscoveryException("socket error instantiating jmdns", e);
            } catch (IOException e) {
                throw new DiscoveryException("io error instantiating jmdns", e);
            }
        }
    }

    public void fini() {
        /*
        for (InternalJmDNSListener listener : serviceListeners) {
            log.debug("Removing listener " + listener);
            jmdns.removeServiceListener(listener.type, listener);
            log.debug("Removed");
        }
        
        log.debug("closing JmDNS");
        jmdns.close();
        log.debug("JmDNS shut down");
        */
    }

    public IServiceInfo registerService(String type, String name, String domain, int port, HashMap props) throws IOException {

        if (domain == null) {
            domain = "local";
        }
        
        // JmDNS API requires the fully qualified
        String fqType = type + "." + domain + ".";
        jmdns.registerServiceType(fqType);

        // XXX - the HashTable is being created from a type-unspecified
        // HashMap.  Suppressing the unchecked call to HashTable for now.
        @SuppressWarnings("unchecked")
        ServiceInfo info = new ServiceInfo(fqType, name, port, 0, 0, new Hashtable(props));

        log.info("Registering service: " + info.getName() + " of type " + info.getType());
        jmdns.registerService(info);

        return new JmDNSServiceInfo(info);
    }

    public void unregisterService(IServiceInfo service) {
        if (service instanceof JmDNSServiceInfo) {
            ServiceInfo info = ((JmDNSServiceInfo) service).getJmDNSServiceInfo();
            jmdns.unregisterService(info);
        }

    }

    public void listenForService(String type, String domain, BaseDiscoveryListener listener) {

        if (domain == null) {
            domain = "local";
        }
        
        log.info("Listening for service of type " + type + " on domain " + domain);
        
        // JmDNS API requires the fully qualified
        String fqType = type + "." + domain + ".";
        jmdns.registerServiceType(fqType);

        InternalJmDNSListener internalListener = new InternalJmDNSListener(fqType, listener);
        serviceListeners.add(internalListener);
        jmdns.addServiceListener(fqType, internalListener);

        ServiceInfo[] list = jmdns.list(fqType);

        log.fine("Telling new listener about " + list.length + " known services of type " + fqType);
        for(int i = 0; i < list.length; i++) {
            listener.serviceResolved(new JmDNSServiceInfo(list[i]));
        }
    }
    
    private static class InternalJmDNSListener implements ServiceListener {

        String type;
        private BaseDiscoveryListener listener;

        public InternalJmDNSListener(String type, BaseDiscoveryListener listener) {
            super();
            this.type = type;
            this.listener = listener;
        }

        public void serviceAdded(ServiceEvent event) {
            // leave this at debug, BaseDiscoveryListener logs at info
            log.fine("Add: " + event.getName() + " of type " + event.getType());
            // Automatically request the object be resolved
            jmdns.requestServiceInfo(event.getType(), event.getName());
            listener.serviceAdded(event.getType(), event.getName());
        }

        public void serviceRemoved(ServiceEvent event) {
            // leave this at debug, BaseDiscoveryListener logs at info
            log.fine("Removed: " + event.getName() + " " + event.getType());
            listener.serviceRemoved(event.getType(), event.getName());
        }

        public void serviceResolved(ServiceEvent event) {
            // leave this at debug, BaseDiscoveryListener logs at info
            log.fine("Resolved: " + event.getName() + " " + event.getType());
            JmDNSServiceInfo info = new JmDNSServiceInfo(event.getInfo());
            listener.serviceResolved(info);
        }
    }

}
