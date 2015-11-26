/*
 *  Copyright: 2007 Kiva Systems, Inc.
 *
 * Licensed under Apache License version 2.0
 *
 *  $Id: DiscoveryException.java 16097 2008-01-04 00:11:44Z jpollak $
 */

package org.jmdns.api;

public class DiscoveryException extends Exception {
    public DiscoveryException() {
        super();
    }

    public DiscoveryException(String message) {
        super(message);
    }

    public DiscoveryException(String message, Throwable cause) {
        super(message,cause);
    }

    public DiscoveryException(Throwable cause) {
        super(cause);
    }
}
