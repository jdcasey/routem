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

import java.net.URI;
import java.net.URISyntaxException;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.commonjava.routem.data.RouteMDataException;
import org.commonjava.routem.route.Redirectory;
import org.commonjava.util.logging.Logger;

@Path( "/redirectory" )
@RequestScoped
public class RedirectoryResource
{

    private final Logger logger = new Logger( getClass() );

    @Inject
    private Redirectory redirectory;

    @Path( "/{path: (.+)?}" )
    @GET
    public Response getRedirect( @PathParam( "path" ) final String path )
    {
        ResponseBuilder builder = null;

        String url = null;
        try
        {
            url = redirectory.selectRoute( path );
            if ( url == null )
            {
                builder = Response.status( Status.NOT_FOUND );
            }
            else
            {
                builder = Response.status( Status.TEMPORARY_REDIRECT )
                                  .location( new URI( url ) );
            }
        }
        catch ( final RouteMDataException e )
        {
            logger.error( "Failed to lookup mirrored route for path: '%s'. Reason: %s", e, path, e.getMessage() );

            builder = Response.serverError();
        }
        catch ( final URISyntaxException e )
        {
            logger.error( "Invalid mirror URL: '%s' given for path: '%s'. Reason: %s", e, url, path, e.getMessage() );

            builder = Response.serverError();
        }

        return builder == null ? Response.serverError()
                                         .build() : builder.build();
    }

}
