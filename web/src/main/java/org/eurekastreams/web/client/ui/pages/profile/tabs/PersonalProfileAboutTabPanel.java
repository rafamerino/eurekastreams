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
package org.eurekastreams.web.client.ui.pages.profile.tabs;

import java.util.HashMap;
import java.util.List;

import org.eurekastreams.server.domain.BackgroundItem;
import org.eurekastreams.server.domain.BackgroundItemType;
import org.eurekastreams.server.domain.Enrollment;
import org.eurekastreams.server.domain.Job;
import org.eurekastreams.server.domain.Page;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.data.GotPersonalEducationResponseEvent;
import org.eurekastreams.web.client.events.data.GotPersonalEmploymentResponseEvent;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.model.PersonalEducationModel;
import org.eurekastreams.web.client.model.PersonalEmploymentModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.pages.profile.settings.EducationPanel;
import org.eurekastreams.web.client.ui.pages.profile.settings.EmploymentPanel;
import org.eurekastreams.web.client.ui.pages.profile.widgets.BackgroundItemLinksPanel;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Panel;

/**
 * Panel used on the About tab of a personal profile to display information about the given user.
 */
public class PersonalProfileAboutTabPanel extends ProfileAboutTabPanel
{
    /**
     * Constructor.
     * 
     * @param person
     *            Person whose data to display.
     */
    public PersonalProfileAboutTabPanel(final Person person)
    {
        final HashMap<String, String> workHistoryTabURL = new HashMap<String, String>();
        workHistoryTabURL.put("tab", "Work History & Education");

        final HashMap<String, String> basicInfoTabURL = new HashMap<String, String>();
        basicInfoTabURL.put("tab", "Basic Info");

        final Panel biographyPanel = createTitledPanel("Biography");
        final Panel interestsPanel = createTitledPanel("Interests");
        final Panel workHistoryPanel = createTitledPanel("Work History");
        final Panel educationPanel = createTitledPanel("Education");

        final String currentViewerAccountId = Session.getInstance().getCurrentPerson().getAccountId();

        addLeft(biographyPanel);
        addRight(interestsPanel);
        addRight(workHistoryPanel);
        addRight(educationPanel);

        if (person.getBiography() == null || stripHTML(person.getBiography()).trim().length() < 1)
        {
            if (currentViewerAccountId == person.getAccountId())
            {
                CreateUrlRequest target = new CreateUrlRequest(Page.PERSONAL_SETTINGS, Session.getInstance()
                        .getCurrentPerson().getAccountId());
                target.setParameters(workHistoryTabURL);
                biographyPanel.add(new Hyperlink("Add a biography.", Session.getInstance().generateUrl(target)));
            }
            else
            {
                biographyPanel.add(new HTML("A biography has not been added."));
            }
        }
        else
        {
            HTML bio = new HTML(person.getBiography());
            bio.addStyleName("profile-about-biography");
            biographyPanel.add(bio);
        }

        List<BackgroundItem> items = person.getBackground() == null ? null : person.getBackground().getBackgroundItems(
                BackgroundItemType.SKILL);

        if (items == null || items.isEmpty())
        {
            if (currentViewerAccountId == person.getAccountId())
            {
                CreateUrlRequest target = new CreateUrlRequest(Page.PERSONAL_SETTINGS, Session.getInstance()
                        .getCurrentPerson().getAccountId());
                target.setParameters(basicInfoTabURL);
                interestsPanel.add(new Hyperlink("Add a list of your interests.", Session.getInstance().generateUrl(
                        target)));
            }
            else
            {
                interestsPanel.add(new HTML("Interests have not been added."));
            }

        }
        else
        {
            interestsPanel.add(new BackgroundItemLinksPanel("interests or hobbies", items));
        }

        Session.getInstance().getEventBus().addObserver(GotPersonalEmploymentResponseEvent.class,
                new Observer<GotPersonalEmploymentResponseEvent>()
                {
                    public void update(final GotPersonalEmploymentResponseEvent event)
                    {
                        Session.getInstance().getEventBus().removeObserver(GotPersonalEmploymentResponseEvent.class,
                                this);

                        if (event.getResponse().isEmpty())
                        {
                            if (currentViewerAccountId == person.getAccountId())
                            {
                                CreateUrlRequest target = new CreateUrlRequest(Page.PERSONAL_SETTINGS, Session
                                        .getInstance().getCurrentPerson().getAccountId());
                                target.setParameters(workHistoryTabURL);
                                workHistoryPanel.add(new Hyperlink("Add your work history.", Session.getInstance()
                                        .generateUrl(target)));
                            }
                            else
                            {
                                workHistoryPanel.add(new HTML("Work history has not been added."));
                            }

                        }
                        else
                        {
                            for (Job job : event.getResponse())
                            {
                                workHistoryPanel.add(new EmploymentPanel(job, true));
                            }
                        }
                    }
                });

        Session.getInstance().getEventBus().addObserver(GotPersonalEducationResponseEvent.class,
                new Observer<GotPersonalEducationResponseEvent>()
                {
                    public void update(final GotPersonalEducationResponseEvent event)
                    {
                        Session.getInstance().getEventBus().removeObserver(GotPersonalEducationResponseEvent.class,
                                this);

                        if (event.getResponse().isEmpty())
                        {
                            if (currentViewerAccountId == person.getAccountId())
                            {
                                CreateUrlRequest target = new CreateUrlRequest(Page.PERSONAL_SETTINGS, Session
                                        .getInstance().getCurrentPerson().getAccountId());

                                target.setParameters(workHistoryTabURL);
                                educationPanel.add(new Hyperlink("Add your educational background.", Session
                                        .getInstance().generateUrl(target)));
                            }
                            else
                            {
                                educationPanel.add(new HTML("Education has not been added."));
                            }

                        }
                        else
                        {
                            for (Enrollment enrollment : event.getResponse())
                            {
                                educationPanel.add(new EducationPanel(enrollment, true));
                            }
                        }
                    }
                });

        PersonalEmploymentModel.getInstance().fetch(person.getId(), true);
        PersonalEducationModel.getInstance().fetch(person.getId(), true);
    }

    /**
     * strips html out of String.
     * 
     * @param htmlString
     *            the string to strip.
     * @return a stripped string.
     */
    private String stripHTML(final String htmlString)
    {
        String stripHTML = "<\\/?\\w+((\\s+(\\w|\\w[\\w-]*\\w)"
                + "(\\s*=\\s*(?:\\\".*?\\\"|\'.*?\'|[^\'\\\">\\s]+))?)+\\s*|\\s*)\\/?>";

        String nohtml = htmlString.replaceAll(stripHTML, "");

        nohtml = nohtml.replaceAll("&nbsp;", "");

        return nohtml;
    }
}
