/*
 *  Copyright: 2007 Kiva Systems, Inc.
 *
 * Licensed under Apache License version 2.0
 *
 *  $Id: BaseDiscoveryListener.java 16097 2008-01-04 00:11:44Z jpollak $
 */
package org.jmdns.api;

import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Logger;

public class BaseDiscoveryListener {
    private static final Logger log = Logger.getLogger(BaseDiscoveryListener.class.getCanonicalName());

    private Object lock = new Object();
    
    private String serviceType;
    private String subType;
    private String domain;
       
    private HashMap<String, IServiceInfo> services =
        new HashMap<String, IServiceInfo>();

    public BaseDiscoveryListener(String serviceType, String subType) {
        this(serviceType, subType, null);      
    }
    
    public BaseDiscoveryListener(String serviceType, String subType, String domain) {
        this.serviceType = serviceType;
        this.subType = subType;
        this.domain = domain;
        
        IDiscoveryRegistry registry = DiscoveryFactory.getRegistry();
        registry.listenForService(serviceType, domain, this);        
    }
    
    public void serviceAdded(String type, String name) {
        log.info("Added:" + name + " of type: " + type);
    }
    
    public void serviceRemoved(String type, String name) {
        log.info("Removing:" + name + " of type: " + type);
        services.remove(name);
    }
    
    public void serviceResolved(IServiceInfo info) {
        String name = info.getName();
        log.info("Resolved:" + name + " at: " + info.getHostAddress());
        
        if (info.getPropertyString("type").equals(subType)) {
            synchronized (lock) {
                services.put(name, info);
                lock.notify();
            }
        } else {
            log.info(info.getPropertyString("type") + " does not match " + subType);
        }
    }
    
    /**
     * Wait for the listener to discover a beacon
     * 
     * @param millisecTimeout - the maximum time to wait in milliseconds. If value is zero, there is no timeout.
     * @throws InterruptedException
     * @return True if a service was found.
     */
    public boolean waitForFirstService(long millisecTimeout) throws InterruptedException {
        synchronized (lock) {
            if (services.size() == 0) {
                // if we already have a service, we don't have to wait
                return waitForNextService(millisecTimeout);
            }
            return true;
        }
    }

    /**
     * 
     * @param millisecTimeout
     * @return True if a service was found
     * @throws InterruptedException
     */
    public boolean waitForNextService(long millisecTimeout) throws InterruptedException {
        synchronized (lock) {
            int size = services.size();
            
            log.info("Waiting for a service of type " + serviceType + ", subType: " + subType +  " in domain " + domain);
            
            if (millisecTimeout != 0) {
                lock.wait(millisecTimeout);
            } else {
                lock.wait();
            }
            
            if (services.size() > size) {
                log.info("Found a service...");
                return true;
            } else {
                return false;
            }
            
        }
    }
    
    
    /** Returns the first service discovered, or null if none have been found */
    public final IServiceInfo getFirstService() {
        if (services.size() > 0) { 
            // return one service - not sure if it is the first one
            return (IServiceInfo) services.values().iterator().next();
        } else {
            return null;
        }
    }
    
    /** Return the service identified by the name */
    public final IServiceInfo getService(String name) {
        return (IServiceInfo) services.get(name);
    }
    
    /** Returns all services discovered so far */
    public final Collection<IServiceInfo> getServices() {
        return services.values();
    }
    
}
