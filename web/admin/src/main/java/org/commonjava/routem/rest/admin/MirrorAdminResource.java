package org.commonjava.routem.rest.admin;

import java.net.URI;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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
import org.commonjava.routem.model.MirrorOf;
import org.commonjava.util.logging.Logger;
import org.commonjava.web.common.model.Listing;
import org.commonjava.web.common.ser.JsonSerializer;

import com.google.gson.reflect.TypeToken;

@Path( "/admin/mirror" )
@RequestScoped
@RequiresAuthentication
public class MirrorAdminResource
{

    private static final String CANONICAL_URL_QUERY_PARAM = "c";

    private static final String TARGET_URL_QUERY_PARAM = "t";

    private final Logger logger = new Logger( getClass() );

    @Inject
    private RouteDataManager dataManager;

    @Inject
    private JsonSerializer serializer;

    @POST
    @RequiresAuthentication
    public Response create( @Context final HttpServletRequest request, @Context final UriInfo uriInfo )
    {
        SecurityUtils.getSubject()
                     .isPermitted( Permission.name( MirrorOf.NAMESPACE, Permission.WILDCARD, Permission.ADMIN ) );

        ResponseBuilder builder = null;

        final String canonicalUrl = request.getParameter( CANONICAL_URL_QUERY_PARAM );
        final String targetUrl = request.getParameter( TARGET_URL_QUERY_PARAM );
        if ( canonicalUrl == null || targetUrl == null )
        {
            return Response.status( Status.BAD_REQUEST )
                           .entity( "You must provide a value for both the Canonical-URL request parameter: '"
                                        + CANONICAL_URL_QUERY_PARAM + "' AND the Target-URL request parameter: '"
                                        + TARGET_URL_QUERY_PARAM + "'." )
                           .build();
        }

        final MirrorOf mirror = new MirrorOf( canonicalUrl, targetUrl );
        try
        {
            final URI location = uriInfo.getAbsolutePathBuilder()
                                        .path( mirror.getMirrorId() )
                                        .build();

            if ( dataManager.store( mirror, false ) )
            {
                builder = Response.ok();
            }
            else
            {
                builder = Response.notModified();
            }

            builder.location( location );
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
                     .isPermitted( Permission.name( MirrorOf.NAMESPACE, Permission.WILDCARD, Permission.ADMIN ) );

        ResponseBuilder builder = null;

        @SuppressWarnings( "unchecked" )
        final MirrorOf mirror = serializer.fromRequestBody( request, MirrorOf.class );

        try
        {
            final URI location = uriInfo.getAbsolutePathBuilder()
                                        .path( mirror.getMirrorId() )
                                        .build();

            if ( dataManager.store( mirror, false ) )
            {
                builder = Response.ok();
            }
            else
            {
                builder = Response.notModified();
            }

            builder.location( location );
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
    public Response getFromQuery( @Context final HttpServletRequest request, @Context final UriInfo uriInfo )
    {
        ResponseBuilder builder = null;

        final String canonicalUrl = request.getParameter( CANONICAL_URL_QUERY_PARAM );
        final String targetUrl = request.getParameter( TARGET_URL_QUERY_PARAM );
        if ( canonicalUrl == null || targetUrl == null )
        {
            return Response.status( Status.BAD_REQUEST )
                           .entity( "You must provide a value for both the Canonical-URL request parameter: '"
                                        + CANONICAL_URL_QUERY_PARAM + "' AND the Target-URL request parameter: '"
                                        + TARGET_URL_QUERY_PARAM + "'." )
                           .build();
        }

        try
        {
            final MirrorOf mirror = dataManager.getMirror( canonicalUrl, targetUrl );
            if ( mirror == null )
            {
                builder = Response.status( Status.NOT_FOUND );
            }
            else
            {
                final URI location = uriInfo.getAbsolutePathBuilder()
                                            .path( mirror.getMirrorId() )
                                            .build();

                final String json = serializer.toString( mirror );
                builder = Response.ok( json )
                                  .location( location );
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
    @Path( "/{mirrorId}" )
    public Response get( @PathParam( "mirrorId" ) final String mirrorId, @Context final UriInfo uriInfo )
    {
        ResponseBuilder builder = null;

        try
        {
            final MirrorOf mirror = dataManager.getMirror( mirrorId );
            if ( mirror == null )
            {
                builder = Response.status( Status.NOT_FOUND );
            }
            else
            {
                final String json = serializer.toString( mirror );
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
    @Path( "/{mirrorId}" )
    @RequiresAuthentication
    public Response delete( @PathParam( "mirrorId" ) final String mirrorId )
    {
        SecurityUtils.getSubject()
                     .isPermitted( Permission.name( MirrorOf.NAMESPACE, Permission.WILDCARD, Permission.ADMIN ) );

        ResponseBuilder builder = null;

        try
        {
            dataManager.deleteMirror( mirrorId );
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
    public Response listToJson()
    {
        ResponseBuilder builder = null;

        try
        {
            final List<MirrorOf> mirrors = dataManager.getAllMirrorOfDefinitions();
            final TypeToken<Listing<MirrorOf>> tt = new TypeToken<Listing<MirrorOf>>()
            {
            };

            builder = Response.ok( serializer.toString( new Listing<MirrorOf>( mirrors ), tt.getType() ) );
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
    public Response listToTxt()
    {
        ResponseBuilder builder = null;

        try
        {
            final List<MirrorOf> mirrors = dataManager.getAllMirrorOfDefinitions();
            final StringBuilder sb = new StringBuilder();
            for ( final MirrorOf mirror : mirrors )
            {
                if ( sb.length() > 0 )
                {
                    sb.append( "\n" );
                }

                sb.append( mirror.getCanonicalUrl() )
                  .append( " --> " )
                  .append( mirror.getTargetUrl() );
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
