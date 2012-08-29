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
package org.fusesource.camel.component.salesforce;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.codehaus.jackson.annotate.JsonProperty;
import org.fusesource.camel.component.salesforce.api.dto.AbstractSObjectBase;

@XStreamAlias("Merchandise__c")
public class Merchandise__c extends AbstractSObjectBase {

    private String Description__c;

    private double Price__c;

    private double Total_Inventory__c;

    @JsonProperty("Description__c")
    public String getDescription__c() {
        return Description__c;
    }

    @JsonProperty("Description__c")
    public void setDescription__c(String description__c) {
        Description__c = description__c;
    }

    @JsonProperty("Price__c")
    public double getPrice__c() {
        return Price__c;
    }

    @JsonProperty("Price__c")
    public void setPrice__c(double price__c) {
        Price__c = price__c;
    }

    @JsonProperty("Total_Inventory__c")
    public double getTotal_Inventory__c() {
        return Total_Inventory__c;
    }

    @JsonProperty("Total_Inventory__c")
    public void setTotal_Inventory__c(double total_Inventory__c) {
        Total_Inventory__c = total_Inventory__c;
    }
}
