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

package test.store.handler;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Map.Entry;

import junit.framework.TestCase;
import krati.core.StoreConfig;
import krati.store.datastore.DataStoreHandler;
import krati.store.datastore.DynamicDataStore;
import krati.store.index.HashIndexDataHandler;
import krati.util.Bytes;
import test.util.FileUtils;

/**
 * TestHashIndexDataHandler1
 * 
 * @author jwu
 * @since 08/18, 2012
 */
public class TestHashIndexDataHandler1 extends TestCase {
    static Random rand = new Random();
    
    protected int getValueLength() {
        return Bytes.NUM_BYTES_IN_INT;
    }
    
    protected DataStoreHandler createDataStoreHandler() {
        return new HashIndexDataHandler();
    }
    
    protected byte[] randomBytes(int length) {
        byte[] bytes = new byte[length];
        rand.nextBytes(bytes);
        return bytes;
    }
    
    protected byte[] randomValue() {
        return randomBytes(getValueLength());
    }
    
    public void testApiBasics() {
        byte[] key1 = randomBytes(32);
        byte[] value1 = randomValue();
        
        byte[] key2 = randomBytes(64);
        byte[] value2 = randomValue();
        
        byte[] key3 = randomBytes(128);
        byte[] value3 = randomValue();
        applyBasicOps(key1, value1, key2, value2, key3, value3);
        
        value1 = randomValue();
        value2 = randomValue();
        value3 = randomValue();
        applyBasicOps(key1, value1, key2, value2, key3, value3);
    }
    
    private void applyBasicOps(byte[] key1, byte[] value1, byte[] key2, byte[] value2, byte[] key3, byte[] value3) {
        DataStoreHandler h = createDataStoreHandler();
        byte[] data;
        
        byte[] data1 = h.assemble(key1, value1);
        byte[] data2 = h.assemble(key1, value1, null);
        assertTrue(Arrays.equals(data1, data2));
        data2 = h.assemble(key1, value1, new byte[0]);
        assertTrue(Arrays.equals(data1, data2));
        
        data = h.assemble(key1, value1);
        assertTrue(Arrays.equals(value1, h.extractByKey(key1, data)));
        assertEquals(1, h.countCollisions(key1, data));
        
        data = h.assemble(key2, value2, data);
        assertTrue(Arrays.equals(value1, h.extractByKey(key1, data)));
        assertTrue(Arrays.equals(value2, h.extractByKey(key2, data)));
        assertEquals(2, h.countCollisions(key1, data));
        assertEquals(2, h.countCollisions(key2, data));
        
        data = h.assemble(key3, value3, data);
        assertTrue(Arrays.equals(value1, h.extractByKey(key1, data)));
        assertTrue(Arrays.equals(value2, h.extractByKey(key2, data)));
        assertTrue(Arrays.equals(value3, h.extractByKey(key3, data)));
        assertEquals(3, h.countCollisions(key1, data));
        assertEquals(3, h.countCollisions(key2, data));
        assertEquals(3, h.countCollisions(key3, data));
        
        int newLength = h.removeByKey(key3, data);
        data = Arrays.copyOf(data, newLength);
        assertTrue(Arrays.equals(value1, h.extractByKey(key1, data)));
        assertTrue(Arrays.equals(value2, h.extractByKey(key2, data)));
        assertEquals(null, h.extractByKey(key3, data));
        assertEquals(2, h.countCollisions(key1, data));
        assertEquals(2, h.countCollisions(key2, data));
        assertEquals(-2, h.countCollisions(key3, data));
        
        newLength = h.removeByKey(key1, data);
        data = Arrays.copyOf(data, newLength);
        assertEquals(null, h.extractByKey(key1, data));
        assertTrue(Arrays.equals(value2, h.extractByKey(key2, data)));
        assertEquals(-1, h.countCollisions(key1, data));
        assertEquals(1, h.countCollisions(key2, data));
        
        newLength = h.removeByKey(key2, data);
        data = Arrays.copyOf(data, newLength);
        assertEquals(null, h.extractByKey(key1, data));
        assertEquals(0, h.countCollisions(key1, data));
        assertEquals(null, h.extractByKey(key2, data));
        assertEquals(0, h.countCollisions(key2, data));
        assertEquals(null, h.extractByKey(key3, data));
        assertEquals(0, h.countCollisions(key3, data));
        
        data1 = h.assemble(key1, value1);
        data1 = h.assemble(key2, value2, data1);
        data1 = h.assemble(key3, value3, data1);
        
        List<byte[]> keys = h.extractKeys(data1);
        assertEquals(3, keys.size());
        List<Entry<byte[], byte[]>> entries = h.extractEntries(data1);
        assertEquals(3, entries.size());
        
        data2 = h.assembleEntries(entries);
        assertTrue(Arrays.equals(data1, data2));
    }
    
    public void testApiNullValues() {
        DataStoreHandler h = createDataStoreHandler();
        byte[] data;
        
        byte[] key = randomBytes(8);
        byte[] value = null;
        
        data = h.assemble(key, value);
        assertEquals(null, data);
        
        byte[] key1 = randomBytes(32);
        byte[] value1 = randomValue();
        
        byte[] data1 = h.assemble(key1, value1);
        byte[] data2 = h.assemble(key1, value1, null);
        assertTrue(Arrays.equals(data1, data2));
        
        byte[] data3 = h.assemble(key, value, data1);
        assertTrue(Arrays.equals(data1, data3));
        
        assertEquals(null, h.extractByKey(key, data1));
        assertEquals(data1.length, h.removeByKey(key, data1));
    }
    
    public void testStoreConfig() throws Exception {
        File dir = FileUtils.getTestDir(getClass().getSimpleName());
        
        StoreConfig config;
        DynamicDataStore store;
        
        config = new StoreConfig(dir, 10000);
        config.setSegmentFileSizeMB(32);
        assertTrue(config.getDataHandler() == null);
        config.setDataHandler(createDataStoreHandler());
        assertTrue(config.getDataHandler() != null);
        
        byte[] key = "key".getBytes();
        byte[] value = randomValue();
        store = new DynamicDataStore(config);
        store.put(key, value);
        assertTrue(Arrays.equals(value, store.get(key)));
        store.close();
        
        StoreConfig config2 = StoreConfig.newInstance(dir);
        assertTrue(config2.getDataHandler() != null);
        assertEquals(config.getDataHandler().getClass(), config2.getDataHandler().getClass());
        
        store = new DynamicDataStore(config2);
        assertTrue(Arrays.equals(value, store.get(key)));
        store.close();
        
        FileUtils.deleteDirectory(dir);
    }
}
