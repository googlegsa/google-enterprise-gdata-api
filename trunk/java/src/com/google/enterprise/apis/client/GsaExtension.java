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


package com.google.enterprise.apis.client;

import com.google.gdata.util.common.xml.XmlWriter;
import com.google.gdata.data.Extension;
import com.google.gdata.data.ExtensionDescription;
import com.google.gdata.data.ExtensionPoint;
import com.google.gdata.data.ExtensionProfile;
import com.google.gdata.util.ParseException;
import com.google.gdata.util.XmlParser.ElementHandler;

import org.xml.sax.Attributes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Extension class used by GsaEntry to support the gsa:content mixed data.
 * 
 * Each extension stores a key (name) and a value (content), in the form of:
 * <gsa:content name="name">content</gsa:content>
 * the content values can either be retrieved/added by a single extension, 
 * using the extension name as the key, or retrieved together 
 * as Maps of Strings, again using the extension name as the key. 
 * GsaEntry does not support repeating extension with the same
 * extension name.
 * 
 * 
 */
public class GsaExtension extends ExtensionPoint implements Extension {
  
  private static ExtensionDescription EXTENSION_DESC = new ExtensionDescription();
  private static final String EXTENSION_LOCAL_NAME = "content";
  private static final String CONTENT_NAME = "name";

  // Variable contentName holds the key (or the index), which is the value in the name= tag.
  // Content is stored directly in the XML Blob.
  private String contentName;
  private String contentValue;
  
  static {
    EXTENSION_DESC.setExtensionClass(GsaExtension.class);
    EXTENSION_DESC.setNamespace(GsaNamespaces.GSA_NAMESPACE);
    EXTENSION_DESC.setLocalName(EXTENSION_LOCAL_NAME);
    EXTENSION_DESC.setRepeatable(true);
  }
  
  /**
   * Returns the extension description.  Used by Feeds, Entries, and Services
   * during the declaration of extensions.
   * 
   * @return the extension description for this extension class
   */
  public static ExtensionDescription getDesc() {
    return EXTENSION_DESC;
  }

  /**
   * Generates the XML output for this extension.  Client library users are not
   * required to have knowledge of this method, unless there are plans to
   * subclass GsaExtension to generate different XML output.  Doing so,
   * however, may make the XML output unrecognizable to the GSA GData server.
   * Note that this extension class supports mixed Attribute + XML data.  
   */
  @Override
  public void generate(XmlWriter writer, ExtensionProfile extProfile) 
                       throws IOException {

    // Even though the xmlBlob is not explicitly used in this method, 
    // it is used by generateExtension due to the fact that the
    // GsaExtensionHandler initialized the XML blob.
    
    // do not use com.google.gdata.util.commons.collect because this is to be distributed 
    // as client library
    List<XmlWriter.Attribute> attrs = new ArrayList<XmlWriter.Attribute>();
    
    attrs.add(new XmlWriter.Attribute(CONTENT_NAME, contentName));
    
    writer.startElement(GsaNamespaces.GSA_NAMESPACE, 
                        EXTENSION_LOCAL_NAME, attrs, null);
    generateExtensions(writer, extProfile);
    writer.endElement(GsaNamespaces.GSA_NAMESPACE, EXTENSION_LOCAL_NAME);
  }

  @SuppressWarnings("unused")
  @Override
  public ElementHandler getHandler(ExtensionProfile extProfile, 
                                   String namespace, 
                                   String localName, 
                                   Attributes attrs) throws ParseException {
    return new GsaExtensionHandler(extProfile);
  }

  /**
   * Getter for the extension name.
   * 
   * @return extension name 
   */
  public String getContentName() {
    return contentName;
  }

  /**
   * Setter for the extension name.
   * 
   * @param contentName name of the extension to set
   */
  public void setContentName(String contentName) {
    this.contentName = contentName;
  }
  
  /**
   * Getter for the extension content data.
   * 
   * @return extension content data
   */
  public String getContentValue() {
    String escaped = xmlBlob.getBlob();
    
    // Normalize all null content values to empty strings.
    // Our design assumption is that if there is an extension with empty value,
    // that value is an empty string instead of null.
    // If a null content extension is intended, then that extension should not
    // exist in the entry in the first place.
    if (escaped == null) {
      contentValue = "";
    } else {
      contentValue = unescapeXml(escaped);
    }
    return contentValue;
  }

