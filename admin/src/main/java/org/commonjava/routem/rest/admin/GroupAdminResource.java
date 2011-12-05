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
import org.commonjava.couch.rbac.Permission;
import org.commonjava.routem.data.RouteDataManager;
import org.commonjava.routem.data.RouteMDataException;
import org.commonjava.routem.model.Group;
import org.commonjava.util.logging.Logger;
import org.commonjava.web.common.model.Listing;
import org.commonjava.web.common.ser.JsonSerializer;

import com.google.gson.reflect.TypeToken;

@Path( "/admin/group" )
@RequestScoped
@RequiresAuthentication
public class GroupAdminResource
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
                     .isPermitted( Permission.name( Group.NAMESPACE, Permission.WILDCARD, Permission.READ ) );

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
        SecurityUtils.getSubject()
                     .isPermitted( Permission.name( Group.NAMESPACE, Permission.WILDCARD, Permission.READ ) );

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
