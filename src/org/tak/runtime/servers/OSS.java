package org.tak.runtime.servers;

import java.applet.AppletContext;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Scanner;

/**
 * User: Tommy
 * 5/28/13
 */
public class OSS implements Server {
    private static final String                  PAGE_ADDRESS = "http://oldschool69.runescape.com/";
    private static final int                     REVISION     = 15;
    private final        HashMap<String, String> parameters   = new HashMap<>();
    private URL pageAddressUrl;
    public OSS() {
        try {
            pageAddressUrl = new URL(PAGE_ADDRESS);
            String pageSource = readPageSource(PAGE_ADDRESS);
            parseParameters(pageSource);
        } catch (MalformedURLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
    @Override
    public String getFilePath() {
        return "/Users/Tommy/Downloads/OSS Clients/rev" + REVISION + ".jar";
    }

    @Override
    public boolean hasMultipliers() {
        return true;
    }

    @Override
    public String getMainClassName() {
        return "client";
    }

    @Override
    public boolean isActive() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public URL getDocumentBase() {
        return pageAddressUrl;
    }

    public URL getCodeBase() {
        return pageAddressUrl;
    }

    @Override
    public String getParameter(String name) {
        return parameters.get(name);
    }

    @Override
    public AppletContext getAppletContext() {
        return null;
    }

    @Override
    public void appletResize(int width, int height) {
        //nothing
    }
    private static String readPageSource(String pageAddress) throws IOException {
        String source = "";
        Scanner scanner = new Scanner(new URL(pageAddress).openStream());
        while (scanner.hasNextLine()) {
            source += scanner.nextLine() + "\n";
        }
        return source;
    }

    private void parseParameters(String pageSource) {
        String[] lines = pageSource.split("\n");
        String paramNameBeginning = "param name=";
        String valueBeginning = "value=";
        for (String line : lines) {
            if (!line.contains(paramNameBeginning)) {
                continue;
            }
            int start = line.indexOf(paramNameBeginning)
                    + paramNameBeginning.length() + 1;
            int end = line.indexOf('"', start);
            String name = line.substring(start, end);
            start = line.indexOf(valueBeginning) + valueBeginning.length() + 1;
            end = line.indexOf('"', start);
            String value = line.substring(start, end);
            parameters.put(name, value);
        }
    }

}
