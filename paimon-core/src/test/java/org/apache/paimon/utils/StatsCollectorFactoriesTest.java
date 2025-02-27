/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.apache.paimon.utils;

import org.apache.paimon.CoreOptions;
import org.apache.paimon.options.Options;
import org.apache.paimon.statistics.FieldStatsCollector;
import org.apache.paimon.statistics.FullFieldStatsCollector;
import org.apache.paimon.statistics.TruncateFieldStatsCollector;
import org.apache.paimon.types.CharType;
import org.apache.paimon.types.DataField;
import org.apache.paimon.types.RowType;
import org.apache.paimon.types.TimestampType;
import org.apache.paimon.types.VarCharType;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

/** Test for {@link StatsCollectorFactories}. */
public class StatsCollectorFactoriesTest {
    @Test
    public void testFieldStats() {
        RowType type =
                new RowType(
                        Arrays.asList(
                                new DataField(0, "a", new VarCharType(), "Someone's desc."),
                                new DataField(1, "b", new TimestampType()),
                                new DataField(2, "c", new CharType())));

        Options options = new Options();
        options.set(
                CoreOptions.FIELDS_PREFIX + ".b." + CoreOptions.STATS_MODE_SUFFIX, "truncate(12)");
        options.set(CoreOptions.FIELDS_PREFIX + ".c." + CoreOptions.STATS_MODE_SUFFIX, "full");

        FieldStatsCollector.Factory[] statsFactories =
                StatsCollectorFactories.createStatsFactories(
                        new CoreOptions(options), type.getFieldNames());
        FieldStatsCollector[] stats = FieldStatsCollector.create(statsFactories);
        Assertions.assertEquals(3, stats.length);
        Assertions.assertEquals(16, ((TruncateFieldStatsCollector) stats[0]).getLength());
        Assertions.assertEquals(12, ((TruncateFieldStatsCollector) stats[1]).getLength());
        Assertions.assertTrue(stats[2] instanceof FullFieldStatsCollector);
    }
}
