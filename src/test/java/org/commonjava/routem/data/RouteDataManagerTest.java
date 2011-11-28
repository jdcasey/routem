package org.commonjava.routem.data;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.apache.log4j.Level;
import org.cjtest.fixture.CouchFixture;
import org.commonjava.couch.test.fixture.LoggingFixture;
import org.commonjava.routem.model.Route;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

public class RouteDataManagerTest
{

    private static final String DB_URL = "http://localhost:5984/test-routem";

    private RouteDataManager dataManager;

    @Rule
    public CouchFixture fixture = new CouchFixture( DB_URL );

    @BeforeClass
    public static void logging()
    {
        LoggingFixture.setupLogging( Level.DEBUG );
    }

    @Before
    public void setup()
        throws Exception
    {
        dataManager = new RouteDataManager( fixture.getCouchManager(), fixture.getCouchConfig() );
        dataManager.install();
    }

    @Test
    public void addOneRouteThenRetrieveItByUrl()
        throws RouteMDataException
    {
        final String url = "http://www.somewhere.com/path/to/repo";
        final Route r = new Route( url );

        assertThat( dataManager.storeRoute( r ), equalTo( true ) );

        final Route result = dataManager.getRoute( url );

        assertThat( result, notNullValue() );
        assertThat( result, equalTo( r ) );
    }

    @Test
    public void addOneRouteWithGroupIdThenRetrieveItByGroupId()
        throws RouteMDataException
    {
        final String url = "http://www.somewhere.com/path/to/repo";
        final Route r = new Route( url );

        final String gid = "org.apache.maven";
        r.addGroupId( gid );

        assertThat( dataManager.storeRoute( r ), equalTo( true ) );

        final List<Route> routes = dataManager.getRoutesContainingGroupId( gid );
        assertThat( routes, notNullValue() );
        assertThat( routes.size(), equalTo( 1 ) );

        final Route result = routes.get( 0 );

        assertThat( result, notNullValue() );
        assertThat( result, equalTo( r ) );
    }

    @Test
    // FIXME
    public void addOneRouteWithGroupIdThenRetrieveItByChildGroupId()
        throws RouteMDataException
    {
        final String url = "http://www.somewhere.com/path/to/repo";
        final Route r = new Route( url );

        final String gid = "org.apache.maven";
        r.addGroupId( gid );

        assertThat( dataManager.storeRoute( r ), equalTo( true ) );

        final List<Route> routes = dataManager.getRoutesContainingGroupId( gid + ".scm" );
        assertThat( routes, notNullValue() );
        assertThat( routes.size(), equalTo( 1 ) );

        final Route result = routes.get( 0 );

        assertThat( result, notNullValue() );
        assertThat( result, equalTo( r ) );
    }

}
