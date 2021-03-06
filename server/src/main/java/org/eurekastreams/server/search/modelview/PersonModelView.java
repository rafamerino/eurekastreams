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
package org.eurekastreams.server.search.modelview;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eurekastreams.commons.search.modelview.ModelView;
import org.eurekastreams.server.domain.ActivityRestrictionEntity;
import org.eurekastreams.server.domain.BackgroundItem;
import org.eurekastreams.server.domain.Followable;
import org.eurekastreams.server.domain.HasEmail;

/**
 * A lightweight view of a Person containing everything needed for display of a search result of an Person.
 */
public class PersonModelView extends ModelView implements Serializable, HasEmail, Followable, ActivityRestrictionEntity
{
    /**
     * Serial version uuid.
     */
    private static final long serialVersionUID = -4695870818188965332L;

    /**
     * The key for the title.
     */
    public static final String TITILE_KEY = "title";

    /**
     * Key for Parent org.
     */
    public static final String ORG_PARENT_KEY = "parentOrganization";

    /**
     * related org key.
     */
    public static final String RELATED_ORG_KEY = "relatedOrganizations";

    /**
     * The key for the nick name/preferred name.
     */
    public static final String PREFERREDNAME_KEY = "preferredName";
    /**
     * key for description.
     */
    public static final String DESCRIPTION_KEY = "jobDescription";
    /**
     * key for skills.
     */
    public static final String SKILLS_KEY = "skills";

    /**
     * key for work phone.
     */
    public static final String WORKPHONE_KEY = "workPhone";
    /**
     * key for cell phone.
     */
    public static final String CELLPHONE_KEY = "cellPhone";
    /**
     * key for fax.
     */
    public static final String FAX_KEY = "fax";
    /**
     * key for email.
     */
    public static final String EMAIL_KEY = "email";

    /**
     * form error message.
     */
    public static final String SKILLS_MESSAGE = "Interests keywords support up to "
            + BackgroundItem.MAX_BACKGROUND_ITEM_NAME_LENGTH + " characters each.";

    /**
     * Role in the system.
     *
     */
    public enum Role implements Serializable
    {
        /**
         * Represents *any* group coordinator.
         */
        GROUP_COORDINATOR,
        /**
         * Represents *any* org coordinator.
         */
        ORG_COORDINATOR,
        /**
         * Represents the root org coordinator.
         */
        ROOT_ORG_COORDINATOR
    }

    /**
     * Set of roles for the person.
     */
    private Set<Role> roles = new HashSet<Role>();

    /**
     * The date this was added.
     */
    private Date dateAdded = UNINITIALIZED_DATE_VALUE;

    /**
     * The account id of this person.
     */
    private String accountId = UNINITIALIZED_STRING_VALUE;

    /**
     * The open social id of this person.
     */
    private String openSocialId = UNINITIALIZED_STRING_VALUE;

    /**
     * The person's title.
     */
    private String title = UNINITIALIZED_STRING_VALUE;

    /**
     * The parent organization's id.
     */
    private long parentOrganizationId = UNINITIALIZED_LONG_VALUE;

    /**
     * The parent organization's short name.
     */
    private String parentOrganizationShortName = UNINITIALIZED_STRING_VALUE;

    /**
     * The parent organization's full name.
     */
    private String parentOrganizationName = UNINITIALIZED_STRING_VALUE;

    /**
     * The person's description.
     */
    private String description = UNINITIALIZED_STRING_VALUE;

    /**
     * The number of people following this person.
     */
    private int followersCount = UNINITIALIZED_INTEGER_VALUE;

    /**
     * Line index for composite streams.
     */
    private int compositeStreamHiddenLineIndex = UNINITIALIZED_INTEGER_VALUE;

    /**
     * Line index for stream searches.
     */
    private int compositeStreamSearchHiddenLineIndex = UNINITIALIZED_INTEGER_VALUE;

    /**
     * Line index for group streams.
     */
    private int groupStreamHiddenLineIndex = UNINITIALIZED_INTEGER_VALUE;

    /**
     * The person's display name.
     */
    private String displayName = UNINITIALIZED_STRING_VALUE;

