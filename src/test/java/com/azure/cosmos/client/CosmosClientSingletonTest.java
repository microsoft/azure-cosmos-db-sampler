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
package com.azure.cosmos.client;

import com.azure.cosmos.CosmosAsyncClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CosmosClientSingletonTest {
    @Mock
    private CosmosAsyncClient mockClient;

    @BeforeEach
    public void setup() {
        CosmosClientSingleton.shutDown();
    }

    @AfterEach
    public void teardown() {
        verifyNoMoreInteractions(mockClient);
    }

    @Test
    public void testLazyClientInit() {
        // Actually initializing the client will throw an NPE since we're passing a null URI.
        assertThrows(NullPointerException.class, () -> CosmosClientSingleton.clientConnect(null, "testKey"));
    }

    @Test
    public void testReturnExistingClient() {
        CosmosClientSingleton.client = mockClient;
        CosmosClientSingleton.key = "testKey";

        CosmosClientSingleton.clientConnect(null, CosmosClientSingleton.key);
    }

    @Test
    public void testClientReinit() {
        CosmosClientSingleton.client = mockClient;
        CosmosClientSingleton.key = "someOtherKey";

        assertThrows(NullPointerException.class, () -> CosmosClientSingleton.clientConnect(null, "testKey"));
        // Manually set the client and key since our initialization on the previous line will actually fail
        CosmosClientSingleton.client = mockClient;
        CosmosClientSingleton.key = "testKey";
        verify(mockClient).close();
        CosmosClientSingleton.clientConnect(null, "testKey");
    }
}
