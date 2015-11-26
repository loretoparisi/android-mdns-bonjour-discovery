package org.jmdns.utils;

import java.io.*;
import java.net.*;
import java.util.logging.Logger;


public class InetInfo {
    private static Logger logger = Logger.getLogger(InetInfo.class.getCanonicalName());

    static String hostname = null;
    static String domainname = null;
    
    public static String getHostname() throws IOException {
        
        String osName = System.getProperty("os.name");
        
        if (hostname == null) {
            if (osName.contains("Linux") || osName.contains("Windows")) {
                final BufferedReader[] outputStreams = new BufferedReader[2];
                SystemTools.execCommandAndWait("hostname", "hostname", null, null, outputStreams);
                hostname = outputStreams[0].readLine();
            } else if (osName.contains("Mac")) {
                final BufferedReader[] outputStreams = new BufferedReader[2];
                SystemTools.execCommandAndWait("hostname", "hostname -s", null, null, outputStreams);
                hostname = outputStreams[0].readLine();
            } else {
                //log.warn("Unsupported operating system - giving our best effort!");
                InetAddress localhost = InetAddress.getLocalHost();
                hostname = localhost.getHostName();
            }
        }
        
        return hostname;
    }
    
    public static String getDomainname() throws IOException {
        String osName = System.getProperty("os.name");
        
        if (domainname == null) {
            final BufferedReader[] outputStreams = new BufferedReader[2];
            
            if (osName.contains("Linux") || osName.contains("Mac")) {
                SystemTools.execCommandAndWait("domainname", "hostname -d", null, null, outputStreams);
                domainname = outputStreams[0].readLine();
            } else if (osName.contains("Windows")) {
                Process proc = SystemTools.execCommand("ipconfig", null, null);
                
                if (outputStreams != null) {
                    if (outputStreams.length == 2) {
                        // stdout - why is stdout obtained by getInputStream()?? 
                        outputStreams[0] = new BufferedReader(new InputStreamReader(proc.getInputStream()));
                        // stderr
                        outputStreams[1] = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
                    } else {
                        logger.warning("execCommandAndWait: cannot capture output streams, unexpected array length " + outputStreams.length);
                    }
                } else {
                    logger.fine("Not capturing output streams, no array provided.");
                } 
                
                // looking for a string like: "DNS Suffix  . : mydomain.com"
                String line;
                while ((line = outputStreams[0].readLine()) != null) {
                    if (line.contains("DNS Suffix")) {
                        domainname = line.substring(line.lastIndexOf(' ') + 1);
                        break;
                    }
                }
                
                proc.destroy();
                            
            } else {
                throw new UnsupportedOperationException("Unsupported operating system!");
            }
        }
        
        return domainname;
    }

    /**
     * Returns the IP address that the JVM will use to contact the given host and port
     * <p/>
     * Use this to find your external IP address.
     */
    public static InetAddress getIPAddress(String host, int port) {
        //String strIPAddress = null;
        Socket local = null;
        InetAddress localAddress = null;
    
        try {
    
            local = new Socket(host, port);
    
            localAddress = local.getLocalAddress();
    
            //strIPAddress = objLocalHost.getHostAddress();
        } catch (ConnectException e) {
            logger.warning("getIPAddress error connecting to " + host + ":" + port + " because " + e.getLocalizedMessage());
        } catch (Exception ex) {
            logger.warning("getIPAddress error connecting to " + host + ":" + port + ex);
            //strIPAddress = "";
        } finally {
            try {
                if (local != null) {
                    local.close();
                }
            } catch (IOException ioe) {
            } // Don't bother to log a close error.
        }
    
        logger.info("Using IP address: " + localAddress);
        return localAddress;
    }
}
