package org.commonjava.routem.route;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.apache.log4j.Level;
import org.commonjava.routem.data.RouteDataManager;
import org.commonjava.routem.data.mem.MemoryRouteDataManager;
import org.commonjava.routem.model.Group;
import org.commonjava.routem.model.MirrorOf;
import org.commonjava.util.logging.Log4jUtil;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class RedirectoryTest
{

    private RouteDataManager dataManager;

    private Redirectory redirectory;

    @BeforeClass
    public static void setupClass()
    {
        Log4jUtil.configure( Level.DEBUG );
    }

    @Before
    public void setup()
    {
        dataManager = new MemoryRouteDataManager();
        redirectory = new Redirectory( dataManager );
    }

    @Test
    public void pomRequestIsRedirectedToMirror()
        throws Exception
    {
        final Group g = new Group( "org.apache.maven", "http://repo1.maven.apache.org/maven2/" );

        assertThat( dataManager.store( g ), equalTo( true ) );

        final MirrorOf m = new MirrorOf( g.getCanonicalUrl(), "http://mirrors.ibiblio.org/pub/mirrors/maven2/" );

        assertThat( dataManager.store( m ), equalTo( true ) );

        final String path = "org/apache/maven/maven/3.0.3/maven-3.0.3.pom";

        final String url = redirectory.selectRoute( path );

        assertThat( url, equalTo( m.getTargetUrl() + path ) );
    }

    @Test
    public void metadataRequestIsRedirectedToMirror()
        throws Exception
    {
        final Group g = new Group( "org.apache.maven", "http://repo1.maven.apache.org/maven2/" );

        assertThat( dataManager.store( g ), equalTo( true ) );

        final MirrorOf m = new MirrorOf( g.getCanonicalUrl(), "http://mirrors.ibiblio.org/pub/mirrors/maven2/" );

        assertThat( dataManager.store( m ), equalTo( true ) );

        final String path = "org/apache/maven/maven/3.0.3/maven-metadata.xml";

        final String url = redirectory.selectRoute( path );

        assertThat( url, equalTo( m.getTargetUrl() + path ) );
    }

}
