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
package org.commonjava.routem.route;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.commonjava.routem.data.RouteDataManager;
import org.commonjava.routem.data.RouteMDataException;
import org.commonjava.routem.model.Group;
import org.commonjava.routem.model.MirrorOf;
import org.commonjava.util.logging.Logger;
import org.commonjava.util.logging.helper.JoinString;

@Singleton
public final class Redirectory
{

    private final Logger logger = new Logger( getClass() );

    @Inject
    private RouteDataManager dataManager;

    @Inject
    private MirrorSelector mirrorSelector;

    Redirectory()
    {
    }

    public Redirectory( final RouteDataManager dataManager, final MirrorSelector mirrorSelector )
    {
        this.dataManager = dataManager;
        this.mirrorSelector = mirrorSelector;
    }

    public String findGroupId( final String path )
        throws RouteMDataException
    {
        if ( path.endsWith( "/" ) )
        {
            return null;
        }

        File file = new File( path );
        File version = file.getParentFile();

        if ( file.getName()
                 .indexOf( '.' ) < 0 )
        {
            version = file;
            file = null;
        }

        final File artifactId = version == null ? null : version.getParentFile();
        final File groupId = artifactId == null ? null : artifactId.getParentFile();

        final File[] checks = { groupId, artifactId, version, file };

        for ( final File check : checks )
        {
            if ( check == null )
            {
                continue;
            }

            String gid = check.getPath()
                              .replace( '/', '.' );

            if ( gid.length() < 1 || gid.equals( "." ) )
            {
                return null;
            }

            if ( gid.startsWith( "." ) )
            {
                gid = gid.substring( 1 );
            }

            final Group group = dataManager.getGroup( gid );
            if ( group != null )
            {
                return gid;
            }
        }

        if ( dataManager.getGroup( Group.WILDCARD ) != null )
        {
            return Group.WILDCARD;
        }

        return null;
    }

    public String selectRoute( final String path )
        throws RouteMDataException
    {
        final String groupId = findGroupId( path );
        if ( groupId == null )
        {
            return null;
        }

        logger.debug( "Lookup using data manager: %s", dataManager );
        final List<MirrorOf> mirrors = dataManager.getMirrorsOfGroup( groupId );

        logger.debug( "Got mirrors:\n\t%s", new JoinString( "\n\t", mirrors ) );
        final MirrorOf mirror = selectMirror( mirrors );
        if ( mirror == null )
        {
            return null;
        }

        try
        {
            return buildUrl( mirror.getTargetUrl(), path );
        }
        catch ( final MalformedURLException e )
        {
            throw new RouteMDataException(
                                           "Cannot construct a valid URL from mirror target: '%s' and path: '%s'. Perhaps the mirror target URL is invalid?",
                                           e, mirror.getTargetUrl(), path );
        }
    }

    private String buildUrl( final String baseUrl, final String... parts )
        throws MalformedURLException
    {
        if ( parts == null || parts.length < 1 )
        {
            return baseUrl;
        }

        final StringBuilder urlBuilder = new StringBuilder();

        if ( !parts[0].startsWith( baseUrl ) )
        {
            urlBuilder.append( baseUrl );
        }

        for ( String part : parts )
        {
            if ( part.startsWith( "/" ) )
            {
                part = part.substring( 1 );
            }

            if ( urlBuilder.length() > 0 && urlBuilder.charAt( urlBuilder.length() - 1 ) != '/' )
            {
                urlBuilder.append( "/" );
            }

            urlBuilder.append( part );
        }

        // if ( params != null && !params.isEmpty() )
        // {
        // urlBuilder.append( "?" );
        // boolean first = true;
        // for ( final Map.Entry<String, String> param : params.entrySet() )
        // {
        // if ( first )
        // {
        // first = false;
        // }
        // else
        // {
        // urlBuilder.append( "&" );
        // }
        //
        // urlBuilder.append( param.getKey() )
        // .append( "=" )
        // .append( param.getValue() );
        // }
        // }

        return new URL( urlBuilder.toString() ).toExternalForm();
    }

    protected RouteDataManager getDataManager()
    {
        return dataManager;
    }

    protected void setDataManager( final RouteDataManager dataManager )
    {
        this.dataManager = dataManager;
    }

    public MirrorOf selectMirror( final List<MirrorOf> mirrors )
    {
        return mirrorSelector.select( mirrors );
    }
}
