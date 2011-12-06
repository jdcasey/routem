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
package org.commonjava.routem.data.couch;

import static org.commonjava.couch.util.IdUtils.namespaceId;

import org.commonjava.couch.model.CouchDocRef;
import org.commonjava.routem.model.MirrorOf;

public class MirrorOfRef
    extends CouchDocRef
{

    public MirrorOfRef( final String canonicalUrl, final String targetUrl )
    {
        super( namespaceId( MirrorOf.NAMESPACE, MirrorOf.mirrorId( canonicalUrl, targetUrl ) ) );
    }

    public MirrorOfRef( final String mirrorId )
    {
        super( namespaceId( MirrorOf.NAMESPACE, mirrorId ) );
    }

}
