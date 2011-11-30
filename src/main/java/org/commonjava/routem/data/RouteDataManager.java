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

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.commonjava.auth.couch.conf.UserManagerConfiguration;
import org.commonjava.auth.couch.data.UserDataException;
import org.commonjava.auth.couch.data.UserDataManager;
import org.commonjava.couch.conf.CouchDBConfiguration;
import org.commonjava.couch.db.CouchDBException;
import org.commonjava.couch.db.CouchManager;
import org.commonjava.couch.model.CouchApp;
import org.commonjava.routem.data.RouteMAppDescription.View;
import org.commonjava.routem.inject.RouteMData;
import org.commonjava.routem.model.Group;
import org.commonjava.routem.model.MirrorOf;
import org.commonjava.util.logging.Logger;

@Singleton
public class RouteDataManager
{

    private final Logger logger = new Logger( getClass() );

    @Inject
    @RouteMData
    private CouchManager couch;

    @Inject
    @RouteMData
    private CouchDBConfiguration couchConfig;

    @Inject
    private UserDataManager userDataManager;

    @Inject
    private UserManagerConfiguration userConfig;

    RouteDataManager()
    {
    }

    public RouteDataManager( final CouchManager couch, final CouchDBConfiguration config,
                             final UserDataManager userDataManager, final UserManagerConfiguration userConfig )
    {
        this.couch = couch;
        this.couchConfig = config;
        this.userDataManager = userDataManager;
        this.userConfig = userConfig;
    }

    public void install()
        throws RouteMDataException
    {
        final RouteMAppDescription description = new RouteMAppDescription();
        final CouchApp app = new CouchApp( RouteMAppDescription.APP_NAME, description );

        try
        {
            if ( couch.dbExists() )
            {
                // static in Couch, so safe to forcibly reload.
                couch.delete( app );
            }

            couch.initialize( description );
        }
        catch ( final CouchDBException e )
        {
            throw new RouteMDataException( "Failed to initialize routing database: %s (application: %s). Reason: %s",
                                           e, couchConfig.getDatabaseUrl(), description.getAppName(), e.getMessage() );
        }

        try
        {
            userDataManager.install();
            userDataManager.setupAdminInformation();
        }
        catch ( final UserDataException e )
        {
            throw new RouteMDataException( "Failed to initialize routing-admin user database: %s. Reason: %s", e,
                                           userConfig.getDatabaseConfig()
                                                     .getDatabaseUrl(), e.getMessage() );
        }
    }

    public Group getGroup( final String groupId )
        throws RouteMDataException
    {
        try
        {
            final List<Group> results = couch.getDocuments( Group.class, true, GroupRef.generateKeys( groupId ) );
            if ( results == null || results.isEmpty() )
            {
                return null;
            }
            else if ( results.size() == 1 )
            {
                return results.get( 0 );
            }
            else
            {
                Collections.sort( results );
                return results.get( 0 );
            }
        }
        catch ( final CouchDBException e )
        {
            throw new RouteMDataException( "Failed to retrieve group: %s. Reason: %s", e, groupId, e.getMessage() );
        }
    }

    public List<Group> getGroupsUsingCanonicalUrl( final String canonicalUrl )
        throws RouteMDataException
    {
        try
        {
            return couch.getViewListing( new RouteMViewRequest( View.GROUPS_USING_CANONICAL_URL, canonicalUrl ),
                                         Group.class );
        }
        catch ( final CouchDBException e )
        {
            throw new RouteMDataException(
                                           "Failed to retrieve listing of group definitions using canonical URL: %s. Reason: %s",
                                           e, canonicalUrl, e.getMessage() );
        }
    }

    // TODO: Wildcard support!
    public List<MirrorOf> getMirrorsOfGroup( final String groupId )
        throws RouteMDataException
    {
        final Group group = getGroup( groupId );
        if ( group == null )
        {
            logger.warn( "Group: %s not found; cannot retrieve list of mirrors.", groupId );
            return Collections.emptyList();
        }

        try
        {
            logger.warn( "Retrieving mirrors of group canonical URL: %s.", group.getCanonicalUrl() );
            return couch.getViewListing( new RouteMViewRequest( View.MIRRORS_OF_CANONICAL_URL, group.getCanonicalUrl() ),
                                         MirrorOf.class );
        }
        catch ( final CouchDBException e )
        {
            throw new RouteMDataException(
                                           "Failed to retrieve route mirror for canonical URL: %s of group: %s. Reason: %s",
                                           e, group.getCanonicalUrl(), groupId, e.getMessage() );
        }
    }

