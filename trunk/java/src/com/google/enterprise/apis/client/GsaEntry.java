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

import com.google.gdata.data.appsforyourdomain.generic.GenericEntry;
import com.google.gdata.data.ExtensionProfile;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Base entry class for the Google Search Appliance API.
 * This class is derived from 
 * {@link com.google.gdata.data.appsforyourdomain.generic.GenericEntry},
 * with extension added to support mixed content data in the namespace 
 * gsa:content. 
 * 
 * 
 */
public class GsaEntry extends GenericEntry {

  public GsaEntry() {
    super();
  }

  @Override
  public void declareExtensions(ExtensionProfile extProfile) {
    super.declareExtensions(extProfile);
    
    extProfile.declare(GsaEntry.class, GsaExtension.getDesc());
  }

  /**
   * Add another set of extension data to the entry.  Each GSA extension stores 
   * the content as a mixed text/XML content of this XML tag.
   * addGsaContent does not support repeating extension data with 
   * the same extension name.  Even though addGsaContent will successfully
   * add repeating extensions, there is no guarantee which of the repeating
   * extensions with the same extension name will be returned by methods
   * getGsaContent() and getAllGsaContents().
   * 
   * @param name name of the content extension
   * @param content content data of the extension
   */
  public void addGsaContent(String name, String content) {
    GsaExtension extension = new GsaExtension();
    extension.setContentName(name);
    extension.setContentValue(content);
    getRepeatingExtension(GsaExtension.class).add(extension);
  }

  /**
   * Retrieve the extension's content data having the input extension name.
   * GsaEntry does not support repeating extensions.  If there
   * are repeating extensions with the same name, there is no guarantee which
   * extension's content data will be returned.
   * 
   * @param name name of the extension
   * @return String value of the content data
   */
  public String getGsaContent(String name) {
    
    for (GsaExtension extension : getRepeatingExtension(GsaExtension.class)) {
      if (extension.getContentName().equals(name)) {
        return extension.getContentValue();
      }
    }
    
    return null;
  }

  /**
   * Removes all extension data having the input extension name.
   * Because GsaEntry does not support repeating extensions, 
   * all extensions having the input name will be removed. 
   * 
   * @param name all extension having this input name will be removed
   */
  public void removeGsaContent(String name) {

    Iterator<GsaExtension> i = getRepeatingExtension(GsaExtension.class).iterator();
    while (i.hasNext()) {
      GsaExtension extension = i.next();
      if (extension.getContentName().equals(name)) {
        i.remove();
      }
    }
  }
  
  /**
   * Retrieves all content data of this GsaEntry's extensions.
   * The content data is stored in a Map, with the map key being the extension names.
   * Because GsaEntry does not support repeating extensions,
   * if there are repeating extensions in the entry, there is no guarantee
   * which extension's content data will be put into the map, and which
   * will be excluded.
   * 
   * @return Map of Strings containing the entry's extension content data
   */
  public Map<String, String> getAllGsaContents() {

    // do not use com.google.gdata.util.commons.collect
    // because this is to be distributed as client library
    Map<String, String> returnMap = new HashMap<String, String>();

    for (GsaExtension extension : getRepeatingExtension(GsaExtension.class)) {
      String currentKey = extension.getContentName();
      returnMap.put(currentKey, extension.getContentValue());
    }
    return returnMap;
  }

}

