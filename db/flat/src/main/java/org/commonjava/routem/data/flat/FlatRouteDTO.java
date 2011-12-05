package org.commonjava.routem.data.flat;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.commonjava.routem.model.Group;
import org.commonjava.routem.model.MirrorOf;

public class FlatRouteDTO
{

    private Set<Group> groups = new TreeSet<Group>();

    private Set<MirrorOf> mirrors = new HashSet<MirrorOf>();

    public FlatRouteDTO()
    {
    }

    public FlatRouteDTO( final List<Group> groups, final List<MirrorOf> mirrors )
    {
        this.groups = new TreeSet<Group>( groups );
        this.mirrors = new HashSet<MirrorOf>( mirrors );
    }

    public Set<Group> getGroups()
    {
        return groups;
    }

    public Set<MirrorOf> getMirrors()
    {
        return mirrors;
    }

    public void setGroups( final Set<Group> groups )
    {
        this.groups = groups;
    }

    public void setMirrors( final Set<MirrorOf> mirrors )
    {
        this.mirrors = mirrors;
    }

}
