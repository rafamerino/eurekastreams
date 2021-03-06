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
package org.eurekastreams.web.client.ui.pages.start;

import org.eurekastreams.server.action.request.start.SetGadgetStateRequest;
import org.eurekastreams.server.action.request.start.SetGadgetStateRequest.State;
import org.eurekastreams.server.domain.Gadget;
import org.eurekastreams.server.domain.gadgetspec.GadgetMetaDataDTO;
import org.eurekastreams.server.domain.gadgetspec.UserPrefDTO;
import org.eurekastreams.web.client.events.GadgetStateChangeEvent;
import org.eurekastreams.web.client.events.GotGadgetMetaDataEvent;
import org.eurekastreams.web.client.events.HideNotificationEvent;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.ShowNotificationEvent;
import org.eurekastreams.web.client.events.UpdateGadgetPrefsEvent;
import org.eurekastreams.web.client.events.data.DeletedGadgetResponseEvent;
import org.eurekastreams.web.client.events.data.UnDeletedGadgetResponseEvent;
import org.eurekastreams.web.client.jsni.GadgetRenderer;
import org.eurekastreams.web.client.jsni.WidgetJSNIFacadeImpl;
import org.eurekastreams.web.client.model.GadgetModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.notifier.Notification;
import org.eurekastreams.web.client.ui.common.notifier.UndoDeleteNotification;
import org.eurekastreams.web.client.ui.pages.start.layouts.DropZonePanel;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * GadgetZonePanel. The UI Representation of a Gadget. This is used to wrap the shindig gadget around. JSNI methods are
 * used to add functionality to the GWT elements that are provided by Shindig. This class only wraps methods that
 * manipulate gadget specific functionality. It should not wrap gadgetContainer Methods.
 * 
 */
public class GadgetPanel extends FlowPanel
{

    /**
     * The GadgetRenderer is the JSNI interface to the gadget container.
     */
    private GadgetRenderer gadgetRenderer = GadgetRenderer.getInstance();

    /**
     * The current state of the gadget.
     */
    private SetGadgetStateRequest.State gadgetState = SetGadgetStateRequest.State.NORMAL;

    /**
     * prefix for all dynamically created Render Zones.
     */
    private String gadgetRenderZonePrefix = "gadget-zone-render-zone-";

    /**
     * prefix for all dynamically created gadget title label styles.
     */
    private String gadgetTitleLabelPrefix = "remote_iframe_";

    /**
     * minimize button.
     */
    Anchor minimizeButton = new Anchor("Minimize");

    /**
     * refresh button.
     */
    Anchor refreshButton = new Anchor("Refresh");

    /**
     * Edit button.
     */
    Anchor editButton = new Anchor("Edit Settings");

    /**
     * Close button.
     */
    Anchor closeButton = new Anchor("Remove");

    /**
     * Maximize button.
     */
    Anchor maximizeButton = new Anchor("Maximize");

    /**
     * gadgetData sent via the constructor argument.
     */
    Gadget gadgetData;

    /**
     * GadgetZone Id.
     */
    Long gadgetIdModifier = null;

    /**
     * this field is needed because of a rendering problem that shindig has if a gadget is minimized by default.
     */
    Boolean initallyMinimized = false;

    /**
     * The second of two children of the GadgetZonePanel, The gadget is rendered in this Panel.
     */
    FlowPanel renderZone = new FlowPanel();

    /**
     * One of two children of the GadgetZonePanel, contains all buttons and the title for the gadget.
     */
    FlowPanel titleBar = new FlowPanel();

    /**
     * The title for the gadget.
     */
    FlowPanel title = new FlowPanel();

    /**
     * Label for the gadget title.
     */
    Label titleLabel = new Label();

    /**
     * The Element ID of the div to render a gadget in. This will simply be a prefix with the gadgetIdModifier added to
     * it.
     */
    private String gadgetRenderZoneId;

    /**
     * needed to enable drag and drop.
     */
    private FocusPanel titleBarContainer = new FocusPanel();

    /**
     * The class applied when a gadget is minimized.
     */
    private static final String MINIMIZED_CSS_CLASS = "gadget-zone-minimized";

    /**
     * The gadget drag controller.
     */
    private PickupDragController gadgetDragController;

