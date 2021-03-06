/*
 * Copyright (c) 2009 Lockheed Martin Corporation
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
package org.eurekastreams.server.persistence.mappers.requests;

/**
 * MapperRequest for finding a DomainEntity by Id.
 * 
 */
public class GetPendingDomainGroupsForOrgRequest
{
    /**
     * org short name.
     */
    private String orgShortName;

    /**
     * page Start.
     */
    private int pageStart;

    /**
     * max results.
     */
    private int maxResults;

    /**
     * Constructor.
     * 
     * @param inOrgShortName
     *            Organization Short Name.
     * @param inPageStart
     *            Paging Start Number.
     * @param inMaxResults
     *            Max reults to return.
     */
    public GetPendingDomainGroupsForOrgRequest(final String inOrgShortName, final int inPageStart,
            final int inMaxResults)
    {
        orgShortName = inOrgShortName;
        pageStart = inPageStart;
        maxResults = inMaxResults;
    }

    /**
     * @return the orgShortName
     */
    public String getOrgShortName()
    {
        return orgShortName;
    }

    /**
     * @param inOrgShortName
     *            the orgShortName to set
     */
    public void setOrgShortName(final String inOrgShortName)
    {
        orgShortName = inOrgShortName;
    }

    /**
     * @return the pageStart
     */
    public int getPageStart()
    {
        return pageStart;
    }

    /**
     * @param inPageStart
     *            the pageStart to set
     */
    public void setPageStart(final int inPageStart)
    {
        pageStart = inPageStart;
    }

    /**
     * @return maxResults.
     */
    public int getMaxResults()
    {
        return maxResults;
    }

    /**
     * @param inMaxResults
     *            max results.
     */
    public void setMaxResults(final int inMaxResults)
    {
        maxResults = inMaxResults;
    }

}
