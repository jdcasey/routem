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
package org.commonjava.routem.model;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.Set;

import org.commonjava.couch.io.Serializer;
import org.junit.Test;

// TODO: Test the pruning of groupId leaves, or refusal to add them if a parent groupId already exists.
public class RouteTest
{

    @Test
    public void addAndRetrieveTwoGroupIds()
    {
        final Route r = new Route( "http://www.repo.com/path/to/repo" );

        final String g1 = "org.apache.maven";
        final String g2 = "org.commonjava.routem";

        r.addGroupId( g1 );
        r.addGroupId( g2 );

        final Set<String> gids = r.getGroupIds();
        assertThat( gids, notNullValue() );
        assertThat( gids.size(), equalTo( 2 ) );
        assertThat( gids.contains( g1 ), equalTo( true ) );
        assertThat( gids.contains( g2 ), equalTo( true ) );
    }

    @Test
    public void addAndRemoveTwoGroupIds()
    {
        final Route r = new Route( "http://www.repo.com/path/to/repo" );

        final String g1 = "org.apache.maven";
        final String g2 = "org.commonjava.routem";

        r.addGroupId( g1 );
        r.addGroupId( g2 );

        Set<String> gids = r.getGroupIds();
        assertThat( gids, notNullValue() );
        assertThat( gids.size(), equalTo( 2 ) );
        assertThat( gids.contains( g1 ), equalTo( true ) );
        assertThat( gids.contains( g2 ), equalTo( true ) );

        r.removeGroupId( g2 );

        gids = r.getGroupIds();
        assertThat( gids, notNullValue() );
        assertThat( gids.size(), equalTo( 1 ) );
        assertThat( gids.contains( g1 ), equalTo( true ) );
        assertThat( gids.contains( g2 ), equalTo( false ) );

        r.removeGroupId( g1 );

        gids = r.getGroupIds();
        assertThat( gids, notNullValue() );
        assertThat( gids.size(), equalTo( 0 ) );
        assertThat( gids.contains( g1 ), equalTo( false ) );
        assertThat( gids.contains( g2 ), equalTo( false ) );
    }

    @Test
    public void addAndCheckHasTwoGroupIds()
    {
        final Route r = new Route( "http://www.repo.com/path/to/repo" );

        final String g1 = "org.apache.maven";
        final String g2 = "org.commonjava.routem";

        r.addGroupId( g1 );
        r.addGroupId( g2 );

        assertThat( r.hasGroupId( g1 ), equalTo( true ) );
        assertThat( r.hasGroupId( g2 ), equalTo( true ) );
    }

    @Test
    public void addAndRemoveTwoGroupIds_HasGroupIdReturnsFalse()
    {
        final Route r = new Route( "http://www.repo.com/path/to/repo" );

        final String g1 = "org.apache.maven";
        final String g2 = "org.commonjava.routem";

        r.addGroupId( g1 );
        r.addGroupId( g2 );

        assertThat( r.hasGroupId( g1 ), equalTo( true ) );
        assertThat( r.hasGroupId( g2 ), equalTo( true ) );

        r.removeGroupId( g2 );

        assertThat( r.hasGroupId( g1 ), equalTo( true ) );
        assertThat( r.hasGroupId( g2 ), equalTo( false ) );

        r.removeGroupId( g1 );

        assertThat( r.hasGroupId( g1 ), equalTo( false ) );
        assertThat( r.hasGroupId( g2 ), equalTo( false ) );
    }

    @Test
    public void addSuperGroupId_HasGroupIdTrueForChild()
    {
        final Route r = new Route( "http://www.repo.com/path/to/repo" );

        final String g1 = "org.apache.maven";
        r.addGroupId( g1 );

        assertThat( r.hasGroupId( g1 + ".scm" ), equalTo( true ) );
        assertThat( r.hasGroupId( "foo." + g1 ), equalTo( false ) );
    }

