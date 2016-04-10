/*
 * Copyright (c) 2010-2012 LinkedIn, Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package krati.store.factory;

import java.io.IOException;

import krati.core.StoreConfig;
import krati.core.StoreFactory;
import krati.io.Serializer;
import krati.store.datastore.DataStore;
import krati.store.objectstore.ObjectStore;
import krati.store.objectstore.SerializableObjectStore;

/**
 * DynamicObjectStoreFactory
 * 
 * @author jwu
 * @since 10/11, 2011
 */
public class DynamicObjectStoreFactory<K, V> implements ObjectStoreFactory<K, V> {
    

    /**
     * Create an instance of {@link ObjectStore} for mapping keys to values.
     * The underlying store is backed by {@link krati.store.datastore.DynamicDataStore DynamicDataStore}.
     * 
     * @param config          - the configuration
     * @param keySerializer   - the serializer for keys
     * @param valueSerializer - the serializer for values
     * @return the newly created store
     * @throws IOException if the store cannot be created.
     */
    @Override
    public ObjectStore<K, V> create(StoreConfig config,
                                    Serializer<K> keySerializer,
                                    Serializer<V> valueSerializer) throws IOException {
        try {
            DataStore<byte[], byte[]> base = StoreFactory.createDynamicDataStore(config);
            return new SerializableObjectStore<K, V>(base, keySerializer, valueSerializer);
        } catch (Exception e) {
            if(e instanceof IOException) {
                throw (IOException)e;
            } else {
                throw new IOException(e);
            }
        }
    }
}
