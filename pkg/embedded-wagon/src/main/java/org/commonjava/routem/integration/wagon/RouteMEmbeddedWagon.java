package org.commonjava.routem.integration.wagon;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.maven.plugin.LegacySupport;
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
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.component.configurator.BasicComponentConfigurator;
import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.commonjava.routem.data.RouteMDataException;
import org.commonjava.routem.model.MirrorOf;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.repository.Authentication;
import org.sonatype.aether.repository.AuthenticationSelector;
import org.sonatype.aether.repository.Proxy;
import org.sonatype.aether.repository.ProxySelector;
import org.sonatype.aether.repository.RemoteRepository;

@Component( role = Wagon.class, hint = "routem" )
public class RouteMEmbeddedWagon
    implements Wagon
{

    @Requirement
    private Logger logger;

    @Requirement
    private PlexusContainer container;

    @Requirement
    private LegacySupport legacySupport;

    private RouteMGateway gateway;

    private final Map<String, Wagon> wagonsByRepositoryUrl = new HashMap<String, Wagon>();

    private boolean interactive;

    private final List<SessionListener> sessionListeners = new ArrayList<SessionListener>();

    private final List<TransferListener> transferListeners = new ArrayList<TransferListener>();

    private int timeoutValue;

    private Properties httpHeaders;

    private Repository source;

    private String routingProtocol = "http";

    public RouteMEmbeddedWagon()
    {
    }

    public void setHttpHeaders( final Properties httpHeaders )
    {
        this.httpHeaders = httpHeaders;
    }

    public Properties getHttpHeaders()
    {
        return httpHeaders;
    }

    @Override
    public void get( final String resourceName, final File destination )
        throws TransferFailedException, ResourceDoesNotExistException, AuthorizationException
    {
        final Wagon wagon = findRoute( resourceName );
        wagon.get( resourceName, destination );
    }

    @Override
    public boolean getIfNewer( final String resourceName, final File destination, final long timestamp )
        throws TransferFailedException, ResourceDoesNotExistException, AuthorizationException
    {
        final Wagon wagon = findRoute( resourceName );
        return wagon.getIfNewer( resourceName, destination, timestamp );
    }

    @Override
    public void put( final File source, final String destination )
        throws TransferFailedException, ResourceDoesNotExistException, AuthorizationException
    {
        throw new UnsupportedOperationException( "Route-M Wagons are read-only for now." );
    }

    @Override
    public void putDirectory( final File sourceDirectory, final String destinationDirectory )
        throws TransferFailedException, ResourceDoesNotExistException, AuthorizationException
    {
        throw new UnsupportedOperationException( "Route-M Wagons are read-only for now." );
    }

    @Override
    public boolean resourceExists( final String resourceName )
        throws TransferFailedException, AuthorizationException
    {
        final Wagon wagon = findRoute( resourceName );
        return wagon.resourceExists( resourceName );
    }

    @Override
    public List<String> getFileList( final String destinationDirectory )
        throws TransferFailedException, ResourceDoesNotExistException, AuthorizationException
    {
        // FIXME: This will be broken in the Redirectory.findGroupId(..) method! Not sure how to determine groupId
        // here...
        final Wagon wagon = findRoute( destinationDirectory );
        return wagon.getFileList( destinationDirectory );
    }

    @Override
    public boolean supportsDirectoryCopy()
    {
        return false;
    }

    @Override
    public Repository getRepository()
    {
        return source;
    }

    @Override
    public void connect( final Repository source )
        throws ConnectionException, AuthenticationException
    {
        connect( source, null, (ProxyInfo) null );
    }

    @Override
    public void connect( final Repository source, final ProxyInfo proxyInfo )
        throws ConnectionException, AuthenticationException
    {
        connect( source, null, proxyInfo );
    }

    @Override
    public void connect( final Repository source, final ProxyInfoProvider proxyInfoProvider )
        throws ConnectionException, AuthenticationException
    {
        connect( source, null, proxyInfoProvider.getProxyInfo( routingProtocol ) );
    }

    @Override
    public void connect( final Repository source, final AuthenticationInfo authenticationInfo )
        throws ConnectionException, AuthenticationException
    {
        connect( source, authenticationInfo, (ProxyInfo) null );
    }

    @Override
    public void connect( final Repository source, final AuthenticationInfo authenticationInfo, final ProxyInfo proxyInfo )
        throws ConnectionException, AuthenticationException
    {
        // NOTE: We cannot init the real wagon yet, because we don't have a groupId to make the mirror selection. Put
        // this off until get()...
        this.source = source;

        final String protocol = source.getProtocol();
        String url = source.getUrl();

        // TODO: SSL, and authentication!
        url = routingProtocol + url.substring( protocol.length() );

        try
        {
            gateway = new RouteMGateway( source.getId(), new URI( url ) );
        }
        catch ( final RouteMDataException e )
        {
            throw new ConnectionException( "Cannot initialize Route-M routing tables: " + e.getMessage(), e );
        }
        catch ( final URISyntaxException e )
        {
            throw new ConnectionException( "Invalid URI for Route-M router: " + e.getMessage(), e );
        }
    }

    @Override
    public void connect( final Repository source, final AuthenticationInfo authenticationInfo,
                         final ProxyInfoProvider proxyInfoProvider )
        throws ConnectionException, AuthenticationException
    {
        connect( source, authenticationInfo, proxyInfoProvider.getProxyInfo( routingProtocol ) );
    }

    @Override
    public void openConnection()
        throws ConnectionException, AuthenticationException
    {
        throw new UnsupportedOperationException( "NOT SUPPORTED. Use connect(..)!" );
    }

    @Override
    public void disconnect()
        throws ConnectionException
    {
        if ( wagonsByRepositoryUrl != null )
        {
            for ( final Wagon wagon : new HashSet<Wagon>( wagonsByRepositoryUrl.values() ) )
            {
                final Repository repo = wagon.getRepository();
                wagonsByRepositoryUrl.remove( repo.getUrl() );

                try
                {
                    wagon.disconnect();
                }
                catch ( final ConnectionException e )
                {
                    if ( logger.isDebugEnabled() )
                    {
                        logger.debug( "Failed to disconnect wagon for: '" + repo.getId() + "'. Reason: "
                                          + e.getMessage(), e );
                    }
                    else
                    {
                        logger.warn( "Failed to disconnect wagon for: '" + repo.getId()
                            + "'. Use -X for more information." );
                    }
                }
            }
        }
    }

    @Override
    public void setTimeout( final int timeoutValue )
    {
        this.timeoutValue = timeoutValue;
    }

    @Override
    public int getTimeout()
    {
        return timeoutValue;
    }

    @Override
    public void addSessionListener( final SessionListener listener )
    {
        sessionListeners.add( listener );
    }

    @Override
    public void removeSessionListener( final SessionListener listener )
    {
        sessionListeners.remove( listener );
    }

    @Override
    public boolean hasSessionListener( final SessionListener listener )
    {
        return sessionListeners.contains( listener );
    }

    @Override
    public void addTransferListener( final TransferListener listener )
    {
        transferListeners.add( listener );
    }

    @Override
    public void removeTransferListener( final TransferListener listener )
    {
        transferListeners.remove( listener );
    }

    @Override
    public boolean hasTransferListener( final TransferListener listener )
    {
        return transferListeners.contains( listener );
    }

    @Override
    public boolean isInteractive()
    {
        return interactive;
    }

    @Override
    public void setInteractive( final boolean interactive )
    {
        this.interactive = interactive;
    }

    private Wagon findRoute( final String path )
        throws TransferFailedException
    {
        Repository repo = null;

        String groupId;
        try
        {
            groupId = gateway.getRedirectory()
                             .findGroupId( path );
        }
        catch ( final RouteMDataException e )
        {
            throw new TransferFailedException( "Route-M database lookup failed for path: '" + path + "'.\nReason: "
                + e.getMessage(), e );
        }

        AuthenticationInfo auth = null;
        ProxyInfo proxy = null;
        final RepositorySystemSession rss = legacySupport.getRepositorySession();

        try
        {
            final List<MirrorOf> mirrors = gateway.getDataManager()
                                                  .getMirrorsOfGroup( groupId );
            final MirrorOf mirror = gateway.getRedirectory()
                                           .selectMirror( mirrors );

            if ( mirror != null )
            {
                final AuthenticationSelector as = rss.getAuthenticationSelector();
                final ProxySelector ps = rss.getProxySelector();

                repo = new Repository( mirror.getMirrorId(), mirror.getTargetUrl() );
                final RemoteRepository rrepo =
                    new RemoteRepository( mirror.getMirrorId(), "default", mirror.getTargetUrl() );

                final Authentication a = as.getAuthentication( rrepo );
                final Proxy p = ps.getProxy( rrepo );

                if ( a != null )
                {
                    auth = new AuthenticationInfo();
                    auth.setPassphrase( a.getPassphrase() );
                    auth.setPassword( a.getPassword() );
                    auth.setPrivateKey( a.getPrivateKeyFile() );
                    auth.setUserName( a.getUsername() );
                }

                {
                    if ( p != null )
                    {
                        proxy = new ProxyInfo();
                        proxy.setHost( p.getHost() );
                        proxy.setType( p.getType() );
                        proxy.setPort( p.getPort() );

                        final Authentication pa = p.getAuthentication();
                        if ( pa != null )
                        {
                            proxy.setUserName( pa.getUsername() );
                            proxy.setPassword( pa.getPassword() );
                        }
                    }
                }
            }
        }
        catch ( final RouteMDataException e )
        {
            throw new TransferFailedException( "Failed to read mirrors for groupId: '" + groupId
                + "' from Route-M db. Reason: " + e.getMessage(), e );
        }

        if ( repo == null )
        {
            throw new TransferFailedException( "No route for group: '" + groupId + "'." );
        }

        final String protocol = repo.getProtocol();
        return createAndConnectWagon( protocol, groupId, repo, auth, proxy, rss );
    }

    private Wagon createAndConnectWagon( final String protocol, final String groupId, final Repository repo,
                                         final AuthenticationInfo auth, final ProxyInfo proxy,
                                         final RepositorySystemSession rss )
        throws TransferFailedException
    {
        Wagon wagon = wagonsByRepositoryUrl.get( repo.getUrl() );
        if ( wagon == null )
        {
            synchronized ( groupId.intern() )
            {
                try
                {
                    wagon = container.lookup( Wagon.class, protocol );
                }
                catch ( final ComponentLookupException e )
                {
                    throw new TransferFailedException( "Cannot find wagon for protocol: '" + protocol + "'.", e );
                }

                configure( wagon, repo.getId(), rss.getConfigProperties() );

                wagonsByRepositoryUrl.put( repo.getUrl(), wagon );

                try
                {
                    wagon.connect( repo, auth, proxy );
                }
                catch ( final ConnectionException e )
                {
                    throw new TransferFailedException( "Cannot connect to repository: " + repo + ". Reason: "
                        + e.getMessage(), e );
                }
                catch ( final AuthenticationException e )
                {
                    throw new TransferFailedException( "Cannot authenticate to repository: " + repo + ". Reason: "
                        + e.getMessage(), e );
                }
            }
        }

        return wagon;
    }

    private void configure( final Wagon wagon, final String id, final Map<String, Object> configProperties )
    {
        final Object config = configProperties.get( "aether.connector.wagon.config." + id );
        if ( config != null )
        {
            PlexusConfiguration pc = null;
            if ( config instanceof PlexusConfiguration )
            {
                pc = (PlexusConfiguration) config;
            }
            else if ( config instanceof Xpp3Dom )
            {
                pc = new XmlPlexusConfiguration( (Xpp3Dom) config );
            }
            else
            {
                logger.warn( "Invalid configuration for repository: '" + id + "':\n\n" + config );
            }

            if ( pc != null )
            {
                try
                {
                    new BasicComponentConfigurator().configureComponent( wagon, pc, container.getContainerRealm() );
                }
                catch ( final ComponentConfigurationException e )
                {
                    if ( logger.isDebugEnabled() )
                    {
                        logger.warn( "Failed to configure wagon for: '" + id + "'\nReason: " + e.getMessage()
                            + "\nConfiguration:\n\n" + config, e );
                    }
                    else
                    {
                        logger.warn( "Failed to configure wagon for: '" + id + "'\nReason: " + e.getMessage() );
                    }
                }
            }
        }

        if ( httpHeaders != null && !httpHeaders.isEmpty() )
        {
            try
            {
                final Method headerMethod = wagon.getClass()
                                                 .getMethod( "setHttpHeaders", Properties.class );
                headerMethod.invoke( wagon, httpHeaders );
            }
            catch ( final NoSuchMethodException e )
            {
                // skip it.
            }
            catch ( final Exception e )
            {
                logger.debug( "Failed to set HTTP headers for " + wagon.getClass()
                                                                       .getName() + ".\nReason: " + e.getMessage(), e );
            }
        }

        wagon.setInteractive( interactive );
        wagon.setTimeout( timeoutValue );
        for ( final TransferListener listener : transferListeners )
        {
            wagon.addTransferListener( listener );
        }

        for ( final SessionListener listener : sessionListeners )
        {
            wagon.addSessionListener( listener );
        }
    }

    public String getRoutingProtocol()
    {
        return routingProtocol;
    }

    public void setRoutingProtocol( final String routingProtocol )
    {
        this.routingProtocol = routingProtocol;
    }

}
