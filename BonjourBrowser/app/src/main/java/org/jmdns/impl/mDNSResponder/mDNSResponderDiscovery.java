/*
 *  Copyright: 2007 Kiva Systems, Inc.
 *
 * Licensed under Apache License version 2.0
 *
 *  $Id: mDNSResponderDiscovery.java 16098 2008-01-04 00:15:52Z jpollak $
 */

package org.jmdns.impl.mDNSResponder;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jmdns.api.*;
import org.jmdns.utils.InetInfo;

import com.apple.dnssd.*;

public class mDNSResponderDiscovery implements IDiscoveryRegistry, RegisterListener {
    static public final Logger log = Logger.getLogger(mDNSResponderDiscovery.class.getCanonicalName());
    
    private LinkedList<MDNSResponderServiceInfo> services =
        new LinkedList<MDNSResponderServiceInfo>();
    
    String broadcastAddress;
    
    public mDNSResponderDiscovery() {
        super();
        
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                log.warning("Shutting down mDNSResponderDiscovery due to jvm stopping.");
                mDNSResponderDiscovery.this.shutdown();
            }
         });            
    }

    protected void shutdown() {
        for(MDNSResponderServiceInfo r : services) {
            unregisterService(r);
        }
    }
    
    public IServiceInfo registerService(String type, String name, String domain,
            int port, HashMap props) throws IOException {
        
        int interfaceIndex = DNSSD.ALL_INTERFACES; // register on all interfaces
        String hostname = InetInfo.getHostname();

        InetAddress localAddress = null;
        
        if (broadcastAddress != null) {
            log.info("Using provided address to find broadcast interface");
            try {
                localAddress = InetAddress.getByName(broadcastAddress);
            } catch (UnknownHostException e) {
                log.severe("Error getting local address with broadcase ip '" + broadcastAddress + ": " + e);
            }
        } 
        
        if (localAddress == null) {
            log.info("Unable to locate interface to broadcast on, broadcasting to all interfaces.");
        } else {
            hostname = localAddress.getHostName();
            
            NetworkInterface iface = NetworkInterface.getByInetAddress(localAddress);
            
            if (iface == null) {
                log.log(Level.WARNING, "No interface found which is bound to IP address " + localAddress + 
                        " (is the broadcastAddress set wrong?). Falling back to all interfaces.");
            } else {
                interfaceIndex = DNSSD.getIfIndexForName(iface.getName());
            }
        }

        TXTRecord txtRecord = new TXTRecord();
        
        for (Iterator i = props.entrySet().iterator(); i.hasNext();) {
            Map.Entry e = (Map.Entry) i.next();
            txtRecord.set((String) e.getKey(), (String) e.getValue());
        }
        
        MDNSResponderServiceInfo service = null;
        try {
            if (log.isLoggable(Level.FINE)) {
                String ifname = DNSSD.getNameForIfIndex(interfaceIndex);
                log.fine("Registering Name: " + name + " Type: " + type + " on interface " + interfaceIndex + " (" + ifname +" @ " + hostname + ")");
            }
            DNSSDRegistration registration = DNSSD.register(0, interfaceIndex, name, type, domain, null, port, txtRecord, this);
            service = new MDNSResponderServiceInfo(name, hostname, port, registration);
            log.fine("Registration of " + name + " complete");
        } catch (DNSSDException e) {
            log.severe("Error registering service " + name + ": " + e);
            return null;
        }
        
        if (service == null) {
            log.severe("Failure to register service " + name + ", null was returned.");
            return null;
        }
        
        services.add(service);
        return service;
    }
    
    public void unregisterService(IServiceInfo service) {
        if (service instanceof MDNSResponderServiceInfo) {
            log.info("Unregistering service " + service);
            try {
                DNSSDRegistration registration = ((MDNSResponderServiceInfo) service).getRegistration();
                registration.stop();
            }
            catch (Exception ex) {
                log.warning("unregistering " + service + ": " + ex); // log the message and continue.
            }

        }
        else {
            log.info("not unregister, unrecognized type of service " + service);
        }
    }    

    public void listenForService(String type, String domain, BaseDiscoveryListener listener) {
        try {
            DNSSD.browse(0, 0, type, domain, new InternalmDNSListener(listener));
        } catch (DNSSDException e) {
            log.severe("Unable to create listener for type " + type + ": " + e);
        }
        
    }

    public void init(String broadcastAddress) {
        this.broadcastAddress = broadcastAddress;
    }

    public void fini() {
        // nothing to do
        
    }

    public void serviceRegistered(DNSSDRegistration registration, int flags, 
            String serviceName, String regType, String domain) {
        // TODO Auto-generated method stub
        
    }

    public void operationFailed(DNSSDService service, int errorCode) {
        log.severe("Operation failed on service " + service + " with error code " + errorCode);
    }
    
    
    private static class InternalmDNSListener implements BrowseListener, ResolveListener {
        
        private BaseDiscoveryListener listener;
        
        public InternalmDNSListener(BaseDiscoveryListener listener) {
            super();
            this.listener = listener;
        }

        public void serviceFound(DNSSDService browser, int flags, int ifIndex, 
                String serviceName, String regType, String domain) {
            log.fine("Add: " + serviceName + " of type " + regType);
            // Automatically request the object be resolved
            try {
                DNSSD.resolve(0, ifIndex, serviceName, regType, domain, this);
            } catch (DNSSDException e) {
                log.severe("Unable to resolve service " + serviceName + " of type " + regType + ": " + e);
            }
            
            listener.serviceAdded(regType, serviceName);
            
        }

        public void serviceLost(DNSSDService browser, int flags, int ifIndex, 
                String serviceName, String regType, String domain) {
            
            log.warning("Removed: " + serviceName + " " + regType);
            
            listener.serviceRemoved(regType, serviceName);            
        }

        public void serviceResolved(DNSSDService browser, int flags, int ifIndex, 
                String serviceName, String hostName, int port, TXTRecord txtRecord) {
            
            int firstDot = serviceName.indexOf('.');
            if (firstDot > 0) {
                String trueServiceName = serviceName.substring(0, firstDot);
                log.fine("Shortening service name from " + serviceName + " to " + trueServiceName);
                serviceName = trueServiceName;
            }
            
            log.fine("Resolved " + serviceName + " at " + hostName + ":" + port);
            MDNSResponderServiceInfo info = new MDNSResponderServiceInfo(serviceName, hostName, port, txtRecord);
            listener.serviceResolved(info);
        }
        
        public void operationFailed(DNSSDService arg0, int arg1) {
            // TODO Auto-generated method stub
            
        }

    }    

}
