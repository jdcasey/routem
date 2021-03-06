package org.commonjava.routem.rest.live.admin;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.lang.reflect.Type;
import java.net.MalformedURLException;

import javax.inject.Inject;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.log4j.Level;
import org.cjtest.fixture.TestAuthenticationControls;
import org.commonjava.auth.couch.conf.UserManagerConfiguration;
import org.commonjava.auth.couch.data.UserDataManager;
import org.commonjava.auth.shiro.couch.CouchRealm;
import org.commonjava.couch.test.fixture.LoggingFixture;
import org.commonjava.routem.data.RouteDataManager;
import org.commonjava.routem.rest.live.fixture.TestRouteMFactory;
import org.commonjava.util.logging.Logger;
import org.commonjava.web.json.model.Listing;
import org.commonjava.web.json.ser.JsonSerializer;
import org.commonjava.web.json.test.WebFixture;
import org.commonjava.web.test.fixture.TestWarArchiveBuilder;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;

import com.google.gson.reflect.TypeToken;

public abstract class AbstractRouteMLiveTest
// extends AbstractUserRESTCouchTest
{

    @BeforeClass
    public static void logging()
    {
        LoggingFixture.setupLogging( Level.DEBUG );
    }

    protected static WebArchive createWar( final Class<?> cls )
    {
        return new TestWarArchiveBuilder( cls ).withExtraClasses( TestRouteMFactory.class, AbstractRouteMLiveTest.class )
                                               .withLog4jProperties()
                                               .withLibrariesIn( new File( "target/dependency" ) )
                                               .build();
    }

    // @Inject
    // @RouteMData
    // protected CouchManager couchManager;

    @Inject
    protected RouteDataManager dataManager;

    @Inject
    protected TestAuthenticationControls authControls;

    protected final Logger logger = new Logger( getClass() );

    @Before
    public void install()
        throws Exception
    {
        logger.info( "Test is using dataManager: %s", dataManager );
        dataManager.install();
        authControls.setDoAuthentication( false );
    }

    protected static final String HOST = "localhost";

    protected static final int PORT = 8080;

    @Inject
    protected JsonSerializer serializer;

    @Inject
    protected UserDataManager userManager;

    @Inject
    protected UserManagerConfiguration config;

    @Inject
    protected CouchRealm realm;

    @Rule
    public WebFixture http = new WebFixture();

    protected void disableRedirection()
    {
        http.disableRedirection();
    }

    protected void enableRedirection()
    {
        http.enableRedirection();
    }

    @Before
    public final void setupRESTCouchTest()
        throws Exception
    {
        userManager.install();
        userManager.setupAdminInformation();

        // setup the security manager.
        realm.setupSecurityManager();
    }

    protected void assertLocationHeader( final HttpResponse response, final String value )
    {
        final Header[] headers = response.getHeaders( "Location" );
        assertThat( headers, notNullValue() );
        assertThat( headers.length, equalTo( 1 ) );

        final String header = headers[0].getValue();
        assertThat( header, equalTo( value ) );
    }

    protected <T> T get( final String url, final Class<T> type )
        throws Exception
    {
        return http.get( url, type );
    }

    protected void get( final String url, final int expectedStatus )
        throws Exception
    {
        http.get( url, expectedStatus );
    }

    protected HttpResponse getWithResponse( final String url, final int expectedStatus )
        throws Exception
    {
        return http.getWithResponse( url, expectedStatus );
    }

    protected HttpResponse getWithResponse( final String url, final int expectedStatus, final String accept )
        throws Exception
    {
        return http.getWithResponse( url, expectedStatus, accept );
    }

    protected <T> Listing<T> getListing( final String url, final TypeToken<Listing<T>> token )
        throws Exception
    {
        return http.getListing( url, token );
    }

    protected HttpResponse delete( final String url )
        throws Exception
    {
        return http.delete( url );
    }

    protected HttpResponse post( final String url, final Object value, final int status )
        throws Exception
    {
        return http.post( url, value, status );
    }

    protected HttpResponse post( final String url, final Object value, final Type type, final int status )
        throws Exception
    {
        return http.post( url, value, type, status );
    }

    protected String resourceUrl( final String... path )
        throws MalformedURLException
    {
        return http.resourceUrl( path );
    }

}
