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

import com.google.gdata.util.common.base.CharEscapers;
import com.google.gdata.client.Query;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Simple, thin implementation of the GsaClient interface.
 * 
 * This simple implementation assumes that the client uses the most straightforward
 * functions of GoogleService.
 * 
 * 
 */
public class GsaClient {
  private static final String HTTP_DEFAULT_PROTOCOL = "http";
  private static final String APP_NAME = "gsa-api-client";
  private static final String FEED_HEADER = "feeds";
  private static final int DEFAULT_HTTP_PORT = 8000;

  private String address;
  private String httpPort;
  private String gsaUrlStr;
  private GsaService service;

  //This constructor will never be invoked
  @SuppressWarnings("unused")
  private GsaClient() {
  }

  /**
   * Constructs an instance connecting to the GSA server with address
   * {@code addr}, default port and default protocol.
   * @param addr the address of the GSA server
   * @param userId the name of the user
   * @param userPwd the password of the user
   * @throws AuthenticationException if authentication failed.
   */
  public GsaClient(String addr, String userId, String userPwd) 
      throws AuthenticationException {
      this(HTTP_DEFAULT_PROTOCOL, addr, DEFAULT_HTTP_PORT, userId, userPwd);
  }

  /**
   * Constructs an instance connecting to the GSA server with address
   * {@code addr}, port {@code port} and default protocol.
   * @param addr the address of the GSA server
   * @param port the port of the GSA server
   * @param userId the name of the user
   * @param userPwd the password of the user
   * @throws AuthenticationException if authentication failed.
   */
  public GsaClient(String addr, int port, String userId, String userPwd)
      throws AuthenticationException {
    this(HTTP_DEFAULT_PROTOCOL, addr, port, userId, userPwd);
  }
  
  /**
   * Constructs an instance connecting to the GSA server with address
   * {@code addr}, port {@code port} and protocol{@code protocol}.
   * @param protocol the protocol of the GSA server
   * @param addr the address of the GSA server
   * @param port the port of the GSA server
   * @param userId the name of the user
   * @param userPwd the password of the user
   * @throws AuthenticationException if authentication failed.
   */
  public GsaClient(String protocol, String addr, int port, String userId, String userPwd)
      throws AuthenticationException {
    this.address = addr;
    this.httpPort = String.valueOf(port);
    this.gsaUrlStr = protocol + "://" + 
                     address + ":" + httpPort + "/" + FEED_HEADER;
    this.service = new GsaService(APP_NAME, protocol, address + ":" + httpPort);
    service.setUserCredentials(userId, userPwd);
  }

  /**
   * Creates a URL object based on the feed name.
   * 
   * @param feedName name of the feed, to be appended to the URI
   * @return a URL by concatenating feed name with header, address, port, etc.
   * @throws MalformedURLException
   */
  private URL getFeedUrl(String feedName) throws MalformedURLException {
    if (feedName == null) {
      throw new MalformedURLException("feed is not specified");
    }
    return new URL(gsaUrlStr + "/" + feedName);
  }
  
  /**
   * Creates a URL object based on the feed name and the entry ID.
   * 
   * @param feedName name of the feed to be appended to the URI
   * @param entryId ID of the entry, to be appended to the URI
   * @return a URL by concatenating different parts of the URI
   * @throws MalformedURLException
   */
  private URL getEntryUrl(String feedName, String entryId) throws MalformedURLException {
    if (feedName == null || entryId == null) {
      throw new MalformedURLException("feed/entry is not specified");
    }
    return new URL(gsaUrlStr + "/" + feedName + "/" + CharEscapers.uriEscaper().escape(entryId));
  }

  /**
   * Creates a URL object based on the feed name, the entry ID and query parameters.
   * 
   * @param feedName name of the feed to be appended to the URI
   * @param entryId ID of the entry, to be appended to the URI
   * @param queries map of query parameters
   * @return a URL by concatenating different parts of the URI
   * @throws MalformedURLException
   */
  private URL getEntryUrl(String feedName, String entryId, Map<String, String> queries)
                          throws MalformedURLException {
    if (feedName == null || entryId == null) {
      throw new MalformedURLException("feed/entry is not specified");
    }
    StringBuilder queryStr = new StringBuilder();
    for (Entry<String, String> q : queries.entrySet()) {
      queryStr.append(queryStr.length() != 0 ? '&' : '?');
      queryStr.append(CharEscapers.uriEscaper().escape(q.getKey()));
      queryStr.append("=");
      queryStr.append(CharEscapers.uriEscaper().escape(q.getValue()));
    }
    return new URL(gsaUrlStr + "/" + feedName + "/" + CharEscapers.uriEscaper().escape(entryId) +
        queryStr.toString());
  }

