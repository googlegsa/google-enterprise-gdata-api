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
using System.Xml;
using System.IO;
using System.Collections.Generic;
using Google.GData.Client;

namespace Google.GData.Gsa
{
    /// <summary>
    /// Sample Command line application to access GSA GData Admin API.
    /// </summary>
    public class GsaCommandLine
    {
        /// <summary>
        /// Gets command from given string.
        /// </summary>
        /// <param name="cmdString">the command string</param>
        /// <returns>command</returns>
        public static Command GetCommand(string cmdString)
        {
            if (cmdString.Equals(RetrieveCommand.CommandName))
            {
                return new RetrieveCommand();
            }
            else if (cmdString.Equals(UpdateCommand.CommandName))
            {
                return new UpdateCommand();
            }
            else if (cmdString.Equals(InsterCommand.CommandName))
            {
                return new InsterCommand();
            }
            else if (cmdString.Equals(DeleteCommand.CommandName))
            {
                return new DeleteCommand();
            }

            throw new ArgumentException("unknow command: " + cmdString);
        }

        /// <summary>
        /// Executes the program.
        /// </summary>
        /// <param name="args">any external arguments passed from the command line.</param>
        public static void Main(string[] args)
        {
            if (args.Length < 7)
            {
                Usage();
                return;
            }
            try
            {
                Command cmd = GetCommand(args[0]);
                cmd.Init(args);
                Console.WriteLine(cmd.Execute());
            }
            catch (ArgumentException e)
            {
                Console.WriteLine(e.StackTrace);
                Usage();
            }
            catch (Exception e)
            {
                Console.WriteLine(e.Message);
                Console.WriteLine(e.StackTrace);
            }
        }

        /// <summary>
        /// Usage Description for the binary.
        /// </summary>
        private static void Usage()
        {
            Console.WriteLine("Usage: GsaCommandLine <command> <options> <query_parameters> feed entry ");
            Console.WriteLine("commands:");
            Console.WriteLine("    retrieve\n    update\n    insert\n    delete");
            Console.WriteLine("options:\n  --protocol:\n  --hostname:\n  --port:\n"
                + "  --username:\n  --password:\n  --input: The input entry file for insert/update\n"
                + "<query_parameters>:\n"
                + "  All the query parameters can be specified by --<query>=<value>\n");
            Console.WriteLine("Example:\n  GsaCommandLine retrieve --protocol=http "
                + "--hostname=gsa1 --port=8000 --username=user --password=password config "
                + " crawlURLs");
        }
    }

    /// <summary>
    /// Base Class for GData command to access GSA Admin API.
    /// </summary>
    public abstract class Command
    {
        protected Dictionary<string, string> OptionMap = new Dictionary<string, string>();
        protected List<string> ArgsList = new List<string>();
        protected GsaService Service;
        private string[] BasicOptions =
        {
            "protocol", "hostname", "port", "username", "password"
        };

        /// <summary>
        /// Initiates arguments and creates gdata service.
        /// </summary>
        /// <param name="args">command line arguments</param>
        public void Init(string[] args)
        {
            ParseArgs(args);
            InitClient();
            RemoveBasicOption();
            Validate();
        }

        /// <summary>
        /// Parses the arguments from String array.
        /// 
        /// Any argument should begin with '--' except the <c>command</c>, <c>feedName</c>
        /// and <c>entryName</c>. The <c>command</c> should be put in the first position
        /// of the command line arguments.
        /// </summary>
        /// <param name="args">Arguments array</param>
        private void ParseArgs(string[] args)
        {
            // parse arguments
            foreach (string arg in args)
            {
                if (arg.StartsWith("--"))
                {
                    ParseOption(arg.Substring(2));
                }
                else
                {
                    ArgsList.Add(arg);
                }
            }
            ValidateBasicOption();
        }

        /// <summary>
        /// Parses key and value pair from a string <c>ArgString</c>.
        /// The key-value pair will be restored in <c>OptionMap</c>. 
        /// </summary>
        /// <param name="argString">the argument String</param>
        private void ParseOption(string argString)
        {
            int index = argString.IndexOf('=');
            if (index == -1)
            {
                OptionMap.Add(argString, null);
            }
            else
            {
                OptionMap.Add(argString.Substring(0, index), argString.Substring(index + 1));
            }
        }

        /// <summary>
        /// Validates command arguments.
        /// </summary>
        protected abstract void Validate();

        /// <summary>
        /// Removes basic options so that only options specified for command will be put
        /// into <c>Validate</c>.
        /// </summary>
        protected void RemoveBasicOption()
        {
            foreach (string option in BasicOptions)
            {
                OptionMap.Remove(option);
            }
        }

        /// <summary>
        /// Validates options for creating <c>GsaService</c>.
        /// </summary>
        private void ValidateBasicOption()
        {
            foreach (string option in BasicOptions)
            {
                if (!OptionMap.ContainsKey(option))
                {
                    throw new ArgumentException("Please specify --" + option);
                }
            }
        }

        /// <summary>
        /// Creates <c>GsaService</c> to access GSA GData API.
        /// </summary>
        private void InitClient()
        {
            if (Service == null)
            {
                Service = new GsaService(OptionMap["protocol"],
                                            OptionMap["hostname"],
                                            int.Parse(OptionMap["port"]),
                                            OptionMap["username"],
                                            OptionMap["password"]);
            }
        }