    public MirrorOf getMirror( final String canonicalUrl, final String targetUrl )
        throws RouteMDataException
    {
        try
        {
            return couch.getDocument( new MirrorOfRef( canonicalUrl, targetUrl ), MirrorOf.class );
        }
        catch ( final CouchDBException e )
        {
            throw new RouteMDataException(
                                           "Failed to retrieve mirror-of entry for canonical URL: %s, target URL: %s. Reason: %s",
                                           e, canonicalUrl, targetUrl, e.getMessage() );
        }
    }

    // TODO: Wildcard support!
    public List<MirrorOf> getMirrorsOfCanonicalUrl( final String canonicalUrl )
        throws RouteMDataException
    {
        try
        {
            return couch.getViewListing( new RouteMViewRequest( View.MIRRORS_OF_CANONICAL_URL, canonicalUrl ),
                                         MirrorOf.class );
        }
        catch ( final CouchDBException e )
        {
            throw new RouteMDataException( "Failed to retrieve mirrors of canonical URL: %s. Reason: %s", e,
                                           canonicalUrl, e.getMessage() );
        }
    }

    public List<MirrorOf> getMirrorComposition( final String targetUrl )
        throws RouteMDataException
    {
        try
        {
            return couch.getViewListing( new RouteMViewRequest( View.MIRROR_COMPOSITION, targetUrl ), MirrorOf.class );
        }
        catch ( final CouchDBException e )
        {
            throw new RouteMDataException( "Failed to retrieve mirror composition for target URL: %s. Reason: %s", e,
                                           targetUrl, e.getMessage() );
        }
    }

    public boolean store( final MirrorOf mirrorOf )
        throws RouteMDataException
    {
        return store( mirrorOf, false );
    }

    public boolean store( final MirrorOf mirrorOf, final boolean skipIfExists )
        throws RouteMDataException
    {
        try
        {
            return couch.store( mirrorOf, skipIfExists );
        }
        catch ( final CouchDBException e )
        {
            throw new RouteMDataException( "Failed to store mirror-of mapping: %s. Reason: %s", e, mirrorOf,
                                           e.getMessage() );
        }
    }

    public boolean store( final Group group )
        throws RouteMDataException
    {
        return store( group, false );
    }

    public boolean store( final Group group, final boolean skipIfExists )
        throws RouteMDataException
    {
        try
        {
            return couch.store( group, skipIfExists );
        }
        catch ( final CouchDBException e )
        {
            throw new RouteMDataException( "Failed to store group definition: %s. Reason: %s", e, group, e.getMessage() );
        }
    }

    public void delete( final MirrorOf mirrorOf )
        throws RouteMDataException
    {
        try
        {
            couch.delete( mirrorOf );
        }
        catch ( final CouchDBException e )
        {
            throw new RouteMDataException( "Failed to delete mirror-of mapping: %s. Reason: %s", e, mirrorOf,
                                           e.getMessage() );
        }
    }

    public void delete( final Group group )
        throws RouteMDataException
    {
        try
        {
            couch.delete( group );
        }
        catch ( final CouchDBException e )
        {
            throw new RouteMDataException( "Failed to delete group definition: %s. Reason: %s", e, group,
                                           e.getMessage() );
        }
    }

    public List<Group> getAllGroupDefinitions()
        throws RouteMDataException
    {
        try
        {
            return couch.getViewListing( new RouteMViewRequest( View.ALL_GROUPS ), Group.class );
        }
        catch ( final CouchDBException e )
        {
            throw new RouteMDataException( "Failed to retrieve full listing of group definitions. Reason: %s", e,
                                           e.getMessage() );
        }
    }

    public List<MirrorOf> getAllMirrorOfDefinitions()
        throws RouteMDataException
    {
        try
        {
            return couch.getViewListing( new RouteMViewRequest( View.ALL_MIRROR_OFS ), MirrorOf.class );
        }
        catch ( final CouchDBException e )
        {
            throw new RouteMDataException( "Failed to retrieve full listing of mirror-of definitions. Reason: %s", e,
                                           e.getMessage() );
        }
    }

}
