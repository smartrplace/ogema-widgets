/**
 * ﻿Copyright 2014-2018 Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.iwes.timeseries.eval.generic.gatewayBackupAnalysis;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.function.BiPredicate;

import org.joda.time.DateTime;
import org.joda.time.Period;

public interface GatewayDataExportI {
    public void writeGatewayDataArchive(Collection<String> gatewayIds, BiPredicate<String, String> recordedDataSelector,
            DateTime start, DateTime end, Period step, OutputStream output) throws IOException;
    public void writeGatewayDataArchive(Collection<String> gatewayIds, BiPredicate<String, String> recordedDataSelector,
            DateTime start, DateTime end, Period step, boolean buildingOnly, OutputStream output) throws IOException;
}
