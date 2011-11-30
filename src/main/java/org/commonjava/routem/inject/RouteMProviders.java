package org.commonjava.routem.inject;

import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.commonjava.couch.change.CouchChangeListener;
import org.commonjava.couch.conf.CouchDBConfiguration;
import org.commonjava.couch.db.CouchFactory;
import org.commonjava.couch.db.CouchManager;
import org.commonjava.couch.io.CouchHttpClient;

@Singleton
public class RouteMProviders
{

    @Inject
    @RouteMData
    private CouchDBConfiguration config;

    @Inject
    private CouchFactory factory;

    private CouchManager couchManager;

    private CouchHttpClient httpClient;

    private CouchChangeListener changeListener;

    @Produces
    @RouteMData
    @Default
    public synchronized CouchChangeListener getChangeListener()
    {
        System.out.println( "Returning change listener for user DB" );
        if ( changeListener == null )
        {
            changeListener = factory.getChangeListener( config );
        }

        return changeListener;
    }

    @Produces
    @RouteMData
    @Default
    public synchronized CouchManager getCouchManager()
    {
        if ( couchManager == null )
        {
            couchManager = factory.getCouchManager( config );
        }

        return couchManager;
    }

    @Produces
    @RouteMData
    @Default
    public synchronized CouchHttpClient getHttpClient()
    {
        if ( httpClient == null )
        {
            httpClient = factory.getHttpClient( config );
        }

        return httpClient;
    }

}
