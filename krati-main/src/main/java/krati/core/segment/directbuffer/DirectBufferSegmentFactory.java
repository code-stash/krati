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

package krati.core.segment.directbuffer;

import java.io.File;
import java.io.IOException;

import krati.core.segment.Segment;
import krati.core.segment.SegmentFactory;
import krati.core.segment.Segment.Mode;

/**
 * DirectBufferSegmentFactory
 * 
 * @author jwu
 * @since 08/20, 2012
 */
public class DirectBufferSegmentFactory implements SegmentFactory {
    
    @Override
    public Segment createSegment(int segmentId, File segmentFile, int initialSizeMB, Segment.Mode mode) throws IOException {
        return new DirectBufferSegment(segmentId, segmentFile, initialSizeMB, mode);
    }
}
