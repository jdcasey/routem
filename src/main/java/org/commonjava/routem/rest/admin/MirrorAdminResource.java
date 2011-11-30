package org.commonjava.routem.rest.admin;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.commonjava.auth.couch.model.Permission;
import org.commonjava.routem.data.RouteDataManager;
import org.commonjava.routem.data.RouteMDataException;
import org.commonjava.routem.model.MirrorOf;
import org.commonjava.util.logging.Logger;
import org.commonjava.web.common.ser.JsonSerializer;

@Path( "/admin/mirror" )
@RequestScoped
@RequiresAuthentication
public class MirrorAdminResource
{

    private final Logger logger = new Logger( getClass() );

    @Inject
    private RouteDataManager dataManager;

    @Inject
    private JsonSerializer serializer;

    @Path( "/list" )
    @Produces( "application/json" )
    @GET
    public Response listGroupsToJson()
    {
        SecurityUtils.getSubject()
                     .isPermitted( Permission.name( MirrorOf.NAMESPACE, Permission.WILDCARD, Permission.READ ) );

        ResponseBuilder builder = null;

        try
        {
            final List<MirrorOf> mirrors = dataManager.getAllMirrorOfDefinitions();
            builder = Response.ok( serializer.toString( mirrors ) );
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
        SecurityUtils.getSubject()
                     .isPermitted( Permission.name( MirrorOf.NAMESPACE, Permission.WILDCARD, Permission.READ ) );

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
