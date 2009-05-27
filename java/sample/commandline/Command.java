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


package sample.commandline;

import com.google.gdata.util.common.xml.XmlWriter;
import com.google.enterprise.apis.client.GsaClient;
import com.google.enterprise.apis.client.GsaEntry;
import com.google.enterprise.apis.client.GsaFeed;
import com.google.gdata.data.ExtensionProfile;
import com.google.gdata.data.ParseSource;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ParseException;
import com.google.gdata.util.ServiceException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Base Class for GData command to access GSA Admin API.
 *  
 * 
 *
 */
public abstract class Command {
  
  protected Map<String, String> optionMap = new HashMap<String, String>();
  protected List<String> argsList = new ArrayList<String>();
  protected GsaClient gsaClient;
  private static final String[] BASIC_OPTIONS = {
    "protocol", "hostname", "port", "username", "password"
  };
  
  /**
   * Initiates arguments and creates gdata client.
   * 
   * @param args command line arguments
   * @throws IllegalArgumentException if any error in arguments
   * @throws AuthenticationException, if authentication failed
   */
  public void init(String[] args) throws IllegalArgumentException, AuthenticationException {
    parseArgs(args);
    initClient();
    removeBasicOption();
    validate();
  }
  
  /**
   * Parses the arguments from String array.
   * 
   * Any argument should begin with '--' except the {@code command}, {@code feedName}
   * and {@code entryName}. The {@code command} should be put in the first position 
   * of the command line arguments.
   * 
   * @param args Arguments array
   */
  private void parseArgs(String [] args) throws IllegalArgumentException {

    // parse arguments
    for (String arg : args) {
      if (arg.startsWith("--")) {
        parseOption(arg.substring(2));
      } else {
        argsList.add(arg);
      }
    }
    validateBasicOption();
  }
  
  /**
   * Parses key and value pair from a string {@code argString}.
   * 
   * The key-value pair will be restored in {@code optionMap}. 
   * 
   * @param argString the argument String
   */
  private void parseOption(String argString) throws IllegalArgumentException {
    int index = argString.indexOf('=');

    if (index == -1) {
      optionMap.put(argString, null);
    } else {
      optionMap.put(argString.substring(0, index), argString.substring(index + 1));
    }
  }
  
  /**
   * Validates command arguments.
   * 
   * @throws IllegalArgumentException
   */
  protected abstract void validate() throws IllegalArgumentException;
  
  /**
   * Removes basic options so that only options specified for command will be put
   * into {@code validate}.
   */
  protected void removeBasicOption() {
    for (String option : BASIC_OPTIONS) {
      optionMap.remove(option);
    }
  }
  
  /**
   * Validates options for creating {@code GsaClient}.
   * 
   * @throws IllegalArgumentException
   */
  private void validateBasicOption() throws IllegalArgumentException {
    for (String option : BASIC_OPTIONS) {
      if (optionMap.get(option) == null) {
        throw new IllegalArgumentException("Please specify --" + option);
      }
    }
  }
  
  /**
   * Creates GsaClient to access GSA GData API.
   * 
   * @throws AuthenticationException if failed to authenticate.
   */
  private void initClient() throws AuthenticationException {
    if (gsaClient == null) {
      gsaClient = new GsaClient(optionMap.get("protocol"), 
                                optionMap.get("hostname"), 
                                Integer.parseInt(optionMap.get("port")), 
                                optionMap.get("username"), 
                                optionMap.get("password"));
     }
  }
  
  /**
   * Gets GsaEntry from the file specified with --input option.
   * 
   * @param inputFileName input file name
   * @return the GsaEntry parsed from given file
   * @throws ServiceException 
   * @throws IOException 
   * @throws ParseException
   */
  protected GsaEntry getEntryFromFile(String inputFileName) throws ParseException, 
      IOException, ServiceException {
    GsaEntry entry = null;

    ParseSource source = new ParseSource(new FileInputStream(inputFileName));
    entry = GsaEntry.readEntry(source, GsaEntry.class, new ExtensionProfile());

    return entry;
  }
  
  /**
   * Generates an XML representation string for given entry.
   * 
   * @param entry the {@code GsaEntry} to generate XML
   * @return XML String
   * @throws IOException 
   */
  protected String getXmlOutput(GsaEntry entry) throws IOException {
    if (entry == null) {
      return null;
    }
    StringWriter stringWriter = new StringWriter();
    XmlWriter xmlWriter = new XmlWriter(stringWriter);
    entry.generate(xmlWriter, new ExtensionProfile());
    
    return stringWriter.toString();
  }
  
  /**
   * Generates an XML representation string for given feed.
   * 
   * @param feed the {@code GsaFeed} to generate XML
   * @return XML String
   * @throws IOException 
   */
  protected String getXmlOutput(GsaFeed feed) throws IOException {
    if (feed == null) {
      return null;
    }
    StringWriter stringWriter = new StringWriter();
    XmlWriter xmlWriter = new XmlWriter(stringWriter);
    feed.generate(xmlWriter, new ExtensionProfile());
    
    return stringWriter.toString();
  }
  
  /**
   * Executes command to access GSA Admin API.
   * 
   * @return result in XML representation string
   * @throws MalformedURLException
   * @throws ServiceException
   * @throws IOException
   */
  public abstract String execute() throws MalformedURLException, ServiceException, IOException;
}
