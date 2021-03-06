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
package org.eurekastreams.server.search.bridge;

import java.util.List;

import org.eurekastreams.server.domain.BackgroundItem;
import org.hibernate.search.bridge.StringBridge;

/**
 * String Bridge for a List&lt;BackgroundItem&gt;.
 */
public class BackgroundItemListStringBridge implements StringBridge
{
    /**
     * Convert the input List&lt;BackgroundItem&gt; into a searchable String.
     * 
     * @param listObj
     *            the List&lt;BackgroundItem&gt; to convert
     * @return a string concatenation of the background items.
     */
    @SuppressWarnings("unchecked")
    @Override
    public String objectToString(final Object listObj)
    {
        if (listObj == null || !(listObj instanceof List))
        {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        
        sb.append(" ");

        for (BackgroundItem item : (List<BackgroundItem>) listObj)
        {
            sb.append(item.getName());
            sb.append(" ");
        }
        
        return sb.toString();
    }
}
