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

/**
 * This contains all the hard-coded entry/feed/parameters names
 * (carefully chosen)
 *
 * 
 *
 */
public class Terms {

  /** Here are the String literals for feed names. */

  public static final String FEED_COLLECTION = "collection";
  public static final String FEED_COMMAND = "command";
  public static final String FEED_CONFIG = "config";
  public static final String FEED_CONNECTOR_MANAGER = "connectorManager";
  public static final String FEED_CONTENT_STATISTICS = "contentStatistics";
  public static final String FEED_CRAWL_ACCESS_NTLM = "crawlAccessNTLM";
  public static final String FEED_DIAGNOSTICS = "diagnostics";
  public static final String FEED_FEED = "feed";
  public static final String FEED_FRONTEND = "frontend";
  public static final String FEED_FEDERATION_CONFIG = "federation";
  public static final String FEED_INFO = "info";
  public static final String FEED_KEYMATCH = "keymatch";
  public static final String FEED_LOGS = "logs";
  public static final String FEED_ONEBOX = "onebox";
  public static final String FEED_OUTPUT_FORMAT = "outputFormat";
  public static final String FEED_SEARCH_LOG = "searchLog";
  public static final String FEED_SEARCH_REPORT = "searchReport";
  public static final String FEED_STATUS = "status";
  public static final String FEED_SYNONYM = "synonym";
  public static final String FEED_PREFIXSCORER = "prefixScorer";

  public static final String FEED_POLICY_ACLS = "policyAcls";

  /** Here are the String literals for entry names. */

  // the special root entry is for dynamic entry names
  public static final String ENTRY_ROOT = "/";

  public static final String ENTRY_CRAWL_SCHEDULE = "crawlSchedule";
  public static final String ENTRY_CRAWL_URLS = "crawlURLs";
  public static final String ENTRY_DESCRIPTION = "description";
  public static final String ENTRY_DOCUMENT_STATUS = "documentStatus";
  public static final String ENTRY_EVENT_LOG = "eventLog";
  public static final String ENTRY_FEED_TRUSTED_IP = "feedTrustedIP";
  public static final String ENTRY_FRESHNESS = "freshness";
  public static final String ENTRY_HOST_LOAD = "hostLoad";
  public static final String ENTRY_IMPORT_EXPORT = "importExport";
  public static final String ENTRY_LICENSE_INFO = "licenseInfo";
  public static final String ENTRY_ONEBOX_SETTING = "oneboxSetting";
  public static final String ENTRY_PAUSE_CRAWL = "pauseCrawl";
  public static final String ENTRY_RECRAWL_NOW = "recrawlNow";
  public static final String ENTRY_RESET_INDEX = "resetIndex";
  public static final String ENTRY_SERVING_STATUS = "servingStatus";
  public static final String ENTRY_SHUTDOWN = "shutdown";
  public static final String ENTRY_SYSTEM_STATUS = "systemStatus";

  /** Here are the String literals for property names in the entries. */

  // ID property exists in all entries
  public static final String PROPERTY_ENTRY_ID = "entryID";

  // crawl related configuration property names
  public static final String PROPERTY_CRAWL_SCHEDULE = "crawlSchedule";
  public static final String PROPERTY_DO_NOT_CRAWL_URLS = "doNotCrawlURLs";
  public static final String PROPERTY_FOLLOW_URLS = "followURLs";
  public static final String PROPERTY_IS_SCHEDULED_CRAWL = "isScheduledCrawl";
  public static final String PROPERTY_PAUSE_CRAWL = "pauseCrawl";
  public static final String PROPERTY_RECRAWL_NOW = "recrawlNow";
  public static final String PROPERTY_RECRAWL_URLS = "recrawlURLs";
  public static final String PROPERTY_RESET_INDEX = "resetIndex";
  public static final String PROPERTY_RESET_STATUS_CODE = "resetStatusCode";
  public static final String PROPERTY_RESET_STATUS_MSG = "resetStatusMsg";
  public static final String PROPERTY_START_URLS = "startURLs";

