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

                string escaped = node.InnerText;
                if (escaped == null)
                {
                    gsa.ContentValue = "";
                }
                else
                {
                    gsa.ContentValue = UnescapeXml(escaped);
                }

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
                    string escaped = EscapeXml(this.ContentValue);
                    writer.WriteString(escaped);
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

        //////////////////////////////////////////////////////////////////////
        /// <summary>Escapes XML-specific characters in a string.</summary>
        /// <param name="inString">input string, which may or may not contain XML special characters</param>
        /// <returns>String with all special characters escaped</returns>
        //////////////////////////////////////////////////////////////////////
        private string EscapeXml(string inString)
        {
            if (inString == null)
            {
                return null;
            }

            StringBuilder builder = new StringBuilder();
            char[] inputArray = inString.ToCharArray();

            for (int i = 0; i < inputArray.Length; i++)
            {
                if (inputArray[i] == '&')
                {
                    builder.Append("&amp;");
                }
                else if (inputArray[i] == '<')
                {
                    builder.Append("&lt;");
                }
                else if (inputArray[i] == '>')
                {
                    builder.Append("&gt;");
                }
                else if (inputArray[i] == '\'')
                {
                    builder.Append("&apos;");
                }
                else if (inputArray[i] == '"')
                {
                    builder.Append("&quot;");
                }
                else
                {
                    builder.Append(inputArray[i]);
                }
            }
            return builder.ToString();
        }

        //////////////////////////////////////////////////////////////////////
        /// <summary>Un-escapes XML escape sequences in a string.</summary>
        /// <param name="xmlString">input string, which may or may not contain XML escape sequences</param>
        /// <returns>String with all escape sequences un-escaped</returns>
        //////////////////////////////////////////////////////////////////////
        private string UnescapeXml(string xmlString)
        {
            int index = xmlString.IndexOf('&');
            if (index < 0)
            {
                return xmlString;
            }
            char[] inputArray = xmlString.ToCharArray();
            char[] outputArray = new char[inputArray.Length];
            Array.Copy(inputArray, 0, outputArray, 0, index);

            int pos = index;
            for (int i = index; i < inputArray.Length; )
            {
                if (inputArray[i] != '&')
                {
                    outputArray[pos++] = inputArray[i++];
                    continue;
                }
                int j = i + 1;

                // Scan until we find a char that is not valid for this sequence.
                for (; j < inputArray.Length; j++)
                {
                    char ch = inputArray[j];
                    if (!Char.IsLetter(ch))
                    {
                        break;
                    }
                }

                bool replaced = false;
                if (j < inputArray.Length && inputArray[j] == ';')
                {
                    string original = new string(inputArray, i, j - i);
                    char replaceWith = char.MinValue;
                    if (original.Equals("&amp"))
                    {
                        replaceWith = '&';
                    }
                    else if (original.Equals("&lt"))
                    {
                        replaceWith = '<';
                    }
                    else if (original.Equals("&gt"))
                    {
                        replaceWith = '>';
                    }
                    else if (original.Equals("&apos"))
                    {
                        replaceWith = '\'';
                    }
                    else if (original.Equals("&quot"))
                    {
                        replaceWith = '"';
                    }

                    if (replaceWith != char.MinValue)
                    {
                        outputArray[pos++] = replaceWith;
                        replaced = true;
                    }
                    // Skip over ';'
                    if (j < inputArray.Length && inputArray[j] == ';')
                    {
                        j++;
                    }
                }

                if (!replaced)
                {
                    // Not a recognized escape sequence, leave as-is
                    Array.Copy(inputArray, i, outputArray, pos, j - i);
                    pos += j - i;
                }
                i = j;
            }

            return new string(outputArray, 0, pos);
        }

    }
}
