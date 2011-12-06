package org.commonjava.routem.rest.live.admin;

import static org.apache.commons.io.IOUtils.copy;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayOutputStream;

import org.apache.http.HttpResponse;
import org.commonjava.routem.model.Group;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith( Arquillian.class )
public class GroupAdminResourceTest
    extends AbstractRouteMLiveTest
{

    @Deployment
    public static WebArchive createWar()
    {
        return createWar( GroupAdminResourceTest.class );
    }

    @Test
    public void addOneGroupAndGetByGroupId()
        throws Exception
    {
        final Group g1 = new Group( "org.apache.maven", "http://repo1.maven.apache.org/maven2/" );

        assertThat( dataManager.store( g1 ), equalTo( true ) );

        final HttpResponse response =
            getWithResponse( resourceUrl( "/admin/group/", g1.getGroupId() ), 200, "application/json" );
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        copy( response.getEntity()
                      .getContent(), baos );

        final String result = new String( baos.toByteArray() );

        assertThat( result.contains( g1.getGroupId() ), equalTo( true ) );
        assertThat( result.contains( g1.getCanonicalUrl() ), equalTo( true ) );

        System.out.println( "Group listing:\n\n\n\n" + result + "\n\n\n\n" );
    }

    @Test
    public void addTwoGroupsAndGetPlainTextListingContainingBoth()
        throws Exception
    {
        final Group g1 = new Group( "org.apache.maven", "http://repo1.maven.apache.org/maven2/" );
        final Group g2 = new Group( "org.commonjava", "http://oss.sonatype.org/content/groups/public/" );

        assertThat( dataManager.store( g1 ), equalTo( true ) );
        assertThat( dataManager.store( g2 ), equalTo( true ) );

        final HttpResponse response = getWithResponse( resourceUrl( "/admin/group/list" ), 200, "text/plain" );
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        copy( response.getEntity()
                      .getContent(), baos );

        final String result = new String( baos.toByteArray() );

        assertThat( result.contains( g1.getGroupId() ), equalTo( true ) );
        assertThat( result.contains( g1.getCanonicalUrl() ), equalTo( true ) );
        assertThat( result.contains( g2.getGroupId() ), equalTo( true ) );
        assertThat( result.contains( g2.getCanonicalUrl() ), equalTo( true ) );

        System.out.println( "Group listing:\n\n\n\n" + result + "\n\n\n\n" );
    }

    @Test
    public void addTwoGroupsAndGetJsonListingContainingBoth()
        throws Exception
    {
        final Group g1 = new Group( "org.apache.maven", "http://repo1.maven.apache.org/maven2/" );
        final Group g2 = new Group( "org.commonjava", "http://oss.sonatype.org/content/groups/public/" );

        assertThat( dataManager.store( g1 ), equalTo( true ) );
        assertThat( dataManager.store( g2 ), equalTo( true ) );

        final HttpResponse response = getWithResponse( resourceUrl( "/admin/group/list" ), 200, "application/json" );
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        copy( response.getEntity()
                      .getContent(), baos );

        final String result = new String( baos.toByteArray() );

        assertThat( result.contains( g1.getGroupId() ), equalTo( true ) );
        assertThat( result.contains( g1.getCanonicalUrl() ), equalTo( true ) );
        assertThat( result.contains( g2.getGroupId() ), equalTo( true ) );
        assertThat( result.contains( g2.getCanonicalUrl() ), equalTo( true ) );

        System.out.println( "Group listing:\n\n\n\n" + result + "\n\n\n\n" );
    }

}
