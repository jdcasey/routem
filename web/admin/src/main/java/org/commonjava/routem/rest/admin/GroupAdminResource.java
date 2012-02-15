package org.commonjava.routem.rest.admin;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.commonjava.couch.rbac.Permission;
import org.commonjava.routem.data.RouteDataManager;
import org.commonjava.routem.data.RouteMDataException;
import org.commonjava.routem.model.Group;
import org.commonjava.util.logging.Logger;
import org.commonjava.web.json.model.Listing;
import org.commonjava.web.json.ser.JsonSerializer;

import com.google.gson.reflect.TypeToken;

@Path( "/admin/group" )
@RequestScoped
public class GroupAdminResource
{

    private static final String CANONICAL_URL_QUERY_PARAM = "c";

    private final Logger logger = new Logger( getClass() );

    @Inject
    private RouteDataManager dataManager;

    @Inject
    private JsonSerializer serializer;

    @PUT
    @Path( "/{groupId}" )
    @RequiresAuthentication
    public Response create( @PathParam( "groupId" ) final String groupId, @Context final HttpServletRequest request,
                            @Context final UriInfo uriInfo )
    {
        SecurityUtils.getSubject()
                     .isPermitted( Permission.name( Group.NAMESPACE, Permission.WILDCARD, Permission.ADMIN ) );

        ResponseBuilder builder = null;

        final String canonicalUrl = request.getParameter( CANONICAL_URL_QUERY_PARAM );
        if ( canonicalUrl == null )
        {
            builder =
                Response.status( Status.BAD_REQUEST )
                        .entity( "You must provide a value for the Canonical-URL request parameter: '"
                                     + CANONICAL_URL_QUERY_PARAM + "'." );
        }

        final Group group = new Group( groupId, canonicalUrl );
        try
        {
            if ( dataManager.store( group, false ) )
            {
                builder = Response.ok()
                                  .location( uriInfo.getAbsolutePathBuilder()
                                                    .path( group.getGroupId() )
                                                    .build() );
            }
            else
            {
                builder = Response.notModified();
            }
        }
        catch ( final RouteMDataException e )
        {
            logger.error( e.getMessage(), e );
            builder = Response.serverError();
        }

        return builder == null ? Response.serverError()
                                         .build() : builder.build();
    }

    @POST
    @Consumes( { MediaType.APPLICATION_JSON } )
    @RequiresAuthentication
    public Response createFromJson( @Context final HttpServletRequest request, @Context final UriInfo uriInfo )
    {
        SecurityUtils.getSubject()
                     .isPermitted( Permission.name( Group.NAMESPACE, Permission.WILDCARD, Permission.ADMIN ) );

        ResponseBuilder builder = null;

        @SuppressWarnings( "unchecked" )
        final Group group = serializer.fromRequestBody( request, Group.class );
        try
        {
            if ( dataManager.store( group, false ) )
            {
                builder = Response.ok()
                                  .location( uriInfo.getAbsolutePathBuilder()
                                                    .path( group.getGroupId() )
                                                    .build() );
            }
            else
            {
                builder = Response.notModified();
            }
        }
        catch ( final RouteMDataException e )
        {
            logger.error( e.getMessage(), e );
            builder = Response.serverError();
        }

        return builder == null ? Response.serverError()
                                         .build() : builder.build();
    }

    @GET
    @Path( "/{groupId}" )
    public Response get( @PathParam( "groupId" ) final String groupId )
    {
        ResponseBuilder builder = null;
        try
        {
            final Group group = dataManager.getGroup( groupId );
            if ( group == null )
            {
                builder = Response.status( Status.NOT_FOUND );
            }
            else
            {
                final String json = serializer.toString( group );
                builder = Response.ok( json );
            }
        }
        catch ( final RouteMDataException e )
        {
            logger.error( e.getMessage(), e );
            builder = Response.serverError();
        }

        return builder == null ? Response.serverError()
                                         .build() : builder.build();
    }

    @DELETE
    @Path( "/{groupId}" )
    @RequiresAuthentication
    public Response delete( @PathParam( "groupId" ) final String groupId )
    {
        SecurityUtils.getSubject()
                     .isPermitted( Permission.name( Group.NAMESPACE, Permission.WILDCARD, Permission.ADMIN ) );

        ResponseBuilder builder = null;
        try
        {
            dataManager.deleteGroup( groupId );
            builder = Response.ok();
        }
        catch ( final RouteMDataException e )
        {
            logger.error( e.getMessage(), e );
            builder = Response.serverError();
        }

        return builder == null ? Response.serverError()
                                         .build() : builder.build();
    }

    @Path( "/list" )
    @Produces( "application/json" )
    @GET
    public Response listGroupsToJson()
    {
        ResponseBuilder builder = null;

        try
        {
            final List<Group> groups = dataManager.getAllGroupDefinitions();
            logger.info( "From dataManager: %s, got groups:\n\t%s\n\n", dataManager,
                         new org.commonjava.util.logging.helper.JoinString( "\n\t", groups ) );

            final TypeToken<Listing<Group>> tt = new TypeToken<Listing<Group>>()
            {
            };

            builder = Response.ok( serializer.toString( new Listing<Group>( groups ), tt.getType() ) );
        }
        catch ( final RouteMDataException e )
        {
            logger.error( e.getMessage(), e );
            builder = Response.serverError();
        }

        return builder == null ? Response.serverError()
                                         .build() : builder.build();
    }

    @Path( "/list" )
    @Produces( "text/plain" )
    @GET
    public Response listGroupsToTxt()
    {
        ResponseBuilder builder = null;

        try
        {
            final List<Group> groups = dataManager.getAllGroupDefinitions();
            logger.info( "From dataManager: %s, got groups:\n\t%s\n\n", dataManager,
                         new org.commonjava.util.logging.helper.JoinString( "\n\t", groups ) );

            final StringBuilder sb = new StringBuilder();
            for ( final Group group : groups )
            {
                if ( sb.length() > 0 )
                {
                    sb.append( "\n" );
                }

                sb.append( group.getGroupId() )
                  .append( " => " )
                  .append( group.getCanonicalUrl() );
            }

            builder = Response.ok( sb.toString() );
        }
        catch ( final RouteMDataException e )
        {
            logger.error( e.getMessage(), e );
            builder = Response.serverError();
        }

        return builder == null ? Response.serverError()
                                         .build() : builder.build();
    }
}
