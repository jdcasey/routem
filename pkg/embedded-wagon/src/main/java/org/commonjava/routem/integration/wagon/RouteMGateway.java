package org.commonjava.routem.integration.wagon;

import java.io.File;
import java.net.URI;

import org.commonjava.routem.boot.HttpBootstrapper;
import org.commonjava.routem.data.RouteDataManager;
import org.commonjava.routem.data.RouteMDataException;
import org.commonjava.routem.data.flat.FlatRouteDataManager;
import org.commonjava.routem.route.PickFirstMirrorSelector;
import org.commonjava.routem.route.Redirectory;
import org.commonjava.web.common.ser.JsonSerializer;

public class RouteMGateway
{

    private static final File ROUTEM_CONFIGURATION_DIR = new File( System.getProperty( "user.home" ),
                                                                   ".m2/routem/main.conf" );

    public static final String ROUTEM_UPDATE_PROP = "routem.update";

    private final Redirectory redirectory;

    private final FlatRouteDataManager dataManager;

    public RouteMGateway( final String routerId, final URI router )
        throws RouteMDataException
    {
        ROUTEM_CONFIGURATION_DIR.mkdirs();

        final File db = new File( ROUTEM_CONFIGURATION_DIR, routerId + ".json" );
        dataManager = new FlatRouteDataManager( db );

        final JsonSerializer serializer = new JsonSerializer();

        final boolean forceUpdate = Boolean.parseBoolean( System.getProperty( ROUTEM_UPDATE_PROP, "false" ) );
        if ( forceUpdate || db.exists() )
        {
            dataManager.install();

            final HttpBootstrapper bootstrapper = new HttpBootstrapper( dataManager, serializer );
            bootstrapper.populateDatabaseFrom( router );
            dataManager.sync();
        }

        // TODO: Find a way to replace this mirror selector!
        redirectory = new Redirectory( dataManager, new PickFirstMirrorSelector() );
    }

    public Redirectory getRedirectory()
    {
        return redirectory;
    }

    public RouteDataManager getDataManager()
    {
        return dataManager;
    }

}
