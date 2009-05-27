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

import com.google.enterprise.apis.client.GsaEntry;
import com.google.enterprise.apis.client.GsaFeed;
import com.google.gdata.util.ServiceException;

import java.io.IOException;
import java.net.MalformedURLException;

/**
 * Command to retrieve Gsa GData Entry/Feed.
 * 
 * 
 *
 */
public class RetrieveCommand extends Command {

  public static final String COMMAND_NAME = "retrieve";
  
  /* (non-Javadoc)
   * @see sample.commandline.Command#execute()
   */
  @Override
  public String execute() throws MalformedURLException, ServiceException, IOException {
    if (argsList.size() == 2) {
      GsaFeed feed;
      if (optionMap.isEmpty()) {
        feed = gsaClient.getFeed(argsList.get(1));
      } else {
        feed = gsaClient.queryFeed(argsList.get(1), optionMap);
      }
      return getXmlOutput(feed);
    } else if (argsList.size() == 3) {
      GsaEntry entry;
      if (optionMap.isEmpty()) {
        entry = gsaClient.getEntry(argsList.get(1), argsList.get(2));
      } else {
        entry = gsaClient.queryEntry(argsList.get(1), argsList.get(2), optionMap);
      }
      return getXmlOutput(entry);
    }
    
    // exception case, should not happen
    return null;
  }

  /* (non-Javadoc)
   * @see sample.commandline.Command#validate()
   */
  @Override
  protected void validate() throws IllegalArgumentException {
    assert(argsList.get(0).equals(COMMAND_NAME));
    
    if (argsList.size() < 2) {
      throw new IllegalArgumentException("Please specify feed/entry for retrieve");
    } else if (argsList.size() > 3) {
      throw new IllegalArgumentException("Unknow argument " + argsList.get(3));
    }
  }

}
