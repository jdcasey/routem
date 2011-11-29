/*******************************************************************************
 * Copyright 2011 John Casey
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.commonjava.routem.data;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.apache.log4j.Level;
import org.cjtest.fixture.CouchFixture;
import org.commonjava.couch.test.fixture.LoggingFixture;
import org.commonjava.routem.model.Group;
import org.commonjava.routem.model.MirrorOf;
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
    public void addOneGroupThenRetrieveItById()
        throws RouteMDataException
    {
        final String url = "http://www.somewhere.com/path/to/repo";
        final String id = "org.apache.maven";

        final Group g = new Group( id, url );

        assertThat( dataManager.store( g ), equalTo( true ) );

        final Group result = dataManager.getGroup( id );

        assertThat( result, notNullValue() );
        assertThat( result, equalTo( g ) );
    }

    @Test
    public void addThenDeleteOneGroup()
        throws RouteMDataException
    {
        final String url = "http://www.somewhere.com/path/to/repo";
        final String id = "org.apache.maven";

        final Group g = new Group( id, url );

        assertThat( dataManager.store( g ), equalTo( true ) );

        Group result = dataManager.getGroup( id );

        assertThat( result, notNullValue() );
        assertThat( result, equalTo( g ) );

        dataManager.delete( g );

        result = dataManager.getGroup( id );

        assertThat( result, nullValue() );
    }

    @Test
    public void addOneMirrorOfThenRetrieveItByUrls()
        throws Exception
    {
        final String curl = "http://repo1.maven.apache.org/maven2/";
        final String target = "http://repository.commonjava.org/";

        final MirrorOf m = new MirrorOf( curl, target );

        assertThat( dataManager.store( m ), equalTo( true ) );

        final MirrorOf result = dataManager.getMirror( curl, target );

        assertThat( result, notNullValue() );
        assertThat( result.getCanonicalUrl(), equalTo( curl ) );
        assertThat( result.getTargetUrl(), equalTo( target ) );
        assertThat( result, equalTo( m ) );
    }

    @Test
    public void addThenDeleteOneMirrorOf()
        throws Exception
    {
        final String curl = "http://repo1.maven.apache.org/maven2/";
        final String target = "http://repository.commonjava.org/";

        final MirrorOf m = new MirrorOf( curl, target );

        assertThat( dataManager.store( m ), equalTo( true ) );

        MirrorOf result = dataManager.getMirror( curl, target );

        assertThat( result, notNullValue() );
        assertThat( result.getCanonicalUrl(), equalTo( curl ) );
        assertThat( result.getTargetUrl(), equalTo( target ) );
        assertThat( result, equalTo( m ) );

        dataManager.delete( m );

        result = dataManager.getMirror( curl, target );

        assertThat( result, nullValue() );
    }

    @Test
    public void storeThreeMirrorOfDefsAndRetrieveCompositionOfOneTargetContainingTwo()
        throws Exception
    {
        final String c1 = "http://repo1.maven.apache.org/maven2/";
        final String c2 = "http://repository.sonatype.org/content/groups/public/";
        final String c3 = "http://repository.jboss.org/nexus/content/groups/public/";

        final String t1 = "http://localhost:8080/repo/";
        final String t2 = "http://repository.mirrorhost.foo/";

        final MirrorOf m1 = new MirrorOf( c1, t1 );
        final MirrorOf m2 = new MirrorOf( c2, t2 );
        final MirrorOf m3 = new MirrorOf( c3, t1 );

        assertThat( dataManager.store( m1 ), equalTo( true ) );
        assertThat( dataManager.store( m2 ), equalTo( true ) );
        assertThat( dataManager.store( m3 ), equalTo( true ) );

        final List<MirrorOf> result = dataManager.getMirrorComposition( t1 );

        assertThat( result, notNullValue() );
        assertThat( result.size(), equalTo( 2 ) );
        assertThat( result.contains( m1 ), equalTo( true ) );
        assertThat( result.contains( m2 ), equalTo( false ) );
        assertThat( result.contains( m3 ), equalTo( true ) );
    }

    @Test
    public void storeThreeMirrorOfDefsAndRetrieveTwoMappedToOneCanonicalUrl()
        throws Exception
    {
        final String c1 = "http://repo1.maven.apache.org/maven2/";
        final String c2 = "http://repository.sonatype.org/content/groups/public/";

        final String t1 = "http://localhost:8080/repo/";
        final String t2 = "http://repository.mirrorhost.foo/";
        final String t3 = "http://http://mirrors.ibiblio.org/pub/mirrors/maven2/";

        final MirrorOf m1 = new MirrorOf( c1, t1 );
        final MirrorOf m2 = new MirrorOf( c2, t2 );
        final MirrorOf m3 = new MirrorOf( c1, t3 );

        assertThat( dataManager.store( m1 ), equalTo( true ) );
        assertThat( dataManager.store( m2 ), equalTo( true ) );
        assertThat( dataManager.store( m3 ), equalTo( true ) );

        final List<MirrorOf> result = dataManager.getMirrorsOfCanonicalUrl( c1 );

        assertThat( result, notNullValue() );
        assertThat( result.size(), equalTo( 2 ) );
        assertThat( result.contains( m1 ), equalTo( true ) );
        assertThat( result.contains( m2 ), equalTo( false ) );
        assertThat( result.contains( m3 ), equalTo( true ) );
    }

    @Test
    public void storeThreeGroupsWithTwoCanonicalUrlsAndRetrieveTwoByUrl()
        throws Exception
    {
        final String g1Url = "http://oss.sonatype.org/content/groups/public/";
        final String g2Url = "http://repo1.maven.apache.org/maven2/";

        final String g1Id = "org.commonjava";
        final String g2Id = "org.apache.maven";
        final String g3Id = "org.codehaus.mojo";

        final Group g1 = new Group( g1Id, g1Url );
        final Group g2 = new Group( g2Id, g2Url );
        final Group g3 = new Group( g3Id, g2Url );

        assertThat( dataManager.store( g1 ), equalTo( true ) );
        assertThat( dataManager.store( g2 ), equalTo( true ) );
        assertThat( dataManager.store( g3 ), equalTo( true ) );

        final List<Group> groups = dataManager.getGroupsUsingCanonicalUrl( g2Url );

        assertThat( groups, notNullValue() );
        assertThat( groups.size(), equalTo( 2 ) );
        assertThat( groups.contains( g1 ), equalTo( false ) );
        assertThat( groups.contains( g2 ), equalTo( true ) );
        assertThat( groups.contains( g3 ), equalTo( true ) );
    }

    @Test
    public void storeTwoNestedGroupsAndReturnLongestMatchForChild()
        throws Exception
    {
        final String g1Id = "org.commonjava";
        final String g2Id = g1Id + ".couch.web";
        final String childId = g2Id + ".aprox";

        final String url = "http://repo1.maven.apache.org/maven2/";

        final Group g1 = new Group( g1Id, url );
        final Group g2 = new Group( g2Id, url );

        assertThat( dataManager.store( g1 ), equalTo( true ) );
        assertThat( dataManager.store( g2 ), equalTo( true ) );

        final Group match = dataManager.getGroup( childId );

        assertThat( match, notNullValue() );
        assertThat( match.getGroupId(), equalTo( g2Id ) );
    }

}