    /**
     * The person's avatar id.
     */
    private String avatarId = UNINITIALIZED_STRING_VALUE;

    /**
     * The number of updates for this person.
     */
    private int updatesCount = UNINITIALIZED_INTEGER_VALUE;

    /**
     * Stream id for this person.
     */
    private long streamId = UNINITIALIZED_LONG_VALUE;

    /**
     * CompositeStream id for this person.
     */
    private long compositeStreamId = UNINITIALIZED_LONG_VALUE;

    /**
     * Set of videos a user has opted out of seeing.
     */
    private HashSet<Long> optOutVideos = new HashSet<Long>();

    /**
     * ToS acceptance flag.
     */
    private boolean tosAcceptance = false;

    /**
     * Flag to determine if person's activities can be commented on.
     */
    private boolean commentable = true;

    /**
     * Flag to determine if person's stream can be posted on.
     */
    private boolean streamPostable = true;

    /**
     * Authentication type.
     */
    private AuthenticationType authenticationType = AuthenticationType.NOTSET;

    /**
     * Email address for this person.
     */
    private String email = UNINITIALIZED_STRING_VALUE;

    /**
     * List of related organization ids.
     */
    private List<Long> relatedOrganizationIds;

    /**
     * The date the current user last accepted the terms of service.
     */
    private Date lastAcceptedTermsOfService = UNINITIALIZED_DATE_VALUE;

    /**
     * Get the name of this entity.
     *
     * @return the name of this entity
     */
    @Override
    protected String getEntityName()
    {
        return "Person";
    }

    /**
     * Empty constructor.
     */
    public PersonModelView()
    {
    }

    /**
     * Load this object's properties from the input Map.
     *
     * @param properties
     *            the Map of the properties to load
     */
    @SuppressWarnings("unchecked")
    @Override
    public void loadProperties(final Map<String, Object> properties)
    {
        // let the parent class get its properties first
        super.loadProperties(properties);

        if (properties.containsKey("dateAdded"))
        {
            setDateAdded((Date) properties.get("dateAdded"));
        }
        if (properties.containsKey("accountId"))
        {
            setAccountId((String) properties.get("accountId"));
        }
        if (properties.containsKey("openSocialId"))
        {
            setOpenSocialId((String) properties.get("openSocialId"));
        }
        if (properties.containsKey("avatarId"))
        {
            setAvatarId((String) properties.get("avatarId"));
        }
        if (properties.containsKey("optOutVideoIds"))
        {
            String videoIds = (String) properties.get("optOutVideoIds");
            HashSet<Long> videoIdSet = new HashSet();

            if (videoIds != null && !(videoIds.isEmpty()))
            {
                String[] videoIdsArray = videoIds.split(";");

                for (String vidId : videoIdsArray)
                {
                    // if it tokenized the ending ; then don't set a blank long.
                    if (!vidId.isEmpty())
                    {
                        videoIdSet.add(Long.valueOf(vidId.trim()));
                    }
                }
            }
            setOptOutVideos(videoIdSet);
        }
        if (properties.containsKey("lastName") && properties.containsKey("preferredName"))
        {
            // this should be done as a ClassBridge but ClassBridges can't currently be projected
            setDisplayName((String) properties.get("preferredName") + " " + (String) properties.get("lastName"));
        }
        if (properties.containsKey("title"))
        {
            setTitle((String) properties.get("title"));
        }
        if (properties.containsKey("parentOrganizationShortName"))
        {
            setParentOrganizationShortName((String) properties.get("parentOrganizationShortName"));
        }
        if (properties.containsKey("parentOrganizationId"))
        {
            setParentOrganizationId((Long) properties.get("parentOrganizationId"));
        }
        if (properties.containsKey("parentOrganizationName"))
        {
            setParentOrganizationName((String) properties.get("parentOrganizationName"));
        }
        if (properties.containsKey("jobDescription"))
        {
            setDescription((String) properties.get("jobDescription"));
        }
        if (properties.containsKey("followersCount"))
        {
            setFollowersCount((Integer) properties.get("followersCount"));
        }
        if (properties.containsKey("updatesCount"))
        {
            setUpdatesCount((Integer) properties.get("updatesCount"));
        }
        if (properties.containsKey("streamId"))
        {
            setStreamId((Long) properties.get("streamId"));
        }
        if (properties.containsKey("compositeStreamHiddenLineIndex"))
        {
            setCompositeStreamHiddenLineIndex((Integer) properties.get("compositeStreamHiddenLineIndex"));
        }
        if (properties.containsKey("compositeStreamSearchHiddenLineIndex"))
        {
            setCompositeStreamSearchHiddenLineIndex((Integer) properties.get("compositeStreamSearchHiddenLineIndex"));
        }
        if (properties.containsKey("groupStreamHiddenLineIndex"))
        {
            setGroupStreamHiddenLineIndex((Integer) properties.get("groupStreamHiddenLineIndex"));
        }
        if (properties.containsKey("compositeStreamId"))
        {
            setCompositeStreamId((Long) properties.get("compositeStreamId"));
        }
        if (properties.containsKey("tosAcceptance"))
        {
            setTosAcceptance((Boolean) properties.get("tosAcceptance"));
        }
        if (properties.containsKey("authenticationType"))
        {
            setAuthenticationType((AuthenticationType) properties.get("authenticationType"));
        }
        if (properties.containsKey("email"))
        {
            setEmail((String) properties.get("email"));
        }
        if (properties.containsKey("commentable"))
        {
            setCommentable((Boolean) properties.get("commentable"));
        }
        if (properties.containsKey("streamPostable"))
        {
            setStreamPostable((Boolean) properties.get("streamPostable"));
        }
        if (properties.containsKey("relatedOrganizationIds"))
        {
            setRelatedOrganizationIds((List<Long>) properties.get("relatedOrganizationIds"));
        }
        if (properties.containsKey("lastAcceptedTermsOfService"))
        {
            setLastAcceptedTermsOfService((Date) properties.get("lastAcceptedTermsOfService"));
        }
    }

