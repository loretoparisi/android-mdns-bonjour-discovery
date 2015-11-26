package org.jmdns.utils;

import java.io.*;
import java.util.*;

public class SystemTools {

    public static Process execCommand(String command, Properties props,  File directory) throws IOException {
        Process proc = Runtime.getRuntime().exec(command, propsToArray(props), directory);
        
        return proc;
    }
    
    public static int execCommandAndWait(String name, String command, Properties props, 
            File directory, final BufferedReader[] outputStreams) throws IOException {
        
        Process proc = Runtime.getRuntime().exec(command, SystemTools.propsToArray(props), directory);
        
        if (outputStreams != null) {
            if (outputStreams.length == 2) {
                // stdout - why is stdout obtained by getInputStream()?? (silly java API)
                outputStreams[0] = new BufferedReader(new InputStreamReader(proc.getInputStream()));
                // stderr
                outputStreams[1] = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
            } else {
                //log.warn("execCommandAndWait: cannot capture output streams, unexpected array length " + outputStreams.length);
            }
        } else {
            //log.debug("Not capturing output streams, no array provided.");
        } 
                
        try {
            proc.waitFor();
        } catch (InterruptedException e) {
            //log.error("Interrupted while waiting for " + name + " process to complete.", e);
        }
    
        return proc.exitValue();
    }

    public static String [] propsToArray(Properties props) throws IOException {
        if (props == null)
            return null;
    
        ArrayList<String> list = new ArrayList<String>();
    
        Enumeration<Object> keys = props.keys();
        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            String value = props.getProperty(key);
            list.add(key +  "=" + value);
        }
        return list.toArray(new String[list.size()]);
    }

}
