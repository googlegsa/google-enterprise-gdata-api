/* Copyright (c) 2008 Google Inc.
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

using System;
using Google.GData.Client;

namespace Google.GData.Gsa
{
    /// <summary>
    /// The base feed class for the Google Search Appliance.
    /// </summary>
    public class GsaFeed : AbstractFeed
    {
        /// <summary>
        ///  default constructor
        /// </summary>
        /// <param name="uriBase">the base URI of the feedEntry</param>
        /// <param name="iService">the Service to use</param>
        public GsaFeed(Uri uriBase, IService iService) : base(uriBase, iService)
        {
        }

        /// <summary>
        /// return GsaEntry relative to GsaFeed
        /// </summary>
        /// <returns>GsaEntry</returns>
        public override AtomEntry CreateFeedEntry()
        {
            return new GsaEntry();
        }
    }
}
