/*
 *  Copyright: 2007 Kiva Systems, Inc.
 *
 * Licensed under Apache License version 2.0
 *
 *  $Id: MDNSResponderServiceInfo.java 16098 2008-01-04 00:15:52Z jpollak $
 */


package org.jmdns.impl.mDNSResponder;

import org.jmdns.api.IServiceInfo;

import com.apple.dnssd.DNSSDRegistration;
import com.apple.dnssd.TXTRecord;

public class MDNSResponderServiceInfo implements IServiceInfo {
    
    String serviceName;
    String hostName;
    int port;
    TXTRecord txtRecord;

    DNSSDRegistration registration;
    
    public MDNSResponderServiceInfo(String serviceName, String hostName, int port, TXTRecord txtRecord) {
        super();
        
        this.serviceName = serviceName;
        this.hostName = hostName;
        this.port = port;
        this.txtRecord = txtRecord;
    }
    
    public MDNSResponderServiceInfo(String serviceName, String hostName, int port, DNSSDRegistration registration) {
        super();
        
        this.registration = registration;
        this.serviceName = serviceName;
        this.hostName = hostName;
        this.port = port;
        
    }
    
    public String getName() {
        return serviceName;
    }

    public String getHostAddress() {
        return hostName;
    }

    public int getPort() {
        return port;
    }

    public String getPropertyString(String string) {
        if (txtRecord != null) {
            return txtRecord.getValueAsString(string);
        } else
            return null;
    }

    public DNSSDRegistration getRegistration() {
        if (registration != null) {
            return registration;
        } else
            return null;        
    }

}
