package org.tak.runtime.servers;

import java.applet.AppletStub;

/**
 * User: Tommy
 * 5/28/13
 */
public interface Server extends AppletStub {
    public String getFilePath();
    public boolean hasMultipliers();
    public String getMainClassName();

}
