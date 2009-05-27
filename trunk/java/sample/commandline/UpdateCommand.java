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
import com.google.gdata.util.ServiceException;

import java.io.IOException;
import java.net.MalformedURLException;

/**
 * Command to update Gsa Gdata entry.
 * 
 * 
 *
 */
public class UpdateCommand extends Command {

  public static final String COMMAND_NAME = "update";

  /* (non-Javadoc)
   * @see sample.commandline.Command#execute()
   */
  @Override
  public String execute() throws MalformedURLException, ServiceException, IOException {
    GsaEntry updateEntry = getEntryFromFile(optionMap.get("input"));
    GsaEntry resultEntry = gsaClient.updateEntry(argsList.get(1), argsList.get(2), updateEntry);
    
    return getXmlOutput(resultEntry);
  }

  /* (non-Javadoc)
   * @see sample.commandline.Command#validate()
   */
  @Override
  protected void validate() throws IllegalArgumentException {
    assert(argsList.get(0).equals(COMMAND_NAME));
    
    if (argsList.size() != 3) {
      throw new IllegalArgumentException("Please specify feed/entry for update");
    }

    if (optionMap.get("input") == null) {
      throw new IllegalArgumentException("Please specify --input for update");
    }
    
    for (String key : optionMap.keySet()) {
      if (!key.equals("input")) {
        throw new IllegalArgumentException("Unknow option " + key);
      }
    }
  }

}
