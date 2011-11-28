/*******************************************************************************
 * Copyright 2011 John Casey
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.commonjava.routem.data;

import static org.commonjava.couch.util.IdUtils.namespaceId;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.commonjava.couch.conf.CouchDBConfiguration;
import org.commonjava.couch.db.CouchDBException;
import org.commonjava.couch.db.CouchManager;
import org.commonjava.couch.model.CouchApp;
import org.commonjava.routem.data.RouteMAppDescription.View;
import org.commonjava.routem.inject.RouteMData;
import org.commonjava.routem.model.Route;

@Singleton
public class RouteDataManager
{

    @Inject
    private CouchManager couch;

    @Inject
    @RouteMData
    private CouchDBConfiguration couchConfig;

    RouteDataManager()
    {
    }

    public RouteDataManager( final CouchManager couch, final CouchDBConfiguration config )
    {
        this.couch = couch;
        this.couchConfig = config;
    }

    public void install()
        throws RouteMDataException
    {
        final RouteMAppDescription description = new RouteMAppDescription();
        final CouchApp app = new CouchApp( RouteMAppDescription.APP_NAME, description );

        try
        {
            if ( couch.dbExists() )
            {
                // static in Couch, so safe to forcibly reload.
                couch.delete( app );
            }

            couch.initialize( description );
        }
        catch ( final CouchDBException e )
        {
            throw new RouteMDataException( "Failed to initialize routing database: %s (application: %s). Reason: %s",
                                           e, couchConfig.getDatabaseUrl(), description.getAppName(), e.getMessage() );
        }
    }

    // FIXME: What about child groupIds?? We need to match those here too.
    public List<Route> getRoutesContainingGroupId( final String groupId )
        throws RouteMDataException
    {
        try
        {
            return couch.getViewListing( new RouteMViewRequest( View.ROUTES_FOR_GROUP, groupId ), Route.class );
        }
        catch ( final CouchDBException e )
        {
            throw new RouteMDataException( "Failed to retrieve route listing for groupId: %s. Reason: %s", e, groupId,
                                           e.getMessage() );
        }
    }

    public Route getRoute( final String targetUrl )
        throws RouteMDataException
    {
        try
        {
            final List<Route> routes =
                couch.getViewListing( new RouteMViewRequest( View.ALL_ROUTES, namespaceId( Route.NAMESPACE, targetUrl ) ),
                                      Route.class );
            if ( routes == null || routes.isEmpty() )
            {
                return null;
            }
            else
            {
                return routes.get( 0 );
            }
        }
        catch ( final CouchDBException e )
        {
            throw new RouteMDataException( "Failed to retrieve route listing for target URL: %s. Reason: %s", e,
                                           targetUrl, e.getMessage() );
        }
    }

    public List<Route> getAllRoutes()
        throws RouteMDataException
    {
        try
        {
            return couch.getViewListing( new RouteMViewRequest( View.ALL_ROUTES ), Route.class );
        }
        catch ( final CouchDBException e )
        {
            throw new RouteMDataException( "Failed to retrieve route listing. Reason: %s", e, e.getMessage() );
        }
    }

    public boolean storeRoute( final Route route )
        throws RouteMDataException
    {
        return storeRoute( route, false );
    }

    public boolean storeRoute( final Route route, final boolean skipIfExists )
        throws RouteMDataException
    {
        try
        {
            return couch.store( route, skipIfExists );
        }
        catch ( final CouchDBException e )
        {
            throw new RouteMDataException( "Failed to store route: %s. Reason: %s", e, route.getTargetUrl(),
                                           e.getMessage() );
        }
    }

}
