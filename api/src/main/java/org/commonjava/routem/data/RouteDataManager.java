package org.commonjava.routem.data;

import java.util.List;

import org.commonjava.routem.model.Group;
import org.commonjava.routem.model.MirrorOf;

public interface RouteDataManager
{

    void install()
        throws RouteMDataException;

    Group getGroup( final String groupId )
        throws RouteMDataException;

    List<Group> getGroupsUsingCanonicalUrl( final String canonicalUrl )
        throws RouteMDataException;

    // TODO: Wildcard support!
    List<MirrorOf> getMirrorsOfGroup( final String groupId )
        throws RouteMDataException;

    MirrorOf getMirror( final String canonicalUrl, final String targetUrl )
        throws RouteMDataException;

    // TODO: Wildcard support!
    List<MirrorOf> getMirrorsOfCanonicalUrl( final String canonicalUrl )
        throws RouteMDataException;

    List<MirrorOf> getMirrorComposition( final String targetUrl )
        throws RouteMDataException;

    boolean store( final MirrorOf mirrorOf )
        throws RouteMDataException;

    boolean store( final MirrorOf mirrorOf, final boolean skipIfExists )
        throws RouteMDataException;

    boolean store( final Group group )
        throws RouteMDataException;

    boolean store( final Group group, final boolean skipIfExists )
        throws RouteMDataException;

    void delete( final MirrorOf mirrorOf )
        throws RouteMDataException;

    void delete( final Group group )
        throws RouteMDataException;

    List<Group> getAllGroupDefinitions()
        throws RouteMDataException;

    List<MirrorOf> getAllMirrorOfDefinitions()
        throws RouteMDataException;

}