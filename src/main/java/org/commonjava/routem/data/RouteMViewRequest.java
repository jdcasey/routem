package org.commonjava.routem.data;

import org.commonjava.couch.db.model.ViewRequest;
import org.commonjava.routem.data.RouteMAppDescription.View;

public class RouteMViewRequest
    extends ViewRequest
{

    public RouteMViewRequest( final View view )
    {
        super( RouteMAppDescription.APP_NAME, view.viewName() );
        setParameter( INCLUDE_DOCS, true );
    }

    public RouteMViewRequest( final View view, final String key )
    {
        this( view );
        setParameter( KEY, key );
    }

}
