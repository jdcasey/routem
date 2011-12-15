package org.commonjava.routem.rest.live;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.apache.log4j.Level;
import org.commonjava.routem.model.Group;
import org.commonjava.routem.model.MirrorOf;
import org.commonjava.routem.model.RouteMReplicationData;
import org.commonjava.util.logging.Log4jUtil;
import org.commonjava.web.common.model.Listing;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.gson.reflect.TypeToken;

@RunWith( Arquillian.class )
public class ReplicatorPullResourceTest
    extends AbstractRouteMLiveTest
{

    public static void setupLogging()
    {
        Log4jUtil.configure( Level.DEBUG );
    }

    @Deployment
    public static WebArchive createWar()
    {
        return createWar( ReplicatorPullResourceTest.class );
    }

    @Test
    public void allReplicationDataMatchesDatabaseContents()
        throws Exception
    {
        final Group g = new Group( "org.apache.maven", "http://repo1.maven.apache.org/maven2/" );

        assertThat( dataManager.store( g ), equalTo( true ) );

        final MirrorOf m = new MirrorOf( g.getCanonicalUrl(), "http://mirrors.ibiblio.org/pub/mirrors/maven2/" );

        assertThat( dataManager.store( m ), equalTo( true ) );

        final RouteMReplicationData data = get( resourceUrl( "/replicator/pull/all" ), RouteMReplicationData.class );
        assertThat( data, notNullValue() );
        assertThat( data.getGroups(), notNullValue() );
        assertThat( data.getGroups()
                        .size(), equalTo( 1 ) );

        assertThat( data.getGroups()
                        .get( 0 ), equalTo( g ) );
        assertThat( data.getGroups()
                        .get( 0 )
                        .getCanonicalUrl(), equalTo( g.getCanonicalUrl() ) );

        assertThat( data.getMirrors(), notNullValue() );
        assertThat( data.getMirrors()
                        .size(), equalTo( 1 ) );

        assertThat( data.getMirrors()
                        .get( 0 ), equalTo( m ) );
    }

    @Test
    public void groupReplicationDataMatchesDatabaseContents()
        throws Exception
    {
        final Group g = new Group( "org.apache.maven", "http://repo1.maven.apache.org/maven2/" );

        assertThat( dataManager.store( g ), equalTo( true ) );

        final Listing<Group> groups =
            getListing( resourceUrl( "/replicator/pull/groups" ), new TypeToken<Listing<Group>>()
            {
            } );

        assertThat( groups, notNullValue() );
        assertThat( groups.getItems(), notNullValue() );
        assertThat( groups.getItems()
                          .size(), equalTo( 1 ) );
        assertThat( groups.getItems()
                          .get( 0 ), equalTo( g ) );
        assertThat( groups.getItems()
                          .get( 0 )
                          .getCanonicalUrl(), equalTo( g.getCanonicalUrl() ) );
    }

    @Test
    public void mirrorReplicationDataMatchesDatabaseContents()
        throws Exception
    {
        final MirrorOf m =
            new MirrorOf( "http://repo1.maven.apache.org/maven2/", "http://mirrors.ibiblio.org/pub/mirrors/maven2/" );

        assertThat( dataManager.store( m ), equalTo( true ) );

        final Listing<MirrorOf> mirrors =
            getListing( resourceUrl( "/replicator/pull/mirrors" ), new TypeToken<Listing<MirrorOf>>()
            {
            } );
        assertThat( mirrors, notNullValue() );
        assertThat( mirrors.getItems(), notNullValue() );
        assertThat( mirrors.getItems()
                           .size(), equalTo( 1 ) );

        assertThat( mirrors.getItems()
                           .get( 0 ), equalTo( m ) );
    }

}
