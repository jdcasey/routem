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
package org.commonjava.routem;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.commonjava.routem.data.RouteDataManager;
import org.commonjava.routem.model.Group;
import org.commonjava.routem.model.MirrorOf;
import org.junit.Test;

public abstract class RouteDataManagerTCK
{

    protected abstract RouteDataManager getDataManager()
        throws Exception;

    protected RouteDataManagerTCK()
    {

    }

    @Test
    public void addOneGroupThenRetrieveItById()
        throws Exception
    {
        final String url = "http://www.somewhere.com/path/to/repo";
        final String id = "org.apache.maven";

        final Group g = new Group( id, url );

        assertThat( getDataManager().store( g ), equalTo( true ) );

        final Group result = getDataManager().getGroup( id );

        assertThat( result, notNullValue() );
        assertThat( result, equalTo( g ) );
    }

    @Test
    public void addThenDeleteOneGroup()
        throws Exception
    {
        final String url = "http://www.somewhere.com/path/to/repo";
        final String id = "org.apache.maven";

        final Group g = new Group( id, url );

        assertThat( getDataManager().store( g ), equalTo( true ) );

        Group result = getDataManager().getGroup( id );

        assertThat( result, notNullValue() );
        assertThat( result, equalTo( g ) );

        getDataManager().delete( g );

        result = getDataManager().getGroup( id );

        assertThat( result, nullValue() );
    }

    @Test
    public void addOneMirrorOfThenRetrieveItByUrls()
        throws Exception
    {
        final String curl = "http://repo1.maven.apache.org/maven2/";
        final String target = "http://repository.commonjava.org/";

        final MirrorOf m = new MirrorOf( curl, target );

        assertThat( getDataManager().store( m ), equalTo( true ) );

        final MirrorOf result = getDataManager().getMirror( curl, target );

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

        assertThat( getDataManager().store( m ), equalTo( true ) );

        MirrorOf result = getDataManager().getMirror( curl, target );

        assertThat( result, notNullValue() );
        assertThat( result.getCanonicalUrl(), equalTo( curl ) );
        assertThat( result.getTargetUrl(), equalTo( target ) );
        assertThat( result, equalTo( m ) );

        getDataManager().delete( m );

        result = getDataManager().getMirror( curl, target );

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

        assertThat( getDataManager().store( m1 ), equalTo( true ) );
        assertThat( getDataManager().store( m2 ), equalTo( true ) );
        assertThat( getDataManager().store( m3 ), equalTo( true ) );

        final List<MirrorOf> result = getDataManager().getMirrorComposition( t1 );

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

        assertThat( getDataManager().store( m1 ), equalTo( true ) );
        assertThat( getDataManager().store( m2 ), equalTo( true ) );
        assertThat( getDataManager().store( m3 ), equalTo( true ) );

        final List<MirrorOf> result = getDataManager().getMirrorsOfCanonicalUrl( c1 );

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

        assertThat( getDataManager().store( g1 ), equalTo( true ) );
        assertThat( getDataManager().store( g2 ), equalTo( true ) );
        assertThat( getDataManager().store( g3 ), equalTo( true ) );

        final List<Group> groups = getDataManager().getGroupsUsingCanonicalUrl( g2Url );

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

        assertThat( getDataManager().store( g1 ), equalTo( true ) );
        assertThat( getDataManager().store( g2 ), equalTo( true ) );

        final Group match = getDataManager().getGroup( childId );

        assertThat( match, notNullValue() );
        assertThat( match.getGroupId(), equalTo( g2Id ) );
    }

}
