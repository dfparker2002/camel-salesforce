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

import org.apache.camel.builder.RouteBuilder;
import org.codehaus.jackson.map.ObjectMapper;
import org.fusesource.camel.component.salesforce.api.dto.*;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class RestApiIntegrationTest extends AbstractSalesforceTestBase {

    private static final Logger LOG = LoggerFactory.getLogger(RestApiIntegrationTest.class);
    private static final String TEST_LINE_ITEM_ID = "1";
    private static final String NEW_LINE_ITEM_ID = "100";

    private ObjectMapper objectMapper;
    private static String testId;

    @Test
    public void testGetVersions() throws Exception {
        doTestGetVersions("");
        doTestGetVersions("Xml");
    }

    @SuppressWarnings("unchecked")
    private void doTestGetVersions(String suffix) throws Exception {
        // test getVersions doesn't need a body
        // assert expected result
        Object o = template().requestBody("direct:getVersions" + suffix, (Object) null);
        List<Version> versions = null;
        if (o instanceof Versions) {
            versions = ((Versions) o).getVersions();
        } else {
            versions = (List<Version>) o;
        }
        assertNotNull(versions);
        LOG.debug("Versions: {}", versions);
    }

    @Test
    public void testGetResources() throws Exception {
        doTestGetResources("");
        doTestGetResources("Xml");
    }

    private void doTestGetResources(String suffix) throws Exception {


        RestResources resources = template().requestBody("direct:getResources" + suffix, null, RestResources.class);
        assertNotNull(resources);
        LOG.debug("Resources: {}", resources);
    }

    @Test
    public void testGetGlobalObjects() throws Exception {
        doTestGetGlobalObjects("");
        doTestGetGlobalObjects("Xml");
    }

    private void doTestGetGlobalObjects(String suffix) throws Exception {


        GlobalObjects globalObjects = template().requestBody("direct:getGlobalObjects" + suffix, null, GlobalObjects.class);
        assertNotNull(globalObjects);
        LOG.debug("GlobalObjects: {}", globalObjects);
    }

    @Test
    public void testGetBasicInfo() throws Exception {
        doTestGetBasicInfo("");
        doTestGetBasicInfo("Xml");
    }

    private void doTestGetBasicInfo(String suffix) throws Exception {
        SObjectBasicInfo objectBasicInfo = template().requestBody("direct:getBasicInfo" + suffix, null, SObjectBasicInfo.class);
        assertNotNull(objectBasicInfo);
        LOG.debug("SObjectBasicInfo: {}", objectBasicInfo);

        // set test Id for testGetSObject
        assertFalse("RecentItems is empty", objectBasicInfo.getRecentItems().isEmpty());
        testId = objectBasicInfo.getRecentItems().get(0).getId();
    }

    @Test
    public void testGetDescription() throws Exception {
        doTestGetDescription("");
        doTestGetDescription("Xml");
    }

    private void doTestGetDescription(String suffix) throws Exception {


        SObjectDescription sObjectDescription = template().requestBody("direct:getDescription" + suffix, null, SObjectDescription.class);
        assertNotNull(sObjectDescription);
        LOG.debug("SObjectDescription: {}", sObjectDescription);
    }

    @Test
    public void testGetSObject() throws Exception {
        doTestGetSObject("");
        doTestGetSObject("Xml");
    }

    private void doTestGetSObject(String suffix) throws Exception {
        if (testId == null) {
            // execute getBasicInfo to get test id from recent items
            doTestGetBasicInfo("");
        }

        Merchandise__c merchandise = template().requestBody("direct:getSObject" + suffix, testId, Merchandise__c.class);
        assertNotNull(merchandise);
        if (suffix.isEmpty()) {
            assertNull(merchandise.getTotal_Inventory__c());
            assertNotNull(merchandise.getPrice__c());
        } else {
            assertNotNull(merchandise.getTotal_Inventory__c());
            assertNull(merchandise.getPrice__c());
        }
        LOG.debug("SObjectById: {}", merchandise);
    }

    @Test
    public void testCreateUpdateDelete() throws Exception {
        doTestCreateUpdateDelete("");
        doTestCreateUpdateDelete("Xml");
    }

    private void doTestCreateUpdateDelete(String suffix) throws InterruptedException {
        Merchandise__c merchandise__c = new Merchandise__c();
        merchandise__c.setName("Wee Wee Wee Plane");
        merchandise__c.setDescription__c("Microlite plane");
        merchandise__c.setPrice__c(2000.0);
        merchandise__c.setTotal_Inventory__c(50.0);
        CreateSObjectResult result = template().requestBody("direct:CreateSObject" + suffix,
            merchandise__c, CreateSObjectResult.class);
        assertNotNull(result);
        assertTrue("Create success", result.getSuccess());
        LOG.debug("Create: " + result);

        // test JSON update
        // make the plane cheaper
        merchandise__c.setPrice__c(1500.0);
        // change inventory to half
        merchandise__c.setTotal_Inventory__c(25.0);
        // also need to set the Id
        merchandise__c.setId(result.getId());

        assertNull(template().requestBodyAndHeader("direct:UpdateSObject" + suffix,
            merchandise__c, SalesforceEndpointConfig.SOBJECT_ID, result.getId()));
        LOG.debug("Update successful");

        // delete the newly created SObject
        assertNull(template().requestBody("direct:deleteSObject" + suffix, result.getId()));
        LOG.debug("Delete successful");
    }

    @Test
    public void testCreateUpdateDeleteWithId() throws Exception {
        doTestCreateUpdateDeleteWithId("");
        doTestCreateUpdateDeleteWithId("Xml");
    }

    private void doTestCreateUpdateDeleteWithId(String suffix) throws InterruptedException {
        // get line item with Name 1
        Line_Item__c line_item__c = template().requestBody("direct:getSObjectWithId" + suffix, TEST_LINE_ITEM_ID,
            Line_Item__c.class);
        assertNotNull(line_item__c);
        LOG.debug("GetWithId: {}", line_item__c);

        // test insert with id
        // set the unit price and sold
        line_item__c.setUnit_Price__c(1000.0);
        line_item__c.setUnits_Sold__c(50.0);
        // update line item with Name NEW_LINE_ITEM_ID
        line_item__c.setName(NEW_LINE_ITEM_ID);

        CreateSObjectResult result = template().requestBodyAndHeader("direct:upsertSObject" + suffix,
            line_item__c, SalesforceEndpointConfig.SOBJECT_EXT_ID_VALUE, NEW_LINE_ITEM_ID,
            CreateSObjectResult.class);
        assertNotNull(result);
        assertTrue(result.getSuccess());
        LOG.debug("CreateWithId: {}", result);

        // clear read only parent type fields
        line_item__c.setInvoice_Statement__c(null);
        line_item__c.setMerchandise__c(null);
        // change the units sold
        line_item__c.setUnits_Sold__c(25.0);

        // update line item with Name NEW_LINE_ITEM_ID
        result = template().requestBodyAndHeader("direct:upsertSObject" + suffix,
            line_item__c, SalesforceEndpointConfig.SOBJECT_EXT_ID_VALUE, NEW_LINE_ITEM_ID,
            CreateSObjectResult.class);
        assertNull(result);
        LOG.debug("UpdateWithId: {}", result);

        // delete the SObject with Name NEW_LINE_ITEM_ID
        assertNull(template().requestBody("direct:deleteSObjectWithId" + suffix, NEW_LINE_ITEM_ID));
        LOG.debug("DeleteWithId successful");
    }

    @Test
    public void testQuery() throws Exception {
        doTestQuery("");
        doTestQuery("Xml");
    }

    private void doTestQuery(String suffix) throws InterruptedException {
        QueryRecordsLine_Item__c queryRecords = template().requestBody("direct:query" + suffix, null,
            QueryRecordsLine_Item__c.class);
        assertNotNull(queryRecords);
        LOG.debug("ExecuteQuery: {}", queryRecords);
    }


    @Test
    public void testSearch() throws Exception {
        doTestSearch("");
        doTestSearch("Xml");
    }

    @SuppressWarnings("unchecked")
    private void doTestSearch(String suffix) throws InterruptedException {

        Object obj = template().requestBody("direct:search" + suffix, (Object) null);
        List<SearchResult> searchResults = null;
        if (obj instanceof SearchResults) {
            SearchResults results = (SearchResults) obj;
            searchResults = results.getResults();
        } else {
            searchResults = (List<SearchResult>) obj;
        }
        assertNotNull(searchResults);
        LOG.debug("ExecuteSearch: {}", searchResults);
    }

    @Override
    protected RouteBuilder doCreateRouteBuilder() throws Exception {

        // create a json mapper
        objectMapper = new ObjectMapper();

        // create test route
        return new RouteBuilder() {
            public void configure() {

                // testGetVersion
                from("direct:getVersions")
                    .to("salesforce:getVersions");

                // allow overriding format per endpoint
                from("direct:getVersionsXml")
                    .to("salesforce:getVersions?format=xml");

                // testGetResources
                from("direct:getResources")
                    .to("salesforce:getResources");

                from("direct:getResourcesXml")
                    .to("salesforce:getResources?format=xml");

                // testGetGlobalObjects
                from("direct:getGlobalObjects")
                    .to("salesforce:getGlobalObjects");

                from("direct:getGlobalObjectsXml")
                    .to("salesforce:getGlobalObjects?format=xml");

                // testGetBasicInfo
                from("direct:getBasicInfo")
                    .to("salesforce:getBasicInfo?sObjectName=Merchandise__c");

                from("direct:getBasicInfoXml")
                    .to("salesforce:getBasicInfo?format=xml&sObjectName=Merchandise__c");

                // testGetDescription
                from("direct:getDescription")
                    .to("salesforce:getDescription?sObjectName=Merchandise__c");

                from("direct:getDescriptionXml")
                    .to("salesforce:getDescription?format=xml&sObjectName=Merchandise__c");

                // testGetSObject
                from("direct:getSObject")
                    .to("salesforce:getSObject?sObjectName=Merchandise__c&sObjectFields=Description__c,Price__c");

                from("direct:getSObjectXml")
                    .to("salesforce:getSObject?format=xml&sObjectName=Merchandise__c&sObjectFields=Description__c,Total_Inventory__c");

                // testCreateSObject
                from("direct:CreateSObject")
                    .to("salesforce:createSObject?sObjectName=Merchandise__c");

                from("direct:CreateSObjectXml")
                    .to("salesforce:createSObject?format=xml&sObjectName=Merchandise__c");

                // testUpdateSObject
                from("direct:UpdateSObject")
                    .to("salesforce:updateSObject?sObjectName=Merchandise__c");

                from("direct:UpdateSObjectXml")
                    .to("salesforce:updateSObject?format=xml&sObjectName=Merchandise__c");

                // testDeleteSObject
                from("direct:deleteSObject")
                    .to("salesforce:deleteSObject?sObjectName=Merchandise__c");

                from("direct:deleteSObjectXml")
                    .to("salesforce:deleteSObject?format=xml&sObjectName=Merchandise__c");

                // testGetSObjectWithId
                from("direct:getSObjectWithId")
                    .to("salesforce:getSObjectWithId?sObjectName=Line_Item__c&sObjectIdName=Name");

                from("direct:getSObjectWithIdXml")
                    .to("salesforce:getSObjectWithId?format=xml&sObjectName=Line_Item__c&sObjectIdName=Name");

                // testUpsertSObject
                from("direct:upsertSObject")
                    .to("salesforce:upsertSObject?sObjectName=Line_Item__c&sObjectIdName=Name");

                from("direct:upsertSObjectXml")
                    .to("salesforce:upsertSObject?format=xml&sObjectName=Line_Item__c&sObjectIdName=Name");

                // testDeleteSObjectWithId
                from("direct:deleteSObjectWithId")
                    .to("salesforce:deleteSObjectWithId?sObjectName=Line_Item__c&sObjectIdName=Name");

                from("direct:deleteSObjectWithIdXml")
                    .to("salesforce:deleteSObjectWithId?format=xml&sObjectName=Line_Item__c&sObjectIdName=Name");

                // testQuery
                from("direct:query")
                    .to("salesforce:query?sObjectQuery=SELECT name from Line_Item__c&sObjectClass=org.fusesource.camel.component.salesforce.QueryRecordsLine_Item__c");

                from("direct:queryXml")
                    .to("salesforce:query?format=xml&sObjectQuery=SELECT name from Line_Item__c&sObjectClass=org.fusesource.camel.component.salesforce.QueryRecordsLine_Item__c");

                // testSearch
                from("direct:search")
                    .to("salesforce:search?sObjectSearch=FIND {Wee}");

                from("direct:searchXml")
                    .to("salesforce:search?format=xml&sObjectSearch=FIND {Wee}");
            }
        };
    }

}
