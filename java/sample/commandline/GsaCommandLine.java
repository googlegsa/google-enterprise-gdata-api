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


/**
 * Sample Command line application to access GSA GData Admin API.
 * 
 * 
 *
 */
public class GsaCommandLine {

  /**
   * Utility classes should not have a public or default constructor.
   */
  private GsaCommandLine() {
    //do nothing
  }

  /**
   * Gets command from given string.
   * 
   * @param cmdString the command string
   * @return command
   */
  public static Command getCommand(String cmdString) {
    if (cmdString.equals(RetrieveCommand.COMMAND_NAME)) {
      return new RetrieveCommand();
    } else if (cmdString.equals(UpdateCommand.COMMAND_NAME)) {
      return new UpdateCommand();
    } else if (cmdString.equals(InsertCommand.COMMAND_NAME)) {
      return new InsertCommand();
    } else if (cmdString.equals(DeleteCommand.COMMAND_NAME)) {
      return new DeleteCommand();
    } 

    throw new IllegalArgumentException("unknow command: " + cmdString);    
  }

  /**
   * Executes the program.
   *
   * @param args any external arguments passed from the command line.
   */
  public static void main(String[] args) {
    if (args.length < 7) {
      usage();
      return;
    }
    try {
      // get command
      Command cmd = getCommand(args[0]);
      
      // init command arguments
      cmd.init(args);
      
      // execute command and output the result
      System.out.println(cmd.execute());
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
      usage();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Usage Description for the binary.
   */
  private static void usage() {
    System.err.println("Usage: GsaCommandLine <command> <options> <query_parameters> feed entry ");
    System.err.println("commands:");
    System.err.println("    retrieve\n    update\n    insert\n    delete");
    System.err.println("options:\n  --protocol:\n  --hostname:\n  --port:\n" 
        + "  --username:\n  --password:\n  --input: The input entry file for insert/update\n"
        + "<query_parameters>:\n"
        + "  All the query parameters can be specified by --<query>=<value>\n");
    System.err.println("Example:\n  GsaCommandLine retrieve --protocol=http " 
        + "--hostname=gsa1 --port=8000 --username=user --password=password config "
        + " crawlURLs");
  }
}