  // status related property names
  public static final String PROPERTY_ARCHIVE_URLS = "archiveURLs";
  public static final String PROPERTY_CPU_TEMPERATURE = "cpuTemperature";
  public static final String PROPERTY_CRAWL_PAGES_PER_SECOND = "crawlPagesPerSecond";
  public static final String PROPERTY_CRAWLED_URLS_TODAY = "crawledURLsToday";
  public static final String PROPERTY_DISK_CAPACITY = "diskCapacity";
  public static final String PROPERTY_ERROR_URLS_TODAY = "errorURLsToday";
  public static final String PROPERTY_FILTERED_BYTES = "filteredBytes";
  public static final String PROPERTY_FORCE_URLS = "forceURLs";
  public static final String PROPERTY_FOUND_URLS = "foundURLs";
  public static final String PROPERTY_FREQUENT_URLS = "frequentURLs";
  public static final String PROPERTY_MACHINE_HEALTH = "machineHealth";
  public static final String PROPERTY_NUM_CRAWLED_URLS = "numCrawledURLs";
  public static final String PROPERTY_NUM_EXCLUDE_URLS = "numExcludedURLs";
  public static final String PROPERTY_NUM_RETRIEVAL_ERRORS = "numRetrievalErrors";
  public static final String PROPERTY_OVERALL_HEALTH = "overallHealth";
  public static final String PROPERTY_QUERIES_PER_MINUTE = "queriesPerMinute";
  public static final String PROPERTY_SEARCH_LATENCY_IN_MS = "searchLatency";
  public static final String PROPERTY_RAID_HEALTH = "raidHealth";
  public static final String PROPERTY_SERVED_URLS = "servedURLs";
  public static final String PROPERTY_TIME_STAMP = "timeStamp";

  // feed related property names
  public static final String PROPERTY_ERROR_RECORDS = "errorRecords";
  public static final String PROPERTY_FEED_DATA_SOURCE = "feedDataSource";
  public static final String PROPERTY_FEED_STATE = "feedState";
  public static final String PROPERTY_FEED_TIME = "feedTime";
  public static final String PROPERTY_FEED_TYPE = "feedType";
  public static final String PROPERTY_SUCCESS_RECORDS = "successRecords";
  public static final String PROPERTY_TRUSTED_IPS = "trustedIPs";

  // Federation properties.
  public static final String PROPERTY_FED_NODE_TYPE = "nodeType";
  public static final String PROPERTY_FED_HOSTNAME = "hostname";
  public static final String PROPERTY_FED_PPP_IP = "federationNetworkIP";
  public static final String PROPERTY_FED_SECRET_TOKEN = "secretToken";
  public static final String PROPERTY_FED_REMOTE_FRONTEND = "remoteFrontend";
  public static final String PROPERTY_FED_SECONDARY_NODES = "secondaryNodes";
  public static final String PROPERTY_FED_SCORING_BIAS = "scoringBias";
  public static final String PROPERTY_FED_SLAVE_TIMEOUT = "slaveTimeout";

  // log related property names
  public static final String PROPERTY_FROM_LINE = "fromLine";
  public static final String PROPERTY_LOG_CONTENT = "logContent";
  public static final String PROPERTY_TO_LINE = "toLine";
  public static final String PROPERTY_TOTAL_LINES = "totalLines";

  // host load related property names
  public static final String PROPERTY_DEFAULT_HOST_LOAD = "defaultHostLoad";
  public static final String PROPERTY_EXCEPTION_HOST_LOAD = "exceptionHostLoad";
  public static final String PROPERTY_MAX_URLS = "maxURLs";

  // crawl access related property names
  public static final String PROPERTY_URL_PATTERN = "urlPattern";
  public static final String PROPERTY_DOMAIN = "domain";
  public static final String PROPERTY_USERNAME = "username";
  public static final String PROPERTY_PASSWORD = "password";
  public static final String PROPERTY_IS_PUBLIC = "isPublic";
  public static final String PROPERTY_ORDER = "order";

  // license related property names
  public static final String PROPERTY_APPLIANCE_ID = "applianceID";
  public static final String PROPERTY_LICENSE_ID = "licenseID";
  public static final String PROPERTY_LICENSE_VALID_UNTIL = "licenseValidUntil";
  public static final String PROPERTY_MAX_COLLECTIONS = "maxCollections";
  public static final String PROPERTY_MAX_FRONTENDS = "maxFrontends";
  public static final String PROPERTY_MAX_PAGES = "maxPages";

