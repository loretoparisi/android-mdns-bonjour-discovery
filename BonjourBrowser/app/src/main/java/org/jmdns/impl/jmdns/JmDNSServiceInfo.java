/*
 *  Copyright: 2007 Kiva Systems, Inc.
 *
 * Licensed under Apache License version 2.0
 *
 *  $Id: JmDNSServiceInfo.java 16098 2008-01-04 00:15:52Z jpollak $
 */

package org.jmdns.impl.jmdns;

import javax.jmdns.ServiceInfo;

import org.jmdns.api.IServiceInfo;

public class JmDNSServiceInfo implements IServiceInfo {

    private ServiceInfo info;
    
    public JmDNSServiceInfo(ServiceInfo info) {
        this.info = info;
    }

    public String getName() {
        return info.getName();
    }

    public String getPropertyString(String name) {
        return info.getPropertyString(name);
    }

    public int getPort() {
        return info.getPort();
    }

    public String getHostAddress() {
        return info.getHostAddress();
    }

    public ServiceInfo getJmDNSServiceInfo() {
        return info;
    }

}
