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
using System.Text;
using Google.GData.Client;

namespace Google.GData.Gsa
{

    /// <summary>
    /// The GsaService class extends the basic Service abstraction
    /// to define a service that is preconfigured for access to the
    /// Google Search Appliance data API.
    /// </summary>
    public class GsaService : Service
    {

        /// <summary>The Google Search Appliance service's name</summary>
        public const string GSAService = "GSA";
        public const string HttpDefaultProtocol = "http";
        public const string AppName = "gsa-api-client";
        public const string FeedHeader = "feeds";
        public const int DefaultHttpPort = 8000;

        private string protocol;
        private string address;
        private string httpPort;
        private string gsaUrlStr;

        /// <summary>
        /// Constructs an instance connecting to the GSA server.
        /// </summary>
        /// <param name="address">the address of the GSA server</param>
        /// <param name="username">the name of the user</param>
        /// <param name="password">the password of the user</param>
        public GsaService(string address, string username, string password)
            : this(HttpDefaultProtocol, address, DefaultHttpPort, username, password)

        {

        }

        /// <summary>
        /// Constructs an instance connecting to the GSA server.
        /// </summary>
        /// <param name="address">the address of the GSA server</param>
        /// <param name="port">the port of the GSA server</param>
        /// <param name="username">the name of the user</param>
        /// <param name="password">the password of the user</param>
        public GsaService(string address, int port, string username, string password)
            : this(HttpDefaultProtocol, address, port, username, password)
        {

        }

        /// <summary>
        /// Constructs an instance connecting to the GSA server.
        /// </summary>
        /// <param name="protocol"></param>
        /// <param name="address">the address of the GSA server</param>
        /// <param name="port">the port of the GSA server</param>
        /// <param name="username">the name of the user</param>
        /// <param name="password">the password of the user</param>
        public GsaService(string protocol, string address, int port, string username, string password)
            : base(GSAService, AppName)
        {
            this.protocol = protocol;
            this.address = address;
            this.httpPort = port.ToString();
            this.gsaUrlStr = protocol + "://" + address + ":" + port + "/" + FeedHeader;
            setUserCredentials(username, password);
            this.NewFeed += new ServiceEventHandler(this.OnNewFeed);
            OnRequestFactoryChanged();
        }



        /// <summary>
        /// notifier if someone changes the requestfactory of the service
        /// </summary>
        public override void OnRequestFactoryChanged()
        {
            base.OnRequestFactoryChanged();
            GDataGAuthRequestFactory factory = this.RequestFactory as GDataGAuthRequestFactory;
            if (factory != null)
            {
                factory.Handler = protocol + "://" + address + ":" + httpPort + "/accounts/ClientLogin";
            }
        }

        //////////////////////////////////////////////////////////////////////
        /// <summary>eventchaining. We catch this by from the base service, which 
        /// would not by default create an atomFeed</summary> 
        /// <param name="sender"> the object which send the event</param>
        /// <param name="e">FeedParserEventArguments, holds the feedentry</param> 
        /// <returns> </returns>
        //////////////////////////////////////////////////////////////////////
        protected void OnNewFeed(object sender, ServiceEventArgs e)
        {
            Tracing.TraceMsg("Created new Gsa Feed");
            if (e == null)
            {
                throw new ArgumentNullException("e");
            }
            e.Feed = new GsaFeed(e.Uri, e.Service);

        }

        /// <summary>
        /// URI encode the string.
        /// </summary>
        private string UriSingleEncode(string origin)
        {
            return Utilities.UriEncodeReserved(origin);
        }

        /// <summary>
        /// Creates a URI string based on the feed name.
        /// </summary>
        /// <param name="feedName">name of the feed, to be appended to the URI</param>
        /// <returns>a URI string by concatenating feed name with header, address, port, etc.</returns>
        private string GetFeedUri(string feedName)
        {
            if (feedName == null)
            {
                throw new ArgumentNullException("feed is not specified");
            }
            return gsaUrlStr + "/" + feedName;
        }

        /// <summary>
        /// Creates a URI string based on the feed name and query parameters.
        /// </summary>
        /// <param name="feedName">name of the feed to be appended to the URI</param>
        /// <param name="queries">Dictionary of query parameters</param>
        /// <returns>a URI string by concatenating different parts of the URI</returns>
        private string GetFeedUri(string feedName, Dictionary<string, string> queries)
        {
            if (feedName == null)
            {
                throw new ArgumentNullException("feed is not specified");
            }
            StringBuilder queryStr = new StringBuilder();
            foreach (KeyValuePair<string, string> q in queries)
            {
                queryStr.Append(queryStr.Length != 0 ? '&' : '?');
                queryStr.Append(UriSingleEncode(q.Key));
                queryStr.Append("=");
                queryStr.Append(UriSingleEncode(q.Value));
            }
            return gsaUrlStr + "/" + feedName + queryStr.ToString() ;
        }

