package org.commonjava.routem.rest.live;

import java.io.File;

import javax.inject.Inject;

import org.apache.log4j.Level;
import org.cjtest.fixture.TestAuthenticationControls;
import org.commonjava.couch.db.CouchManager;
import org.commonjava.couch.test.fixture.LoggingFixture;
import org.commonjava.couch.user.web.test.AbstractUserRESTCouchTest;
import org.commonjava.routem.data.RouteDataManager;
import org.commonjava.routem.inject.RouteMData;
import org.commonjava.routem.rest.live.fixture.TestRouteMConfigFactory;
import org.commonjava.routem.rest.live.fixture.TestUserManagerConfigProducer;
import org.commonjava.web.test.fixture.TestWarArchiveBuilder;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.BeforeClass;

public abstract class AbstractRouteMLiveTest
    extends AbstractUserRESTCouchTest
{

    @BeforeClass
    public static void logging()
    {
        LoggingFixture.setupLogging( Level.DEBUG );
    }

    protected static WebArchive createWar( final Class<?> cls )
    {
        return new TestWarArchiveBuilder( cls ).withExtraClasses( TestRouteMConfigFactory.class,
                                                                  TestUserManagerConfigProducer.class,
                                                                  AbstractRouteMLiveTest.class )
                                               .withLog4jProperties()
                                               .withLibrariesIn( new File( "target/dependency" ) )
                                               .build();
    }

    @Inject
    @RouteMData
    protected CouchManager couchManager;

    @Inject
    protected RouteDataManager dataManager;

    @Inject
    protected TestAuthenticationControls authControls;

    @Before
    public void install()
        throws Exception
    {
        dataManager.install();
        authControls.setDoAuthentication( isDoAuthentication() );
    }

    protected abstract boolean isDoAuthentication();

    @Override
    protected CouchManager getCouchManager()
    {
        return couchManager;
    }

}
