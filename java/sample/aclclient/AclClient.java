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


package sample.aclclient;

import com.google.enterprise.apis.client.GsaClient;
import com.google.enterprise.apis.client.GsaEntry;
import com.google.enterprise.apis.client.GsaFeed;
import com.google.enterprise.apis.client.Terms;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * A sample to import acl ruls from a file to GSA through GData API.
 * 
 * 
 */
public class AclClient {

  protected static GsaClient gsaClient;
  
  private AclClient() {
  }
  
  /** Clears all the policy acl rules. */
  private static void clearAllRules() throws MalformedURLException,
      ServiceException, IOException {
    Map<String, String> queries = new HashMap<String, String>();
    List<GsaEntry> entries = new LinkedList<GsaEntry>();
    
    Integer startLine = 0;
    queries.put(Terms.QUERY_MAX_LINES, "500");
    
    GsaFeed feed;
    
    do {
      queries.put(Terms.QUERY_START_LINE, startLine.toString());
      feed = gsaClient.queryFeed(Terms.FEED_POLICY_ACLS, queries);
      entries.addAll(feed.getEntries());
      startLine += 500;
    } while (feed.getEntries().size() > 0);
    
    for (GsaEntry entry : entries) {
      gsaClient.deleteEntry(Terms.FEED_POLICY_ACLS, 
          entry.getGsaContent(Terms.PROPERTY_URL_PATTERN));
      System.out.println("Cleared rule " + entry.getGsaContent(Terms.PROPERTY_URL_PATTERN));
    }
  }
  
  /** Adds new rules from a reader. 
   *  The format of the ACL rules should be something like:
   *  'group:enterprise user:tom user:john group:interns\n
   *   user:jerry group:school'
   */
  private static void addNewRules(Reader reader) throws IOException, ServiceException {
    BufferedReader bufferReader = new BufferedReader(reader);
    String line = null;
    
    while ((line = bufferReader.readLine()) != null) {
      if (!line.equals("")) {
        String[] result = line.split(" ", 2);
        if (result.length == 2) {
          System.out.println("Add rule: " + line);
          GsaEntry entry = new GsaEntry();
          entry.addGsaContent(Terms.PROPERTY_URL_PATTERN, result[0]);
          entry.addGsaContent(Terms.PROPERTY_POLICY_ACL, result[1]);
          gsaClient.insertEntry(Terms.FEED_POLICY_ACLS, entry);
        }
      }
    }
  }
  
  public static void main(String[] args) {
    String address = null;
    String fileName = null;
    String user = null;
    String password = null;
    boolean isReplaceAll = false;
    
    // Parses arguments
    for (int i = 0; i < args.length && args[i].startsWith("-");) {
      String arg = args[i++];
      if (arg.equalsIgnoreCase("--host")) {
        if (i < args.length) {
          address = args[i++];          
        } else {
          System.err.println("Please specify hostname");
        }
      } else if (arg.equalsIgnoreCase("--user")) {  
        if (i < args.length) {
          user = args[i++];          
        } else {
          System.err.println("Please specify username");
        }
      } else if (arg.equalsIgnoreCase("--password")) {
        if (i < args.length) {
          password = args[i++];          
        } else {
          System.err.println("Please specify password");
        }
      } else if (arg.equalsIgnoreCase("--file")) {
        if (i < args.length) {
          fileName = args[i++];          
        } else {
          System.err.println("Please specify a file to import acl rules");
        }
      } else if (arg.equalsIgnoreCase("--replace_all")) {
        isReplaceAll = true;
      } else {
        System.err.println("Unknow option " + arg);
      }
    }
    
    if (address == null || user == null || password == null || fileName == null) {
      System.err.println("Usage: AclClient --host hostname --user username " +
          "--password password --file filename [--replace_all]");
      return;
    }
    try {
      if (password.equals("-")) {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        password = in.readLine();
      }
      System.out.println("Start import");
      gsaClient = new GsaClient(address, user, password);
      System.out.println("Login to " + address);
      
      // clear all old rules if required
      if (isReplaceAll) {
        clearAllRules();
        System.out.println("Cleared all the rules");
      }
      
      //add new rules from file
      addNewRules(new FileReader(fileName));
      System.out.println("All rules have been imported.");
      
    } catch (AuthenticationException e) {
      e.printStackTrace();
    } catch (MalformedURLException e) {  
      e.printStackTrace();
    } catch (ServiceException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }  
}
