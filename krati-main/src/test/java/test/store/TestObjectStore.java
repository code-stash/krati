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
import java.util.Iterator;

import krati.core.segment.SegmentFactory;
import krati.store.DataStore;
import krati.store.SerializableObjectStore;
import krati.store.StaticDataStore;

import test.AbstractTest;

import test.protos.KeySerializer;
import test.protos.MemberDataGen;
import test.protos.MemberProtos;
import test.protos.MemberSerializer;

/**
 * Test SerializableObjectStore
 * 
 * @author jwu
 *
 */
public class TestObjectStore extends AbstractTest {
    
    public TestObjectStore() {
        super(TestObjectStore.class.getName());
    }
    
    protected SegmentFactory getSegmentFactory() {
        return new krati.core.segment.memory.MemorySegmentFactory();
    }
    
    protected DataStore<byte[], byte[]> getDataStore(File storeDir, int capacity) throws Exception {
        return new StaticDataStore(storeDir,
                                   capacity,
                                   1000,      /* entrySize */
                                   5,         /* maxEntries */
                                   _segFileSizeMB,
                                   getSegmentFactory());
    }
    
    public void testObjectStore() throws Exception {
        cleanTestOutput();
        
        int memberCnt = 10000;
        int capacity = memberCnt * 2;
        
        File objectStoreDir = new File(TEST_OUTPUT_DIR, "object_store");
        DataStore<byte[], byte[]> dataStore = getDataStore(objectStoreDir, capacity);
        SerializableObjectStore<String, MemberProtos.Member> memberStore =
            new SerializableObjectStore<String, MemberProtos.Member>(dataStore, new KeySerializer(), new MemberSerializer());
        
        MemberProtos.MemberBook book = MemberDataGen.generateMemberBook(memberCnt);
        
        for (MemberProtos.Member m : book.getMemberList()) {
            memberStore.put(m.getEmail(0), m);
        }
        
        for (MemberProtos.Member m : book.getMemberList()) {
            memberStore.put(m.getEmail(0), m);
        }
        
        for (MemberProtos.Member m : book.getMemberList()) {
            memberStore.put(m.getEmail(0), m);
        }
        
        memberStore.persist();
        
        for (MemberProtos.Member m : book.getMemberList()) {
            assertTrue("Member " + m.getMemberId(), memberStore.get(m.getEmail(0)).equals(m));
        }
        
        Iterator<String> keyIter = memberStore.keyIterator();
        while (keyIter.hasNext()) {
            String key = keyIter.next();
            MemberProtos.Member m = memberStore.get(key);
            assertEquals(memberStore.getValueSerializer().serialize(m).length, memberStore.getLength(key));
            assertTrue("Member " + m.getMemberId() + ": key=" + key + " email=" + m.getEmail(0), m.getEmail(0).equals(key));
        }
        
        cleanTestOutput();
    }
}
