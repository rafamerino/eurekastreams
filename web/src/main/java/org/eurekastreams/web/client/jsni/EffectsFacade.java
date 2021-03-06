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
package org.eurekastreams.web.client.jsni;

import com.google.gwt.dom.client.Element;

/**
 * Effects.
 */
public class EffectsFacade
{
    /**
     * Fade in an element. It is not necessary to hide the element before fadeIn() is called.
     * 
     * @param e
     *            the element to apply the effect to.
     */
    public void fadeIn(final Element e)
    {
        EffectsFacade.nativeFadeIn(e);
    }
    
    /**
     * Fade out an element.
     * 
     * @param e
     *            the element to apply the effect to.
     */
    public void fadeOut(final Element e)
    {
        EffectsFacade.nativeFadeOut(e);
    }
    
    /**
     * Native implementation of fade out effect.
     * 
     * @param e
     *            the element to apply the effect to.
     */
    public static native void nativeFadeOut(final Element e)/*-{
    	$wnd.jQuery(e).fadeOut('slow');
	}-*/;

    /**
     * Native implementation of fade in effect.
     * 
     * @param e
     *            the element to apply the effect to.
     */
    public static native void nativeFadeIn(final Element e)/*-{
                    $wnd.jQuery(e).hide();
                    $wnd.jQuery(e).fadeIn('slow');
                }-*/;
}
