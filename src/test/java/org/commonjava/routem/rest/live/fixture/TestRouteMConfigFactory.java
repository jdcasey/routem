package org.commonjava.routem.rest.live.fixture;

import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;

import org.commonjava.couch.conf.CouchDBConfiguration;
import org.commonjava.couch.conf.DefaultCouchDBConfiguration;
import org.commonjava.couch.test.fixture.TestData;
import org.commonjava.routem.inject.RouteMData;

public class TestRouteMConfigFactory
{

    public static final String DB_URL = "http://localhost:5984/test-routem";

    @TestData
    @Produces
    @RouteMData
    @Default
    public CouchDBConfiguration getTestConfiguration()
    {
        return new DefaultCouchDBConfiguration( DB_URL );
    }

}