  /**
   * Creates an instance of the feed identified by the feed name.
   *  
   * @param feedName name of the feed
   * @return an instance of the feed
   * @throws MalformedURLException
   * @throws ServiceException
   * @throws IOException
   */
  public GsaFeed getFeed(String feedName) throws MalformedURLException, 
                                                 ServiceException, IOException {
    
    // Mark the feed as an Event feed:
    //new GsaFeed().declareExtensions(service.getExtensionProfile());
    URL feedUrl = getFeedUrl(feedName);
    
    // Send the request and receive the response:
    GsaFeed myFeed = service.getFeed(feedUrl, GsaFeed.class);
    return myFeed;
  }

  /**
   * Creates a feed whose entries are filtered based on the query parameters.
   * 
   * @param feedName name of the feed
   * @param queries map of query parameters
   * @return a feed containing the filtered entries
   * @throws MalformedURLException
   * @throws ServiceException
   * @throws IOException
   */
  public GsaFeed queryFeed(String feedName, Map<String, String> queries) 
                           throws MalformedURLException, ServiceException, IOException {
    URL feedUrl = getFeedUrl(feedName);
    Query query = new Query(feedUrl);

    for (Entry<String, String> q : queries.entrySet()) {
      query.setStringCustomParameter(q.getKey(), q.getValue());
    }

    GsaFeed resultsFeed = service.query(query, GsaFeed.class);
    return resultsFeed;
  }

  /**
   * Inserts an entry into a feed provider.
   * 
   * @param feedName name of the feed provider in which to insert the entry.
   * @param entry entry representation of the data to insert into the server.
   * @return an entry representing the data successfully inserted into server.
   * @throws ServiceException
   * @throws IOException
   */
  public GsaEntry insertEntry(String feedName, GsaEntry entry)
                              throws ServiceException, IOException {
    URL feedUrl = getFeedUrl(feedName);
    GsaEntry insertedEntry = service.insert(feedUrl, entry);
    return insertedEntry;
  }
  
  /**
   * Returns an entry from the feed provider.
   * 
   * @param feedName name of the feed containing the entry
   * @param entryId ID of the entry to fetch from the feed provider
   * @return an entry corresponding to data fetched from the server
   * @throws MalformedURLException
   * @throws ServiceException
   * @throws IOException
   */
  public GsaEntry getEntry(String feedName, String entryId) 
      throws MalformedURLException, ServiceException, IOException {
    URL entryUrl = getEntryUrl(feedName, entryId);
    GsaEntry retrievedEntry = service.getEntry(entryUrl, GsaEntry.class);
    return retrievedEntry;
  }

  /**
   * Returns an entry from the feed provider which is filtered
   * based on the query parameters.
   * 
   * @param feedName name of the feed containing the entry
   * @param entryId ID of the entry to fetch from the feed provider
   * @param queries map of query parameters
   * @return an entry corresponding to data fetched from the server
   * @throws MalformedURLException
   * @throws ServiceException
   * @throws IOException
   */
  public GsaEntry queryEntry(String feedName, String entryId, Map<String, String> queries)
      throws MalformedURLException, ServiceException, IOException {
    URL entryUrl = getEntryUrl(feedName, entryId, queries);
    GsaEntry retrievedEntry = service.getEntry(entryUrl, GsaEntry.class);
    return retrievedEntry;
  }

  /**
   * Invokes the update function on the server's feed provider.
   * 
   * @param feedName name of the feed containing the entry
   * @param entryId ID of the entry to update
   * @param updateEntry entry object containing the data to be updated to the server
   * @return an entry corresponding to data successfully updated in the server
   * @throws MalformedURLException
   * @throws ServiceException
   * @throws IOException
   */
  public GsaEntry updateEntry(String feedName, String entryId, GsaEntry updateEntry) 
                              throws MalformedURLException, ServiceException, IOException {
    URL editUrl = getEntryUrl(feedName, entryId);
    GsaEntry updatedEntry = service.update(editUrl, updateEntry);
    return updatedEntry;
  }

  /**
   * Invokes the delete function on the server's feed provider.
   * 
   * @param feedName name of the feed containing the entry
   * @param entryId ID of the entry to delete
   * @throws MalformedURLException
   * @throws ServiceException
   * @throws IOException
   */
  public void deleteEntry(String feedName, String entryId) throws MalformedURLException,
                                                                  ServiceException, IOException {
    URL deleteUrl = getEntryUrl(feedName, entryId);
    service.delete(deleteUrl);
  }

  /**
   * Returns the host address used to instantiate this GSA client.
   * 
   * @return the host address used to instantiate the GSA client
   */
  public String getAddress() {
    return address;
  }

  /**
   * Returns the Gsa Url String used to construct this GSA Feed.
   * 
   * @return the Gsa Url String used to construct the GSA Feed
   */
  public String gsaUrlStr() {
    return gsaUrlStr;
  }  
  
}
