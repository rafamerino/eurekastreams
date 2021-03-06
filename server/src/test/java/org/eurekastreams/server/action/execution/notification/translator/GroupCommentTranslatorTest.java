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
package org.eurekastreams.server.action.execution.notification.translator;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eurekastreams.server.domain.NotificationDTO;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.StreamEntityDTO;
import org.eurekastreams.server.persistence.mappers.db.GetCommentorIdsByActivityId;
import org.eurekastreams.server.persistence.mappers.stream.BulkActivitiesMapper;
import org.eurekastreams.server.persistence.mappers.stream.GetCoordinatorIdsByGroupId;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Tests the group comment notification translator.
 */
public class GroupCommentTranslatorTest
{
    /** Context for building mock objects. */
    private final Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** Mock commentors mapper. */
    private GetCommentorIdsByActivityId commentorsMapper = context.mock(GetCommentorIdsByActivityId.class);

    /** Mock activities mapper. */
    private BulkActivitiesMapper activitiesMapper = context.mock(BulkActivitiesMapper.class);

    /** Mock activities mapper. */
    private GetCoordinatorIdsByGroupId coordinatorsMapper = context.mock(GetCoordinatorIdsByGroupId.class);

    /** System under test. */
    private GroupCommentTranslator sut;

    /** Test data. */
    private static final long ACTOR_ID = 1111L;

    /** Test data. */
    private static final long STREAM_OWNER_ID = 2222L;

    /** Test data. */
    private static final long DESTINATION_ID = 3333L;

    /** Test data. */
    private static final long ACTIVITY_ID = 4444L;

    /** Test data. */
    private static final long COMMENTOR = 5555L;

    /** Test data. */
    private static final long COORDINATOR_ID = 6666L;

    /**
     * Test the translator.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testTranslateWithCoordinators()
    {
        sut = new GroupCommentTranslator(commentorsMapper, activitiesMapper, coordinatorsMapper);

        final StreamEntityDTO actor = new StreamEntityDTO();
        actor.setId(STREAM_OWNER_ID);

        final ActivityDTO activity = new ActivityDTO();
        activity.setActor(actor);

        final List<Long> coordinators = Arrays.asList(COORDINATOR_ID);

        context.checking(new Expectations()
        {
            {
                oneOf(coordinatorsMapper).execute(DESTINATION_ID);
                will(returnValue(new ArrayList(coordinators)));

                oneOf(activitiesMapper).execute(ACTIVITY_ID, null);
                will(returnValue(activity));

                oneOf(commentorsMapper).execute(ACTIVITY_ID);
                will(returnValue(Collections.singletonList(COMMENTOR)));
            }
        });

        Collection<NotificationDTO> results = sut.translate(ACTOR_ID, DESTINATION_ID, ACTIVITY_ID);
        assertEquals(3, results.size());
        context.assertIsSatisfied();
    }

    /**
     * Test the translator.
     */
    @Test
    public void testTranslateWithoutCoordinators()
    {
        sut = new GroupCommentTranslator(commentorsMapper, activitiesMapper, null);

        final StreamEntityDTO actor = new StreamEntityDTO();
        actor.setId(STREAM_OWNER_ID);

        final ActivityDTO activity = new ActivityDTO();
        activity.setActor(actor);

        context.checking(new Expectations()
        {
            {
                oneOf(activitiesMapper).execute(ACTIVITY_ID, null);
                will(returnValue(activity));

                oneOf(commentorsMapper).execute(ACTIVITY_ID);
                will(returnValue(Collections.singletonList(COMMENTOR)));
            }
        });

        Collection<NotificationDTO> results = sut.translate(ACTOR_ID, DESTINATION_ID, ACTIVITY_ID);
        assertEquals(2, results.size());
        context.assertIsSatisfied();
    }
}