    /**
     * Get the date the person was added to the system.
     *
     * @return the dateAdded
     */
    public Date getDateAdded()
    {
        return dateAdded;
    }

    /**
     * Set the date the person was added to the system.
     *
     * @param inDateAdded
     *            the dateAdded to set
     */
    public void setDateAdded(final Date inDateAdded)
    {
        this.dateAdded = inDateAdded;
    }

    /**
     * Get the person's account id.
     *
     * @return the accountId
     */
    public String getAccountId()
    {
        return accountId;
    }

    /**
     * Set the person's account id.
     *
     * @param inAccountId
     *            the accountId to set
     */
    public void setAccountId(final String inAccountId)
    {
        this.accountId = inAccountId;
    }

    /**
     * Get the person's OpenSocial id.
     *
     * @return the openSocialId
     */
    public String getOpenSocialId()
    {
        return openSocialId;
    }

    /**
     * Set the person's OpenSocial id.
     *
     * @param inOpenSocialId
     *            the openSocialId to set
     */
    public void setOpenSocialId(final String inOpenSocialId)
    {
        openSocialId = inOpenSocialId;
    }

    /**
     * Get the person's title.
     *
     * @return the title
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * Set the person's title.
     *
     * @param inTitle
     *            the title to set
     */
    public void setTitle(final String inTitle)
    {
        this.title = inTitle;
    }

    /**
     * Get the person's parent organization's id.
     *
     * @return the parentOrganizationId
     */
    public long getParentOrganizationId()
    {
        return parentOrganizationId;
    }

    /**
     * Set the person's parent organization's id.
     *
     * @param inParentOrganizationId
     *            the parentOrganizationId to set
     */
    public void setParentOrganizationId(final long inParentOrganizationId)
    {
        parentOrganizationId = inParentOrganizationId;
    }

    /**
     * Get the person's parent organization's short name.
     *
     * @return the parentOrganizationShortName
     */
    public String getParentOrganizationShortName()
    {
        return parentOrganizationShortName;
    }

    /**
     * Set the person's parent organization's short name.
     *
     * @param inParentOrganizationShortName
     *            the parentOrganizationShortName to set
     */
    public void setParentOrganizationShortName(final String inParentOrganizationShortName)
    {
        this.parentOrganizationShortName = inParentOrganizationShortName;
    }

