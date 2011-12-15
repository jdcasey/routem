package org.commonjava.routem.model;

import java.util.List;

public class RouteMReplicationData
{

    private List<Group> groups;

    private List<MirrorOf> mirrors;

    public RouteMReplicationData()
    {
    }

    public RouteMReplicationData( final List<Group> groups, final List<MirrorOf> mirrors )
    {
        this.groups = groups;
        this.mirrors = mirrors;
    }

    public List<Group> getGroups()
    {
        return groups;
    }

    public List<MirrorOf> getMirrors()
    {
        return mirrors;
    }

    public void setGroups( final List<Group> groups )
    {
        this.groups = groups;
    }

    public void setMirrors( final List<MirrorOf> mirrors )
    {
        this.mirrors = mirrors;
    }

}