  // collection related property names
  public static final String PROPERTY_COLLECTION_NAME = "collectionName";
  public static final String PROPERTY_IMPORT_DATA = "importData";

  // crawlDiagnostics related property names
  public static final String PROPERTY_BACKWARD_LINKS = "backwardLinks";
  public static final String PROPERTY_CACHED = "isCached";
  public static final String PROPERTY_COLLECTION_LIST = "collectionList";
  public static final String PROPERTY_CRAWLDIAG_CRWALSTATE = "crawlState";
  public static final String PROPERTY_CRAWLDIAG_URIAT = "uriAt";
  public static final String PROPERTY_CRAWLDIAG_STATE = "docState";
  public static final String PROPERTY_CRAWLDIAG_IS_CS_ERROR = "isCookieServerError";
  public static final String PROPERTY_DOC_DATE = "date";
  public static final String PROPERTY_DOC_LASTMOD_DATE = "lastModifiedDate";
  public static final String PROPERTY_FORWARD_LINKS = "forwardLinks";

  // contentStatistics related property names
  public static final String PROPERTY_MAX_SIZE = "maxSize";
  public static final String PROPERTY_MIN_SIZE = "minSize";
  public static final String PROPERTY_TOTAL_SIZE = "totalSize";
  public static final String PROPERTY_NUM_FILES = "numFiles";
  public static final String PROPERTY_AVG_SIZE = "avgSize";

  // connectorManager related property names
  public static final String PROPERTY_URL = "url";
  public static final String PROPERTY_DESCRIPTION = "description";
  public static final String PROPERTY_STATUS = "status";

  // search report and search log related property names
  public static final String PROPERTY_REPORT_CREATION_DATE = "reportCreationDate";
  public static final String PROPERTY_REPORT_CONTENT = "reportContent";
  public static final String PROPERTY_REPORT_DATE = "reportDate";
  public static final String PROPERTY_REPORT_IS_FINAL = "isFinal";
  public static final String PROPERTY_REPORT_NAME = "reportName";
  public static final String PROPERTY_REPORT_STATE = "reportState";
  public static final String PROPERTY_REPORT_WITH_RESULTS = "withResults";
  public static final String PROPERTY_REPORT_TOP_COUNT = "topCount";
  public static final String PROPERTY_REPORT_DIAG_TERMS = "diagnosticTerms";

  // Configuration import and export
  public static final String PROPERTY_IMPORT_EXPORT_DATA = "xmlData";
  public static final String PROPERTY_IMPORT_EXPORT_PASSWORD = "password";

  // frontend related property names
  public static final String PROPERTY_FRONTEND_ONEBOX = "frontendOnebox";
  public static final String PROPERTY_IS_DEFAULT_LANGUAGE = "isDefaultLanguage";
  public static final String PROPERTY_IS_STYLE_SHEET_EDITED = "isStyleSheetEdited";
  public static final String PROPERTY_INSERT_METHOD = "insertMethod";
  public static final String PROPERTY_MAX_RESULTS = "maxResults";
  public static final String PROPERTY_NEW_LINES = "newLines";
  public static final String PROPERTY_NUM_LINES = "numLines";
  public static final String PROPERTY_NUM_PAGES = "numPages";
  public static final String PROPERTY_ORIGINAL_LINES = "originalLines";
  public static final String PROPERTY_PAGE_NUM = "pageNum";
  public static final String PROPERTY_PAGE_RANK = "pageRank";
  public static final String PROPERTY_REMOVE_URLS = "removeUrls";
  public static final String PROPERTY_RESTORE_DEFAULT_FORMAT = "restoreDefaultFormat";
  public static final String PROPERTY_SAVED_LINES = "savedLines";
  public static final String PROPERTY_START_LINE = "startLine";
  public static final String PROPERTY_STYLE_SHEET_CONTENT = "styleSheetContent";
  public static final String PROPERTY_TIMEOUT = "timeout";
  public static final String PROPERTY_TYPE = "type";
  public static final String PROPERTY_UPDATE_METHOD = "updateMethod";

  // rescoring related property names
  public static final String PROPERTY_RESCORING_POLICY = "rescoringPolicy";
  public static final String PROPERTY_RESCORING_WEIGHT = "rescoringWeight";

