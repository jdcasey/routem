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

import org.apache.log4j.Level;
import org.commonjava.routem.RouteDataManagerTCK;
import org.commonjava.util.logging.Log4jUtil;
import org.junit.Before;
import org.junit.BeforeClass;

public class DefaultRouteDataManagerTest
    extends RouteDataManagerTCK
{

    private final DefaultRouteDataManager dataManager = new DefaultRouteDataManager();

    @BeforeClass
    public static void logging()
    {
        Log4jUtil.configure( Level.DEBUG );
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
