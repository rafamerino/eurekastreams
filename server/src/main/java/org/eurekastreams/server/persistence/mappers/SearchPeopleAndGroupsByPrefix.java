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
package org.eurekastreams.server.persistence.mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.commons.search.ProjectionSearchRequestBuilder;
import org.eurekastreams.commons.search.modelview.ModelView;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.persistence.mappers.cache.GetPrivateCoordinatedAndFollowedGroupIdsForUser;
import org.eurekastreams.server.persistence.mappers.requests.GetEntitiesByPrefixRequest;
import org.eurekastreams.server.persistence.mappers.stream.GetPeopleByAccountIds;
import org.eurekastreams.server.search.modelview.DisplayEntityModelView;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.hibernate.search.jpa.FullTextQuery;

/**
 * Search for stream-postable people and groups by prefix, using Hibernate Search.
 */
public class SearchPeopleAndGroupsByPrefix extends
        ReadMapper<GetEntitiesByPrefixRequest, List<DisplayEntityModelView>>
{
    /**
     * Logger.
     */
    private Log log = LogFactory.getLog(SearchPeopleAndGroupsByPrefix.class);

    /**
     * default max results from query.
     */
    private final Integer maxResults;

    /**
     * Search request builder.
     */
    private final ProjectionSearchRequestBuilder searchRequestBuilder;

    /**
     * Mapper to get a list of all group ids that aren't public that a user can see activity for.
     */
    private final GetPrivateCoordinatedAndFollowedGroupIdsForUser getGroupIdsMapper;

    /**
     * Mapper used to translate user accountId to DB id.
     */
    private final GetPeopleByAccountIds personByAccountIdMapper;    
    
    /**
     * Flag to enable group searching only.
     */
    private boolean searchGroupsOnly;

    /**
     * Constructor.
     * 
     * @param inMaxResults
     *            the max number of results to return
     * @param inSearchRequestBuilder
     *            the search request builder
     * @param inGetGroupIdsMapper
     *            Mapper to get groups user has access to.
     * @param inPersonByAccountIdMapper
     *            Mapper used to translate user accountId to DB id.
     * @param inSearchGroupsOnly
     *            Flag to search for groups only.
     */
    public SearchPeopleAndGroupsByPrefix(final Integer inMaxResults,
            final ProjectionSearchRequestBuilder inSearchRequestBuilder,
            final GetPrivateCoordinatedAndFollowedGroupIdsForUser inGetGroupIdsMapper,
            final GetPeopleByAccountIds inPersonByAccountIdMapper, final boolean inSearchGroupsOnly)
    {
        maxResults = inMaxResults;
        searchRequestBuilder = inSearchRequestBuilder;
        getGroupIdsMapper = inGetGroupIdsMapper;
        personByAccountIdMapper = inPersonByAccountIdMapper;
        searchGroupsOnly = inSearchGroupsOnly;
    }

    /**
     * Search for people and groups by prefix.
     * 
     * @param inRequest
     *            The request object containing parameters for search.
     * @return List of DisplayEntityModelView representing people/groups matching search criteria.
     */
    @Override
    public List<DisplayEntityModelView> execute(final GetEntitiesByPrefixRequest inRequest)
    {
        // build a search string that includes all of the fields for both people
        // and groups.
        // - people: firstName, lastName, preferredName
        // - group: name
        // - both: isStreamPostable, isPublic
        // Due to text stemming, we need to search with and without the wildcard
        String searchText = String.format(
                "+(name:(%1$s* %1$s) lastName:(%1$s* %1$s) preferredName:(%1$s* %1$s)^0.5) +isStreamPostable:true %2$s",
                inRequest.getPrefix(), getGroupVisibilityClause(inRequest));        

        if (log.isTraceEnabled())
        {
            log.trace("Searching for " + maxResults + " people and groups with Lucene query: " + searchText);
        }

        FullTextQuery query = searchRequestBuilder.buildQueryFromNativeSearchString(searchText);

        searchRequestBuilder.setPaging(query, 0, maxResults);

        // get the model views (via the injected cache transformer)
        List<ModelView> searchResults = (List<ModelView>) query.getResultList();

        if (log.isTraceEnabled())
        {
            log.trace("Found " + searchResults.size() + " search results");
        }

        // transform the list to DisplayEntityModelView
        List<DisplayEntityModelView> displayModelViews = new ArrayList<DisplayEntityModelView>();
        for (ModelView modelView : searchResults)
        {
            DisplayEntityModelView displayModelView = new DisplayEntityModelView();
            if (modelView instanceof PersonModelView && !searchGroupsOnly)
            {
                PersonModelView person = (PersonModelView) modelView;

                if (log.isTraceEnabled())
                {
                    log.trace("Found person '" + person.getAccountId() + " with search prefix '" + searchText + "'");
                }

                displayModelView.setDisplayName(person.getDisplayName());
                displayModelView.setStreamScopeId(person.getStreamId());
                displayModelView.setType(EntityType.PERSON);
                displayModelView.setUniqueKey(person.getAccountId());
                displayModelViews.add(displayModelView);
            }
            else if (modelView instanceof DomainGroupModelView)
            {
                DomainGroupModelView group = (DomainGroupModelView) modelView;

                if (log.isTraceEnabled())
                {
                    log.trace("Found domain group '" + group.getShortName() + " with search prefix '" + searchText
                            + "'");
                }

                displayModelView.setDisplayName(group.getName());
                displayModelView.setStreamScopeId(group.getStreamId());
                displayModelView.setType(EntityType.GROUP);
                displayModelView.setUniqueKey(group.getShortName());
                displayModelViews.add(displayModelView);
            }
        }

        return displayModelViews;
    }

    /**
     * Returns search clause used to sort out groups user doesn't have access to.
     * 
     * @param inRequest
     *            The search parameters
     * @return Search clause used to sort out groups user doesn't have access to.
     */
    private String getGroupVisibilityClause(final GetEntitiesByPrefixRequest inRequest)
    {
        // get user id from userKey passed from client.
        Long userId = personByAccountIdMapper.fetchId(inRequest.getUserKey());

        StringBuffer result = new StringBuffer("+(isPublic:true ");

        // get all the group ids followed or coordinated by current user.
        Set<Long> groupIds = getGroupIdsMapper.execute(userId);

        // If group list is greater than zero, include private group visibility clause.
        if (groupIds.size() != 0)
        {
            result.append("( +id:(");
            for (Long id : groupIds)
            {
                result.append(id + " ");
            }
            //TODO: this "-isPublic:true" is because text stemmer turns "false" into "fals"
            //and the query fails. Investigate Lucene API to see how to do this via object
            //model rather than query string generation to get around this.
            result.append(") -isPublic:true)");
        }
        result.append(")");        

        return result.toString();

    }
}
