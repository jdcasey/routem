package org.commonjava.routem.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.enterprise.inject.Default;
import javax.inject.Singleton;

import org.commonjava.routem.model.Group;
import org.commonjava.routem.model.MirrorOf;
import org.commonjava.util.logging.Logger;

@Singleton
@Default
public class DefaultRouteDataManager
    implements RouteDataManager
{

    private final Logger logger = new Logger( getClass() );

    private final Set<Group> groups = new TreeSet<Group>();

    private final Set<MirrorOf> mirrors = new HashSet<MirrorOf>();

    @Override
    public void install()
        throws RouteMDataException
    {
        groups.clear();
        mirrors.clear();
    }

    @Override
    public Group getGroup( final String groupId )
        throws RouteMDataException
    {
        logger.debug( "Lookup for groupId: '%s'", groupId );
        for ( final Group group : groups )
        {
            if ( group.getGroupId()
                      .equals( groupId ) )
            {
                logger.debug( "EXACT MATCH: '%s'", group.getGroupId() );
                return group;
            }
            else if ( groupId.startsWith( group.getGroupId() ) )
            {
                logger.debug( "PARENT MATCH: '%s'", group.getGroupId() );
                return group;
            }
        }

        return null;
    }

    @Override
    public List<Group> getGroupsUsingCanonicalUrl( final String canonicalUrl )
        throws RouteMDataException
    {
        final List<Group> result = new ArrayList<Group>();
        for ( final Group group : groups )
        {
            if ( group.getCanonicalUrl()
                      .equals( canonicalUrl ) )
            {
                result.add( group );
            }
        }

        return result;
    }

    @Override
    public List<MirrorOf> getMirrorsOfGroup( final String groupId )
        throws RouteMDataException
    {
        final Group group = getGroup( groupId );
        if ( group == null )
        {
            logger.debug( "Group is null for: '%s'", groupId );
            return null;
        }

        return getMirrorsOfCanonicalUrl( group.getCanonicalUrl() );
    }

    @Override
    public MirrorOf getMirror( final String canonicalUrl, final String targetUrl )
        throws RouteMDataException
    {
        for ( final MirrorOf mirror : mirrors )
        {
            if ( mirror.getCanonicalUrl()
                       .equals( canonicalUrl ) && mirror.getTargetUrl()
                                                        .equals( targetUrl ) )
            {
                return mirror;
            }
        }

        return null;
    }

    @Override
    public List<MirrorOf> getMirrorsOfCanonicalUrl( final String canonicalUrl )
        throws RouteMDataException
    {
        logger.debug( "Lookup canonical-URL: '%s'", canonicalUrl );
        final List<MirrorOf> result = new ArrayList<MirrorOf>();
        for ( final MirrorOf mirrorOf : mirrors )
        {
            if ( mirrorOf.getCanonicalUrl()
                         .equals( canonicalUrl ) )
            {
                logger.debug( "MATCH: '%s'", mirrorOf );
                result.add( mirrorOf );
            }
        }

        return result;
    }

    @Override
    public List<MirrorOf> getMirrorComposition( final String targetUrl )
        throws RouteMDataException
    {
        final List<MirrorOf> result = new ArrayList<MirrorOf>();
        for ( final MirrorOf mirrorOf : mirrors )
        {
            if ( mirrorOf.getTargetUrl()
                         .equals( targetUrl ) )
            {
                result.add( mirrorOf );
            }
        }

        return result;
    }

    @Override
    public boolean store( final MirrorOf mirrorOf )
        throws RouteMDataException
    {
        logger.debug( "Storing: %s", mirrorOf );
        final boolean result = mirrors.add( mirrorOf );
        logger.debug( "Stored? %s", result );

        return result;
    }

    @Override
    public boolean store( final MirrorOf mirrorOf, final boolean skipIfExists )
        throws RouteMDataException
    {
        if ( !skipIfExists && mirrors.contains( mirrorOf ) )
        {
            logger.debug( "Removing: %s", mirrorOf );
            mirrors.remove( mirrorOf );
        }

        logger.debug( "Storing: %s", mirrorOf );
        final boolean result = mirrors.add( mirrorOf );
        logger.debug( "Stored? %s", result );

        return result;
    }

    @Override
    public boolean store( final Group group )
        throws RouteMDataException
    {
        logger.debug( "Storing: %s", group );
        final boolean result = groups.add( group );
        logger.debug( "Stored? %s", result );

        return result;
    }

    @Override
    public boolean store( final Group group, final boolean skipIfExists )
        throws RouteMDataException
    {
        if ( !skipIfExists && mirrors.contains( group ) )
        {
            logger.debug( "Removing: %s", group );
            groups.remove( group );
        }

        logger.debug( "Storing: %s", group );
        final boolean result = groups.add( group );
        logger.debug( "Stored? %s", result );

        return result;
    }

    @Override
    public void delete( final MirrorOf mirrorOf )
        throws RouteMDataException
    {
        mirrors.remove( mirrorOf );
    }

    @Override
    public void delete( final Group group )
        throws RouteMDataException
    {
        groups.remove( group );
    }

    @Override
    public List<Group> getAllGroupDefinitions()
        throws RouteMDataException
    {
        return new ArrayList<Group>( groups );
    }

    @Override
    public List<MirrorOf> getAllMirrorOfDefinitions()
        throws RouteMDataException
    {
        return new ArrayList<MirrorOf>( mirrors );
    }

    @Override
    public void deleteGroup( final String groupId )
        throws RouteMDataException
    {
        for ( final Group group : new HashSet<Group>( groups ) )
        {
            if ( group.getGroupId()
                      .equals( groupId ) )
            {
                groups.remove( group );
                break;
            }
        }
    }

    @Override
    public void deleteMirror( final String canonicalUrl, final String targetUrl )
        throws RouteMDataException
    {
        for ( final MirrorOf mirror : new HashSet<MirrorOf>( mirrors ) )
        {
            if ( mirror.getCanonicalUrl()
                       .equals( canonicalUrl ) && mirror.getTargetUrl()
                                                        .equals( targetUrl ) )
            {
                mirrors.remove( mirror );
                break;
            }
        }
    }

    @Override
    public MirrorOf getMirror( final String mirrorId )
        throws RouteMDataException
    {
        for ( final MirrorOf mirror : mirrors )
        {
            if ( mirror.getMirrorId()
                       .equals( mirrorId ) )
            {
                return mirror;
            }
        }

        return null;
    }

    @Override
    public void deleteMirror( final String mirrorId )
        throws RouteMDataException
    {
        for ( final MirrorOf mirror : new HashSet<MirrorOf>( mirrors ) )
        {
            if ( mirror.getMirrorId()
                       .equals( mirrorId ) )
            {
                mirrors.remove( mirror );
            }
        }
    }

}
