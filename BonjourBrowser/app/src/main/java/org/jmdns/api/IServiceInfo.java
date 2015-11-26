/*
 *  Copyright: 2007 Kiva Systems, Inc.
 *
 * Licensed under Apache License version 2.0
 *
 *  $Id: IServiceInfo.java 16097 2008-01-04 00:11:44Z jpollak $
 */
package org.jmdns.api;

public interface IServiceInfo {

    /** Returns the name of the advertised service */  
    String getName();

    /** Returns the host IP of the advertised service */
    String getHostAddress();

    /** Returns the host port of the advertised service */
    int getPort();

    /** Returns the named property from the TXT record of the advertised service */
    String getPropertyString(String string);
    
}
