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

/**
 * Class holding feed-wide constants and name spaces.
 * 
 * 
 */
public class GsaNamespaces {

  private static final String GSA_URI = "http://schemas.google.com/gsa/2007";
  private static final String GSA_ALIAS = "gsa";

  /**
   * Namespace object for the Google Search Appliance name space.
   * Contains an URI and an alias for the search appliance APIs.
   */
  public static final XmlWriter.Namespace GSA_NAMESPACE = 
      new XmlWriter.Namespace(GSA_ALIAS, GSA_URI);
}