    /**
     * Get the person's parent organization's name.
     *
     * @return the parentOrganizationName
     */
    public String getParentOrganizationName()
    {
        return parentOrganizationName;
    }

    /**
     * Set the person's parent organization's name.
     *
     * @param inParentOrganizationName
     *            the parentOrganizationName to set
     */
    public void setParentOrganizationName(final String inParentOrganizationName)
    {
        this.parentOrganizationName = inParentOrganizationName;
    }

    /**
     * Get the person's description.
     *
     * @return the description
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Set the person's description.
     *
     * @param inDescription
     *            the description to set
     */
    public void setDescription(final String inDescription)
    {
        this.description = inDescription;
    }

    /**
     * Get the number of people following this person.
     *
     * @return the followersCount
     */
    public int getFollowersCount()
    {
        return followersCount;
    }

    /**
     * Set the number of people following this person.
     *
     * @param inFollowersCount
     *            the followersCount to set
     */
    public void setFollowersCount(final int inFollowersCount)
    {
        this.followersCount = inFollowersCount;
    }

    /**
     * Get the person's display name.
     *
     * @return the displayName
     */
    public String getDisplayName()
    {
        return displayName;
    }

    /**
     * Set the person's display name.
     *
     * @param inDisplayName
     *            the displayName to set
     */
    public void setDisplayName(final String inDisplayName)
    {
        this.displayName = inDisplayName;
    }

    /**
     * Get the person's avatar id.
     *
     * @return the avatarId
     */
    public String getAvatarId()
    {
        return avatarId;
    }

    /**
     * Set the person's avatar id.
     *
     * @param inAvatarId
     *            the avatarId to set
     */
    public void setAvatarId(final String inAvatarId)
    {
        this.avatarId = inAvatarId;
    }

    /**
     * Set the entity id.
     *
     * @param inEntityId
     *            the entity id of the person.
     */
    // TODO: pull this out - this is temporary
    @Override
    public void setEntityId(final long inEntityId)
    {
        super.setEntityId(inEntityId);
    }

    /**
     * The the number of updates for this person.
     *
     * @return the updatesCount
     */
    public int getUpdatesCount()
    {
        return updatesCount;
    }

    /**
     * Set the number of updates for this person.
     *
     * @param inUpdatesCount
     *            the updatesCount to set
     */
    public void setUpdatesCount(final int inUpdatesCount)
    {
        this.updatesCount = inUpdatesCount;
    }

    /**
     * @return the streamId
     */
    public long getStreamId()
    {
        return streamId;
    }

    /**
     * @param inStreamId
     *            the streamId to set
     */
    public void setStreamId(final long inStreamId)
    {
        this.streamId = inStreamId;
    }

    /**
     * @return the compositeStreamHiddenLineIndex
     */
    public int getCompositeStreamHiddenLineIndex()
    {
        return compositeStreamHiddenLineIndex;
    }

    /**
     * @param inCompositeStreamHiddenLineIndex
     *            the compositeStreamHiddenLineIndex to set
     */
    public void setCompositeStreamHiddenLineIndex(final int inCompositeStreamHiddenLineIndex)
    {
        compositeStreamHiddenLineIndex = inCompositeStreamHiddenLineIndex;
    }

    /**
     * @return the compositeStreamSearchHiddenLineIndex
     */
    public int getCompositeStreamSearchHiddenLineIndex()
    {
        return compositeStreamSearchHiddenLineIndex;
    }

    /**
     * @param inCompositeStreamSearchHiddenLineIndex
     *            the compositeStreamSearchHiddenLineIndex to set
     */
    public void setCompositeStreamSearchHiddenLineIndex(final int inCompositeStreamSearchHiddenLineIndex)
    {
        compositeStreamSearchHiddenLineIndex = inCompositeStreamSearchHiddenLineIndex;
    }

    /**
     * @return the groupStreamHiddenLineIndex
     */
    public int getGroupStreamHiddenLineIndex()
    {
        return groupStreamHiddenLineIndex;
    }