    /**
     * The parent drop zone.
     */
    private DropZonePanel parentDropZone = null;

    /**
     * Creates the Gadget render zone and gives the zone a incremented id.
     * 
     * @param gadget
     *            The Gadget from the Model you are rendering.
     */
    public GadgetPanel(final Gadget gadget)
    {
        gadgetIdModifier = gadget.getId();
        gadgetRenderZoneId = gadgetRenderZonePrefix + gadgetIdModifier.toString();

        renderZone.getElement().setId(gadgetRenderZoneId);

        this.gadgetData = gadget;
        // Style the pieces of the title bar
        title.setStylePrimaryName("gadget-zone-chrome-title-bar-title-button");
        titleLabel.getElement().setId(gadgetTitleLabelPrefix + gadgetIdModifier.toString() + "_title");
        title.add(titleLabel);
        closeButton.addStyleName("gadget-close");
        maximizeButton.addStyleName("gadget-maximize");
        refreshButton.addStyleName("gadget-refresh");
        minimizeButton.addStyleName("gadget-minimize");
        editButton.addStyleName("gadget-edit");

        closeButton.addStyleName("gadget-button");
        maximizeButton.addStyleName("gadget-button");
        refreshButton.addStyleName("gadget-button");
        minimizeButton.addStyleName("gadget-button");
        editButton.addStyleName("gadget-button");

        // Creates the title Bar
        titleBar.setStylePrimaryName("gadget-zone-chrome-title-bar");
        titleBar.getElement().setId("gadgets-gadget-title-bar-" + gadgetIdModifier.toString());
        // Add buttons to title bar
        titleBar.add(minimizeButton);
        titleBar.add(title);
        titleBar.add(maximizeButton);
        titleBar.add(closeButton);
        titleBar.add(editButton);
        titleBar.add(refreshButton);

        titleBarContainer.add(titleBar);
        this.setStylePrimaryName("gadget-zone");
        this.add(titleBarContainer);
        this.add(renderZone);

        // set up the close command
        closeButton.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent event)
            {
                if (new WidgetJSNIFacadeImpl().confirm("Are you sure you want to delete this app?"))
                {
                    GadgetModel.getInstance().delete(gadgetData.getId());
                }
            }
        });
        editButton.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent event)
            {
                gadgetRenderer.openPreferences(gadgetIdModifier.toString());
                if (gadgetState == State.MINIMIZED)
                {
                    if (initallyMinimized)
                    {
                        gadgetRenderer.registerSingleGadgetInContainer(gadgetRenderZoneId, gadget.getGadgetDefinition()
                                .getUrl(), gadgetIdModifier, gadget.getGadgetDefinition().getId(), gadget
                                .getGadgetUserPref());

                        gadgetRenderer.addGadgetChrome(gadgetRenderZoneId, gadgetIdModifier);

                        gadgetRenderer.renderGadget(gadgetIdModifier.toString());

                        initallyMinimized = false;
                    }
                    setGadgetState(State.NORMAL);
                }
            }
        });
        refreshButton.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent event)
            {
                gadgetRenderer.refreshGadget(gadgetIdModifier.toString());
            }
        });
        maximizeButton.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent event)
            {
                if (gadgetState == State.MAXIMIZED)
                {
                    setGadgetState(State.NORMAL);
                }
                else
                {
                    setGadgetState(State.MAXIMIZED);
                }
            }
        });
        minimizeButton.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent event)
            {
                if (gadgetState == State.MINIMIZED)
                {
                    if (initallyMinimized)
                    {
                        gadgetRenderer.registerSingleGadgetInContainer(gadgetRenderZoneId, gadget.getGadgetDefinition()
                                .getUrl(), gadgetIdModifier, gadget.getGadgetDefinition().getId(), gadget
                                .getGadgetUserPref());

                        gadgetRenderer.addGadgetChrome(gadgetRenderZoneId, gadgetIdModifier);

                        gadgetRenderer.renderGadget(gadgetIdModifier.toString());

                        initallyMinimized = false;
                    }
                    setGadgetState(State.NORMAL);
                }
                else
                {
                    setGadgetState(State.MINIMIZED);
                }
            }
        });

        if (gadgetData.isMinimized())
        {
            initallyMinimized = true;
            setGadgetState(State.MINIMIZED);
        }

        setUpEvents();

        if (!initallyMinimized)
        {
            gadgetRenderer.registerSingleGadgetInContainer(gadgetRenderZoneId, gadget.getGadgetDefinition().getUrl(),
                    gadgetIdModifier, gadget.getGadgetDefinition().getId(), gadget.getGadgetUserPref());

            gadgetRenderer.addGadgetChrome(gadgetRenderZoneId, gadgetIdModifier);
        }
    }

    /**
     * Set up the event handling.
     */
    private void setUpEvents()
    {
        final GadgetPanel thisBuffered = this;

        Session.getInstance().getEventBus().addObserver(DeletedGadgetResponseEvent.class,
                new Observer<DeletedGadgetResponseEvent>()
                {
                    public void update(final DeletedGadgetResponseEvent event)
                    {
                        if (event.getResponse() == gadgetData.getId())
                        {
                            thisBuffered.setVisible(false);
                            thisBuffered.addStyleName("hidden");
                            Session.getInstance().getEventBus().notifyObservers(
                                    new ShowNotificationEvent(new Notification(new UndoDeleteNotification("App",
                                            new ClickHandler()
                                            {
                                                public void onClick(final ClickEvent event)
                                                {
                                                    GadgetModel.getInstance().undoDelete(gadgetData.getId());
                                                    Session.getInstance().getEventBus().notifyObservers(
                                                            new HideNotificationEvent());
                                                }
                                            }), "")));
                        }
                    }
                });
        Session.getInstance().getEventBus().addObserver(UnDeletedGadgetResponseEvent.class,
                new Observer<UnDeletedGadgetResponseEvent>()
                {
                    public void update(final UnDeletedGadgetResponseEvent arg1)
                    {
                        if (arg1.getResponse().getId() == gadgetData.getId())
                        {
                            thisBuffered.setVisible(true);
                            thisBuffered.removeStyleName("hidden");
                        }
                    }
                });
        Session.getInstance().getEventBus().addObserver(GotGadgetMetaDataEvent.class,
                new Observer<GotGadgetMetaDataEvent>()
                {
                    public void update(final GotGadgetMetaDataEvent arg1)
                    {
                        for (GadgetMetaDataDTO metadata : arg1.getMetadata())
                        {
                            if (metadata.getGadgetDefinition().getId() == gadgetData.getGadgetDefinition().getId())
                            {
                                boolean foundSetting = false;
                                String titleText = metadata.getTitle();

                                if (metadata.getUserPrefs() != null)
                                {
                                    // had to go to with a normal for loop rather than a for-in due to some
                                    // issues with GWT making wierd javascript
                                    for (int i = 0; i < metadata.getUserPrefs().size(); i++)
                                    {
                                        if (metadata.getUserPrefs().get(i).getClass() == UserPrefDTO.class)
                                        {
                                            UserPrefDTO userPref = (UserPrefDTO) metadata.getUserPrefs().get(i);
                                            if (!userPref.getDataType().equals(UserPrefDTO.DataType.HIDDEN))
                                            {
                                                foundSetting = true;
                                                break;
                                            }
                                            gadgetData.getGadgetUserPref();
                                        }
                                    }
                                }
                                editButton.setVisible(foundSetting);
                                if (gadgetData.getGadgetUserPref() != null
                                        && !gadgetData.getGadgetUserPref().equals(""))
                                {
                                    JSONObject gadgetUserPrefsJSON = (JSONObject) JSONParser.parse(gadgetData
                                            .getGadgetUserPref());
                                    String gadgetTitleFromUserPrefs = getGadgetTitleFromUserPrefs(gadgetUserPrefsJSON
                                            .getJavaScriptObject());
                                    if (gadgetTitleFromUserPrefs != null && !gadgetTitleFromUserPrefs.equals(""))
                                    {
                                        titleText = getGadgetTitleFromUserPrefs(gadgetUserPrefsJSON
                                                .getJavaScriptObject());
                                    }
                                }
                                if (titleLabel.getText() == "")
                                {
                                    titleLabel.setText(titleText);
                                }
                            }
                        }
                    }
                });
        Session.getInstance().getEventBus().addObserver(GadgetStateChangeEvent.class,
                new Observer<GadgetStateChangeEvent>()
                {
                    public void update(final GadgetStateChangeEvent event)
                    {
                        if (event.getId() == gadgetData.getId())
                        {
                            if (event.getView().equals("canvas"))
                            {
                                setGadgetState(State.MAXIMIZED, event.getParams());
                            }
                            else if (event.getView().equals("home"))
                            {
                                setGadgetState(State.NORMAL, event.getParams());
                            }
                        }
                    }
                });
        Session.getInstance().getEventBus().addObserver(UpdateGadgetPrefsEvent.class,
                new Observer<UpdateGadgetPrefsEvent>()
                {
                    public void update(final UpdateGadgetPrefsEvent event)
                    {
                        if (event.getId() == gadgetData.getId())
                        {
                            gadgetData.setGadgetUserPref(event.getUserPrefs());
                        }
                    }
                });
    }

    /**
     * @param inParentDropZone
     *            the parent drop zone.
     */
    public void setDropZone(final DropZonePanel inParentDropZone)
    {
        parentDropZone = inParentDropZone;
    }

    /**
     * Gets the entity type of the JSON object.
     * 
     * @param jsObj
     *            the JSON object.
     * @return the entity type.
     */
    private native String getGadgetTitleFromUserPrefs(final JavaScriptObject jsObj)
    /*-{
         return jsObj['eureka-container-gadget-title'];
    }-*/;

    /**
     * Set the gadget state w/o params.
     * 
     * @param state
     *            the gadget state.
     */
    public void setGadgetState(final State state)
    {
        setGadgetState(state, null);
    }

    /**
     * Gets the state.
     * 
     * @return the state.
     */
    public State getGadgetState()
    {
        return gadgetState;
    }

    /**
     * Setter.
     * 
     * @param state
     *            new state of the gadget
     * @param viewParams
     *            the params.
     */
    public void setGadgetState(final State state, final String viewParams)
    {
        State previousGadgetState = gadgetState;
        gadgetState = state;

        switch (state)
        {
        case NORMAL:
            RootPanel.get().removeStyleName("maximized-gadget");

            if (null != parentDropZone)
            {
                parentDropZone.removeStyleName("maximized-drop-zone");
            }

            gadgetRenderer.restoreGadgetZone();
            minimizeButton.removeStyleName("minimized");
            renderZone.setStyleName("");
            if (previousGadgetState == State.MAXIMIZED)
            {
                gadgetRenderer.gadgetIFrameUrlRefreshing(gadgetIdModifier.toString());

                gadgetRenderer.changeContainerView("home");
                gadgetRenderer.refreshGadgetIFrameUrl(gadgetIdModifier.toString(), viewParams);
            }
            break;
        case MAXIMIZED:
            RootPanel.get().addStyleName("maximized-gadget");

            if (null != parentDropZone)
            {
                parentDropZone.addStyleName("maximized-drop-zone");
            }

            gadgetRenderer.gadgetIFrameUrlRefreshing(gadgetIdModifier.toString());

            gadgetRenderer.changeContainerView("canvas");
            gadgetRenderer.refreshGadgetIFrameUrl(gadgetIdModifier.toString(), viewParams);
            gadgetRenderer.changeContainerView("home");

            minimizeButton.removeStyleName("minimized");

            gadgetRenderer.maximizeGadgetZone(this.getElement());
            break;
        case MINIMIZED:
            minimizeButton.addStyleName("minimized");
            renderZone.setStyleName(MINIMIZED_CSS_CLASS);
            break;
        default:
            break;
        }

        GadgetModel.getInstance().setState(new SetGadgetStateRequest(gadgetData.getId(), state));
    }

    /**
     * Make the gadget draggable.
     * 
     * @param inGadgetDragController
     *            the gadget drag controller.
     */
    public void makeGadgetDraggable(final PickupDragController inGadgetDragController)
    {
        gadgetDragController = inGadgetDragController;
        gadgetDragController.makeDraggable(this, titleBarContainer);
    }

    /**
     * Make the gadget undraggable.
     */
    public void makeGadgetUndraggable()
    {
        gadgetDragController.makeNotDraggable(this);
    }

    /**
     * Get the gadget data.
     * 
     * @return the gadget data.
     */
    public Gadget getGadgetData()
    {
        return gadgetData;
    }

}
