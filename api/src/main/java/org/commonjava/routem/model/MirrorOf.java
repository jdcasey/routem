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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;

import com.google.gson.annotations.SerializedName;

public final class MirrorOf
    extends ModelMetadata
    implements Serializable
{

    public static final String NAMESPACE = "mirror_of";

    private static final long serialVersionUID = 1L;

    @SerializedName( "target_url" )
    private String targetUrl;

    @SerializedName( "canonical_url" )
    private String canonicalUrl;

    @SerializedName( "X_selection_hints" )
    private Map<String, String> selectionHints;

    @SerializedName( "mirror_id" )
    private String mirrorId;

    public MirrorOf( final String canonicalUrl, final String targetUrl )
    {
        this.canonicalUrl = canonicalUrl;
        this.targetUrl = targetUrl;
        this.mirrorId = mirrorId( canonicalUrl, targetUrl );
    }

    public static String mirrorId( final String canonicalUrl, final String targetUrl )
    {
        return DigestUtils.md5Hex( canonicalUrl + targetUrl );
    }

    protected MirrorOf()
    {
    }

    public MirrorOf( final String canonicalUrl, final String targetUrl, final Map<String, String> selectionHints )
    {
        this.canonicalUrl = canonicalUrl;
        this.targetUrl = targetUrl;
        this.selectionHints = selectionHints;
        this.mirrorId = mirrorId( canonicalUrl, targetUrl );
    }

    public Map<String, String> getSelectionHints()
    {
        return selectionHints == null ? Collections.<String, String> emptyMap()
                        : Collections.unmodifiableMap( selectionHints );
    }

    protected void setSelectionHints( final Map<String, String> selectionHints )
    {
        if ( selectionHints == null )
        {
            return;
        }

        this.selectionHints = new HashMap<String, String>( selectionHints );
    }

    public String setSelectionHint( final String hint, final String value )
    {
        if ( selectionHints == null )
        {
            selectionHints = new HashMap<String, String>();
        }

        return selectionHints.put( hint, value );
    }

    public String getTargetUrl()
    {
        return targetUrl;
    }

    public String getCanonicalUrl()
    {
        return canonicalUrl;
    }

    protected void setTargetUrl( final String targetUrl )
    {
        this.targetUrl = targetUrl;
    }

    protected void setCanonicalUrl( final String canonicalUrl )
    {
        this.canonicalUrl = canonicalUrl;
    }

    public String getMirrorId()
    {
        return mirrorId;
    }

    protected void setMirrorId( final String mirrorId )
    {
        this.mirrorId = mirrorId;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( mirrorId == null ) ? 0 : mirrorId.hashCode() );
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
        final MirrorOf other = (MirrorOf) obj;
        if ( mirrorId == null )
        {
            if ( other.mirrorId != null )
            {
                return false;
            }
        }
        else if ( !mirrorId.equals( other.mirrorId ) )
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return String.format( "MirrorOf [targetUrl=%s, canonicalUrl=%s, selectionHints=%s, mirrorId=%s]", targetUrl,
                              canonicalUrl, selectionHints, mirrorId );
    }

}
