package org.jmdns.utils;

import java.io.*;
import java.util.logging.Logger;


public class DNSInfo {
    private static Logger logger = Logger.getLogger(DNSInfo.class.getCanonicalName());
    
    public static String getDNSServer() throws IOException {
        String osName = System.getProperty("os.name");
        
        final BufferedReader[] outputStreams = new BufferedReader[2];
        String dnsServer = null;
        
        if (osName.contains("Linux") || osName.contains("Mac")) {
            SystemTools.execCommandAndWait("dns", "grep nameserver /etc/resolv.conf", null, null, outputStreams);
            while (outputStreams[0].ready()) {
                String line = outputStreams[0].readLine();    
                if (line.startsWith("nameserver")) {
                    dnsServer = line.substring(line.indexOf(' ')+1);
                    break;
                }
            }
        } else if (osName.contains("Windows")) {
            Process proc = SystemTools.execCommand("ipconfig /all", null, null);
            
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
            
            // looking for a string like: "DNS Servers . . . . : 192.168.1.1"
            String line;
            while ((line = outputStreams[0].readLine()) != null) {
                if (line.contains("DNS Servers")) {
                    dnsServer = line.substring(line.lastIndexOf(' ') + 1);
                    break;
                }
            }
            
            proc.destroy();
                            
        } else {
            throw new UnsupportedOperationException("Unsupported operating system: " + osName + "!");
        }
        
        return dnsServer;
    }
    
    /**
     * Uses operating system specific tools to determine the default search domain
     * 
     * @return The default search domain or null, if one can't be found.
     * 
     * @throws IOException
     */
    public static String defaultSearchDomain() {
        String osName = System.getProperty("os.name");
        
        String defaultSearchDomain = null;
        
        try {
            if (osName.contains("Linux")) {
                FileInputStream fis = new FileInputStream("/etc/resolv.conf");
                BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
                
                while (reader.ready()) {
                    String line = reader.readLine();    
                    if (line.startsWith("search") || line.startsWith("domain")) {
                        String split[] = line.split(" ");
                        
                        if (split.length > 1) {
                            defaultSearchDomain = split[1];
                        }
                        break;
                    }
                }
            } else if (osName.contains("Windows")) {
                final BufferedReader[] outputStreams = new BufferedReader[2];
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
                
                // looking for a string like: "DNS Suffix  . : domainname.com"
                String line;
                while ((line = outputStreams[0].readLine()) != null) {
                    if (line.contains("DNS Suffix")) {
                        defaultSearchDomain = line.substring(line.lastIndexOf(' ') + 1);
                        break;
                    }
                }
                
                proc.destroy();
                                
            } else {
                return null;
            }
        } catch (IOException e) {
            logger.warning("Error looking up default search domain, using 'null': " + e);
            return null;
        }
        
        return defaultSearchDomain;
    }
}
