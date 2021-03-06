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

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public final class Group
    extends ModelMetadata
    implements Serializable, Comparable<Group>
{

    public static final String NAMESPACE = "group";

    private static final long serialVersionUID = 1L;

    public static final String WILDCARD = "*";

    @SerializedName( "group_id" )
    private String groupId;

    @SerializedName( "canonical_url" )
    private String canonicalUrl;

    public Group( final String groupId, final String canonicalUrl )
    {
        this.groupId = groupId;
        this.canonicalUrl = canonicalUrl;
    }

    protected Group()
    {
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
        final Group other = (Group) obj;
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
    public int compareTo( final Group o )
    {
        // Intentionally reversed, to put the longest match first!
        return o.getGroupId()
                .compareTo( getGroupId() );
    }

}
