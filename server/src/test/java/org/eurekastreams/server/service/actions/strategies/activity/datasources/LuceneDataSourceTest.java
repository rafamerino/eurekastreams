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
package org.eurekastreams.server.service.actions.strategies.activity.datasources;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.lucene.search.Sort;
import org.eurekastreams.commons.search.ProjectionSearchRequestBuilder;
import org.hibernate.search.jpa.FullTextQuery;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test fixture for LuceneDataSource.
 */
public class LuceneDataSourceTest
{
    /**
     * Mocking context.
     */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * Test execute method with search keywords.
     */
    @Test
    public void testExecuteWithKeywords()
    {
        final ProjectionSearchRequestBuilder builder = context.mock(ProjectionSearchRequestBuilder.class, "builder");
        final ProjectionSearchRequestBuilder allBuilder = context.mock(ProjectionSearchRequestBuilder.class,
                "allBuilder");
        LuceneDataSource sut = new LuceneDataSource(builder, allBuilder);

        final JSONObject request = new JSONObject();
        final JSONObject query = new JSONObject();
        final int pageSize = 388;

        request.put("count", pageSize);
        query.put("keywords", "hithere:(foo)");
        request.put("query", query);

        final FullTextQuery ftq = context.mock(FullTextQuery.class);
        final List<Long> results = new ArrayList<Long>();

        context.checking(new Expectations()
        {
            {
                oneOf(builder).buildQueryFromNativeSearchString("hithere(foo)");
                will(returnValue(ftq));

                oneOf(ftq).getResultList();
                will(returnValue(results));

                oneOf(builder).setPaging(ftq, 0, pageSize);
            }
        });

        assertSame(results, sut.fetch(request, 0L));
        context.assertIsSatisfied();
    }

    /**
     * Test execute method with search keywords, sorting by relevance.
     */
    @Test
    public void testExecuteWithKeywordsSortByRelevance()
    {
        final ProjectionSearchRequestBuilder builder = context.mock(ProjectionSearchRequestBuilder.class, "builder");
        final ProjectionSearchRequestBuilder allBuilder = context.mock(ProjectionSearchRequestBuilder.class,
                "allBuilder");
        LuceneDataSource sut = new LuceneDataSource(builder, allBuilder);

        final JSONObject request = new JSONObject();
        final JSONObject query = new JSONObject();
        final int pageSize = 388;

        request.put("count", pageSize);
        query.put("keywords", "hithere:(foo)");
        query.put("sortBy", "relevance");
        request.put("query", query);

        final FullTextQuery ftq = context.mock(FullTextQuery.class);
        final List<Long> results = new ArrayList<Long>();

        context.checking(new Expectations()
        {
            {
                oneOf(builder).buildQueryFromNativeSearchString("hithere(foo)");
                will(returnValue(ftq));

                oneOf(ftq).getResultList();
                will(returnValue(results));

                oneOf(builder).setPaging(ftq, 0, pageSize);
            }
        });

        assertSame(results, sut.fetch(request, 0L));
        context.assertIsSatisfied();
    }

    /**
     * Test execute method with search keywords, sorting by date.
     */
    @Test
    public void testExecuteWithKeywordsSortByDate()
    {
        final ProjectionSearchRequestBuilder builder = context.mock(ProjectionSearchRequestBuilder.class, "builder");
        final ProjectionSearchRequestBuilder allBuilder = context.mock(ProjectionSearchRequestBuilder.class,
                "allBuilder");
        LuceneDataSource sut = new LuceneDataSource(builder, allBuilder);

        final JSONObject request = new JSONObject();
        final JSONObject query = new JSONObject();
        final int pageSize = 388;

        request.put("count", pageSize);
        query.put("keywords", "hithere:(foo)");
        query.put("sortBy", "date");
        request.put("query", query);

        final FullTextQuery ftq = context.mock(FullTextQuery.class);
        final List<Long> results = new ArrayList<Long>();

        context.checking(new Expectations()
        {
            {
                oneOf(builder).buildQueryFromNativeSearchString("hithere(foo)");
                will(returnValue(ftq));

                oneOf(ftq).getResultList();
                will(returnValue(results));

                // unfortunately it's difficult to check for the correct type of sort
                oneOf(ftq).setSort(with(any(Sort.class)));

                oneOf(builder).setPaging(ftq, 0, pageSize);
            }
        });

        assertSame(results, sut.fetch(request, 0L));
        context.assertIsSatisfied();
    }

    /**
     * Test execute method without search keywords or sort.
     */
    @Test
    public void testExecuteWithoutKeywordsAndWithoutSort()
    {
        final ProjectionSearchRequestBuilder builder = context.mock(ProjectionSearchRequestBuilder.class, "builder");
        final ProjectionSearchRequestBuilder allBuilder = context.mock(ProjectionSearchRequestBuilder.class,
                "allBuilder");
        LuceneDataSource sut = new LuceneDataSource(builder, allBuilder);

        assertNull(sut.fetch(new JSONObject(), 0L));
        context.assertIsSatisfied();
    }

    // TODO: try with sort, try with keywords separately
}
