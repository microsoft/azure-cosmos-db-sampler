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

import com.azure.cosmos.CosmosAsyncClient;
import com.azure.cosmos.CosmosAsyncContainer;
import com.azure.cosmos.CosmosAsyncDatabase;
import com.azure.cosmos.CosmosException;
import com.azure.cosmos.client.CosmosDBTestUtils;
import com.azure.cosmos.implementation.HttpConstants;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.util.CosmosPagedFlux;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.SampleResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CosmosDBSamplerTest {
    private CosmosDBSampler sampler;
    @Mock
    private CosmosAsyncClient mockClient;
    @Mock
    private CosmosAsyncDatabase mockDatabase;
    @Mock
    private CosmosAsyncContainer mockContainer;

    @BeforeEach
    public void setup() {
        sampler = new CosmosDBSampler();
        sampler.setCosmosDBURI("testURI");
        sampler.setCosmosDBKey("testKey");
        sampler.setDatabaseName("testDatabase");
        sampler.setContainerID("testContainer");
        sampler.setCosmosDBQuery("testCosmosDBQuery");
        sampler.setQueryType("testQueryType");
        sampler.setRunID("testRunID");
        CosmosDBTestUtils.setCosmosClient(mockClient, sampler.getCosmosDBURI(), sampler.getCosmosDBKey());
        when(mockClient.getDatabase(sampler.getDatabaseName())).thenReturn(mockDatabase);
        when(mockDatabase.getContainer(sampler.getContainerID())).thenReturn(mockContainer);
    }

    @AfterEach
    public void teardown() {
        verifyNoMoreInteractions(mockClient, mockDatabase, mockContainer);
    }

    @Test
    public void testCosmosError() {
        when(mockContainer.queryItems(eq(sampler.getCosmosDBQuery()), any(CosmosQueryRequestOptions.class),
                eq(JsonNode.class)))
                .thenThrow(CosmosException.class);
        SampleResult result = sampler.sample(new Entry());

        assertFalse(result.isSuccessful());
        assertEquals(Integer.toString(HttpConstants.StatusCodes.INTERNAL_SERVER_ERROR), result.getResponseCode());
    }

    @Test
    public void testSingleSample() {
        String resultCosmosDiagosticsString = "userAgent=azsdk-java-cosmos/4.28.0 MacOSX/11.3 JRE/11.0.12\n" +
                " Retrieved Document Count                 :               9\n" +
                " Retrieved Document Size                  :            3763 bytes\n" +
                " Output Document Count                    :               9\n" +
                " Output Document Size                     :            3820 bytes\n" +
                " Index Utilization                        :          100.00 %\n" +
                " Total Query Execution Time               :        0.190000 milliseconds\n" +
                " Query Preparation Times Query Compilation Time : 0.030000 milliseconds\n" +
                " Logical Plan Build Time : 0.000000 milliseconds\n" +
                " Physical Plan Build Time : 0.000000 milliseconds\n" +
                " Query Optimization Time : 0.000000 milliseconds\n" +
                " Index Lookup Time : 0.000000 milliseconds\n" +
                " Document Load Time : 0.030000 milliseconds\n" +
                " Runtime Execution Times Query Engine Times : 0.010000 milliseconds\n" +
                " Request Charge : 2.43 RUs";
        ArrayList<String> diagnosticDataStrings = new ArrayList<>();
        diagnosticDataStrings.add(resultCosmosDiagosticsString);
        CosmosDBSampler testSampler = new CosmosDBSampler() {
            @Override
            protected void processCosmosResponseData(CosmosPagedFlux<JsonNode> response, Map<String, String> diagnosticsData) {
                diagnosticDataStrings.forEach(diagnosticsString -> processCosmosDiagnosticsData(diagnosticsString, diagnosticsData));
            }
        };
        // Copy properties from the sampler set up in the setup method
        sampler.propertyIterator().forEachRemaining(testSampler::setProperty);

        when(mockContainer.queryItems(eq(testSampler.getCosmosDBQuery()), any(CosmosQueryRequestOptions.class),
                eq(JsonNode.class)))
                .thenReturn(null); // returning null is ok because our test sampler doesn't read from the null
        // variable (see processResponseData above)
        SampleResult result = testSampler.sample(new Entry());

        String expectedResponse = "{\"RequestCharge\":\"2.43RUs\"," +
                "\"query\":\"" + testSampler.getCosmosDBQuery() + "\"," +
                "\"OutputDocumentSize\":\"3820bytes\"," +
                "\"QueryPreparationTimesQueryCompilationTime\":\"0.030000milliseconds\"," +
                "\"PhysicalPlanBuildTime\":\"0.000000milliseconds\"," +
                "\"QueryOptimizationTime\":\"0.000000milliseconds\"," +
                "\"RetrievedDocumentCount\":\"9\"," +
                "\"RetrievedDocumentSize\":\"3763bytes\"," +
                "\"queryType\":\"" + testSampler.getQueryType() + "\"," +
                "\"OutputDocumentCount\":\"9\"," +
                "\"RuntimeExecutionTimesQueryEngineTimes\":\"0.010000milliseconds\"," +
                "\"runID\":\"" + testSampler.getRunID() + "\"," +
                "\"IndexUtilization\":\"100.00%\"," +
                "\"TotalQueryExecutionTime\":\"0.190000milliseconds\"," +
                "\"LogicalPlanBuildTime\":\"0.000000milliseconds\"," +
                "\"DocumentLoadTime\":\"0.030000milliseconds\"," +
                "\"IndexLookupTime\":\"0.000000milliseconds\"}";

        assertTrue(result.isSuccessful());
        assertEquals(Integer.toString(HttpConstants.StatusCodes.OK), result.getResponseCode());
        assertEquals("OK", result.getResponseMessage());
        assertEquals(SampleResult.TEXT, result.getDataType());
        assertEquals(StandardCharsets.UTF_8.name(), result.getDataEncodingNoDefault());
        assertEquals(expectedResponse, result.getResponseDataAsString());
        assertEquals(expectedResponse, result.getSamplerData());
    }
}
