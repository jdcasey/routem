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
package org.commonjava.routem.data.mem;

import org.apache.log4j.Level;
import org.commonjava.couch.test.fixture.LoggingFixture;
import org.commonjava.routem.RouteDataManagerTCK;
import org.commonjava.routem.data.RouteDataManager;
import org.commonjava.routem.data.mem.MemoryRouteDataManager;
import org.junit.Before;
import org.junit.BeforeClass;

public class MemoryRouteDataManagerTest
    extends RouteDataManagerTCK
{

    private final MemoryRouteDataManager dataManager = new MemoryRouteDataManager();

    @BeforeClass
    public static void logging()
    {
        LoggingFixture.setupLogging( Level.DEBUG );
    }

    @Before
    public void setup()
        throws Exception
    {
        dataManager.install();
    }

    @Override
    protected RouteDataManager getDataManager()
        throws Exception
    {
        return dataManager;
    }

}
