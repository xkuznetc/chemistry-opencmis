package cz.muni.fi.editor.cmisserver.fileshare.impl;

import cz.muni.fi.editor.cmisserver.fileshare.UserManager;
import org.apache.chemistry.opencmis.commons.exceptions.CmisPermissionDeniedException;
import org.apache.chemistry.opencmis.commons.server.CallContext;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Code is taken from default apache fileshare implementation.
 * Created by emptak on 2/6/17.
 */
public class EditorUserManager implements UserManager
{
    private final Map<String, String> logins = new HashMap<>();

    @Override
    public synchronized Collection<String> getLogins()
    {
        return logins.keySet();
    }

    @Override
    public synchronized void addLogin(String username, String password)
    {
        if (username == null || password == null)
        {
            return;
        }

        logins.put(username.trim(), password);
    }

    private synchronized boolean authenticate(String username, String password)
    {
        String pwd = logins.get(username);

        if (pwd == null)
        {
            return false;
        }

        return pwd.equals(password);
    }

    @Override
    public String authenticate(CallContext context)
    {
        if (!authenticate(context.getUsername(), context.getPassword()))
        {
            throw new CmisPermissionDeniedException("Invalid username or password.");
        }

        return context.getUsername();
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder(128);

        for (String user : logins.keySet())
        {
            sb.append('[');
            sb.append(user);
            sb.append(']');
        }

        return sb.toString();
    }
}
