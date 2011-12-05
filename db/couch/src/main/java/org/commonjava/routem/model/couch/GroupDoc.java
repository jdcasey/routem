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
package org.commonjava.routem.model.couch;

import static org.commonjava.couch.util.IdUtils.namespaceId;
import static org.commonjava.routem.model.couch.MetadataKeys.ID_METADATA;
import static org.commonjava.routem.model.couch.MetadataKeys.REV_METADATA;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.commonjava.couch.model.AbstractCouchDocument;
import org.commonjava.couch.model.DenormalizedCouchDoc;
import org.commonjava.routem.model.Group;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public final class GroupDoc
    extends AbstractCouchDocument
    implements DenormalizedCouchDoc, Serializable, Comparable<GroupDoc>
{

    public static final String NAMESPACE = "group";

    private static final long serialVersionUID = 1L;

    @SerializedName( "group_id" )
    private String groupId;

    @SerializedName( "canonical_url" )
    private String canonicalUrl;

    @Expose( deserialize = false )
    private final String doctype = NAMESPACE;

    public GroupDoc( final Group group )
    {
        this.groupId = group.getGroupId();
        this.canonicalUrl = group.getCanonicalUrl();
        setCouchDocRev( group.getMetadata( REV_METADATA, String.class ) );
    }

    protected GroupDoc()
    {
    }

    public Group toGroup()
    {
        final Group g = new Group( groupId, canonicalUrl );
        g.setMetadata( ID_METADATA, getCouchDocId() );
        g.setMetadata( REV_METADATA, getCouchDocRev() );

        return g;
    }

    public String getGroupId()
    {
        return groupId;
    }

    public String getCanonicalUrl()
    {
        return canonicalUrl;
    }

    protected void setGroupId( final String groupId )
    {
        this.groupId = groupId;
    }

    protected void setCanonicalUrl( final String canonicalUrl )
    {
        this.canonicalUrl = canonicalUrl;
    }

    @Override
    public String toString()
    {
        return String.format( "Group [groupId=%s, canonicalUrl=%s]", groupId, canonicalUrl );
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( groupId == null ) ? 0 : groupId.hashCode() );
        return result;
    }

    @Override
    public boolean equals( final Object obj )
    {
        if ( this == obj )
        {
            return true;
        }
        if ( obj == null )
        {
            return false;
        }
        if ( getClass() != obj.getClass() )
        {
            return false;
        }
        final GroupDoc other = (GroupDoc) obj;
        if ( groupId == null )
        {
            if ( other.groupId != null )
            {
                return false;
            }
        }
        else if ( !groupId.equals( other.groupId ) )
        {
            return false;
        }
        return true;
    }

    @Override
    public void calculateDenormalizedFields()
    {
        setCouchDocId( namespaceId( NAMESPACE, groupId ) );
    }

    public String getDoctype()
    {
        return doctype;
    }

    @Override
    public int compareTo( final GroupDoc o )
    {
        // Intentionally reversed, to put the longest match first!
        return o.getGroupId()
                .compareTo( getGroupId() );
    }

    public static List<Group> toGroups( final List<GroupDoc> docs )
    {
        final List<Group> result = new ArrayList<Group>();
        for ( final GroupDoc doc : docs )
        {
            result.add( doc.toGroup() );
        }

        return result;
    }

}
