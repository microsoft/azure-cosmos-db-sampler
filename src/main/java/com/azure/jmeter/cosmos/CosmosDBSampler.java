/*
 * Copyright (c) Microsoft Corporation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.azure.jmeter.cosmos;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.azure.cosmos.CosmosAsyncClient;
import com.azure.cosmos.CosmosAsyncContainer;
import com.azure.cosmos.CosmosAsyncDatabase;
import com.azure.cosmos.CosmosException;
import com.azure.cosmos.implementation.HttpConstants;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.util.CosmosPagedFlux;
import com.azure.cosmos.client.CosmosClientSingleton;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.jmeter.samplers.Sampler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import reactor.core.publisher.Flux;

import org.apache.jmeter.samplers.AbstractSampler;
import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testelement.TestStateListener;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * A JMeter {@link Sampler} that queries Azure Cosmos DB.
 */
public class CosmosDBSampler extends AbstractSampler implements TestStateListener {

    private static final Logger logger = LogManager.getLogger(CosmosDBSampler.class);

    private static final String COSMOS_DB_URI = "CosmosDB.cosmosDBURI";
    private static final String DATABASE_NAME = "CosmosDB.databaseName";
    private static final String COSMOS_KEY = "CosmosDB.cosmosDBKey";
    private static final String CONTAINER_ID = "CosmosDB.containerID";
    private static final String PARTITION_KEY_PATH = "CosmosDB.partitionKeyPath";
    private static final String COSMOS_QUERY = "CosmosDB.cosmosDBQueries";
    private static final String COSMOS_RUN_ID = "CosmosDB.cosmosDBRunID";
    private static final String COSMOS_QUERY_TYPE = "CosmosDB.queryType";

    private static final Pattern BEFORE_COLON_PATTERN = Pattern.compile("[A-Za-z].*?:");
    private static final Pattern AFTER_COLON_PATTERN = Pattern.compile(":(.*)");
    private static final Pattern CUSTOM_RETRIEVE_DOCUMENT_SIZE_PATTERN = Pattern.compile("RetrievedDocumentCount.*?:");

    private CosmosAsyncContainer container;

    @Override
    public SampleResult sample(Entry entry) {
        SampleResult result = new SampleResult();
        result.setSampleLabel("Azure Cosmos DB Sampler");
        result.sampleStart();

        try {
            Map<String, String> diagnosticsData = new HashMap<>();
            connectCosmosClientIfNecessary();
            try {
                CosmosPagedFlux<JsonNode> response = queryCosmosContainer(diagnosticsData);
                result.setDataEncoding(UTF_8.name());
                result.setDataType(SampleResult.TEXT);
                result.setResponseCodeOK();
                result.setResponseMessage("OK");
                result.setSuccessful(true);
                JsonNode responseData = buildSampleResponseData(response, diagnosticsData);
                result.setSamplerData(responseData.toString());
                result.setResponseData(responseData.toString(), UTF_8.name());
            } finally {
                result.sampleEnd();
            }
        } catch (Exception ex) {
            result.setResponseMessage("Exception: " + ex);
            result.setSuccessful(false);
            result.setResponseCode(Integer.toString(HttpConstants.StatusCodes.INTERNAL_SERVER_ERROR));
            result.setResponseMessage(ex.toString());
        }
        return result;
    }

    public void setCosmosDBURI(String uri) {
        // Force re-acquisition of the container
        if (!Objects.equals(uri, getCosmosDBURI())) {
            container = null;
        }
        setProperty(COSMOS_DB_URI, uri);
    }

    public String getCosmosDBURI() {
        return getPropertyAsString(COSMOS_DB_URI, "");
    }

    public void setDatabaseName(String name) {
        // Force re-acquisition of the container
        if (!Objects.equals(name, getDatabaseName())) {
            container = null;
        }
        setProperty(DATABASE_NAME, name);
    }

    public String getDatabaseName() {
        return getPropertyAsString(DATABASE_NAME, "");
    }

    public void setCosmosDBKey(String key) {
        // Force re-acquisition of the container
        if (!Objects.equals(key, getCosmosDBKey())) {
            container = null;
        }
        setProperty(COSMOS_KEY, key);
    }

    public String getCosmosDBKey() {
        return getPropertyAsString(COSMOS_KEY, "");
    }

    public void setContainerID(String containerID) {
        // Force re-acquisition of the container
        if (!Objects.equals(containerID, getContainerID())) {
            container = null;
        }
        setProperty(CONTAINER_ID, containerID);
    }

    public String getContainerID() {
        return getPropertyAsString(CONTAINER_ID, "");
    }

    public void setPartitionKeyPath(String path) {
        setProperty(PARTITION_KEY_PATH, path);
    }

