/*
 *  Copyright: 2007 Kiva Systems, Inc.
 *
 * Licensed under Apache License version 2.0
 *
 *  $Id: IDiscoveryRegistry.java 16097 2008-01-04 00:11:44Z jpollak $
 */
package org.jmdns.api;

import java.io.IOException;
import java.util.HashMap;

public interface IDiscoveryRegistry {
    
    /** Register a service of the type specified 
     * @param domain TODO*/
    IServiceInfo registerService(String type, String name, String domain, int port, HashMap props) throws IOException;
    
    /** Register a listener that is called whenever a the given type appears on the network 
     * @param domain TODO*/
    void listenForService(String type, String domain, BaseDiscoveryListener listener);

    /** Call to unregister the service (if you own it) */
    void unregisterService(IServiceInfo service);
    
    /** Initialize the registry */
    void init(String broadcastAddress) throws DiscoveryException;
    
    /** Uninitialized the registry, currently may not be called */
    void fini();
    
}
