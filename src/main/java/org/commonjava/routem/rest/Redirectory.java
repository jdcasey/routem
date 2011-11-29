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
package org.commonjava.routem.rest;

import static org.commonjava.couch.util.UrlUtils.buildUrl;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.commonjava.routem.data.RouteDataManager;
import org.commonjava.routem.data.RouteMDataException;
import org.commonjava.routem.model.MirrorOf;
import org.commonjava.util.logging.Logger;

@Path( "/redirectory" )
@Singleton
@RequestScoped
public class Redirectory
{

    private final Logger logger = new Logger( getClass() );

    @Inject
    private RouteDataManager dataManager;

    @Path( "/{path: (.+)?}" )
    @GET
    public Response getRedirect( @PathParam( "path" ) final String path )
    {
        ResponseBuilder builder = null;
        ;

        final String groupId = findGroupId( path );
        try
        {
            final List<MirrorOf> mirrors = dataManager.getMirrorsOfGroup( groupId );

            if ( mirrors == null || mirrors.isEmpty() )
            {
                // TODO: Wildcard support!
                builder = Response.status( Status.NOT_FOUND );
            }

            // TODO: Select a mirror properly!
            final MirrorOf selected = mirrors.get( 0 );

            builder.contentLocation( new URI( buildUrl( selected.getTargetUrl(), path ) ) );
        }
        catch ( final RouteMDataException e )
        {
            logger.error( "Failed to lookup mirror list for groupId: %s (path: %s). Reason: %s", e, groupId, path,
                          e.getMessage() );
            builder = Response.serverError();
        }
        catch ( final MalformedURLException e )
        {
            logger.error( "Failed to construct redirection URL for path: %s. Reason: %s", e, path, e.getMessage() );
            builder = Response.serverError();
        }
        catch ( final URISyntaxException e )
        {
            logger.error( "Failed to construct redirection URL for path: %s. Reason: %s", e, path, e.getMessage() );
            builder = Response.serverError();
        }

        return builder == null ? Response.serverError()
                                         .build() : builder.build();
    }

    private String findGroupId( final String path )
    {
        if ( path.endsWith( "/" ) )
        {
            return null;
        }

        File f = new File( path );
        for ( int i = 0; i < 3; i++ )
        {
            final File d = f.getParentFile();
            if ( d == null )
            {
                return null;
            }
            f = d;
        }

        return f.getPath();
    }

}
