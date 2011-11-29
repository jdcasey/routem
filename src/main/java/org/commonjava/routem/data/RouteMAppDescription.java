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
package org.commonjava.routem.data;

import java.util.HashSet;
import java.util.Set;

import org.commonjava.couch.db.model.AppDescription;

public class RouteMAppDescription
    implements AppDescription
{

    public static final String APP_NAME = "routing-logic";

    public enum View
    {
        MIRRORS_OF_CANONICAL_URL( "mirrors-of-canonical-url" ),
        MIRROR_COMPOSITION( "mirror-composition" ),
        GROUPS_USING_CANONICAL_URL( "groups-using-canonical-url" );

        String name;

        private View( final String name )
        {
            this.name = name;
        }

        public String viewName()
        {
            return name;
        }
    }

    private static Set<String> viewNames;

    @Override
    public String getAppName()
    {
        return APP_NAME;
    }

    @Override
    public String getClasspathAppResource()
    {
        return APP_NAME;
    }

    @Override
    public Set<String> getViewNames()
    {
        if ( viewNames == null )
        {
            final Set<String> names = new HashSet<String>();
            for ( final View view : View.values() )
            {
                names.add( view.viewName() );
            }

            viewNames = names;
        }

        return viewNames;
    }

}
