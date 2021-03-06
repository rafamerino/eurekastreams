/*
 * Copyright (c) 2010 Lockheed Martin Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.eurekastreams.server.action.execution.profile;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.server.action.request.profile.GetBreadcrumbsListRequest;
import org.eurekastreams.server.domain.BreadcrumbDTO;
import org.eurekastreams.server.persistence.mappers.GetRecursiveParentOrgIds;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.eurekastreams.server.persistence.mappers.stream.GetOrganizationsByIds;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests the action to create org hierarchy breadcrumbs.
 */
public class GetBreadcrumbsListExecutionTest extends MapperTest
{
    /**
     * Context for building mock objects.
     */
    private final Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * System under test.
     */
    private GetBreadcrumbsListExecution sut;

    /**
     * Parent ids mapper.
     */
    @Autowired
    private GetRecursiveParentOrgIds getRecursiveParentOrgIdsMapper;

    /**
     * Org mapper.
     */
    @Autowired
    private GetOrganizationsByIds bulkOrganizationsMapper;

    /**
     * Mocked instance of the principal object.
     */
    private Principal currentPrincipalMock = context.mock(Principal.class);

    /**
     * Setup test fixtures.
     */
    @Before
    public void setup()
    {
        sut = new GetBreadcrumbsListExecution(getRecursiveParentOrgIdsMapper, bulkOrganizationsMapper);
    }

    /**
     * Tests performAction.
     *
     * @throws Exception
     *             not expected.
     */
    @Test
    @SuppressWarnings("unchecked")
    public final void testExecute() throws Exception
    {
        //Child org of root,1 breadcrumb should be returned.
        GetBreadcrumbsListRequest request = new GetBreadcrumbsListRequest(6);
        ServiceActionContext currentContext = new ServiceActionContext(request, currentPrincipalMock);

        ArrayList<BreadcrumbDTO> breadcrumbs = (ArrayList<BreadcrumbDTO>) sut.execute(currentContext);
        assertEquals(1, breadcrumbs.size());

        //Root org, no breadcrumbs should be returned.
        GetBreadcrumbsListRequest request2 = new GetBreadcrumbsListRequest(5);
        ServiceActionContext currentContext2 = new ServiceActionContext(request2, currentPrincipalMock);

        ArrayList<BreadcrumbDTO> breadcrumbs2 = (ArrayList<BreadcrumbDTO>) sut.execute(currentContext2);
        assertEquals(0, breadcrumbs2.size());
    }
}
