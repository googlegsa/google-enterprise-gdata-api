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
using System.Text;
using System.Xml;
using Google.GData.Client;

namespace Google.GData.Gsa
{
    /// <summary>
    /// Extension class used by GsaEntry to support the gsa:content mixed data.
    /// 
    /// Each extension stores a key (name) and a value (content), in the form of:
    /// <code>
    /// <gsa:content name="name">content</gsa:content>
    /// </code>
    /// the content values can either be retrieved/added by a single extension,
    /// using the extension name as the key, or retrieved together 
    /// as Maps of Strings, again using the extension name as the key. 
    /// GsaEntry does not support repeating extension with the same
    /// extension name.
    /// </summary>
    public class GsaExtension : IExtensionElementFactory
    {

        private const string ExtensionLocalName = "content";
        private const string PropertyName = "name";

        private string contentName;
        private string contentValue;

        public string ContentName
        {
            get { return contentName; }
            set { contentName = value; }
        }

        public string ContentValue
        {
            get { return contentValue; }
            set { contentValue = value; }
        }

        /// <summary>
        /// Constructs an empty GsaExtension instance
        /// </summary>
        public GsaExtension()
        {
        }

        /// <summary>
        /// Constructs an empty GsaExtension with provided data.
        /// </summary>
        /// <param name="name">content name</param>
        /// <param name="value">content value</param>
        public GsaExtension(String name, String value)
        {
            this.contentName = name;
            this.contentValue = value;
        }

        //////////////////////////////////////////////////////////////////////
        /// <summary>Parses an xml node to create a GsaExtension object.</summary> 
        /// <param name="node">the node to parse node</param>
        /// <param name="parser">the xml parser to use if we need to dive deeper</param>
        /// <returns>the created GsaExtension object</returns>
        //////////////////////////////////////////////////////////////////////
        public IExtensionElementFactory CreateInstance(XmlNode node, AtomFeedParser parser)
        {
            Tracing.TraceCall();
            GsaExtension gsa = null;

            if (node != null)
            {
                object localname = node.LocalName;
                if (localname.Equals(this.XmlName) == false ||
                  node.NamespaceURI.Equals(this.XmlNameSpace) == false)
                {
                    return null;
                }
            }

            gsa = new GsaExtension();
            
            if (node != null)
            {
                if (node.Attributes == null || node.Attributes[PropertyName] == null)
                {
                    throw new ArgumentException("GsaContent name can not found in this name space");
                }
                gsa.ContentName = node.Attributes[PropertyName].Value;
                gsa.ContentValue = node.InnerText == null ? "" : node.InnerText;
            }
            return gsa;
        }

        /// <summary>
        /// Persistence method for the GsaExtension object
        /// </summary>
        /// <param name="writer">the xmlwriter to write into</param>
        public void Save(XmlWriter writer)
        {
            if (Utilities.IsPersistable(this.ContentName))
            {

                writer.WriteStartElement(XmlPrefix, XmlName, XmlNameSpace);

                writer.WriteAttributeString(PropertyName, this.ContentName);

                if (Utilities.IsPersistable(this.ContentValue))
                {
                    writer.WriteString(this.ContentValue);
                }
                writer.WriteEndElement();
            }
        }


        //////////////////////////////////////////////////////////////////////
        /// <summary>returns the XML local name that is used</summary> 
        //////////////////////////////////////////////////////////////////////
        public string XmlName
        {
            get { return "content"; }
        }

        //////////////////////////////////////////////////////////////////////
        /// <summary>returns the XML namespace that is processed</summary> 
        //////////////////////////////////////////////////////////////////////
        public string XmlNameSpace
        {
            get { return "http://schemas.google.com/gsa/2007"; }
        }

        //////////////////////////////////////////////////////////////////////
        /// <summary>returns the xml prefix used</summary> 
        //////////////////////////////////////////////////////////////////////
        public string XmlPrefix
        {
            get { return "gsa"; }
        }
    }
}
