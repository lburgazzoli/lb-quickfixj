/*******************************************************************************
 * Copyright (c) quickfixj.org  All rights reserved.
 *
 * This file is part of the QuickFIX FIX Engine
 *
 * This file may be distributed under the terms of the quickfixj.org
 * license as defined by quickfixj.org and appearing in the file
 * LICENSE included in the packaging of this file.
 *
 * This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING
 * THE WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE.
 *
 * See http://www.quickfixengine.org/LICENSE for licensing information.
 *
 * Contact ask@quickfixj.org if any conditions of this licensing
 * are not clear to you.
 ******************************************************************************/

package quickfix;

/**
 *
 */
public class SessionConstants {
    /**
     * Session setting for heartbeat interval (in seconds).
     */
    public static final String SETTING_HEARTBTINT = "HeartBtInt";

    /**
     * Session setting for enabling message latency checks. Values are "Y" or
     * "N".
     */
    public static final String SETTING_CHECK_LATENCY = "CheckLatency";

    /**
     * If set to Y, messages must be received from the counterparty with the
     * correct SenderCompID and TargetCompID. Some systems will send you
     * different CompIDs by design, so you must set this to N.
     */
    public static final String SETTING_CHECK_COMP_ID = "CheckCompID";

    /**
     * Session setting for maximum message latency (in seconds).
     */
    public static final String SETTING_MAX_LATENCY = "MaxLatency";

    /**
     * Session setting for the examples delay multiplier (0-1, as fraction of Heartbeat interval)
     */
    public static final String SETTING_TEST_REQUEST_DELAY_MULTIPLIER = "TestRequestDelayMultiplier";

    /**
     * Session scheduling setting to specify that session never reset
     */
    public static final String SETTING_NON_STOP_SESSION = "NonStopSession";

    /**
     * Session scheduling setting to specify first day of trading week.
     */
    public static final String SETTING_START_DAY = "StartDay";

    /**
     * Session scheduling setting to specify last day of trading week.
     */
    public static final String SETTING_END_DAY = "EndDay";

    /**
     * Session scheduling setting to specify time zone for the session.
     */
    public static final String SETTING_TIMEZONE = "TimeZone";

    /**
     * Session scheduling setting to specify starting time of the trading day.
     */
    public static final String SETTING_START_TIME = "StartTime";

    /**
     * Session scheduling setting to specify end time of the trading day.
     */
    public static final String SETTING_END_TIME = "EndTime";

    /**
     * Session setting to indicate whether a data dictionary should be used. If
     * a data dictionary is not used then message validation is not possble.
     */
    public static final String SETTING_USE_DATA_DICTIONARY = "UseDataDictionary";

    /**
     * Session setting specifying the path to the data dictionary to use for
     * this session. This setting supports the possibility of a custom data
     * dictionary for each session. Normally, the default data dictionary for a
     * specific FIX version will be specified.
     */
    public static final String SETTING_DATA_DICTIONARY = "DataDictionary";

    /**
     * Session setting specifying the path to the transport data dictionary.
     * This setting supports the possibility of a custom transport data
     * dictionary for each session. This setting would only be used with FIXT 1.1 and
     * new transport protocols.
     */
    public static final String SETTING_TRANSPORT_DATA_DICTIONARY = "TransportDataDictionary";

    /**
     * Session setting specifying the path to the application data dictionary to use for
     * this session. This setting supports the possibility of a custom application data
     * dictionary for each session. This setting would only be used with FIXT 1.1 and
     * new transport protocols. This setting can be used as a prefix to specify multiple
     * application dictionaries for the FIXT transport. For example:
     * <pre><code>
     * DefaultApplVerID=FIX.4.2
     * AppDataDictionary=FIX42.xml
     * AppDataDictionary.FIX.4.4=FIX44.xml
     * </code></pre>
     * This would use FIX42.xml for the default application version ID and FIX44.xml for
     * any FIX 4.4 messages.
     */
    public static final String SETTING_APP_DATA_DICTIONARY = "AppDataDictionary";

    /**
     * Default is "Y".
     * If set to N, fields that are out of order (i.e. body fields in the header, or header fields in the body) will not be rejected.
     */
    public static final String SETTING_VALIDATE_FIELDS_OUT_OF_ORDER = "ValidateFieldsOutOfOrder";

    /**
     * Session validation setting for enabling whether field ordering is
     * validated. Values are "Y" or "N". Default is "Y".
     */
    public static final String SETTING_VALIDATE_UNORDERED_GROUP_FIELDS = "ValidateUnorderedGroupFields";

    /**
     * Session validation setting for enabling whether field values are
     * validated. Empty fields values are not allowed. Values are "Y" or "N".
     * Default is "Y".
     */
    public static final String SETTING_VALIDATE_FIELDS_HAVE_VALUES = "ValidateFieldsHaveValues";

    /**
     * Allow to bypass the message validation. Default is "Y".
     */
    public static final String SETTING_VALIDATE_INCOMING_MESSAGE = "ValidateIncomingMessage";

    /**
     * Session setting for logon timeout (in seconds).
     */
    public static final String SETTING_LOGON_TIMEOUT = "LogonTimeout";

    /**
     * Session setting for logout timeout (in seconds).
     */
    public static final String SETTING_LOGOUT_TIMEOUT = "LogoutTimeout";