        /// <summary>
        /// Gets GsaEntry from the file specified with --input option.
        /// </summary>
        /// <param name="inputFileName">input file name</param>
        /// <returns>the GsaEntry parsed from given file</returns>
        protected GsaEntry GetEntryFromFile(string inputFileName)
        {
            GsaFeed feed = new GsaFeed(null, null);
            feed.Parse(File.OpenRead(inputFileName), AlternativeFormat.Atom);
            GsaEntry entry = null;
            if (feed.Entries.Count > 0)
            {
                entry = feed.Entries[0] as GsaEntry;
            }
            return entry;
        }

        /// <summary>
        /// Output an XML representation string for given entry.
        /// </summary>
        /// <param name="entry">the <c>GsaEntry</c> to generate XML</param>
        protected void OutputXml(GsaEntry entry)
        {
            entry.SaveToXml(new XmlTextWriter(Console.Out));

        }

        /// <summary>
        /// Output an XML representation string for given feed.
        /// </summary>
        /// <param name="feed">the <c>GsaFeed</c> to generate XML</param>
        protected void OutputXml(GsaFeed feed)
        {
            feed.SaveToXml(new XmlTextWriter(Console.Out));
        }

        /// <summary>
        /// Executes command to access GSA Admin API.
        /// </summary>
        /// <returns></returns>
        public abstract string Execute();
    }

    /// <summary>
    /// Command to delete Gsa GData entry.
    /// </summary>
    public class DeleteCommand : Command
    {
        public const string CommandName = "delete";

        public override string Execute()
        {
            string feed = ArgsList[1];
            string entry = ArgsList[2];

            Service.DeleteEntry(feed, entry);

            return "DELETE " + feed + "/" + entry + "successfully";
        }

        protected override void Validate()
        {
            if (ArgsList.Count != 3)
            {
                throw new ArgumentException("Please specify feed/entry for delete");
            }

            if (OptionMap.Count > 0)
            {
                throw new ArgumentException("Unknow option");
            }
           
        }

    }


    /// <summary>
    /// Command to insert entry to Gsa GData feed.
    /// </summary>
    public class InsterCommand : Command
    {
        public const string CommandName = "insert";

        public override string Execute()
        {
            GsaEntry insertEntry = GetEntryFromFile(OptionMap["input"]);
            GsaEntry resultEntry = Service.InsertEntry(ArgsList[1], insertEntry);

            OutputXml(resultEntry);

            return null;
        }

        protected override void Validate()
        {
            if (ArgsList.Count != 2)
            {
                throw new ArgumentException("Please specify feed for insert");
            }

            if (!OptionMap.ContainsKey("input"))
            {
                throw new ArgumentException("Please specify --input for insert");
            }

            foreach (string key in OptionMap.Keys)
            {
                if (!key.Equals("input"))
                {
                    throw new ArgumentException("Unknow option " + key);
                }
            }

        }
    }

    /// <summary>
    /// Command to retrieve Gsa GData Entry/Feed.
    /// </summary>
    public class RetrieveCommand : Command
    {
        public const string CommandName = "retrieve";

        public override string Execute()
        {
            if (ArgsList.Count == 2)
            {
                GsaFeed feed;
                if (OptionMap.Count == 0)
                {
                    feed = Service.GetFeed(ArgsList[1]);
                }
                else
                {
                    feed = Service.QueryFeed(ArgsList[1], OptionMap);
                }
                OutputXml(feed);
            }
            else if (ArgsList.Count == 3)
            {
                GsaEntry entry;
                if (OptionMap.Count == 0)
                {
                    entry = Service.GetEntry(ArgsList[1], ArgsList[2]);
                }
                else
                {
                    entry = Service.QueryEntry(ArgsList[1], ArgsList[2], OptionMap);
                }
                OutputXml(entry);
            }

            // exception case, should not happen
            return null;
        }

        protected override void Validate()
        {
            if (ArgsList.Count < 2)
            {
                throw new ArgumentException("Please specify feed/entry for retrieve");
            }
            else if (ArgsList.Count > 3)
            {
                throw new ArgumentException("Unknow argument " + ArgsList[3]);
            }
        }
    }

    /// <summary>
    /// Command to update Gsa Gdata entry.
    /// </summary>
    public class UpdateCommand : Command
    {
        public const string CommandName = "update";

        public override string Execute()
        {
            GsaEntry updateEntry = GetEntryFromFile(OptionMap["input"]);
            GsaEntry resultEntry = Service.UpdateEntry(ArgsList[1], ArgsList[2], updateEntry);

            OutputXml(resultEntry);
            return null;
        }

        protected override void Validate()
        {
            if (ArgsList.Count != 3)
            {
                throw new ArgumentException("Please specify feed/entry for update");
            }

            if (!OptionMap.ContainsKey("input"))
            {
                throw new ArgumentException("Please specify --input for update");
            }

            foreach (string key in OptionMap.Keys)
            {
                if (!key.Equals("input"))
                {
                    throw new ArgumentException("Unknow option " + key);
                }
            }

        }
    }
}