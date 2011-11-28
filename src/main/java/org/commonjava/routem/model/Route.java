package org.commonjava.routem.model;

import static org.commonjava.couch.util.IdUtils.namespaceId;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.commonjava.couch.model.AbstractCouchDocument;
import org.commonjava.couch.model.DenormalizedCouchDoc;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public final class Route
    extends AbstractCouchDocument
    implements DenormalizedCouchDoc
{

    public static final String NAMESPACE = "route";

    public static final String GROUP_ID_WILDCARD = "*";

    private static final Set<String> WILDCARD_SET =
        Collections.unmodifiableSet( Collections.singleton( GROUP_ID_WILDCARD ) );

    @SerializedName( "target_url" )
    private String targetUrl;

    @SerializedName( "group_ids" )
    private Set<String> groupIds;

    @Expose( deserialize = false )
    private final String doctype = NAMESPACE;

    public Route( final String targetUrl )
    {
        this.targetUrl = targetUrl;
    }

    Route()
    {
    }

    @Override
    public void calculateDenormalizedFields()
    {
        setCouchDocId( namespaceId( NAMESPACE, targetUrl ) );
    }

    public boolean hasGroupId( final String groupId )
    {
        return hasGroupId( groupIds, groupId );
    }

    private static boolean hasGroupId( final Collection<String> groupIds, final String groupId )
    {
        // .equals() used for deserialized objects.
        if ( groupIds == WILDCARD_SET || WILDCARD_SET.equals( groupIds ) )
        {
            return true;
        }

        if ( groupIds == null )
        {
            return false;
        }

        if ( groupIds.contains( groupId ) )
        {
            return true;
        }

        for ( final String gid : groupIds )
        {
            if ( groupId.startsWith( gid ) )
            {
                return true;
            }
        }

        return false;
    }

    public Set<String> getGroupIds()
    {
        return Collections.unmodifiableSet( groupIds );
    }

    public synchronized boolean addGroupId( final String groupId )
    {
        // .equals() used for deserialized objects.
        if ( groupIds == WILDCARD_SET || WILDCARD_SET.equals( groupIds ) )
        {
            return false;
        }
        else if ( GROUP_ID_WILDCARD.equals( groupId ) )
        {
            groupIds = WILDCARD_SET;
            return true;
        }

        if ( groupIds == null )
        {
            groupIds = new HashSet<String>();
        }

        if ( hasGroupId( groupId ) )
        {
            return false;
        }

        final boolean added = groupIds.add( groupId );
        if ( added )
        {
            pruneGroupIdSet();
        }

        return added;
    }

    private synchronized void pruneGroupIdSet()
    {
        final List<String> gids = new ArrayList<String>( groupIds );
        Collections.sort( gids );

        int idx = 0;
        for ( final String gid : gids )
        {
            if ( idx == 0 )
            {
                continue;
            }

            final List<String> prevAndParents = gids.subList( 0, idx );
            if ( hasGroupId( prevAndParents, gid ) )
            {
                groupIds.remove( gid );
            }

            idx++;
        }
    }

    public boolean removeGroupId( final String groupId )
    {
        if ( groupIds == null )
        {
            return false;
        }

        return groupIds.remove( groupId );
    }

    protected void setGroupIds( final Set<String> groupIds )
    {
        if ( groupIds != null )
        {
            this.groupIds = new HashSet<String>( groupIds );
        }
    }

    public String getTargetUrl()
    {
        return targetUrl;
    }

    protected void setTargetUrl( final String targetUrl )
    {
        this.targetUrl = targetUrl;
    }

    @Override
    public String toString()
    {
        return String.format( "Route [targetUrl=%s]", targetUrl );
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ( ( targetUrl == null ) ? 0 : targetUrl.hashCode() );
        return result;
    }

    @Override
    public boolean equals( final Object obj )
    {
        if ( this == obj )
        {
            return true;
        }
        if ( !super.equals( obj ) )
        {
            return false;
        }
        if ( getClass() != obj.getClass() )
        {
            return false;
        }
        final Route other = (Route) obj;
        if ( targetUrl == null )
        {
            if ( other.targetUrl != null )
            {
                return false;
            }
        }
        else if ( !targetUrl.equals( other.targetUrl ) )
        {
            return false;
        }
        return true;
    }

    public String getDoctype()
    {
        return doctype;
    }

}
