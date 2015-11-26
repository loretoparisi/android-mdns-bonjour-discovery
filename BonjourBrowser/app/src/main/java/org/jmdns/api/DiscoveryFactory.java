/*
 *  Copyright: 2007 Kiva Systems, Inc.
 *
 * Licensed under Apache License version 2.0
 *
 *  $Id: DiscoveryFactory.java 16098 2008-01-04 00:15:52Z jpollak $
 */
package org.jmdns.api;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.jmdns.impl.jmdns.JmDNSDiscovery;
import org.jmdns.impl.mDNSResponder.mDNSResponderDiscovery;

public class DiscoveryFactory {
    static final Logger log = Logger.getLogger(DiscoveryFactory.class.getCanonicalName());

    public static final String DISCOVERY_REGISTRY_JMDNS = "jmdns";
    public static final String DISCOVERY_REGISTRY_MDNSRESPONDER = "mDNSResponder";
    public static final String DISCOVERY_REGISTRY_DEFAULT = DISCOVERY_REGISTRY_JMDNS;
    public static final String BROADCAST_ALL_ADDRESSES = null;
    
    protected static IDiscoveryRegistry discoveryRegistry = null;

    /**
     * 
     * @return true if the discovery registry sub-system already was initialized.
     */
    public static boolean isInitialized() {
        return discoveryRegistry != null;
    }
    
    /**
     * 
     * @param registryType - Required. The registry type to use.
     * @param broadcastAddress - Optional. The IP address you would like to 
     *                           mDNS service to bind on, or null for all interfaces. 
     * @throws DiscoveryException 
     * 
     * @return True if the sub-system was initialized, false if it already was initialized.
     */
    public static boolean initRegistry(String registryType, String broadcastAddress) throws DiscoveryException {

        if (discoveryRegistry == null) {
            
            log.info("Creating and initializing discovery registry " + registryType);
            
            if (registryType.equals(DISCOVERY_REGISTRY_JMDNS)) {
                discoveryRegistry = new JmDNSDiscovery();
            } else if (registryType.equals(DISCOVERY_REGISTRY_MDNSRESPONDER)) {
                discoveryRegistry = new mDNSResponderDiscovery();
            } else {
                throw new DiscoveryException("Unrecognized discovery registry type " + registryType);
            }
            
            discoveryRegistry.init(broadcastAddress);
            
            // add a shutdown hook, so when we catch a CTRL-C or whatever, we stop
            // the discoveryRegistry.
            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    log.info("Discovery Registry Shutting down");
                    discoveryRegistry.fini();
                }
             });
        } else {
            log.log(Level.WARNING, "discoveryRegistry system was already initialized");
            return false;
        }
        
        return true;
    }
    
    public static boolean initRegistry(String registryType) throws DiscoveryException {
        return initRegistry(registryType, null);
    }
    
    public static boolean initRegistry() throws DiscoveryException {
        return initRegistry(DISCOVERY_REGISTRY_DEFAULT);
    }
    
    /**
     * Non-initialization code should call this. IE, code that expects the discovery registry 
     * to already be set up.
     * 
     * @return
     */
    public static IDiscoveryRegistry getRegistry() {
        return discoveryRegistry;
    }

    // This object should not be constructed.
    private DiscoveryFactory() {
        super();
    }

}