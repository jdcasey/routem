package org.commonjava.routem.integration.wagon;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.maven.wagon.ConnectionException;
import org.apache.maven.wagon.ResourceDoesNotExistException;
import org.apache.maven.wagon.TransferFailedException;
import org.apache.maven.wagon.Wagon;
import org.apache.maven.wagon.authentication.AuthenticationException;
import org.apache.maven.wagon.authentication.AuthenticationInfo;
import org.apache.maven.wagon.authorization.AuthorizationException;
import org.apache.maven.wagon.events.SessionListener;
import org.apache.maven.wagon.events.TransferListener;
import org.apache.maven.wagon.proxy.ProxyInfo;
import org.apache.maven.wagon.proxy.ProxyInfoProvider;
import org.apache.maven.wagon.repository.Repository;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;

@Component( role = Wagon.class, hint = "routem" )
public class RouteMEmbeddedWagon
    implements Wagon
{

    @Requirement
    private Map<String, Wagon> wagons;

    private Wagon delegate;

    public RouteMEmbeddedWagon()
    {
    }

    public RouteMEmbeddedWagon( final Map<String, Wagon> wagons )
    {
        this.wagons = wagons;
    }

    protected void setWagons( final Map<String, Wagon> wagons )
    {
        this.wagons = wagons;
    }

    protected Map<String, Wagon> getWagons()
    {
        return wagons;
    }

    @Override
    public void get( final String resourceName, final File destination )
        throws TransferFailedException, ResourceDoesNotExistException, AuthorizationException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean getIfNewer( final String resourceName, final File destination, final long timestamp )
        throws TransferFailedException, ResourceDoesNotExistException, AuthorizationException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void put( final File source, final String destination )
        throws TransferFailedException, ResourceDoesNotExistException, AuthorizationException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void putDirectory( final File sourceDirectory, final String destinationDirectory )
        throws TransferFailedException, ResourceDoesNotExistException, AuthorizationException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean resourceExists( final String resourceName )
        throws TransferFailedException, AuthorizationException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public List<String> getFileList( final String destinationDirectory )
        throws TransferFailedException, ResourceDoesNotExistException, AuthorizationException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean supportsDirectoryCopy()
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Repository getRepository()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void connect( final Repository source )
        throws ConnectionException, AuthenticationException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void connect( final Repository source, final ProxyInfo proxyInfo )
        throws ConnectionException, AuthenticationException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void connect( final Repository source, final ProxyInfoProvider proxyInfoProvider )
        throws ConnectionException, AuthenticationException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void connect( final Repository source, final AuthenticationInfo authenticationInfo )
        throws ConnectionException, AuthenticationException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void connect( final Repository source, final AuthenticationInfo authenticationInfo, final ProxyInfo proxyInfo )
        throws ConnectionException, AuthenticationException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void connect( final Repository source, final AuthenticationInfo authenticationInfo,
                         final ProxyInfoProvider proxyInfoProvider )
        throws ConnectionException, AuthenticationException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void openConnection()
        throws ConnectionException, AuthenticationException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void disconnect()
        throws ConnectionException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void setTimeout( final int timeoutValue )
    {
        // TODO Auto-generated method stub

    }

    @Override
    public int getTimeout()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void addSessionListener( final SessionListener listener )
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeSessionListener( final SessionListener listener )
    {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean hasSessionListener( final SessionListener listener )
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void addTransferListener( final TransferListener listener )
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeTransferListener( final TransferListener listener )
    {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean hasTransferListener( final TransferListener listener )
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isInteractive()
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setInteractive( final boolean interactive )
    {
        // TODO Auto-generated method stub

    }

}
