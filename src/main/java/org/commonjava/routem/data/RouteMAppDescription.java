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
        ALL_ROUTES( "all-routes" ), ROUTES_FOR_GROUP( "routes-for-group" );

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
