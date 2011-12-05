package org.commonjava.routem.rest.live;

import static org.commonjava.couch.util.UrlUtils.buildUrl;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;

import javax.inject.Inject;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolException;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.protocol.HttpContext;
import org.apache.log4j.Level;
import org.commonjava.couch.test.fixture.LoggingFixture;
import org.commonjava.routem.data.RouteDataManager;
import org.commonjava.util.logging.Logger;
import org.commonjava.web.common.model.Listing;
import org.commonjava.web.common.ser.JsonSerializer;
import org.commonjava.web.test.fixture.TestWarArchiveBuilder;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.BeforeClass;

import com.google.gson.reflect.TypeToken;

public abstract class AbstractRouteMLiveTest
// extends AbstractRESTCouchTest
{

    @BeforeClass
    public static void logging()
    {
        LoggingFixture.setupLogging( Level.DEBUG );
    }

    protected static WebArchive createWar( final Class<?> cls )
    {
        return new TestWarArchiveBuilder( cls ).withExtraClasses( AbstractRouteMLiveTest.class )
                                               .withLog4jProperties()
                                               .withLibrariesIn( new File( "target/dependency" ) )
                                               .build();
    }

    // @Inject
    // @RouteMData
    // protected CouchManager couchManager;

    @Inject
    protected RouteDataManager dataManager;

    protected final Logger logger = new Logger( getClass() );

    @Before
    public final void setupTest()
        throws Exception
    {
        logger.info( "Installing DB using dataManager: %s", dataManager );
        dataManager.install();

        final ThreadSafeClientConnManager ccm = new ThreadSafeClientConnManager();
        ccm.setMaxTotal( 20 );

        http = new DefaultHttpClient( ccm );
    }

    protected static final String HOST = "localhost";

    protected static final int PORT = 8080;

    @Inject
    protected JsonSerializer serializer;

    protected DefaultHttpClient http;

    protected void disableRedirection()
    {
        http.setRedirectStrategy( new DefaultRedirectStrategy()
        {
            @Override
            public boolean isRedirected( final HttpRequest request, final HttpResponse response,
                                         final HttpContext context )
                throws ProtocolException
            {
                return false;
            }
        } );
    }

    protected void enableRedirection()
    {
        http.setRedirectStrategy( new DefaultRedirectStrategy() );
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
        final HttpGet get = new HttpGet( url );
        try
        {
            return http.execute( get, new ResponseHandler<T>()
            {
                @SuppressWarnings( "unchecked" )
                @Override
                public T handleResponse( final HttpResponse response )
                    throws ClientProtocolException, IOException
                {
                    final StatusLine sl = response.getStatusLine();
                    assertThat( sl.getStatusCode(), equalTo( HttpStatus.SC_OK ) );

                    return serializer.fromStream( response.getEntity()
                                                          .getContent(), "UTF-8", type );
                }
            } );
        }
        finally
        {
            get.abort();
        }
    }

    protected void get( final String url, final int expectedStatus )
        throws Exception
    {
        final HttpGet get = new HttpGet( url );
        try
        {
            http.execute( get, new ResponseHandler<Void>()
            {
                @Override
                public Void handleResponse( final HttpResponse response )
                    throws ClientProtocolException, IOException
                {
                    final StatusLine sl = response.getStatusLine();
                    assertThat( sl.getStatusCode(), equalTo( expectedStatus ) );

                    return null;
                }
            } );
        }
        finally
        {
            get.abort();
        }
    }

    protected HttpResponse getWithResponse( final String url, final int expectedStatus )
        throws Exception
    {
        final HttpGet get = new HttpGet( url );
        try
        {
            final HttpResponse response = http.execute( get );
            final StatusLine sl = response.getStatusLine();
            assertThat( sl.getStatusCode(), equalTo( expectedStatus ) );

            return response;
        }
        finally
        {
            get.abort();
        }
    }

    protected HttpResponse getWithResponse( final String url, final int expectedStatus, final String accept )
        throws Exception
    {
        final HttpGet get = new HttpGet( url );
        get.setHeader( "Accept", accept );

        try
        {
            final HttpResponse response = http.execute( get );
            final StatusLine sl = response.getStatusLine();
            assertThat( sl.getStatusCode(), equalTo( expectedStatus ) );

            return response;
        }
        finally
        {
            get.abort();
        }
    }

    protected <T> Listing<T> getListing( final String url, final TypeToken<Listing<T>> token )
        throws Exception
    {
        final HttpGet get = new HttpGet( url );
        try
        {
            return http.execute( get, new ResponseHandler<Listing<T>>()
            {
                @SuppressWarnings( "unchecked" )
                @Override
                public Listing<T> handleResponse( final HttpResponse response )
                    throws ClientProtocolException, IOException
                {
                    final StatusLine sl = response.getStatusLine();
                    assertThat( sl.getStatusCode(), equalTo( HttpStatus.SC_OK ) );

                    return serializer.listingFromStream( response.getEntity()
                                                                 .getContent(), "UTF-8", token );
                }
            } );
        }
        finally
        {
            get.abort();
        }
    }

    protected HttpResponse delete( final String url )
        throws Exception
    {
        final HttpDelete request = new HttpDelete( url );
        try
        {
            final HttpResponse response = http.execute( request );

            assertThat( response.getStatusLine()
                                .getStatusCode(), equalTo( HttpStatus.SC_OK ) );

            return response;
        }
        finally
        {
            request.abort();
        }
    }

    protected HttpResponse post( final String url, final Object value, final int status )
        throws Exception
    {
        final HttpPost request = new HttpPost( url );
        request.setEntity( new StringEntity( serializer.toString( value ), "application/json", "UTF-8" ) );

        try
        {
            final HttpResponse response = http.execute( request );

            assertThat( response.getStatusLine()
                                .getStatusCode(), equalTo( status ) );

            return response;
        }
        finally
        {
            request.abort();
        }
    }

    protected HttpResponse post( final String url, final Object value, final Type type, final int status )
        throws Exception
    {
        final HttpPost request = new HttpPost( url );
        request.setEntity( new StringEntity( serializer.toString( value, type ), "application/json", "UTF-8" ) );

        try
        {
            final HttpResponse response = http.execute( request );

            assertThat( response.getStatusLine()
                                .getStatusCode(), equalTo( status ) );

            return response;
        }
        finally
        {
            request.abort();
        }
    }

    protected String resourceUrl( final String path )
        throws MalformedURLException
    {
        return buildUrl( "http://localhost:8080/test/api/", apiVersion(), path );
    }

    protected String apiVersion()
    {
        return "1.0";
    }

}
