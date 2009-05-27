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

import com.google.gdata.client.GoogleService;
import com.google.gdata.client.Service;
import com.google.gdata.util.Version;

/**
 * 
 *
 */
public class GsaService extends GoogleService {
  public static final String GSA_SERVICE = "GSA";
  
  /** The Versions class contains all released versions for GsaService. */
  public static class Versions {

    /**
      * Version 1 is the initial version and is based upon Version 1 of the
      * GData Protocol.
      */
    public static final Version V1 =
         new Version(GsaService.class, "1.0", Service.Versions.V1);

    /**
      * Version 2 adds full compliance with the Atom Publishing Protocol and is
      * based upon Version 2 of the GData protocol.
      */
    public static final Version V2 =
         new Version(GsaService.class, "2.0", Service.Versions.V2);
  } 

  /**
   * Version 2 is the current default version for GsaService.
   */
  public static final Version DEFAULT_VERSION =
    Service.initServiceVersion(GsaService.class, GsaService.Versions.V2);

  /**
   * Constructs an instance connecting to the GSA service for an application
   * with the name {@code applicationName}.  
   * The service will authenticate at the provided {@code domainName}.
   * 
   *  @param applicationName the name of the client application accessing the
   *                        service. Application names should preferably have
   *                        the format [company-id]-[app-name]-[app-version].
   *                        The name will be used by the Google servers to
   *                        monitor the source of authentication.
   * @param protocol        name of protocol to use for authentication
   *                        ("http"/"https")
   * @param domainName      the name of the domain hosting the login handler
   */
  public GsaService(String applicationName, 
                    String protocol, String domainName) {
    super(GSA_SERVICE, applicationName, protocol, domainName);
    declareExtensions();
  }
  
  /**
   * Declare the extensions of the feeds for the GSA service.
   */
  private void declareExtensions() {
    new GsaEntry().declareExtensions(extProfile);
    new GsaFeed().declareExtensions(extProfile);
  }
}
