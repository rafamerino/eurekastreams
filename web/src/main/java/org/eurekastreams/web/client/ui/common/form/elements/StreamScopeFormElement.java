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
package org.eurekastreams.web.client.ui.common.form.elements;

import java.io.Serializable;
import java.util.LinkedList;

import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.StreamScopeAddedEvent;
import org.eurekastreams.web.client.events.StreamScopeDeletedEvent;
import org.eurekastreams.web.client.ui.common.autocomplete.AutoCompleteEntityDropDownPanel;
import org.eurekastreams.web.client.ui.common.stream.filters.list.StreamScopeListPanel;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * Form element for stream scopes.
 *
 */
public class StreamScopeFormElement extends FlowPanel implements FormElement
{
    /**
     * The label.
     */
    private Label listMembers;
    /**
     * The scope list panel.
     */
    private StreamScopeListPanel scopeListPanel;

    /**
     * The key.
     *
     * @return the key.
     */
    public String getKey()
    {
        return key;
    }
    
    /**
     * The form element's key/id.
     */
    private String key;
    
    /**
     * Flag to allow multiple stream scopes to be selected.
     */
    boolean allowMultiple;

    /**
     * The value.
     *
     * @return the value.
     */
    public Serializable getValue()
    {
        if (allowMultiple)
        {
            return scopeListPanel.getScopes();
        }
        else
        {
            return scopeListPanel.getScopes().size() > 0 ? scopeListPanel.getScopes().get(0).getUniqueKey() : "";
        }
    }

    /**
     * Default constructor.
     *
     * @param inKey
     *            the key name for the element.
     * @param inScopes
     *            the scopes.
     * @param inTitle
     *            the form element title.
     * @param inInstructions
     *            the instructions.
     * @param isRequired
     *            is the element required.
     * @param inAllowMultiple
     *            does this element allow multiple scopes to be selected.
     * @param inAutoCompleteUrl
     *            the url used to retrieve search results for the autocomplete box.
     * @param inMaxLength
     *            the maximum characters for the autocomplete textbox.
     */
    public StreamScopeFormElement(final String inKey, final LinkedList<StreamScope> inScopes, final String inTitle,
            final String inInstructions, final boolean isRequired, final boolean inAllowMultiple,
            final String inAutoCompleteUrl, final int inMaxLength)
    {
        key = inKey;
        allowMultiple = inAllowMultiple;
        this.addStyleName("scope-form-element");

        listMembers = new Label(inTitle);
        listMembers.addStyleName("form-label");

        Label instructions = new Label(inInstructions);
        instructions.addStyleName("form-instructions");

        scopeListPanel = new StreamScopeListPanel(inScopes);
        
        final AutoCompleteEntityDropDownPanel autoComplete =
                new AutoCompleteEntityDropDownPanel(inAutoCompleteUrl);
        autoComplete.setMaxLength(inMaxLength);
        autoComplete.setOnItemSelectedCommand(new AutoCompleteEntityDropDownPanel.OnItemSelectedCommand()
        {
            public void itemSelected(final JavaScriptObject obj)
            {
                if (!getEntityType(obj.toString()).equals("NOTSET"))
                {
                    autoComplete.clearText();
                    
                    if (!allowMultiple)
                    {
                        autoComplete.setVisible(false);
                    }
                    EventBus.getInstance().notifyObservers(
                            new StreamScopeAddedEvent(new StreamScope(getDisplayName(obj.toString()), ScopeType
                                    .valueOf(getEntityType(obj.toString())), getUniqueId(obj.toString()), Long
                                    .parseLong(getStreamScopeId(obj.toString())))));
                }
            }
        });
        
        EventBus.getInstance().addObserver(StreamScopeDeletedEvent.getEvent(),
                new Observer<StreamScopeDeletedEvent>()
                {
                    public void update(final StreamScopeDeletedEvent arg1)
                    {
                        if (!allowMultiple)
                        {
                            autoComplete.setVisible(true);
                        }
                    }
                });

        if (!allowMultiple && !inScopes.isEmpty())
        {
            autoComplete.setVisible(false);
        }

        this.add(listMembers);
        this.add(scopeListPanel);
        this.add(autoComplete);
        if (isRequired)
        {
            Label requiredLabel = new Label("(required)");
            requiredLabel.addStyleName("required-form-label");
            this.add(requiredLabel);
        }

        this.add(instructions);
    }

    /**
     * Gets the display name of the JSON object.
     *
     * @param jsObj
     *            the JSON object.
     * @return the display name.
     */
    private native String getDisplayName(final String jsObj) /*-{ return jsObj.split(",")[0]; }-*/;

    /**
     * Gets the entity type of the JSON object.
     *
     * @param jsObj
     *            the JSON object.
     * @return the entity type.
     */
    private native String getEntityType(final String jsObj) /*-{ return jsObj.split(",")[1]; }-*/;

    /**
     * Gets the unique id of the JSON object.
     *
     * @param jsObj
     *            the JSON object.
     * @return the unique id.
     */
    private native String getUniqueId(final String jsObj) /*-{ return jsObj.split(",")[2]; }-*/;

    /**
     * Gets the StreamScope id of the JSON object.
     *
     * @param jsObj
     *            the JSON object.
     * @return the StreamScope id.
     */
    private native String getStreamScopeId(final String jsObj) /*-{ return jsObj.split(",")[3]; }-*/;

    /**
     * Gets called if this element has an error.
     *
     * @param errMessage
     *            the error Message.
     */
    public void onError(final String errMessage)
    {
        listMembers.addStyleName("form-error");
    }

    /**
     * Gets called if this element was successful.
     */
    public void onSuccess()
    {
        listMembers.removeStyleName("form-error");
    }

}
