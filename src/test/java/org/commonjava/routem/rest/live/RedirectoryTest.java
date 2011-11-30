package org.commonjava.routem.rest.live;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.File;

import javax.inject.Inject;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.log4j.Level;
import org.commonjava.couch.db.CouchManager;
import org.commonjava.couch.test.fixture.LoggingFixture;
import org.commonjava.routem.data.RouteDataManager;
import org.commonjava.routem.inject.RouteMData;
import org.commonjava.routem.model.Group;
import org.commonjava.routem.model.MirrorOf;
import org.commonjava.routem.rest.live.fixture.TestRouteMConfigFactory;
import org.commonjava.web.test.AbstractRESTCouchTest;
import org.commonjava.web.test.fixture.TestWarArchiveBuilder;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith( Arquillian.class )
public class RedirectoryTest
    extends AbstractRESTCouchTest
{

    @BeforeClass
    public static void logging()
    {
        LoggingFixture.setupLogging( Level.DEBUG );
    }

    @Deployment
    public static WebArchive createWar()
    {
        return new TestWarArchiveBuilder( RedirectoryTest.class ).withExtraClasses( TestRouteMConfigFactory.class )
                                                                 .withLog4jProperties()
                                                                 .withLibrariesIn( new File( "target/dependency" ) )
                                                                 .build();
    }

    @Inject
    private RouteDataManager dataManager;

    @Before
    public void install()
        throws Exception
    {
        dataManager.install();
    }

    @Inject
    @RouteMData
    private CouchManager couchManager;

    @Test
    public void pomRequestIsRedirectedToMirror()
        throws Exception
    {
        disableRedirection();

        final Group g = new Group( "org.apache.maven", "http://repo1.maven.apache.org/maven2/" );

        assertThat( dataManager.store( g ), equalTo( true ) );

        final MirrorOf m = new MirrorOf( g.getCanonicalUrl(), "http://mirrors.ibiblio.org/pub/mirrors/maven2/" );

        assertThat( dataManager.store( m ), equalTo( true ) );

        final String path = "org/apache/maven/maven/3.0.3/maven-3.0.3.pom";

        final HttpResponse response = getWithResponse( "http://localhost:8080/test/api/1.0/redirectory/" + path, 307 );

        final Header[] headers = response.getHeaders( "Location" );
        assertThat( headers, notNullValue() );
        assertThat( headers.length, equalTo( 1 ) );
        assertThat( headers[0].getValue(), equalTo( "http://mirrors.ibiblio.org/pub/mirrors/maven2/" + path ) );

    }

    @Test
    public void metadataRequestIsRedirectedToMirror()
        throws Exception
    {
        disableRedirection();

        final Group g = new Group( "org.apache.maven", "http://repo1.maven.apache.org/maven2/" );

        assertThat( dataManager.store( g ), equalTo( true ) );

        final MirrorOf m = new MirrorOf( g.getCanonicalUrl(), "http://mirrors.ibiblio.org/pub/mirrors/maven2/" );

        assertThat( dataManager.store( m ), equalTo( true ) );

        final String path = "org/apache/maven/maven/3.0.3/maven-metadata.xml";

        final HttpResponse response = getWithResponse( "http://localhost:8080/test/api/1.0/redirectory/" + path, 307 );

        final Header[] headers = response.getHeaders( "Location" );
        assertThat( headers, notNullValue() );
        assertThat( headers.length, equalTo( 1 ) );
        assertThat( headers[0].getValue(), equalTo( "http://mirrors.ibiblio.org/pub/mirrors/maven2/" + path ) );

    }

    @Override
    protected CouchManager getCouchManager()
    {
        return couchManager;
    }

}
