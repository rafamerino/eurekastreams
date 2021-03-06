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
package org.eurekastreams.web.client.model;

import org.eurekastreams.server.action.request.stream.GetStreamSearchResultsRequest;
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.web.client.events.data.GotActivitySearchResponseEvent;
import org.eurekastreams.web.client.ui.Session;

/**
 * Activity Search Model.
 *
 */
public class ActivitySearchModel extends BaseModel implements Fetchable<GetStreamSearchResultsRequest>
{
    /**
     * Singleton.
     */
    private static ActivitySearchModel model = new ActivitySearchModel();

    /**
     * Gets the singleton.
     *
     * @return the singleton.
     */
    public static ActivitySearchModel getInstance()
    {
        return model;
    }

    /**
     * Retrieves a list of flagged activities for the org.
     *
     * @param inRequest
     *            Request.
     * @param inUseClientCacheIfAvailable
     *            If ok to return cached results.
     */
    public void fetch(final GetStreamSearchResultsRequest inRequest, final boolean inUseClientCacheIfAvailable)
    {
        super.callReadAction("getStreamSearchResults", inRequest,
                new OnSuccessCommand<PagedSet<ActivityDTO>>()
                {
                    public void onSuccess(final PagedSet<ActivityDTO> response)
                    {
                        Session.getInstance().getEventBus().notifyObservers(
                                new GotActivitySearchResponseEvent(response));
                    }
                }, inUseClientCacheIfAvailable);
    }
}
