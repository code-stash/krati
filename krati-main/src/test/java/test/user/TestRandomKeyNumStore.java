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

package test.user;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import test.AbstractTest;
import test.StatsLog;
import test.util.FileUtils;

import junit.framework.TestCase;
import krati.core.StoreConfig;
import krati.core.StoreFactory;
import krati.core.segment.SegmentFactory;
import krati.core.segment.mapped.MappedSegmentFactory;
import krati.store.DataStore;

/**
 * TestRandomKeyNumStore
 * 
 * @author jwu
 * 06/09, 2011
 */
public class TestRandomKeyNumStore extends TestCase {
    protected File _homeDir;
    protected DataStore<byte[], byte[]> _store;
    protected Random _rand = new Random();
    
    @Override
    protected void setUp() {
        try {
            _homeDir = FileUtils.getTestDir(getClass().getSimpleName());
            _store = createStore(_homeDir);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    protected void tearDown() {
        try {
            _store.close();
            FileUtils.deleteDirectory(_homeDir);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            _homeDir = null;
            _store = null;
        }
    }
    
    protected int getKeySize() {
        return 10;
    }
    
    protected int getValueSize() {
        return 4;
    }
    
    protected int getKeyCount() {
        return AbstractTest._keyCount;
    }
    
    protected int getSegmentFileSizeMB() {
        return AbstractTest._segFileSizeMB;
    }
    
    protected SegmentFactory createSegmentFactory() {
        return new MappedSegmentFactory();
    }
    
    protected int getCapacity() {
        return (int)Math.min((long)(getKeyCount() * 1.5), Integer.MAX_VALUE);
    }
    
    protected DataStore<byte[], byte[]> createStore(File homeDir) throws Exception {
        StoreConfig config = new StoreConfig(homeDir, getCapacity());
        config.setSegmentFileSizeMB(getSegmentFileSizeMB());
        config.setSegmentFactory(createSegmentFactory());
        
        return StoreFactory.createDynamicDataStore(config);
    }
    
    public void test() throws Exception {
        String unitTestName = getClass().getSimpleName(); 
        StatsLog.beginUnit(unitTestName);
        long startTime = System.currentTimeMillis();
        
        byte[] keyBytes = new byte[getKeySize()];
        byte[] valBytes = new byte[getValueSize()];
        
        Map<String, byte[]> map = new HashMap<String, byte[]>();
        int maxSize = 3000;
        
        for(int i = 0, cnt = getKeyCount(); i < cnt; i++) {
            _rand.nextBytes(keyBytes);
            _rand.nextBytes(valBytes);
            
            String strKey = new String((byte[])keyBytes.clone(), "UTF-16");
            byte[] rawKey = strKey.getBytes("UTF-16");
            
            // Update store
            _store.put(rawKey, valBytes);
            
            // Update test map
            if(map.size() < maxSize) {
                map.put(strKey, (byte[])valBytes.clone());
            }
            if(map.containsKey(strKey)) {
                map.put(strKey, (byte[])valBytes.clone());
            }
        }
        
        _store.sync();
        
        for(Map.Entry<String, byte[]> e : map.entrySet()) {
            assertTrue(Arrays.equals(e.getValue(), _store.get(e.getKey().getBytes("UTF-16"))));
        }
        StatsLog.logger.info(map.size() + " keys verified");
        
        long elapsedTime = System.currentTimeMillis() - startTime;
        double rate = Math.ceil(getKeyCount() * 100.0 / elapsedTime) / 100.0;
        StatsLog.logger.info("#ops=" + getKeyCount() + " time=" + elapsedTime + " ms rate=" + rate + " per ms");
        StatsLog.endUnit(unitTestName);
    }
}
