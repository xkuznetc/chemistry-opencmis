package cz.muni.fi.editor.cmisserver.fileshare;

import org.apache.chemistry.opencmis.commons.server.CallContext;

import java.util.Collection;

/**
 * Created by emptak on 2/6/17.
 */
public interface UserManager
{
    Collection<String> getLogins();

    void addLogin(String username, String password);

    String authenticate(CallContext context);
}
