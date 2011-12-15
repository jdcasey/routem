package org.commonjava.routem.boot;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.commonjava.routem.data.RouteDataManager;
import org.commonjava.routem.data.RouteMDataException;
import org.commonjava.routem.model.Group;
import org.commonjava.routem.model.MirrorOf;
import org.commonjava.routem.model.RouteMReplicationData;
import org.commonjava.web.common.model.Listing;
import org.commonjava.web.common.ser.JsonSerializer;

import com.google.gson.reflect.TypeToken;

@Singleton
@Named( "http" )
public class HttpBootstrapper
    implements RouteDataBootstrapper
{

    @Inject
    private RouteDataManager dataManager;

    @Inject
    private JsonSerializer serializer;

    protected HttpBootstrapper()
    {
    }

    public HttpBootstrapper( final RouteDataManager dataManager, final JsonSerializer serializer )
    {
        this.dataManager = dataManager;
        this.serializer = serializer;
    }

    @Override
    public final void populateDatabaseFrom( final URI groupsUri, final URI mirrorsUri )
        throws RouteMDataException
    {
        final HttpClient client = createHttpClient();

        final List<? extends Group> groups = getListing( groupsUri, client, Group.class );
        final List<? extends MirrorOf> mirrors = getListing( groupsUri, client, MirrorOf.class );

        dataManager.install();
        for ( final Group group : groups )
        {
            dataManager.store( group );
        }

        for ( final MirrorOf mirror : mirrors )
        {
            dataManager.store( mirror );
        }
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public final void populateDatabaseFrom( final URI uri )
        throws RouteMDataException
    {
        RouteMReplicationData data = null;
        final HttpUriRequest request = createRequest( uri );
        try
        {
            final HttpResponse response = createHttpClient().execute( request );

            final StatusLine status = response.getStatusLine();
            if ( status.getStatusCode() != 200 )
            {
                throw new RouteMDataException( "Failed to retrieve listing from: '%s'. Status: '%s'", uri, status );
            }

            final HttpEntity entity = response.getEntity();
            final String encoding = entity.getContentEncoding() == null ? "UTF-8" : entity.getContentEncoding()
                                                                                          .getValue();
            final InputStream content = entity.getContent();

            data = serializer.fromStream( content, encoding, RouteMReplicationData.class );
        }
        catch ( final ClientProtocolException e )
        {
            throw new RouteMDataException( "Failed to load listing from: '%s'. Reason: %s", e, uri, e.getMessage() );
        }
        catch ( final IOException e )
        {
            throw new RouteMDataException( "Failed to load listing from: '%s'. Reason: %s", e, uri, e.getMessage() );
        }

        dataManager.install();
        if ( data.getGroups() != null )
        {
            for ( final Group group : data.getGroups() )
            {
                dataManager.store( group );
            }
        }

        if ( data.getMirrors() != null )
        {
            for ( final MirrorOf mirror : data.getMirrors() )
            {
                dataManager.store( mirror );
            }
        }
    }

    protected HttpClient createHttpClient()
    {
        final DefaultHttpClient client = new DefaultHttpClient();

        return client;
    }

    protected HttpUriRequest createRequest( final URI uri )
    {
        return new HttpGet( uri );
    }

    protected <T> List<? extends T> getListing( final URI uri, final HttpClient client, final Class<T> type )
        throws RouteMDataException
    {
        final TypeToken<Listing<T>> token = new TypeToken<Listing<T>>()
        {
        };

        final HttpUriRequest request = createRequest( uri );
        try
        {
            final HttpResponse response = client.execute( request );

            final StatusLine status = response.getStatusLine();
            if ( status.getStatusCode() != 200 )
            {
                throw new RouteMDataException( "Failed to retrieve listing from: '%s'. Status: '%s'", uri, status );
            }

            final HttpEntity entity = response.getEntity();
            final String encoding = entity.getContentEncoding() == null ? "UTF-8" : entity.getContentEncoding()
                                                                                          .getValue();
            final InputStream content = entity.getContent();

            @SuppressWarnings( "unchecked" )
            final Listing<T> listing = serializer.listingFromStream( content, encoding, token );

            return listing.getItems();
        }
        catch ( final ClientProtocolException e )
        {
            throw new RouteMDataException( "Failed to load listing from: '%s'. Reason: %s", e, uri, e.getMessage() );
        }
        catch ( final IOException e )
        {
            throw new RouteMDataException( "Failed to load listing from: '%s'. Reason: %s", e, uri, e.getMessage() );
        }
    }

    protected final RouteDataManager getDataManager()
    {
        return dataManager;
    }

    protected final JsonSerializer getSerializer()
    {
        return serializer;
    }

    protected final void setDataManager( final RouteDataManager dataManager )
    {
        this.dataManager = dataManager;
    }

    protected final void setSerializer( final JsonSerializer serializer )
    {
        this.serializer = serializer;
    }

}
