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

package test.store;

import java.io.File;

import test.StatsLog;

import krati.core.StoreFactory;
import krati.core.segment.SegmentFactory;
import krati.store.DataStore;

/**
 * TestDynamicStore using MemorySegment.
 * 
 * @author jwu
 *
 */
public class TestDynamicStore extends EvalDataStore {
    
    public TestDynamicStore() {
        super(TestDynamicStore.class.getName());
    }
    
    protected SegmentFactory getSegmentFactory() {
        return new krati.core.segment.memory.MemorySegmentFactory();
    }
    
    @Override
    protected DataStore<byte[], byte[]> getDataStore(File storeDir) throws Exception {
        int initialCapacity = (int)(_keyCount * 1.5);
        return StoreFactory.createDynamicDataStore(storeDir, initialCapacity, _segFileSizeMB, getSegmentFactory());
    }
    
    public void testDynamicStore() throws Exception {
        String unitTestName = getClass().getSimpleName(); 
        StatsLog.beginUnit(unitTestName);
        
        evalPerformance(_numReaders, 1, _runTimeSeconds);
        
        cleanTestOutput();
        StatsLog.endUnit(unitTestName);
    }
}