    @Test
    public void addParentAndChildGroupIds_GroupIdSetContainsOnlyParent()
    {
        final Route r = new Route( "http://www.repo.com/path/to/repo" );

        final String g1 = "org.apache.maven";
        assertThat( r.addGroupId( g1 ), equalTo( true ) );
        assertThat( r.addGroupId( g1 + ".scm" ), equalTo( false ) );

        final Set<String> gids = r.getGroupIds();
        assertThat( gids, notNullValue() );
        assertThat( gids.size(), equalTo( 1 ) );
        assertThat( gids.contains( g1 ), equalTo( true ) );
        assertThat( gids.contains( g1 + ".scm" ), equalTo( false ) );

        // covered by parent groupId.
        assertThat( r.hasGroupId( g1 ), equalTo( true ) );
        assertThat( r.hasGroupId( g1 + ".scm" ), equalTo( true ) );
    }

    @Test
    public void addWildcardGroupId_HasGroupIdTrueForAll()
    {
        final Route r = new Route( "http://www.repo.com/path/to/repo" );

        assertThat( r.addGroupId( Route.GROUP_ID_WILDCARD ), equalTo( true ) );

        final String g1 = "org.apache.maven";
        assertThat( r.hasGroupId( g1 + ".scm" ), equalTo( true ) );
        assertThat( r.hasGroupId( "foo." + g1 ), equalTo( true ) );
    }

    @Test
    public void addWildcardGroupId_GroupIdSetContainsOnlyParent()
    {
        final Route r = new Route( "http://www.repo.com/path/to/repo" );

        assertThat( r.addGroupId( Route.GROUP_ID_WILDCARD ), equalTo( true ) );

        final String g1 = "org.apache.maven";
        assertThat( r.addGroupId( g1 ), equalTo( false ) );
        assertThat( r.addGroupId( g1 + ".scm" ), equalTo( false ) );

        final Set<String> gids = r.getGroupIds();
        assertThat( gids, notNullValue() );
        assertThat( gids.size(), equalTo( 1 ) );

        assertThat( gids.contains( Route.GROUP_ID_WILDCARD ), equalTo( true ) );
        assertThat( gids.contains( g1 ), equalTo( false ) );
        assertThat( gids.contains( g1 + ".scm" ), equalTo( false ) );

        // covered by parent groupId.
        assertThat( r.hasGroupId( g1 ), equalTo( true ) );
        assertThat( r.hasGroupId( g1 + ".scm" ), equalTo( true ) );
    }

    @Test
    public void addWildcardGroupId_HasGroupIdTrueForAll_AfterRoundTrip()
    {
        Route r = new Route( "http://www.repo.com/path/to/repo" );

        final Serializer ser = new Serializer();
        final String str = ser.toString( r );
        r = ser.fromJson( str, Route.class );

        assertThat( r.addGroupId( Route.GROUP_ID_WILDCARD ), equalTo( true ) );

        final String g1 = "org.apache.maven";
        assertThat( r.hasGroupId( g1 + ".scm" ), equalTo( true ) );
        assertThat( r.hasGroupId( "foo." + g1 ), equalTo( true ) );
    }

    @Test
    public void addWildcardGroupId_GroupIdSetContainsOnlyParent_AfterRoundTrip()
    {
        Route r = new Route( "http://www.repo.com/path/to/repo" );

        assertThat( r.addGroupId( Route.GROUP_ID_WILDCARD ), equalTo( true ) );

        final Serializer ser = new Serializer();
        final String str = ser.toString( r );
        r = ser.fromJson( str, Route.class );

        final String g1 = "org.apache.maven";
        assertThat( r.addGroupId( g1 ), equalTo( false ) );
        assertThat( r.addGroupId( g1 + ".scm" ), equalTo( false ) );

        final Set<String> gids = r.getGroupIds();
        assertThat( gids, notNullValue() );
        assertThat( gids.size(), equalTo( 1 ) );

        assertThat( gids.contains( Route.GROUP_ID_WILDCARD ), equalTo( true ) );
        assertThat( gids.contains( g1 ), equalTo( false ) );
        assertThat( gids.contains( g1 + ".scm" ), equalTo( false ) );

        // covered by parent groupId.
        assertThat( r.hasGroupId( g1 ), equalTo( true ) );
        assertThat( r.hasGroupId( g1 + ".scm" ), equalTo( true ) );
    }

}
