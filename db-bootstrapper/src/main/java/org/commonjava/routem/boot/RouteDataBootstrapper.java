package org.commonjava.routem.boot;

import java.net.URI;

import org.commonjava.routem.data.RouteMDataException;

public interface RouteDataBootstrapper
{

    void populateDatabaseFrom( final URI uri )
        throws RouteMDataException;

    void populateDatabaseFrom( final URI groupsUri, final URI mirrorsUri )
        throws RouteMDataException;

}