    /**
     * @param inGroupStreamHiddenLineIndex
     *            the groupStreamHiddenLineIndex to set
     */
    public void setGroupStreamHiddenLineIndex(final int inGroupStreamHiddenLineIndex)
    {
        groupStreamHiddenLineIndex = inGroupStreamHiddenLineIndex;
    }

    /**
     * @return the compositeStreamId
     */
    public long getCompositeStreamId()
    {
        return compositeStreamId;
    }

    /**
     * @param inCompositeStreamId
     *            the compositeStreamId to set
     */
    public void setCompositeStreamId(final long inCompositeStreamId)
    {
        this.compositeStreamId = inCompositeStreamId;
    }

    /**
     * Set the roles.
     *
     * @param inRoles
     *            the roles.
     */
    public void setRoles(final HashSet<Role> inRoles)
    {
        roles = inRoles;
    }

    /**
     * Gets the roles.
     *
     * @return the roles.
     */
    public Set<Role> getRoles()
    {
        return roles;
    }

    /**
     * @return the tosAcceptance
     */
    public boolean getTosAcceptance()
    {
        return tosAcceptance;
    }

    /**
     * @param inToSAcceptance
     *            the tosAcceptance flag to set
     */
    public void setTosAcceptance(final boolean inToSAcceptance)
    {
        tosAcceptance = inToSAcceptance;
    }

    /**
     * @return the authenticationType
     */
    public AuthenticationType getAuthenticationType()
    {
        return authenticationType;
    }

    /**
     * @param inAuthenticationType
     *            the authenticationType to set
     */
    public void setAuthenticationType(final AuthenticationType inAuthenticationType)
    {
        authenticationType = inAuthenticationType;
    }

    /**
     * @return Person's email.
     */
    public String getEmail()
    {
        return email;
    }

    /**
     * @param inEmail
     *            Person's email.
     */
    public void setEmail(final String inEmail)
    {
        email = inEmail;
    }

    /**
     * @param inCommentable
     *            The commentable flag value to set.
     */
    public void setCommentable(final boolean inCommentable)
    {
        commentable = inCommentable;
    }

    /**
     * @return The commentable flag.
     */
    public boolean isCommentable()
    {
        return commentable;
    }

    /**
     * @return The streamPostable flag.
     */
    public boolean isStreamPostable()
    {
        return streamPostable;
    }

    /**
     * @param inStreamPostable
     *            The streamPostable flag value to set.
     */
    public void setStreamPostable(final boolean inStreamPostable)
    {
        streamPostable = inStreamPostable;
    }

    /**
     * Get the IDs of the related organizations for this person.
     *
     * @return the IDs of the related organizations for this person.
     */
    public List<Long> getRelatedOrganizationIds()
    {
        return relatedOrganizationIds;
    }

    /**
     * Set the IDs of the related organizations for this person.
     *
     * @param inRelatedOrganizationIds
     *            the IDs of the related organizations for this person
     */
    public void setRelatedOrganizationIds(final List<Long> inRelatedOrganizationIds)
    {
        this.relatedOrganizationIds = inRelatedOrganizationIds;
    }

    /**
     *
     * @return videos the person has opted out of.
     */
    public HashSet<Long> getOptOutVideos()
    {
        return optOutVideos;
    }

    /**
     * set the set of videos a person has opted out of.
     *
     * @param inOptOutVideos
     *            the set of videos.
     *
     */
    public void setOptOutVideos(final HashSet<Long> inOptOutVideos)
    {
        optOutVideos = inOptOutVideos;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getId()
    {
        return super.getEntityId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUniqueId()
    {
        return accountId;
    }

    /**
     * Get the last date the person accepted the terms of service.
     *
     * @return the last date the person accepted the terms of service
     */
    public Date getLastAcceptedTermsOfService()
    {
        return lastAcceptedTermsOfService;
    }

    /**
     * Set the last date the person accepted the terms of service.
     *
     * @param inLastAcceptedTermsOfService
     *            the last date the person accepted the terms of service.
     */
    public void setLastAcceptedTermsOfService(final Date inLastAcceptedTermsOfService)
    {
        lastAcceptedTermsOfService = inLastAcceptedTermsOfService;
    }
}
