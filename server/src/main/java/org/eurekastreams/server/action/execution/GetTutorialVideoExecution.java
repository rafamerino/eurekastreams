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
package org.eurekastreams.server.action.execution;

import java.io.Serializable;

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.persistence.mappers.db.GetTutorialVideos;

/**
 * Execution for getting the tutorialVideos.
 * 
 */
public class GetTutorialVideoExecution implements ExecutionStrategy<PrincipalActionContext>
{

    /**
     * Mapper to look up a tutorial video.
     */
    private final GetTutorialVideos tutorialVideoMapper;

    /**
     * @param inTutorialVideoMapper
     *            the tutorialVideoMapper.
     */
    public GetTutorialVideoExecution(final GetTutorialVideos inTutorialVideoMapper)
    {
        tutorialVideoMapper = inTutorialVideoMapper;
    }

    /**
     * @param inActionContext
     *            the context for the action.
     * @return the tutorial video.
     */
    @Override
    public Serializable execute(final PrincipalActionContext inActionContext)
    {        
        return tutorialVideoMapper.execute(null);
    }
}