  /**
   * Setter for the extension content data.
   * 
   * @param contentValue 
   */
  public void setContentValue(String contentValue) {
    String escaped = escapeXml(contentValue);
    
    xmlBlob.setBlob(escaped);
    this.contentValue = escaped;
  }

  /**
   * Escapes XML-specific characters in a string.
   * 
   * @param inString input string, which may or may not contain XML special characters
   * @return String with all special characters escaped
   */
  private String escapeXml(String inString) {
    if (inString == null) {
      return null;
    }
    
    StringBuilder builder = new StringBuilder();
    char[] inputArray = inString.toCharArray();
    
    for (int i = 0; i < inputArray.length; i++) {
      if (inputArray[i] == '&') {
        builder.append("&amp;");
      } else if (inputArray[i] == '<') {
        builder.append("&lt;");
      } else if (inputArray[i] == '>') {
        builder.append("&gt;");
      } else if (inputArray[i] == '\'') {
        builder.append("&apos;");
      } else if (inputArray[i] == '"') {
        builder.append("&quot;");
      } else {
        builder.append(inputArray[i]);
      }
    }
    return builder.toString();    
  }
  
  /**
   * Un-escapes XML escape sequences in a string.
   * 
   * @param xmlString input string, which may or may not contain XML escape sequences
   * @return String with all escape sequences un-escaped
   */
  private String unescapeXml(String xmlString) {
    int index = xmlString.indexOf('&');
    if (index < 0) {
      return xmlString;
    }
    char[] inputArray = xmlString.toCharArray();
    char[] outputArray = new char[inputArray.length];
    System.arraycopy(inputArray, 0, outputArray, 0, index);
    
    // Note: escaped[pos] = end of the escaped char array.
    int pos = index;
    for (int i = index; i < inputArray.length;) {
      if (inputArray[i] != '&') {
        outputArray[pos++] = inputArray[i++];
        continue;
      }
      int j = i + 1;
      
      // Scan until we find a char that is not valid for this sequence.
      for (; j < inputArray.length; j++) {
        char ch = inputArray[j];
        if (!Character.isLetter(ch)) {
          break;
        }
      }
      
      boolean replaced = false;
      if (j < inputArray.length && inputArray[j] == ';') {
        String original = new String(inputArray, i, j - i);
        Character replaceWith;
        if (original.equals("&amp")) {
          replaceWith = '&';
        } else if (original.equals("&lt")) {
          replaceWith = '<';
        } else if (original.equals("&gt")) {
          replaceWith = '>';
        } else if (original.equals("&apos")) {
          replaceWith = '\'';
        } else if (original.equals("&quot")) {
          replaceWith = '"';
        } else {
          replaceWith = null;
        }

        if (replaceWith != null) {
          outputArray[pos++] = replaceWith;
          replaced = true;
        }
        // Skip over ';'
        if (j < inputArray.length && inputArray[j] == ';') {
          j++;
        }
      }
      
      if (!replaced) {
        // Not a recognized escape sequence, leave as-is
        System.arraycopy(inputArray, i, outputArray, pos, j - i);
        pos += j - i;
      }
      i = j;
    }

    return new String(outputArray, 0, pos); 
  }
  
  /**
   * The Extension handler inner class for this GSA Extension.
   */
  private class GsaExtensionHandler extends ExtensionPoint.ExtensionHandler {

    /**
     * Constructor method must initializes the XML Blob, in order for proper
     * parsing and generation.
     * 
     * @param extProfile the extension profile passed from the service or the feed provider
     */
    public GsaExtensionHandler(ExtensionProfile extProfile) {
      super(extProfile, GsaExtension.class);
      
      // initialize so that the extension supports mixed value + xml content
      initializeXmlBlob(getXmlBlob(), true, false);
    }

    /**
     * The override method only needs to handle parsing of the name= tag (which is the index).
     * The XML Blob is handled automatically by the superclass, which is the SAX parser.
     */
    @Override
    public void processAttribute(String namespace, String localName, String localValue)
        throws ParseException, NumberFormatException {
      
      // Process the name tag only.  The content will be parsed as XmlBlob
      if (localName.equals(CONTENT_NAME)) {
        contentName = localValue;
      } else {
        throw new ParseException("Unknown key = " + localName + " in this name space");
      }
    }
  }

}

