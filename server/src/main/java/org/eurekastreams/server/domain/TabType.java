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
/**
 * 
 */
package org.eurekastreams.server.domain;

import java.util.HashMap;

/**
 * What states something can be in.
 * 
 * in an enum, name() and valueOf() are bidirectional
 * 
 *  For our purposes, constructor/toEnum and toString are bidirectional. 
 */
public enum TabType 
{ 
    /**  */
    ORG_ABOUT("About Organization"), 
    /**  */
    PERSON_ABOUT("About Person"),
    /** */
    GROUP_ABOUT("About Group"),
    /**  */
    WELCOME("Welcome"), 
    /**  */
    APP("Application");

    /** 
     * human-readable name (ie, "Business Development" as opposed to enum.BD).
     */
    private final String displayableName;
    
    /**
     * for string-enum conversion.
     */
    private static HashMap<String, TabType> map = new HashMap<String, TabType>();
    
    static 
    {
        TabType[] types = TabType.values();
        for (TabType type : types)
        {
            map.put(type.toString(), type);
        }
        
    }
    
    /**
     * constructor for this enum.
     * 
     * @param name a displayable name.
     */
    private TabType(final String name)
    {
        displayableName = name;
    }
    
    /**
     * 
     * @param displayableName to convert.
     * @return the enum corresponding to that string.
     */
    public static TabType toEnum(final String displayableName) 
    {
        return map.get(displayableName);
    }
    
    /**
     * @return the displayable name that was passed into the constructor.
     */
    @Override
    public String toString() 
    { 
        return displayableName; 
    }

}
