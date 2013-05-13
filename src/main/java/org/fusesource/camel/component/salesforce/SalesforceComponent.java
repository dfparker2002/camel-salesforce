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

import org.apache.camel.CamelException;
import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;
import org.apache.camel.util.ObjectHelper;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.RedirectListener;
import org.fusesource.camel.component.salesforce.api.SalesforceException;
import org.fusesource.camel.component.salesforce.api.dto.AbstractSObjectBase;
import org.fusesource.camel.component.salesforce.internal.OperationName;
import org.fusesource.camel.component.salesforce.internal.PayloadFormat;
import org.fusesource.camel.component.salesforce.internal.SalesforceSession;
import org.fusesource.camel.component.salesforce.internal.client.SalesforceSecurityListener;
import org.fusesource.camel.component.salesforce.internal.streaming.SubscriptionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Represents the component that manages {@link SalesforceEndpoint}.
 */
public class SalesforceComponent extends DefaultComponent {

    private static final Logger LOG = LoggerFactory.getLogger(SalesforceComponent.class);

    private static final String DEFAULT_VERSION = "27.0";
    private static final int MAX_CONNECTIONS_PER_ADDRESS = 20;
    private static final int CONNECTION_TIMEOUT = 15000;
    private static final int RESPONSE_TIMEOUT = 15000;

    private SalesforceLoginConfig loginConfig;
    private SalesforceEndpointConfig config;
    private String[] packages;

    // component state
    private HttpClient httpClient;
    private SalesforceSession session;
    private Map<String, Class<?>> classMap;

    // Lazily created helper for consumer endpoints
    private SubscriptionHelper subscriptionHelper;

    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        // get Operation from remaining URI
        OperationName operationName = null;
        String topicName = null;
        try {
            LOG.debug("Creating endpoint for ", remaining);
            operationName = OperationName.fromValue(remaining);
        } catch (IllegalArgumentException ex) {
            // if its not an operation name, treat is as topic name for consumer endpoints
            topicName = remaining;
        }

        // create endpoint config
        if (config == null) {
            config = new SalesforceEndpointConfig();

            // inherit default values from component
            config.setFormat(PayloadFormat.JSON.toString());
            config.setApiVersion(DEFAULT_VERSION);
        }

        // create a deep copy and map parameters
        final SalesforceEndpointConfig copy = config.copy();
        setProperties(copy, parameters);

        final SalesforceEndpoint endpoint = new SalesforceEndpoint(uri, this, copy,
            operationName, topicName);

        // map remaining parameters to endpoint (specifically, synchronous)
        setProperties(endpoint, parameters);

        return endpoint;
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();

        // validate properties
        ObjectHelper.notNull(loginConfig, "loginConfig");

        // create a Jetty HttpClient if not already set
        if (null == httpClient) {
            if (config != null && config.getHttpClient() != null) {
                httpClient = config.getHttpClient();
            } else {
                httpClient = new HttpClient();
                httpClient.setConnectorType(HttpClient.CONNECTOR_SELECT_CHANNEL);
                httpClient.setMaxConnectionsPerAddress(MAX_CONNECTIONS_PER_ADDRESS);
                httpClient.setConnectTimeout(CONNECTION_TIMEOUT);
                httpClient.setTimeout(RESPONSE_TIMEOUT);
            }
        }

        // add redirect listener to handle Salesforce redirects
        String listenerClass = RedirectListener.class.getName();
        if (httpClient.getRegisteredListeners() == null ||
            !httpClient.getRegisteredListeners().contains(listenerClass)) {
            httpClient.registerListener(listenerClass);
        }
        listenerClass = SalesforceSecurityListener.class.getName();
        if (httpClient.getRegisteredListeners() == null ||
            !httpClient.getRegisteredListeners().contains(listenerClass)) {
            httpClient.registerListener(listenerClass);
        }

        // start the Jetty client to initialize thread pool, etc.
        httpClient.start();

        // support restarts
        if (null == this.session) {
            this.session = new SalesforceSession(httpClient, loginConfig);
        }

        // login at startup if lazyLogin is disabled
        if (!loginConfig.isLazyLogin()) {
            try {
                // get a new token
                session.login(session.getAccessToken());
            } catch (SalesforceException e) {
                throw new CamelException(e.getMessage(), e);
            }
        }

        if (packages != null && packages.length > 0) {
            // parse the packages to create SObject name to class map
            classMap = parsePackages();
        } else {
            // use an empty map to avoid NPEs later
            LOG.warn("Missing property packages, getSObject* operations will NOT work");
            classMap = Collections.unmodifiableMap(new HashMap<String, Class<?>>());
        }
    }

    private Map<String, Class<?>> parsePackages() {
        Map<String, Class<?>> result = new HashMap<String, Class<?>>();
        Set<Class<?>> classes = getCamelContext().getPackageScanClassResolver().findImplementations(AbstractSObjectBase.class, packages);
        for (Class<?> aClass : classes) {
            // findImplementations also returns AbstractSObjectBase for some reason!!!
            if (AbstractSObjectBase.class != aClass) {
                result.put(aClass.getSimpleName(), aClass);
            }
        }

        return Collections.unmodifiableMap(result);
    }

    @Override
    protected void doStop() throws Exception {
        super.doStop();

        try {
            if (subscriptionHelper != null) {
                // shutdown all streaming connections
                subscriptionHelper.shutdown();
            }
            if (session != null && session.getAccessToken() != null) {
                try {
                    // logout of Salesforce
                    session.logout();
                } catch (SalesforceException ignored) {
                }
            }
        } finally {
            if (httpClient != null) {
                // shutdown http client connections
                httpClient.stop();
            }
        }
    }

    public SubscriptionHelper getSubscriptionHelper() throws Exception {
        if (subscriptionHelper == null) {
            // lazily create subscription helper
            subscriptionHelper = new SubscriptionHelper(this);
        }
        return subscriptionHelper;
    }

    public SalesforceLoginConfig getLoginConfig() {
        return loginConfig;
    }

    public void setLoginConfig(SalesforceLoginConfig loginConfig) {
        this.loginConfig = loginConfig;
    }

    public SalesforceEndpointConfig getConfig() {
        return config;
    }

    public void setConfig(SalesforceEndpointConfig config) {
        this.config = config;
    }

    public String[] getPackages() {
        return packages;
    }

    public void setPackages(String[] packages) {
        this.packages = packages;
    }

    public HttpClient getHttpClient() {
        return httpClient;
    }

    public SalesforceSession getSession() {
        return session;
    }

    public Map<String, Class<?>> getClassMap() {
        return classMap;
    }

}