  // prefix rescorer config related property names
  public static final String PROPERTY_PREFIXSCORER_CONFIG_NAME = "prefixScorerConfigName";
  public static final String PROPERTY_PREFIXSCORER_CONFIG_URL = "prefixScorerConfigUrl";
  public static final String PROPERTY_PREFIXSCORER_TRYPREFIXES = "prefixScorerTryPrefixes";
  public static final String PROPERTY_PREFIXSCORER_MAPTYPE = "prefixScorerMapType";

  // shutdown related property names
  public static final String PROPERTY_COMMAND = "command";
  public static final String PROPERTY_RUNNING_STATUS = "runningStatus";


  
  public static final String PROPERTY_POLICY_ACL = "acl";
  
  /** Here are some static, pre-defined property content values. */

  public static final String VALUE_DELETE = "delete";

  public static final String VALUE_KEYMATCH_APPEND = "append";
  public static final String VALUE_KEYMATCH_REPLACE = "replace";
  public static final String VALUE_KEYMATCH_UPDATE = "update";

  public static final String VALUE_METHOD_DEFAULT = "default";
  public static final String VALUE_METHOD_CUSTOMIZE = "customize";
  public static final String VALUE_METHOD_IMPORT = "import";

  public static final String VALUE_CONNECTED = "Connected";
  public static final String VALUE_DISCONNECTED = "Disconnected";

  public static final int VALUE_RESET_STATUS_DONE = 1;
  public static final int VALUE_RESET_STATUS_PROGRESS = 2;
  public static final int VALUE_RESET_STATUS_ERROR = 3;
  public static final int VALUE_RESET_STATUS_UNKNOWN = 4;

  public static final String VALUE_RUNNING_STATUS_RUNNING = "running";
  public static final String VALUE_RUNNING_STATUS_SHUTTING_DOWN = "shuttingDown";
  public static final String VALUE_RUNNING_STATUS_REBOOTING = "rebooting";

  public static final String VALUE_COMMAND_SHUTDOWN = "shutdown";
  public static final String VALUE_COMMAND_REBOOT = "reboot";

  /** Here are some static, pre-defined property content messages. */

  public static final String MSG_RESET_STATUS_ERROR = "ERROR";
  public static final String MSG_RESET_STATUS_INPROGRESS = "PROGRESS";
  public static final String MSG_RESET_STATUS_READY = "READY";
  public static final String MSG_RESET_STATUS_UNKNOWN = "UNKNOWN";

  /** Here are the query parameter names. */

  // the most common query parameter
  public static final String QUERY_Q = "query";

  public static final String QUERY_PASSWORD = "password";

  public static final String QUERY_LANGUAGE = "language";
  public static final String QUERY_MAX_LINES = "maxLines";
  public static final String QUERY_START_LINE = "startLine";

  public static final String QUERY_CRAWLDIAG_NEGATIVESTATE = "negativeState";
  public static final String QUERY_COLLECTION_NAME = "collectionName";
  public static final String QUERY_CRAWLDIAG_URIAT = "uriAt";
  public static final String QUERY_CRAWLDIAG_SORT = "sort";
  public static final String QUERY_CRAWLDIAG_VIEW = "view";
  public static final String QUERY_PAGE_NUM = "pageNum";
  public static final String QUERY_CRAWLDIAG_PARTIALMATCH = "partialMatch";
  public static final String QUERY_CRAWLDIAG_FLATLIST = "flatList";

  public static final String QUERY_ACL_PATTERN_MATCH_MODE = "matchMode";
  
  /** Error codes illegal */
  public static final String ERROR_VALIDATION = "ValidationError";
  public static final String ERROR_ILLEGAL_PARAMETER = "IllegalParameter";
  public static final String ERROR_MISSING_PARAMETER = "MissingParameter";
  public static final String ERROR_INTERNAL = "InternalError";
  public static final String ERROR_ENTRY_NOT_EXIST = "EntryNotExist";
  public static final String ERROR_ENTRY_ALREADY_EXIST = "EntryAlreadyExist";
  public static final String ERROR_LICENSE_LIMIT = "LicenseLimit";
  public static final String ERROR_PERMISSION_DENIED = "PermissionDenied";
}
