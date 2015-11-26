package musixmatch.com.bonjourbrowser;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.io.*;
import java.net.*;
import javax.jmdns.*;
import java.util.Enumeration;
import com.strangeberry.jmdns.tools.ServiceBrowser;
import android.os.AsyncTask;
import android.util.Log;
import android.text.format.Formatter;
import android.net.wifi.WifiManager;

public class Browser extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);

        new BrowserTask().execute();

    }

    public InetAddress getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        String ip = inetAddress.getHostAddress();
                        Log.i("jmds", "***** IP="+ ip);
                        return inetAddress;
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("jmds", ex.toString());
        }
        return null;
    }

    private class BrowserTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            InetAddress intf = null;
            try {

                WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);

                if (intf == null) {
                    //intf = InetAddress.getLocalHost();
                    intf = getLocalIpAddress();
                }

                Log.v("jmds", "Musixmatch Testbed/Starting on address " + intf );

                System.getProperties().put("jmdns.debug", "0");

                JmDNS jmdns = new JmDNS(intf);

                //jmdns.registerServiceType("");

                ServiceBrowser sb = new ServiceBrowser(jmdns);

            } catch(IOException e) {
                Log.e("jmds", "Error starting JmDNS", e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }
}
