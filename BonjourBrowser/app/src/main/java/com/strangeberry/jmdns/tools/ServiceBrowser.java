// %Z%%M%, %I%, %G%
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

package com.strangeberry.jmdns.tools;

import android.util.Log;
import android.webkit.ConsoleMessage;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.jmdns.*;

/**
 * User Interface for browsing JmDNS services.
 *
 * @author	Arthur van Hoff, Werner Randelshofer
 * @version 	%I%, %G%
 */
public class ServiceBrowser implements ServiceListener, ServiceTypeListener {

    JmDNS jmdns;
    Vector headers;
    String type;
    List types;
    List services;

    public ServiceBrowser(JmDNS jmdns) throws IOException {

        this.jmdns = jmdns;
        
        jmdns.addServiceTypeListener(this);
        
        // register some well known types
        String list[] = new String[] {

            "_http._tcp.local.",
            "_ftp._tcp.local.",
            "_tftp._tcp.local.",
            "_ssh._tcp.local.",
            "_smb._tcp.local.",
            "_printer._tcp.local.",
            "_airport._tcp.local.",
            "_afpovertcp._tcp.local.",
            "_ichat._tcp.local.",
            "_eppc._tcp.local.",
            "_presence._tcp.local."
        };
        
        for (int i = 0 ; i < list.length ; i++) {
            jmdns.registerServiceType(list[i]);
        }
        
    }
    
    /**
     * Add a service.
     */
    public void serviceAdded(ServiceEvent event) {
        final String name = event.getName();
        
        System.out.println("ADD: " + name);
    }
    
    /**
     * Remove a service.
     */
    public void serviceRemoved(ServiceEvent event) {
        final String name = event.getName();

        System.out.println("REMOVE: " + name);
    }
    
    /**
     * A new service type was <discovered.
     */
    public void serviceTypeAdded(ServiceEvent event) {
        final String type = event.getType();

        System.out.println("TYPE: " + type);
    }
    
    
    /*void insertSorted(List model, String value) {
        for (int i = 0, n = model.size() ; i < n ; i++) {
            if (value.compareToIgnoreCase((String)model.elementAt(i)) < 0) {
                model.insertElementAt(value, i);
                return;
            }
        }
        model.addElement(value);
    }*/
    
    /**
     * Resolve a service.
     */
    public void serviceResolved(ServiceEvent event) {
        String name = event.getName();
        String type = event.getType();
        ServiceInfo info = event.getInfo();

        Log.v("jmds", info.toString());

        /*if (name.equals(serviceList.getSelectedValue())) {
            if (info == null) {
                Log.v("jmds", ("service not found") );
            } else {
                
                StringBuffer buf = new StringBuffer();
                buf.append(name);
                buf.append('.');
                buf.append(type);
                buf.append('\n');
                buf.append(info.getServer());
                buf.append(':');
                buf.append(info.getPort());
                buf.append('\n');
                buf.append(info.getAddress());
                buf.append(':');
                buf.append(info.getPort());
                buf.append('\n');
                for (Enumeration names = info.getPropertyNames() ; names.hasMoreElements() ; ) {
                    String prop = (String)names.nextElement();
                    buf.append(prop);
                    buf.append('=');
                    buf.append(info.getPropertyString(prop));
                    buf.append('\n');
                }

                Log.v("jmds", buf.toString());
            }
        }*/
    }
    
    /*public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            if (e.getSource() == typeList) {
                type = (String)typeList.getSelectedValue();
                jmdns.removeServiceListener(type, this);
                services.setSize(0);
                info.setText("");
                if (type != null) {
                jmdns.addServiceListener(type, this);
                }
            } else if (e.getSource() == serviceList) {
                String name = (String)serviceList.getSelectedValue();
                if (name == null) {
                    info.setText("");
                } else {
                    System.out.println(this+" valueChanged() type:"+type+" name:"+name);
                    System.out.flush();
                    ServiceInfo service = jmdns.getServiceInfo(type, name);
                    if (service == null) {
                        info.setText("service not found");
                    } else {
                        jmdns.requestServiceInfo(type, name);
                    }
                }
            }
        }
    }*/
    
    public String toString() {
        return "RVBROWSER";
    }

}
