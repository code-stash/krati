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

import krati.io.serializer.LongSerializer;
import krati.store.datastore.DataStoreHandler;
import krati.store.handler.Long2LongDataStoreHandler;

/**
 * TestLong2LongDataStoreHandler
 * 
 * @author jwu
 * @since 08/19, 2012
 */
public class TestLong2LongDataStoreHandler extends AbstractTestDataStoreHandler {
    protected long keyStart;
    protected long valueStart;
    protected LongSerializer serializer;
    
    @Override
    protected void setUp() {
        keyStart = rand.nextInt();
        valueStart = rand.nextInt();
        serializer = new LongSerializer();
    }
    
    protected DataStoreHandler createDataStoreHandler() {
        return new Long2LongDataStoreHandler();
    }
    
    protected byte[] nextKey() {
        return serializer.serialize(keyStart++);
    }
    
    protected byte[] nextValue() {
        return serializer.serialize(valueStart++);
    }
}