    public String getPartitionKeyPath() {
        return getPropertyAsString(PARTITION_KEY_PATH, "");
    }

    public void setCosmosDBQuery(String query) {
        setProperty(COSMOS_QUERY, query);
    }

    public String getCosmosDBQuery() {
        return getPropertyAsString(COSMOS_QUERY, "");
    }

    public void setRunID(String text) {
        setProperty(COSMOS_RUN_ID, text);
    }

    public String getRunID() {
        return getPropertyAsString(COSMOS_RUN_ID, "");
    }

    public void setQueryType(String text) {
        setProperty(COSMOS_QUERY_TYPE, text);
    }

    public String getQueryType() {
        return getPropertyAsString(COSMOS_QUERY_TYPE, "");
    }

    @Override
    public void testStarted() {
        testStarted(""); // $NON-NLS-1$
    }

    @Override
    public void testEnded() {
        CosmosClientSingleton.shutDown();
        testEnded(""); // $NON-NLS-1$
    }

    @Override
    public void testStarted(String host) {
        // ignored
    }

    @Override
    public void testEnded(String host) {
        // ignored
    }

    protected void connectCosmosClientIfNecessary() {
        if (container == null) {
            String uri = getCosmosDBURI();
            String key = getCosmosDBKey();

            CosmosAsyncClient client = CosmosClientSingleton.clientConnect(uri, key);
            CosmosAsyncDatabase database = client.getDatabase(getDatabaseName());
            container = database.getContainer(getContainerID());
        }
    }

    private CosmosPagedFlux<JsonNode> queryCosmosContainer(Map<String, String> diagnosticsData) {
        CosmosPagedFlux<JsonNode> pagedFluxResponse;
        CosmosQueryRequestOptions queryOptions = new CosmosQueryRequestOptions();
        queryOptions.setMaxDegreeOfParallelism(10);
        queryOptions.setMaxBufferedItemCount(100);
        queryOptions.setQueryMetricsEnabled(true);

        try {
            long millis = System.currentTimeMillis();

            pagedFluxResponse = container.queryItems(getCosmosDBQuery(), queryOptions, JsonNode.class);

            if (logger.isDebugEnabled()) {
                long resultMillis = System.currentTimeMillis() - millis;
                diagnosticsData.put("Duration", String.valueOf(resultMillis));
                logger.debug("RESPONSE TIME AFTER: {} ms", resultMillis);
            }
        } catch (final CosmosException ce) {
            logger.error("Read Item failed with {}", ce.toString());
            throw new IllegalStateException("Did not return response from query", ce);
        }
        return pagedFluxResponse;
    }

    private JsonNode buildSampleResponseData(CosmosPagedFlux<JsonNode> response, Map<String, String> diagnosticsData) {
        ObjectMapper mapper = new ObjectMapper();
        String sqlQuery = getCosmosDBQuery();

        try {
            // Add SQL Query to the response
            diagnosticsData.put("query", sqlQuery);
            // Add runID to the response
            diagnosticsData.put("runID", getRunID());
            // Add the query type to the response
            diagnosticsData.put("queryType", getQueryType());

            processCosmosResponseData(response, diagnosticsData);

        } catch (final CosmosException ce) {
            logger.error(String.format("Read Item failed with %s\n", ce));
        }
        return mapper.valueToTree(diagnosticsData);
    }

    protected void processCosmosResponseData(CosmosPagedFlux<JsonNode> response, Map<String, String> diagnosticsData) {
        int pageSize = 10;
        response.byPage(pageSize).flatMap(fr -> {
            processCosmosDiagnosticsData(fr.getCosmosDiagnostics().toString(), diagnosticsData);
            return Flux.empty();
        }).blockLast();
    }

    protected void processCosmosDiagnosticsData(String cosmosDiagnostics, Map<String, String> diagnosticsData) {
        String[] splitDiagnostic = cosmosDiagnostics.split(System.lineSeparator());

        for (String string : splitDiagnostic) {
            Matcher matchBeforeColon = BEFORE_COLON_PATTERN.matcher(string);
            Matcher matchAfterColon = AFTER_COLON_PATTERN.matcher(string);
            Matcher matchRetrieve = CUSTOM_RETRIEVE_DOCUMENT_SIZE_PATTERN.matcher(string);

            if (matchBeforeColon.find() && matchAfterColon.find()) {
                diagnosticsData.put(matchBeforeColon.group().replaceAll(":", "").replaceAll("\\s", ""),
                        matchAfterColon.group().replaceAll(":", "").replaceAll("\\s", ""));
            }

            if (matchRetrieve.find()) {
                diagnosticsData.put(matchRetrieve.group().replaceAll(":", ""),
                        string.substring(string.indexOf("Retrieved Document Count") + 57));
            }
        }
    }
}
