package org.tak.runtime.servers;

import java.applet.AppletContext;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;

/**
 * User: Tommy
 * 5/31/13
 */
public class BattleScape implements Server {
    private Hashtable<String, String> parameters = new Hashtable<>();
    public BattleScape() {
        parameters.put("worldid", "1");
        parameters.put("portoff", "0");
        parameters.put("lowmem", "0");
        parameters.put("free", "0");
        parameters.put("version", "718");
    }
    @Override
    public String getFilePath() {
        return "/Users/Tommy/Downloads/BSClients/bs3.jar";
    }

    @Override
    public boolean hasMultipliers() {
        return false;
    }

    @Override
    public String getMainClassName() {
        return "client";  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getParameter(String paramString) {
        return parameters.get(paramString);
    }

    public URL getDocumentBase() {
        return getCodeBase();
    }

    public URL getCodeBase() {
        try {
            return new URL("http://www.battle-scape.com");
        } catch (MalformedURLException ignored) {
        }
        throw new RuntimeException();
    }

    public void appletResize(int paramInt1, int paramInt2) {
    }

    public AppletContext getAppletContext() {
        return null;
    }

    public boolean isActive() {
        return true;
    }
}