    /**
     * Session setting for doing an automatic sequence number reset on logout.
     * Valid values are "Y" or "N". Default is "N".
     */
    public static final String SETTING_RESET_ON_LOGOUT = "ResetOnLogout";

    /**
     * Check the next expected target SeqNum against the received SeqNum. Default is "Y".
     * If a mismatch is detected, apply the following logic:
     * <ul>
     * <li>if lower than expected SeqNum , logout</li>
     * <li>if higher, send a resend request</li>
     * </ul>
     */
    public static final String SETTING_VALIDATE_SEQUENCE_NUMBERS = "ValidateSequenceNumbers";

    /**
     * Session setting for doing an automatic sequence number reset on
     * disconnect. Valid values are "Y" or "N". Default is "N".
     */
    public static final String SETTING_RESET_ON_DISCONNECT = "ResetOnDisconnect";

    /**
     * Session setting for doing an automatic reset when an error occurs. Valid values are "Y" or "N". Default is "N". A
     * reset means disconnect, sequence numbers reset, store cleaned and reconnect, as for a daily reset.
     */
    public static final String SETTING_RESET_ON_ERROR = "ResetOnError";

    /**
     * Session setting for doing an automatic disconnect when an error occurs. Valid values are "Y" or "N". Default is
     * "N".
     */
    public static final String SETTING_DISCONNECT_ON_ERROR = "DisconnectOnError";

    /**
     * Session setting to enable milliseconds in message timestamps. Valid
     * values are "Y" or "N". Default is "Y". Only valid for FIX version >= 4.2.
     */
    public static final String SETTING_MILLISECONDS_IN_TIMESTAMP = "MillisecondsInTimeStamp";

    /**
     * Controls validation of user-defined fields.
     */
    public static final String SETTING_VALIDATE_USER_DEFINED_FIELDS = "ValidateUserDefinedFields";

    /**
     * Session setting that causes the session to reset sequence numbers when initiating
     * a logon (>= FIX 4.2).
     */
    public static final String SETTING_RESET_ON_LOGON = "ResetOnLogon";

    /**
     * Session description. Used by external tools.
     */
    public static final String SETTING_DESCRIPTION = "Description";

    /**
     * Requests that state and message data be refreshed from the message store at
     * logon, if possible. This supports simple failover behavior for acceptors
     */
    public static final String SETTING_REFRESH_ON_LOGON = "RefreshOnLogon";

    /**
     * Configures the session to send redundant resend requests (off, by default).
     */
    public static final String SETTING_SEND_REDUNDANT_RESEND_REQUEST = "SendRedundantResendRequests";

    /**
     * Persist messages setting (true, by default). If set to false this will cause the Session to
     * not persist any messages and all resend requests will be answered with a gap fill.
     */
    public static final String SETTING_PERSIST_MESSAGES = "PersistMessages";

    /**
     * Use actual end of sequence gap for resend requests rather than using "infinity"
     * as the end sequence of the gap. Not recommended by the FIX specification, but
     * needed for some counterparties.
     */
    public static final String SETTING_USE_CLOSED_RESEND_INTERVAL = "ClosedResendInterval";

    /**
     * Allow unknown fields in messages. This is intended for unknown fields with tags < 5000
     * (not user defined fields)
     */
    public static final String SETTING_ALLOW_UNKNOWN_MSG_FIELDS = "AllowUnknownMsgFields";

    public static final String SETTING_DEFAULT_APPL_VER_ID = "DefaultApplVerID";

    /**
     * Allow to disable heart beat failure detection
     */
    public static final String SETTING_DISABLE_HEART_BEAT_CHECK = "DisableHeartBeatCheck";

    /**
     * Return the last msg seq number processed (optional tag 369). Valid values are "Y" or "N".
     * Default is "N".
     */
    public static final String SETTING_ENABLE_LAST_MSG_SEQ_NUM_PROCESSED = "EnableLastMsgSeqNumProcessed";

    /**
     * Return the next expected message sequence number (optional tag 789 on Logon) on sent Logon message
     * and use value of tag 789 on received Logon message to synchronize session.
     * Valid values are "Y" or "N".
     * Default is "N".
     * This should not be enabled for FIX versions lower than 4.4
     */
    public static final String SETTING_ENABLE_NEXT_EXPECTED_MSG_SEQ_NUM = "EnableNextExpectedMsgSeqNum";

    public static final String SETTING_REJECT_INVALID_MESSAGE = "RejectInvalidMessage";

    public static final String SETTING_REJECT_MESSAGE_ON_UNHANDLED_EXCEPTION = "RejectMessageOnUnhandledException";

    public static final String SETTING_REQUIRES_ORIG_SENDING_TIME = "RequiresOrigSendingTime";

    public static final String SETTING_FORCE_RESEND_WHEN_CORRUPTED_STORE = "ForceResendWhenCorruptedStore";

    public static final String SETTING_ALLOWED_REMOTE_ADDRESSES = "AllowedRemoteAddresses";

    /**
     * Setting to limit the size of a resend request in case of missing messages.
     * This is useful when the remote FIX engine does not allow to ask for more than n message for a ResendRequest
     */
    public static final String SETTING_RESEND_REQUEST_CHUNK_SIZE = "ResendRequestChunkSize";

}
