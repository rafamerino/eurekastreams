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
package org.eurekastreams.server.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.eurekastreams.commons.model.DomainEntity;
import org.eurekastreams.commons.search.analysis.HtmlStemmerAnalyzer;
import org.eurekastreams.commons.search.analysis.TextStemmerAnalyzer;
import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.domain.stream.StreamView;
import org.eurekastreams.server.search.bridge.BackgroundItemListStringBridge;
import org.eurekastreams.server.search.bridge.DomainGroupPeopleIdClassBridge;
import org.eurekastreams.server.search.bridge.OrgIdHierarchyFieldBridge;
import org.eurekastreams.server.search.bridge.OrganizationToShortNameFieldBridge;
import org.hibernate.annotations.Formula;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.ClassBridge;
import org.hibernate.search.annotations.DateBridge;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.FieldBridge;
import org.hibernate.search.annotations.Fields;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Resolution;
import org.hibernate.search.annotations.Store;
import org.hibernate.validator.Length;
import org.hibernate.validator.Pattern;
import org.hibernate.validator.Size;

/**
 * Represents a group, which holds people.
 */
@Entity
@Indexed
@ClassBridge(name = "followerAndCoordinatorIds", index = Index.TOKENIZED, store = Store.NO,
// whitespace analyzer and custom class bridge to use JPA to get the ids rather than load extra objects
analyzer = @Analyzer(impl = WhitespaceAnalyzer.class), impl = DomainGroupPeopleIdClassBridge.class)
public class DomainGroup extends DomainEntity implements AvatarEntity, Followable, OrganizationChild,
        DomainGroupEntity, CompositeEntity, ActivityRestrictionEntity
{
    /**
     * Serial version uid.
     */
    private static final long serialVersionUID = 9185565459547481906L;

    /** Used for validation. */
    @Transient
    public static final int MAX_NAME_LENGTH = 50;

    /** Used for validation. */
    @Transient
    public static final int MAX_SHORT_NAME_LENGTH = 20;

    /** Used for validation. */
    @Transient
    public static final int MAX_DESCRIPTION_LENGTH = 250;

    // TODO Messages should be moved to the group model view.

    /** Used for validation. */
    @Transient
    public static final String NAME_LENGTH_MESSAGE = "Group Name supports up to " + MAX_NAME_LENGTH + " characters.";

    /** Used for validation. */
    @Transient
    public static final String SHORT_NAME_LENGTH_MESSAGE =
            "Group Web Address supports up to " + MAX_SHORT_NAME_LENGTH + " characters.";

    /** Used for validation. */
    @Transient
    public static final String NAME_REQUIRED = "Group Name is required.";

    /** Used for validation. */
    @Transient
    public static final String SHORTNAME_REQUIRED = "Group Web Address is required.";

    /** Used for validation. */
    @Transient
    public static final String MIN_COORDINATORS_MESSAGE = "Groups must have at least one coordinator.";

    /** Used for validation. */
    @Transient
    public static final String DESCRIPTION_LENGTH_MESSAGE =
            "Description supports up to " + MAX_DESCRIPTION_LENGTH + " characters.";

    /** Used for validation. */
    @Transient
    public static final String ALPHA_NUMERIC_PATTERN = "[A-Za-z0-9]*";

    /** Used for validation. */
    @Transient
    public static final String SHORT_NAME_CHARACTERS =
            "A short name can only contain alphanumeric characters and no spaces.";

    /**
     * The name of the group.
     */
    @Basic(optional = false)
    @Length(min = 1, max = MAX_NAME_LENGTH, message = NAME_LENGTH_MESSAGE)
    @Field(name = "name", index = Index.TOKENIZED,
    // use text stemmer for index and search
    analyzer = @Analyzer(impl = TextStemmerAnalyzer.class), store = Store.NO)
    private String name;

    /**
     * The gadget tasks.
     */
    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST })
    @JoinTable(name = "Group_Task", // join column
    joinColumns = { @JoinColumn(table = "Group", name = "groupId") },
    // inverse join columns
    inverseJoinColumns = { @JoinColumn(table = "Task", name = "taskId") },
    // unique constraints
    uniqueConstraints = { @UniqueConstraint(columnNames = { "groupId", "taskId" }) })
    private List<Task> completedTasks;

    /**
     * The short version of group name.
     */
    @Column(nullable = false, unique = true)
    @Length(min = 1, max = MAX_SHORT_NAME_LENGTH, message = SHORT_NAME_LENGTH_MESSAGE)
    @Pattern(regex = ALPHA_NUMERIC_PATTERN, message = SHORT_NAME_CHARACTERS)
    @Field(name = "shortName", index = Index.UN_TOKENIZED, store = Store.NO)
    private String shortName;

    /**
     * The overview of the group.
     */
    @Basic
    @Lob
    @Field(name = "overview", index = Index.TOKENIZED, store = Store.NO,
    // html-stemmer analyzer will be used for indexing and, text-stemmer for searching
    analyzer = @Analyzer(impl = HtmlStemmerAnalyzer.class))
    private String overview;

    /**
     * The description statement of the group.
     */
    @Basic
    @Length(min = 1, max = MAX_DESCRIPTION_LENGTH, message = DESCRIPTION_LENGTH_MESSAGE)
    @Field(name = "description", index = Index.TOKENIZED, store = Store.NO,
    // text-stemmer analyzer will be used for indexing and, text-stemmer for searching
    analyzer = @Analyzer(impl = TextStemmerAnalyzer.class))
    private String description;

    /**
     * The date the group was added into the system, defaults to the current time, indexed into search engine. Note, for
     * the date to be sortable, it needs to be either Index.UN_TOKENIZED or Index.NO_NORMS.
     */
    @Column(nullable = false)
    @Field(name = "dateAdded", index = Index.UN_TOKENIZED, store = Store.NO)
    @Temporal(TemporalType.TIMESTAMP)
    @DateBridge(resolution = Resolution.SECOND)
    private Date dateAdded = new Date();

    /**
     * List of coordinators for this group.
     */
    @Size(min = 1, message = MIN_COORDINATORS_MESSAGE)
    @ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST })
    @JoinTable(name = "Group_Coordinators")
    private Set<Person> coordinators;

    /**
     * Person who created the group.
     */
    @ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST })
    @JoinColumn(name = "createdById")
    private Person createdBy = new Person();

    /**
     * The skills that are contained in this background.
     */
    @OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST })
    @JoinTable(name = "Group_Capability",
    // join columns
    joinColumns = { @JoinColumn(table = "DomainGroup", name = "domainGroupId") },
    // inverse join columns
    inverseJoinColumns = { @JoinColumn(table = "BackgroundItem", name = "capabilityId") },
    // unique constraints
    uniqueConstraints = { @UniqueConstraint(columnNames = { "domainGroupId", "capabilityId" }) })
    @Field(name = "capabilities", bridge = @FieldBridge(impl = BackgroundItemListStringBridge.class),
    // line break
    index = Index.TOKENIZED, store = Store.NO, analyzer = @Analyzer(impl = TextStemmerAnalyzer.class))
    private List<BackgroundItem> capabilities;

    /**
     * Whether this is a public group (true) or a private group (false).
     */
    @Basic
    @Field(name = "isPublic", index = Index.UN_TOKENIZED, store = Store.NO)
    private boolean publicGroup;

    /**
     * The url of the group.
     */
    @Basic(optional = true)
    @Pattern(regex = URL_REGEX_PATTERN, message = WEBSITE_MESSAGE)
    private String url;

    /**
     * Whether the entity allows comments on their post.
     */
    @Basic(optional = false)
    @Field(name = "isCommentable", index = Index.UN_TOKENIZED, store = Store.NO)
    private boolean commentable = true;

    /**
     * Whether the entity allows people to post to their wall.
     */
    @Basic(optional = false)
    @Field(name = "isStreamPostable", index = Index.UN_TOKENIZED, store = Store.NO)
    private boolean streamPostable = true;

    /**
     *
     */
    @Basic
    private Integer avatarCropX;

    /**
     *
     */
    @Basic
    private Integer avatarCropY;

    /**
     *
     */
    @Basic
    private Integer avatarCropSize;

    /**
     * avatar id image for this user.
     */
    @Basic
    private String avatarId;

    /**
     * Count of people following this group.
     */
    @Basic(optional = false)
    @Field(name = "followersCount", index = Index.UN_TOKENIZED, store = Store.NO)
    private int followersCount;

    /**
     * The number of updates for this group.
     */
    @Basic(optional = false)
    @Field(name = "updatesCount", index = Index.UN_TOKENIZED, store = Store.NO)
    private int updatesCount = 0;

    /**
     * Only used for query reference, don't load this.
     */
    @SuppressWarnings("unused")
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "GroupFollower",
    // join columns
    joinColumns = { @JoinColumn(table = "DomainGroup", name = "followingId") },
    // inverse joincolumns
    inverseJoinColumns = { @JoinColumn(table = "Person", name = "followerId") })
    private List<Person> followers;

    /**
     * banner id for this org.
     */
    @Basic
    private String bannerId;

    /**
     * Transient field used only for displaying a banner on profile pages.  This is needed so that
     * a common strategy can be used across groups, orgs, and people to display banners.  When profiles
     * support DTO's, this can be moved there.
     */
    @Transient
    private Long bannerEntityId;

    /**
     * Stream view for a group.
     */
    // TODO make this non-nullable when create group strat. is updated.
    @OneToOne(fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
    @JoinColumn(name = "entityStreamViewId")
    private StreamView entityStreamView;

    /**
     * Stream scope representing this group.
     */
    @OneToOne(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
    @JoinColumn(name = "streamScopeId")
    private StreamScope streamScope;

    /**
     * The approval status of the group.
     */
    @Basic
    @Field(name = "isPending", index = Index.UN_TOKENIZED, store = Store.NO)
    private boolean isPending;

    /**
     * Default constructor.
     */
    public DomainGroup()
    {
        // no-op
    }

    /**
     * set the id - useful for unit testing.
     *
     * @param newId
     *            the new id
     */
    @Override
    protected void setId(final long newId)
    {
        super.setId(newId);
    }

    /**
     * Override equality to be based on the group's id.
     *
     * @param rhs
     *            target object
     * @return true if equal, false otherwise.
     */
    @Override
    public boolean equals(final Object rhs)
    {
        return (rhs instanceof DomainGroup && this.getId() == ((DomainGroup) rhs).getId());
    }

    /**
     * HashCode override.
     *
     * @see java.lang.Object#hashCode()
     * @return hashcode for object.
     */
    @Override
    public int hashCode()
    {
        // NOTE: unable to use HashCodeBuilder here due to GWT limitation.
        int hashCode = 0;
        hashCode ^= (new Long(getId())).hashCode();
        hashCode ^= shortName.hashCode();
        return hashCode;
    }

    /**
     * Parent organization.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parentOrganizationId")
    @Fields(value = {
    // "parentOrganizationShortName"
            @Field(name = "parentOrganizationShortName", index = Index.UN_TOKENIZED, store = Store.NO,
            // field bridge
            bridge = @FieldBridge(impl = OrganizationToShortNameFieldBridge.class)),

            // "parentOrganizationIdHierarchy" - a space-separated list of all org ids (inclusive) up the tree
            @Field(name = "parentOrganizationIdHierarchy", index = Index.TOKENIZED, store = Store.NO,
            // WhitespaceAnalyzer to split on spaces, not lowercase, and not use stop words - necessary to mention
            // since we're tokenizing
            analyzer = @Analyzer(impl = WhitespaceAnalyzer.class),
            // field bridge
            bridge = @FieldBridge(impl = OrgIdHierarchyFieldBridge.class)) })
    private Organization parentOrganization;

    /**
     * Get the parent org id w/o loading the org.
     */
    @Formula("parentOrganizationId")
    private Long parentOrgId;

     /**
     * People who are requesting membership to the group. Only used if the group is private. Field is private with no
     * getters/setters since it is used only for table/key creation.
     */
    @SuppressWarnings("unused")
    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
    @JoinTable(name = "GroupMembershipRequests",
    // join columns
    joinColumns = { @JoinColumn(table = "DomainGroup", name = "groupId") },
    // inverse join columns
    inverseJoinColumns = { @JoinColumn(table = "Person", name = "personId") })
    private Set<Person> membershipRequests;

    /**
     * Constructor. This should have every non-null parameter included.
     *
     * @param inName
     *            - Full name of group.
     * @param inShortName
     *            Short name of group.
     * @param inCreatedBy
     *            The Person that created the group.
     */
    public DomainGroup(final String inName, final String inShortName, final Person inCreatedBy)
    {
        name = inName;
        setShortName(inShortName);
        createdBy = inCreatedBy;
    }

    /**
     * Add coordinator to group.
     *
     * @param person
     *            The Person to add.
     */
    public void addCoordinator(final Person person)
    {
        if (coordinators == null)
        {
            coordinators = new HashSet<Person>();
        }
        coordinators.add(person);
    }

    /**
     * Getter for list of coordinators.
     *
     * @return list of coordinators.
     */
    public Set<Person> getCoordinators()
    {
        return coordinators;
    }

    /**
     * Setter for list of coordinators.
     *
     * @param inCoordinators
     *            list of coordinators.
     */
    public void setCoordinators(final Set<Person> inCoordinators)
    {
        coordinators = inCoordinators;
    }

    /**
     * @return the capabilities
     */
    public List<BackgroundItem> getCapabilities()
    {
        return (capabilities == null) ? new ArrayList<BackgroundItem>(0) : capabilities;
    }

    /**
     * @param inCapabilities
     *            the capabilities to set
     */
    public void setCapabilities(final List<BackgroundItem> inCapabilities)
    {
        this.capabilities = inCapabilities;
    }

    /**
     * @return the group's name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Setter for group name.
     *
     * @param inName
     *            new name
     */
    public void setName(final String inName)
    {
        name = (null == inName) ? "" : inName;
    }

    /**
     * Getter for group short name.
     *
     * @return the shortName
     */
    public String getShortName()
    {
        return shortName;
    }

    /**
     * Setter for group short name.
     *
     * @param inShortName
     *            the shortName to set.
     */
    public void setShortName(final String inShortName)
    {
        shortName = (null == inShortName) ? "" : inShortName.toLowerCase();
    }

    /**
     * Getter.
     *
     * @return the overview
     */
    public String getOverview()
    {
        return overview;
    }

    /**
     * Setter.
     *
     * @param inOverview
     *            the overview to set
     */
    public void setOverview(final String inOverview)
    {
        this.overview = inOverview;
    }

    /**
     * @return the description
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * @param inDescription
     *            the description to set
     */
    public void setDescription(final String inDescription)
    {
        description = inDescription;
    }

    /**
     * @return the group's parent organization
     */
    public Organization getParentOrganization()
    {
        return parentOrganization;
    }

    /**
     * @param inParentOrganization
     *            the parentOrganization to set
     */
    public void setParentOrganization(final Organization inParentOrganization)
    {
        parentOrganization = inParentOrganization;
    }

    /**
     * check to see if the specified account id is a coordinator for this group.
     *
     * @param account
     *            to check.
     * @return if they're a coordinator.
     */
    public boolean isCoordinator(final String account)
    {
        for (Person p : coordinators)
        {
            if (p.getAccountId().equals(account))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns the completed tasks for a person.
     *
     * @return the tasks.
     */
    public List<Task> getCompletedTasks()
    {
        return completedTasks;
    }

    /**
     * Needed for serialization.
     *
     * @param inTasks
     *            the tasks.
     */
    public void setCompletedTasks(final List<Task> inTasks)
    {
        completedTasks = inTasks;
    }

    /**
     * @return the publicGroup
     */
    public boolean isPublicGroup()
    {
        return publicGroup;
    }

    /**
     * @param inPublicGroup
     *            the publicGroup to set
     */
    public void setPublicGroup(final boolean inPublicGroup)
    {
        this.publicGroup = inPublicGroup;
    }

    /**
     * @return the url
     */
    public String getUrl()
    {
        return url;
    }

    /**
     * @param inUrl
     *            the url to set
     */
    public void setUrl(final String inUrl)
    {
        url = inUrl;
    }

    /**
     * Get avatar x coord.
     *
     * @return avatar x coord.
     */
    public Integer getAvatarCropX()
    {
        return avatarCropX;
    }

    /**
     * Set avatar x coord.
     *
     * @param value
     *            x coord.
     */
    public void setAvatarCropX(final Integer value)
    {
        avatarCropX = value;
    }

    /**
     * Get avatar y coord.
     *
     * @return avatar y coord.
     */
    public Integer getAvatarCropY()
    {
        return avatarCropY;
    }

    /**
     * Set avatar y coord.
     *
     * @param value
     *            y coord.
     */
    public void setAvatarCropY(final Integer value)
    {
        avatarCropY = value;
    }

    /**
     * Get avatar crop size.
     *
     * @return avatar crop size.
     */
    public Integer getAvatarCropSize()
    {
        return avatarCropSize;
    }

    /**
     * Set avatar crop size.
     *
     * @param value
     *            crop size.
     */
    public void setAvatarCropSize(final Integer value)
    {
        avatarCropSize = value;
    }

    /**
     * @return the avatar Id
     */
    public String getAvatarId()
    {
        return avatarId;
    }

    /**
     * @param inAvatarId
     *            the avatar to set
     */
    public void setAvatarId(final String inAvatarId)
    {
        avatarId = inAvatarId;
    }

    /**
     * @return the followersCount
     */
    public int getFollowersCount()
    {
        return followersCount;
    }

    /**
     * @param inFollowersCount
     *            the followersCount to set
     */
    public void setFollowersCount(final int inFollowersCount)
    {
        followersCount = inFollowersCount;
    }

    /**
     * @return the group's banner id
     */
    public String getBannerId()
    {
        return bannerId;
    }

    /**
     * @param inBannerId
     *            the banner to set
     */
    public void setBannerId(final String inBannerId)
    {
        bannerId = inBannerId;
    }

    /**
     * Get the number of updates for this group.
     *
     * @return the updatesCount
     */
    public int getUpdatesCount()
    {
        return updatesCount;
    }

    /**
     * Set the number of updates for this group.
     *
     * @param inUpdatesCount
     *            the updatesCount to set
     */
    protected void setUpdatesCount(final int inUpdatesCount)
    {
        this.updatesCount = inUpdatesCount;
    }

    /**
     * Set the date the group was added to the system.
     *
     * @param inDateAdded
     *            the dateAdded to set
     */
    protected void setDateAdded(final Date inDateAdded)
    {
        this.dateAdded = inDateAdded;
    }

    /**
     * Get the date the group was added to the system.
     *
     * @return the dateAdded
     */
    public Date getDateAdded()
    {
        return dateAdded;
    }

    /**
     * @return the entityStreamView
     */
    public StreamView getEntityStreamView()
    {
        return entityStreamView;
    }

    /**
     * @param inEntityStreamView
     *            the entityStreamView to set
     */
    public void setEntityStreamView(final StreamView inEntityStreamView)
    {
        this.entityStreamView = inEntityStreamView;
    }

    /**
     * @return the status
     */
    public boolean isPending()
    {
        return isPending;
    }

    /**
     * Sets if the group is pending. Note: Name is awkward, but follows the bean spec. (setIsPending would go with
     * getIsPending; setIsPending is NOT a match for isPending and thus the field doesn't serialize)
     *
     * @param inIsPending
     *            the status to set
     */
    public void setPending(final boolean inIsPending)
    {
        isPending = inIsPending;
    }

    /**
     * @param inCreatedBy
     *            set the person who created the group.
     */
    public void setCreatedBy(final Person inCreatedBy)
    {
        createdBy = inCreatedBy;
    }

    /**
     * @return person who created the group.
     */
    public Person getCreatedBy()
    {
        return createdBy;
    }

    /**
     * @return if the profile is set to allow comments.
     */
    public boolean isCommentable()
    {
        return commentable;
    }

    /**
     * @param inCommentable
     *            if the profile is set to allow comments.
     */
    public void setCommentable(final boolean inCommentable)
    {
        commentable = inCommentable;
    }

    /**
     * @return if the profile is set to all wall comments.
     */
    public boolean isStreamPostable()
    {
        return streamPostable;
    }

    /**
     * @param inStreamPostable
     *            set the wall comment property.
     */
    public void setStreamPostable(final boolean inStreamPostable)
    {
        streamPostable = inStreamPostable;
    }

    // ----------------------------------------------------
    // ------------------ CACHE UPDATING ------------------

    /**
     * Call-back after a Person entity has been updated. This tells the static cacheUpdater if set.
     */
    @SuppressWarnings("unused")
    @PostUpdate
    private void onPostUpdate()
    {
        if (entityCacheUpdater != null)
        {
            entityCacheUpdater.onPostUpdate(this);
        }
    }

    /**
     * Call-back after the entity has been persisted. This tells the static cacheUpdater if set.
     */
    @SuppressWarnings("unused")
    @PostPersist
    private void onPostPersist()
    {
        if (entityCacheUpdater != null)
        {
            entityCacheUpdater.onPostPersist(this);
        }
    }

    /**
     * The entity cache updater.
     */
    private static transient EntityCacheUpdater<DomainGroup> entityCacheUpdater;

    /**
     * Setter for the static PersonUpdater.
     *
     * @param inEntityCacheUpdater
     *            the PersonUpdater to set
     */
    public static void setEntityCacheUpdater(final EntityCacheUpdater<DomainGroup> inEntityCacheUpdater)
    {
        entityCacheUpdater = inEntityCacheUpdater;
    }

    // ---------------- END CACHE UPDATING ----------------
    // ----------------------------------------------------

    /**
     * @return the streamScope
     */
    public StreamScope getStreamScope()
    {
        return streamScope;
    }

    /**
     * @param inStreamScope
     *            the streamScope to set
     */
    public void setStreamScope(final StreamScope inStreamScope)
    {
        this.streamScope = inStreamScope;
    }

    /**
     * Getter for the domain short name as implementation for Followable.
     *
     * @return - UniqueId of the Group - shortname.
     */
    @Override
    public String getUniqueId()
    {
        return getShortName();
    }

    /**
     * Get the parent org id without loading the parent organization.
     *
     * @return the parent org id without loading the parent organization
     */
    public Long getParentOrgId()
    {
        return parentOrgId;
    }

    /**
     * Set the parent org id.
     *
     * @param inParentOrgId
     *            the parent org id
     */
    protected void setParentOrgId(final Long inParentOrgId)
    {
        parentOrgId = inParentOrgId;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Long getBannerEntityId()
    {
        return bannerEntityId;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void setBannerEntityId(final Long inBannerEntityId)
    {
        bannerEntityId = inBannerEntityId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getParentOrganizationName()
    {
        return parentOrganization.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getParentOrganizationShortName()
    {
        return parentOrganization.getShortName();
    }
}