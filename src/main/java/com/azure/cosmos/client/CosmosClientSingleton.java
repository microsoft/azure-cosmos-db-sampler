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

import com.azure.cosmos.ConsistencyLevel;
import com.azure.cosmos.CosmosAsyncClient;
import com.azure.cosmos.CosmosClientBuilder;

import java.util.Objects;

public class CosmosClientSingleton {
    static CosmosAsyncClient client = null;
    static String uri = null;
    static String key = null;

    public static synchronized void shutDown() {
        if (client != null) {
            client.close();
        }
        client = null;
        key = null;
        uri = null;
    }

    public static synchronized CosmosAsyncClient clientConnect(String uri, String key) {
        if (client == null || !Objects.equals(uri, CosmosClientSingleton.uri) || !Objects.equals(key, CosmosClientSingleton.key)) {
            // A previously valid client that is to be re-initialized due to a change
            // in uri/key needs to be cleaned up first.
            if (client != null) {
                shutDown();
            }
            client = new CosmosClientBuilder()
                    .endpoint(uri)
                    .key(key)
                    .directMode()
                    .clientTelemetryEnabled(false)
                    .consistencyLevel(ConsistencyLevel.SESSION)
                    .buildAsyncClient();
            CosmosClientSingleton.uri = uri;
            CosmosClientSingleton.key = key;
        }
        return client;
    }
}
