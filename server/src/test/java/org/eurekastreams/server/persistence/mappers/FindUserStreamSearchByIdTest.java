/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
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

import static org.junit.Assert.assertNotNull;

import javax.persistence.NoResultException;

import org.eurekastreams.server.domain.stream.StreamSearch;
import org.eurekastreams.server.persistence.mappers.requests.FindUserStreamFilterByIdRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test for FindUserStreamSearchById.
 *
 */
public class FindUserStreamSearchByIdTest extends MapperTest
{
    /**
     * System under test.
     */
    @Autowired
    private FindUserStreamSearchById findUserStreamSearchById;

    /**
     * Test execute expecting result.
     */
    @Test
    public void testExecuteUserWithStreamView()
    {
        final long personId = 42;
        final long ssId = 1;
        FindUserStreamFilterByIdRequest request = new FindUserStreamFilterByIdRequest(personId, ssId);
        StreamSearch result = findUserStreamSearchById.execute(request);

        assertNotNull(result);
    }

    /**
     * Test execute expecting no result.
     */
    @Test(expected = NoResultException.class)
    public void testExecuteUserWithNoStreamView()
    {
        final long personId = 99;
        final long ssId = 1;
        FindUserStreamFilterByIdRequest request = new FindUserStreamFilterByIdRequest(personId, ssId);
        StreamSearch result = findUserStreamSearchById.execute(request);

        assertNotNull(result);
    }

}
