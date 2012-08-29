/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fusesource.camel.component.salesforce.api.dto;

public class SObject extends AbstractDTOBase {
    private String name;
    private String label;
    private boolean updateable;
    private String keyPrefix;
    private boolean custom;
    private SObjectUrls urls;
    private boolean searchable;
    private String labelPlural;
    private boolean layoutable;
    private boolean activateable;
    private boolean createable;
    private boolean deprecatedAndHidden;
    private boolean deletable;
    private boolean customSetting;
    private boolean feedEnabled;
    private String listviewable;
    private String lookupLayoutable;
    private boolean mergeable;
    private boolean queryable;
    private boolean replicateable;
    private boolean retrieveable;
    private String searchLayoutable;
    private boolean undeletable;
    private boolean triggerable;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isUpdateable() {
        return updateable;
    }

    public void setUpdateable(boolean updateable) {
        this.updateable = updateable;
    }

    public String getKeyPrefix() {
        return keyPrefix;
    }

    public void setKeyPrefix(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }

    public boolean isCustom() {
        return custom;
    }

    public void setCustom(boolean custom) {
        this.custom = custom;
    }

    public SObjectUrls getUrls() {
        return urls;
    }

    public void setUrls(SObjectUrls urls) {
        this.urls = urls;
    }

    public boolean isSearchable() {
        return searchable;
    }

    public void setSearchable(boolean searchable) {
        this.searchable = searchable;
    }

    public String getLabelPlural() {
        return labelPlural;
    }

    public void setLabelPlural(String labelPlural) {
        this.labelPlural = labelPlural;
    }

    public boolean isLayoutable() {
        return layoutable;
    }

    public void setLayoutable(boolean layoutable) {
        this.layoutable = layoutable;
    }

    public boolean isActivateable() {
        return activateable;
    }

    public void setActivateable(boolean activateable) {
        this.activateable = activateable;
    }

    public boolean isCreateable() {
        return createable;
    }

    public void setCreateable(boolean createable) {
        this.createable = createable;
    }

    public boolean isDeprecatedAndHidden() {
        return deprecatedAndHidden;
    }

    public void setDeprecatedAndHidden(boolean deprecatedAndHidden) {
        this.deprecatedAndHidden = deprecatedAndHidden;
    }

    public boolean isDeletable() {
        return deletable;
    }

    public void setDeletable(boolean deletable) {
        this.deletable = deletable;
    }

    public boolean isCustomSetting() {
        return customSetting;
    }

    public void setCustomSetting(boolean customSetting) {
        this.customSetting = customSetting;
    }

    public boolean isFeedEnabled() {
        return feedEnabled;
    }

    public void setFeedEnabled(boolean feedEnabled) {
        this.feedEnabled = feedEnabled;
    }

    public String getListviewable() {
        return listviewable;
    }

    public void setListviewable(String listviewable) {
        this.listviewable = listviewable;
    }

    public String getLookupLayoutable() {
        return lookupLayoutable;
    }

    public void setLookupLayoutable(String lookupLayoutable) {
        this.lookupLayoutable = lookupLayoutable;
    }

    public boolean isMergeable() {
        return mergeable;
    }

    public void setMergeable(boolean mergeable) {
        this.mergeable = mergeable;
    }

    public boolean isQueryable() {
        return queryable;
    }

    public void setQueryable(boolean queryable) {
        this.queryable = queryable;
    }

    public boolean isReplicateable() {
        return replicateable;
    }

    public void setReplicateable(boolean replicateable) {
        this.replicateable = replicateable;
    }

    public boolean isRetrieveable() {
        return retrieveable;
    }

    public void setRetrieveable(boolean retrieveable) {
        this.retrieveable = retrieveable;
    }

    public String getSearchLayoutable() {
        return searchLayoutable;
    }

    public void setSearchLayoutable(String searchLayoutable) {
        this.searchLayoutable = searchLayoutable;
    }

    public boolean isUndeletable() {
        return undeletable;
    }

    public void setUndeletable(boolean undeletable) {
        this.undeletable = undeletable;
    }

    public boolean isTriggerable() {
        return triggerable;
    }

    public void setTriggerable(boolean triggerable) {
        this.triggerable = triggerable;
    }

}
