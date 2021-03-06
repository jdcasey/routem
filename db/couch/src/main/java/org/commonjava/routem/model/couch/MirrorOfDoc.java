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
import static org.commonjava.routem.model.MirrorOf.NAMESPACE;
import static org.commonjava.routem.model.couch.MetadataKeys.ID_METADATA;
import static org.commonjava.routem.model.couch.MetadataKeys.REV_METADATA;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.commonjava.couch.model.AbstractCouchDocument;
import org.commonjava.couch.model.DenormalizedCouchDoc;
import org.commonjava.routem.model.MirrorOf;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public final class MirrorOfDoc
    extends AbstractCouchDocument
    implements DenormalizedCouchDoc, Serializable
{

    private static final long serialVersionUID = 1L;

    @SerializedName( "target_url" )
    private String targetUrl;

    @SerializedName( "canonical_url" )
    private String canonicalUrl;

    @SerializedName( "X_selection_hints" )
    private Map<String, String> selectionHints;

    @SerializedName( "mirror_id" )
    private String mirrorId;

    public MirrorOfDoc( final MirrorOf mirror )
    {
        this.canonicalUrl = mirror.getCanonicalUrl();
        this.targetUrl = mirror.getTargetUrl();
        this.selectionHints = mirror.getSelectionHints();
        this.mirrorId = mirror.getMirrorId();
        setCouchDocRev( mirror.getMetadata( REV_METADATA, String.class ) );
    }

    protected MirrorOfDoc()
    {
    }

    public MirrorOf toMirrorOf()
    {
        final MirrorOf m = new MirrorOf( canonicalUrl, targetUrl, selectionHints );
        m.setMetadata( ID_METADATA, getCouchDocId() );
        m.setMetadata( REV_METADATA, getCouchDocRev() );

        return m;
    }

    @Expose( deserialize = false )
    private final String doctype = NAMESPACE;

    @Override
    public void calculateDenormalizedFields()
    {
        setCouchDocId( namespaceId( NAMESPACE, mirrorId ) );
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

    public String getDoctype()
    {
        return doctype;
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

    public static List<MirrorOf> toMirrorOfs( final List<MirrorOfDoc> docs )
    {
        final List<MirrorOf> result = new ArrayList<MirrorOf>();
        for ( final MirrorOfDoc doc : docs )
        {
            result.add( doc.toMirrorOf() );
        }

        return result;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
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
        if ( !super.equals( obj ) )
        {
            return false;
        }
        if ( getClass() != obj.getClass() )
        {
            return false;
        }
        final MirrorOfDoc other = (MirrorOfDoc) obj;
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
        return String.format( "MirrorOfDoc [targetUrl=%s, canonicalUrl=%s, selectionHints=%s, mirrorId=%s]", targetUrl,
                              canonicalUrl, selectionHints, mirrorId );
    }

}
