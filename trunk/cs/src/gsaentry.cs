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
using System.Collections.Generic;
using Google.GData.Apps;
using Google.GData.Client;

namespace Google.GData.Gsa
{
    /// <summary>
    /// Base entry class for the Google Search Appliance API.
    /// </summary>
    public class GsaEntry : AppsExtendedEntry
    {

        /// <summary>
        /// Constructs a new GsaEntry instance
        /// </summary>
        public GsaEntry() : base()
        {
            this.AddExtension(new GsaExtension());

        }

        /// <summary>
        /// Add another set of extension data to the entry.  Each GSA extension stores 
        /// the content as a mixed text/XML content of this XML tag.
        /// addGsaContent does not support repeating extension data with 
        /// the same extension name.  Even though addGsaContent will successfully
        /// add repeating extensions, there is no guarantee which of the repeating
        /// extensions with the same extension name will be returned by methods
        /// GetGsaContent() and GetAllGsaContents().
        /// </summary>
        /// <param name="name">name of the content extension</param>
        /// <param name="content">content data of the extension</param>
        public void AddGsaContent(string name, string content)
        {
            GsaExtension extension = new GsaExtension(name, content);
            this.ExtensionElements.Add(extension);

        }

        /// <summary>
        /// Retrieve the extension's content data having the input extension name.
        /// GsaEntry does not support repeating extensions.  If there
        /// are repeating extensions with the same name, there is no guarantee which
        /// extension's content data will be returned.
        /// </summary>
        /// <param name="name">name of the extension</param>
        /// <returns>value of the content data</returns>
        public string GetGsaContent(string name)
        {
            foreach (IExtensionElementFactory ob in this.ExtensionElements)
            {
                GsaExtension gsa = ob as GsaExtension;
                if (gsa != null)
                {
                    if (gsa.ContentName.Equals(name))
                    {
                        return gsa.ContentValue;
                    }
                }

            }
            return null;

        }

        /// <summary>
        /// Removes all extension data having the input extension name.
        /// Because GsaEntry does not support repeating extensions, 
        /// all extensions having the input name will be removed. 
        /// </summary>
        /// <param name="name">all extension having this input name will be removed</param>
        public void RemoveGsaContent(string name)
        {
            IEnumerator<IExtensionElementFactory> ie = this.ExtensionElements.GetEnumerator();
            while (ie.MoveNext())
            {
                GsaExtension gsa = ie.Current as GsaExtension;
                if (gsa != null)
                {
                    if (gsa.ContentName.Equals(name))
                    {
                        this.ExtensionElements.Remove(gsa);
                        break;
                    }

                }
            }
        }

        /// <summary>
        /// Retrieves all content data of this GsaEntry's extensions.
        /// The content data is stored in a Dictionary, with the Dictionary key being the extension names.
        /// Because GsaEntry does not support repeating extensions,
        /// if there are repeating extensions in the entry, there is no guarantee
        /// which extension's content data will be put into the map, and which
        /// will be excluded.
        /// </summary>
        /// <returns>Dictionary of strings containing the entry's extension content data</returns>
        public Dictionary<string, string> GetAllGsaContents()
        {
            Dictionary<string, string> contents = new Dictionary<string, string>();
            foreach (IExtensionElementFactory ob in this.ExtensionElements)
            {
                GsaExtension gsa = ob as GsaExtension;
                if (gsa != null)
                {
                    contents.Add(gsa.ContentName, gsa.ContentValue);
                }
            }
            return contents;
        }
    }
}
