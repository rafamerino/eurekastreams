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
package org.eurekastreams.server.persistence.mappers.cache;

import org.eurekastreams.server.persistence.mappers.chained.RefreshStrategy;
import org.eurekastreams.server.persistence.mappers.stream.CachedDomainMapper;

/**
 * Refresh the everyone stream id.
 */
public class RefreshEveryoneStreamIdCacheMapper extends CachedDomainMapper implements RefreshStrategy<Object, Long>
{
    /**
     * Refresh the everyone stream id.
     *
     * @param request
     *            nothing.
     * @param data
     *            the id.
     */
    @Override
    public void refresh(final Object request, final Long data)
    {
        getCache().set(CacheKeys.CORE_STREAMVIEW_ID_EVERYONE, data);
    }

}
