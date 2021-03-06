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

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.action.request.profile.GetPendingGroupsRequest;
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.persistence.mappers.GetPendingDomainGroupsForOrg;
import org.eurekastreams.server.persistence.mappers.requests.GetPendingDomainGroupsForOrgRequest;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;

/**
 * Gets the Pending groups for an organization.
 */
public class GetPendingGroupsExecutionStrategy implements ExecutionStrategy<ActionContext>
{
    /**
     * Logger.
     */
    private Log log = LogFactory.make();

    /**
     * The action mapper the performs the lookup.
     */
    private final GetPendingDomainGroupsForOrg actionMapper;

    /**
     * Constructor.
     *
     * @param inActionMapper
     *            injecting the ActionMapper.
     */
    public GetPendingGroupsExecutionStrategy(final GetPendingDomainGroupsForOrg inActionMapper)
    {
        actionMapper = inActionMapper;
    }

    /**
     * Executor for getting pending groups for an organization.
     *
     * @param inActionContext
     *            the action context
     * @return the pending groups for the org short name
     */
    @Override
    public Serializable execute(final ActionContext inActionContext)
    {
        GetPendingGroupsRequest actionRequest = (GetPendingGroupsRequest) inActionContext.getParams();

        GetPendingDomainGroupsForOrgRequest request = new GetPendingDomainGroupsForOrgRequest(actionRequest
                .getOrganizationShortName(), actionRequest.getStartIndex(), actionRequest.getEndIndex());

        PagedSet<DomainGroupModelView> pendingGroups = actionMapper.execute(request);

        if (log.isTraceEnabled())
        {
            log.trace("Found " + pendingGroups.getTotal() + " Pending Groups for org "
                    + actionRequest.getOrganizationShortName());
        }

        return pendingGroups;
    }

}
