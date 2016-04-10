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

package test.store.api;

import java.io.File;

import krati.core.segment.mapped.MappedSegmentFactory;
import krati.core.segment.memory.MemorySegmentFactory;
import krati.store.DataStore;
import krati.store.IndexedDataStore;

/**
 * TestIndexedDataStoreApi
 * 
 * @author jwu
 * @since 06/05, 2011
 */
public class TestIndexedDataStoreApi extends AbstractTestDataStoreApi {

    @Override
    protected DataStore<byte[], byte[]> createStore(File homeDir) throws Exception {
        return new IndexedDataStore(
                homeDir,
                1 << 16, /* initialCapacity */
                100,     /* batchSize */
                5,       /* numSyncBatches */
                8,       /* index segmentFileSizeMB */
                new MemorySegmentFactory(),
                16,      /* store segmentFileSizeMB */
                new MappedSegmentFactory());
    }
}
