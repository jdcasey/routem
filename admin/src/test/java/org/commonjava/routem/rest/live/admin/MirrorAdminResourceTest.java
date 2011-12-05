package org.commonjava.routem.rest.live.admin;

import static org.apache.commons.io.IOUtils.copy;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayOutputStream;

import org.apache.http.HttpResponse;
import org.commonjava.routem.model.MirrorOf;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith( Arquillian.class )
public class MirrorAdminResourceTest
    extends AbstractRouteMLiveTest
{

    @Deployment
    public static WebArchive createWar()
    {
        return createWar( MirrorAdminResourceTest.class );
    }

    @Test
    public void addTwoMirrorsAndGetPlainTextListingContainingBoth()
        throws Exception
    {
        final MirrorOf m1 = new MirrorOf( "http://repo1.maven.apache.org/maven2/", "http://localhost:8080/my-mirror/" );
        final MirrorOf m2 =
            new MirrorOf( "http://oss.sonatype.org/content/groups/public/", "http://localhost:8080/my-mirror/" );

        assertThat( dataManager.store( m1 ), equalTo( true ) );
        assertThat( dataManager.store( m2 ), equalTo( true ) );

        final HttpResponse response = getWithResponse( resourceUrl( "/admin/mirror/list" ), 200, "text/plain" );
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        copy( response.getEntity()
                      .getContent(), baos );

        final String result = new String( baos.toByteArray() );

        assertThat( result.contains( m1.getCanonicalUrl() ), equalTo( true ) );
        assertThat( result.contains( m1.getTargetUrl() ), equalTo( true ) );
        assertThat( result.contains( m2.getCanonicalUrl() ), equalTo( true ) );
        assertThat( result.contains( m2.getTargetUrl() ), equalTo( true ) );

        System.out.println( "Group listing:\n\n\n\n" + result + "\n\n\n\n" );
    }

    @Test
    public void addTwoMirrorsAndGetJsonListingContainingBoth()
        throws Exception
    {
        final MirrorOf m1 = new MirrorOf( "http://repo1.maven.apache.org/maven2/", "http://localhost:8080/my-mirror/" );
        final MirrorOf m2 =
            new MirrorOf( "http://oss.sonatype.org/content/groups/public/", "http://localhost:8080/my-mirror/" );

        assertThat( dataManager.store( m1 ), equalTo( true ) );
        assertThat( dataManager.store( m2 ), equalTo( true ) );

        final HttpResponse response = getWithResponse( resourceUrl( "/admin/mirror/list" ), 200, "application/json" );
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        copy( response.getEntity()
                      .getContent(), baos );

        final String result = new String( baos.toByteArray() );

        assertThat( result.contains( m1.getCanonicalUrl() ), equalTo( true ) );
        assertThat( result.contains( m1.getTargetUrl() ), equalTo( true ) );
        assertThat( result.contains( m2.getCanonicalUrl() ), equalTo( true ) );
        assertThat( result.contains( m2.getTargetUrl() ), equalTo( true ) );

        System.out.println( "Group listing:\n\n\n\n" + result + "\n\n\n\n" );
    }

}
