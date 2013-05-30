package org.tak.runtime.servers;

import java.applet.AppletContext;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: Tommy
 * 5/28/13
 */
public class RuneScape implements Server {
    private static final Pattern pattern = Pattern.compile("<param name=\"?([^\\s]+)\"?\\s+value=\"?([^>]*)\"?>", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    private static final int                     REVISION   = 762;
    private final        HashMap<String, String> parameters = new HashMap<>();

    public RuneScape() {
        try {
            fetchParameters();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getFilePath() {
        return "/Users/Tommy/RSClients/r" + REVISION + ".jar";
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
        return false;
    }
    @Override
    public URL getDocumentBase() {
        try {
            return new URL("http://www.runescape.com");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public URL getCodeBase() {
        try {
            return new URL("http://www.runescape.com");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
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

    }
    public void fetchParameters() throws IOException {
        final String pageSource = getPageSource(new URL("http://world7.runescape.com"));
        final Matcher matcher = pattern.matcher(pageSource);
        while (matcher.find()) {
            if (!parameters.containsKey(matcher.group(1))) {
                parameters.put(matcher.group(1).replaceAll("\"", ""), matcher.group(2).replaceAll("\"", ""));
            }
        }
    }
    public String getPageSource(final URL url) throws IOException {
        try {
            URLConnection conn = url.openConnection();
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            rd.close();
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}