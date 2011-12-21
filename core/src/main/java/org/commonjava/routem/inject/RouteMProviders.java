package org.commonjava.routem.inject;

import javax.inject.Singleton;

import org.commonjava.routem.data.RouteDataManager;

@Singleton
public class RouteMProviders
{

    // @Any
    // @Alternative
    // @Inject
    private RouteDataManager dataManager;

    RouteMProviders()
    {
    }

    // @Produces
    // @Default
    public RouteDataManager dataManager()
    {
        return dataManager;
    }

}
