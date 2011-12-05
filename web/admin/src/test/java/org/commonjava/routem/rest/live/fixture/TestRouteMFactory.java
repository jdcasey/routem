package org.commonjava.routem.rest.live.fixture;

import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;

import org.commonjava.auth.couch.conf.DefaultUserManagerConfig;
import org.commonjava.auth.couch.conf.UserManagerConfiguration;
import org.commonjava.auth.couch.inject.UserData;
import org.commonjava.couch.conf.CouchDBConfiguration;

public class TestRouteMFactory
{

    private UserManagerConfiguration umConfig;

    @Produces
    @UserData
    @Default
    public synchronized CouchDBConfiguration getCouchDBConfiguration()
    {
        return getUserManagerConfiguration().getDatabaseConfig();
    }

    @Produces
    @Default
    public synchronized UserManagerConfiguration getUserManagerConfiguration()
    {
        if ( umConfig == null )
        {
            umConfig =
                new DefaultUserManagerConfig( "admin@nowhere.com", "password", "Admin", "User",
                                              "http://localhost:5984/test-user-manager" );
        }

        return umConfig;
    }

}