        /// <summary>
        /// Creates a URI string based on the feed name and the entry ID.
        /// </summary>
        /// <param name="feedName">name of the feed to be appended to the URI</param>
        /// <param name="entryId">ID of the entry, to be appended to the URI</param>
        /// <returns>a URI string by concatenating different parts of the URI</returns>
        private string GetEntryUri(string feedName, string entryId)
        {
            if (feedName == null || entryId == null)
            {
                throw new ArgumentNullException("feed/entry is not specified");
            }
            return gsaUrlStr + "/" + feedName + "/" + UriSingleEncode(entryId);
        }

        /// <summary>
        /// Creates a URI string based on the feed name, the entry ID and query parameters.
        /// </summary>
        /// <param name="feedName">name of the feed to be appended to the URI</param>
        /// <param name="entryId">ID of the entry, to be appended to the URI</param>
        /// <param name="queries">Dictionary of query parameters</param>
        /// <returns>a URI string by concatenating different parts of the URI</returns>
        private string GetEntryUri(string feedName, string entryId, Dictionary<string, string> queries)
        {
            if (feedName == null || entryId == null)
            {
                throw new ArgumentNullException("feed/entry is not specified");
            }
            StringBuilder queryStr = new StringBuilder();
            foreach (KeyValuePair<string, string> q in queries)
            {
                queryStr.Append(queryStr.Length != 0 ? '&' : '?');
                queryStr.Append(UriSingleEncode(q.Key));
                queryStr.Append("=");
                queryStr.Append(UriSingleEncode(q.Value));
            }
            return gsaUrlStr + "/" + feedName + "/" + UriSingleEncode(entryId) + queryStr.ToString();
        }

        /// <summary>
        /// Creates an instance of the feed identified by the feed name.
        /// </summary>
        /// <param name="feedName">name of the feed</param>
        /// <returns>an instance of the feed</returns>
        public GsaFeed GetFeed(string feedName)
        {
            string uri = GetFeedUri(feedName);
            GsaFeed feed = Query(new FeedQuery(uri)) as GsaFeed;
            return feed;
        }

        /// <summary>
        /// Creates a feed whose entries are filtered based on the query parameters.
        /// </summary>
        /// <param name="feedName">name of the feed</param>
        /// <param name="queries">Dictionary of query parameters</param>
        /// <returns>a feed containing the filtered entries</returns>
        public GsaFeed QueryFeed(string feedName, Dictionary<string, string> queries)
        {
            string uri = GetFeedUri(feedName, queries);
            GsaFeed feed = Query(new FeedQuery(uri)) as GsaFeed;
            return feed;
        }


        /// <summary>
        /// Returns an entry from the feed provider.
        /// </summary>
        /// <param name="feedName">name of the feed containing the entry</param>
        /// <param name="entryId">ID of the entry to fetch from the feed provider</param>
        /// <returns>an entry corresponding to data fetched from the server</returns>
        public GsaEntry GetEntry(string feedName, string entryId)
        {
            string uri = GetEntryUri(feedName, entryId);
            GsaFeed feed = Query(new FeedQuery(uri)) as GsaFeed;
            return feed.Entries[0] as GsaEntry;
        }

        /// <summary>
        /// Returns an entry from the feed provider which is filtered
        /// based on the query parameters.
        /// </summary>
        /// <param name="feedName">name of the feed containing the entry</param>
        /// <param name="entryId">ID of the entry to fetch from the feed provider</param>
        /// <param name="queries">Dictionary of query parameters</param>
        /// <returns>an entry corresponding to data fetched from the server</returns>
        public GsaEntry QueryEntry(string feedName, string entryId, Dictionary<string, string> queries)
        {
            string uri = GetEntryUri(feedName, entryId, queries);
            GsaFeed feed = Query(new FeedQuery(uri)) as GsaFeed;
            return feed.Entries[0] as GsaEntry;
        }

        /// <summary>
        /// Inserts an entry into a feed provider.
        /// </summary>
        /// <param name="feedName">name of the feed provider in which to insert the entry</param>
        /// <param name="entry">entry representation of the data to insert into the server</param>
        /// <returns>an entry representing the data successfully inserted into server</returns>
        public GsaEntry InsertEntry(string feedName, GsaEntry entry)
        {
            string uri = GetFeedUri(feedName);
            return Insert(new Uri(uri), entry);
        }

        /// <summary>
        /// Invokes the update function on the server's feed provider.
        /// </summary>
        /// <param name="feedName">name of the feed containing the entry</param>
        /// <param name="entryId">ID of the entry to update</param>
        /// <param name="updateEntry">entry object containing the data to be updated to the server</param>
        /// <returns>an entry corresponding to data successfully updated in the server</returns>
        public GsaEntry UpdateEntry(string feedName, string entryId, GsaEntry updateEntry)
        {
            string uri = GetEntryUri(feedName, entryId);
            updateEntry.EditUri = new AtomUri(uri);
            return Update(updateEntry);
        }

        /// <summary>
        /// Invokes the delete function on the server's feed provider.
        /// </summary>
        /// <param name="feedName">name of the feed containing the entry</param>
        /// <param name="entryId">ID of the entry to delete</param>
        public void DeleteEntry(string feedName, string entryId)
        {
            string uri = GetEntryUri(feedName, entryId);
            Delete(new Uri(uri));
        }


    }


}