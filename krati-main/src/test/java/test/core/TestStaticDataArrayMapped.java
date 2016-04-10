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

package test.core;

import java.io.File;

import krati.core.segment.SegmentFactory;
import krati.store.arraystore.AbstractDataArray;
import krati.store.arraystore.StaticDataArray;

/**
 * TestStaticDataArrayMapped
 * 
 * @author jwu
 * 
 */
public class TestStaticDataArrayMapped extends EvalDataArray {
    
    @Override
    protected SegmentFactory createSegmentFactory() {
        return new krati.core.segment.mapped.MappedSegmentFactory();
    }
    
    @Override
    protected AbstractDataArray createDataArray(File homeDir) throws Exception {
        return new StaticDataArray(_idCount,
                                   homeDir,
                                   createSegmentFactory(),
                                   _segFileSizeMB);
    }
}
