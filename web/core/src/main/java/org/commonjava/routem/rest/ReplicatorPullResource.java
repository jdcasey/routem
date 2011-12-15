package org.commonjava.routem.rest;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.commonjava.routem.data.RouteDataManager;
import org.commonjava.routem.data.RouteMDataException;
import org.commonjava.routem.model.Group;
import org.commonjava.routem.model.MirrorOf;
import org.commonjava.routem.model.RouteMReplicationData;
import org.commonjava.util.logging.Logger;
import org.commonjava.web.common.model.Listing;
import org.commonjava.web.common.ser.JsonSerializer;

import com.google.gson.reflect.TypeToken;

@Singleton
@Path( "/replicator/pull" )
public class ReplicatorPullResource
{

    private final Logger logger = new Logger( getClass() );

    @Inject
    private RouteDataManager dataManager;

    @Inject
    private JsonSerializer serializer;

    @GET
    @Path( "/all" )
    @Produces( MediaType.APPLICATION_JSON )
    public Response consolidatedData()
    {
        ResponseBuilder builder = null;
        try
        {
            final RouteMReplicationData data =
                new RouteMReplicationData( dataManager.getAllGroupDefinitions(),
                                           dataManager.getAllMirrorOfDefinitions() );

            final String json = serializer.toString( data );

            builder = Response.ok( json );
        }
        catch ( final RouteMDataException e )
        {
            logger.error( "Failed to lookup mirror/group data for PULL replication. Reason: %s", e, e.getMessage() );

            builder = Response.serverError();
        }

        return builder == null ? Response.serverError()
                                         .build() : builder.build();
    }

    @GET
    @Path( "/groups" )
    @Produces( MediaType.APPLICATION_JSON )
    public Response groupData()
    {
        ResponseBuilder builder = null;
        try
        {
            final Listing<Group> listing = new Listing<Group>( dataManager.getAllGroupDefinitions() );
            logger.info( "returning %d groups: %s", listing.getItems()
                                                           .size(), listing.getItems() );

            final String json = serializer.toString( listing, new TypeToken<Listing<Group>>()
            {
            }.getType() );

            logger.info( "Returning JSON: %s", json );

            builder = Response.ok( json );
        }
        catch ( final RouteMDataException e )
        {
            logger.error( "Failed to lookup group data for PULL replication. Reason: %s", e, e.getMessage() );

            builder = Response.serverError();
        }

        return builder == null ? Response.serverError()
                                         .build() : builder.build();
    }

    @GET
    @Path( "/mirrors" )
    @Produces( MediaType.APPLICATION_JSON )
    public Response mirrorData()
    {
        ResponseBuilder builder = null;
        try
        {
            final Listing<MirrorOf> listing = new Listing<MirrorOf>( dataManager.getAllMirrorOfDefinitions() );
            final String json = serializer.toString( listing, new TypeToken<Listing<MirrorOf>>()
            {
            }.getType() );

            builder = Response.ok( json );
        }
        catch ( final RouteMDataException e )
        {
            logger.error( "Failed to lookup mirror data for PULL replication. Reason: %s", e, e.getMessage() );

            builder = Response.serverError();
        }

        return builder == null ? Response.serverError()
                                         .build() : builder.build();
    }

}
