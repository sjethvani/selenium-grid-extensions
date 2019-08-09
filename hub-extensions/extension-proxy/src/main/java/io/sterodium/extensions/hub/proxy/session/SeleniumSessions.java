package io.sterodium.extensions.hub.proxy.session;


import java.net.URL;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.grid.internal.GridRegistry;
import org.openqa.grid.internal.TestSession;

/**
 * @author Alexey Nikolaenko alexey@tcherezov.com
 *         Date: 21/09/2015
 */
public class SeleniumSessions {

    private static final Pattern SESSION_ID_PATTERN = Pattern.compile("/session/([^/]+).*");
    private static final Logger LOGGER = Logger.getLogger(SeleniumSessions.class.getName());

    private final GridRegistry registry;

    public SeleniumSessions(GridRegistry registry) {
        this.registry = registry;
    }

    public URL getRemoteHostForSession(String sessionId) {
    	LOGGER.info("inside getRemoteHostForSession for sessionId "+sessionId);
        for (TestSession activeSession : registry.getActiveSessions()) {
        	if ((activeSession != null) && (sessionId != null) && (activeSession.getExternalKey() != null)) {
        		if (sessionId.equals(activeSession.getExternalKey().getKey())) {
                    return activeSession.getSlot().getProxy().getRemoteHost();
                }
        	}
        	else
        	{
        		LOGGER.info("inside getRemoteHostForSession ignoring for null values");
        	}
        }
        throw new IllegalArgumentException("Invalid sessionId. No active session is present for id:" + sessionId);
    }

    public void refreshTimeout(String sessionId) {
    	LOGGER.info("inside refreshTimeout for sessionId "+sessionId);
        for (TestSession activeSession : registry.getActiveSessions()) {
            if ((activeSession != null) && (sessionId != null) && (activeSession.getExternalKey() != null)) {
                if (sessionId.equals(activeSession.getExternalKey().getKey())) {
                    refreshTimeout(activeSession);
                }
            }
            else
            {
        		LOGGER.info("inside refreshTimeout ignoring for null values");
            }
        }
    }

    private void refreshTimeout(TestSession activeSession) {
        if (activeSession.getInactivityTime() != 0) {
            activeSession.setIgnoreTimeout(false);
        }
    }

    public static String getSessionIdFromPath(String pathInfo) {
        Matcher matcher = SESSION_ID_PATTERN.matcher(pathInfo);
        if (matcher.matches()) {
            return matcher.group(1);
        }
        throw new IllegalArgumentException("Invalid request. Session Id is not present");
    }

    public static String trimSessionPath(String pathInfo) {
        return pathInfo.replaceFirst("/session/" + getSessionIdFromPath(pathInfo), "");
    }

}
