/*******************************************************************************
 * Copyright (c) quickfixengine.org  All rights reserved.
 *
 * This file is part of the QuickFIX FIX Engine
 *
 * This file may be distributed under the terms of the quickfixengine.org
 * license as defined by quickfixengine.org and appearing in the file
 * LICENSE included in the packaging of this file.
 *
 * This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING
 * THE WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE.
 *
 * See http://www.quickfixengine.org/LICENSE for licensing information.
 *
 * Contact ask@quickfixengine.org if any conditions of this licensing
 * are not clear to you.
 ******************************************************************************/

package quickfix;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.field.converter.BooleanConverter;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Settings for sessions. Settings are grouped by FIX version and target company
 * ID. There is also a default settings section that is inherited by the
 * session-specific sections.
 *
 * Setting constants are declared in the classes using the settings. To find the
 * string constants, navigate to the class constant for the setting, select the
 * link for the setting and then and select the "Constant Field Values" link in
 * the detailed field description.
 *
 * @see quickfix.FileLogFactory
 * @see quickfix.ScreenLogFactory
 * @see quickfix.FileStoreFactory
 * @see quickfix.JdbcSetting
 * @see quickfix.Session
 * @see quickfix.DefaultSessionFactory
 */
public class SessionSettings {
    private static final Logger log = LoggerFactory.getLogger(SessionSettings.class);

    public static final String BEGINSTRING = "BeginString";
    public static final String SENDERCOMPID = "SenderCompID";
    public static final String SENDERSUBID = "SenderSubID";
    public static final String SENDERLOCID = "SenderLocationID";
    public static final String TARGETCOMPID = "TargetCompID";
    public static final String TARGETSUBID = "TargetSubID";
    public static final String TARGETLOCID = "TargetLocationID";
    public static final String SESSION_QUALIFIER = "SessionQualifier";
    public static final String SETTING_RECONNECT_INTERVAL = "ReconnectInterval";


    // This was using the line.separator system property but that caused
    // problems with moving configuration files between *nix and Windows.
    private static final String NEWLINE = "\r\n";

    private Properties variableValues;
    private Properties settings;

    /**
     * c-tor
     */
    public SessionSettings() {
        this(new Properties());
    }


    /**
     * c-tor
     *
     * @param settings
     */
    public SessionSettings(final Properties settings) {
        this.variableValues = System.getProperties();
        this.settings = settings;
    }

    /**
     *
     * @return
     */
    public Properties getProperties() {
        return this.settings;
    }

    /**
     * Get a settings string.
     *
     * @param key
     *            the settings key
     * @return the string value for the setting
     *
     * @throws ConfigError
     *             configurion error, probably a missing setting.
     * @throws FieldConvertError
     *             error during field type conversion.
     */
    public String getString(String key) throws ConfigError, FieldConvertError {
        final String value = interpolate(this.settings.getProperty(key));
        if (value == null) {
            throw new ConfigError(key + " not defined");
        }

        return value;
    }

    /**
     * Get a settings value as a long integer.
     *
     * @param key the settings key
     * @return the long integer value for the setting
     *
     * @throws ConfigError
     *             configurion error, probably a missing setting.
     * @throws FieldConvertError
     *             error during field type conversion.
     */
    public long getLong(String key) throws ConfigError, FieldConvertError {
        try {
            return Long.parseLong(getString(key));
        } catch (final NumberFormatException e) {
            throw new FieldConvertError(e.getMessage());
        }
    }

    /**
     * Get a settings value as a long integer.
     *
     * @param key
     *            the settings key
     * @return the long integer value for the setting
     *
     * @throws ConfigError
     *             configurion error, probably a missing setting.
     * @throws FieldConvertError
     *             error during field type conversion.
     */
    public int getInt(String key) throws ConfigError, FieldConvertError {
        try {
            return Integer.parseInt(getString(key));
        } catch (final NumberFormatException e) {
            throw new FieldConvertError(e.getMessage());
        }
    }

    /**
     * Get a settings value as a double number.
     *
     * @param key
     *            the settings key
     * @return the double number value for the setting
     *
     * @throws ConfigError
     *             configurion error, probably a missing setting.
     * @throws FieldConvertError
     *             error during field type conversion.
     */
    public double getDouble(String key) throws ConfigError, FieldConvertError {
        try {
            return Double.parseDouble(getString(key));
        } catch (final NumberFormatException e) {
            throw new FieldConvertError(e.getMessage());
        }
    }

    /**
     * Get a settings value as a boolean value.
     *
     * @param key
     *            the settings key
     * @return the boolean value for the setting
     *
     * @throws ConfigError
     *             configurion error, probably a missing setting.
     * @throws FieldConvertError
     *             error during field type conversion.
     */
    public boolean getBool(String key) throws ConfigError, FieldConvertError {
        try {
            return BooleanConverter.convert(getString(key));
        } catch (final FieldConvertError e) {
            throw new ConfigError(e);
        }
    }

    /**
     * Sets a string-valued session setting.
     *
     * @param key
     *            the setting key
     * @param value
     *            the string value
     */
    public void setString(String key, String value) {
        this.settings.setProperty(key, value.trim());
    }

    /**
     * Sets a long integer-valued session setting.
     *
     * @param key
     *            the setting key
     * @param value
     *            the long integer value
     */
    public void setLong(String key, long value) {
        this.settings.setProperty(key, Long.toString(value));
    }

    /**
     * Sets a double-valued session setting.
     *
     * @param key
     *            the setting key
     * @param value
     *            the double value
     */
    public void setDouble(String key, double value) {
        this.settings.setProperty(key, Double.toString(value));
    }

    /**
     * Sets a boolean-valued session setting.
     *
     * @param key
     *            the setting key
     * @param value
     *            the boolean value
     */
    public void setBool(String key, boolean value) {
        this.settings.setProperty(key, BooleanConverter.convert(value));

    }

    /**
     * Predicate for determining if a setting exists.
     *
     * @param key
     *            the setting key
     * @return true is setting exists, false otherwise.
     */
    public boolean isSetting(String key) {
        return this.settings.getProperty(key) != null;
    }

    public void removeSetting(SessionID sessionID, String key) {
        this.settings.remove(key);
    }

    private final Pattern variablePattern = Pattern.compile("\\$\\{(.+?)}");

    private String interpolate(String value) {
        if (value == null || value.indexOf('$') == -1) {
            return value;
        }
        final StringBuffer buffer = new StringBuffer();
        final Matcher m = variablePattern.matcher(value);
        while (m.find()) {
            if (m.start() > 0 && value.charAt(m.start() - 1) == '\\') {
                continue;
            }
            final String variable = m.group(1);
            final String variableValue = variableValues.getProperty(variable);
            if (variableValue != null) {
                m.appendReplacement(buffer, variableValue);
            }
        }
        m.appendTail(buffer);
        return buffer.toString();
    }

    /**
     * Set properties that will be the source of variable values in the settings. A variable
     * is of the form ${variable} and will be replaced with values from the
     * map when the setting is retrieved.
     *
     * By default, the System properties are used for variable values. If
     * you use this method, it will override the defaults so use the Properties
     * default value mechanism if you want to chain a custom properties object
     * with System properties as the default.
     *
     * <code><pre>
     * // Custom properties with System properties as default
     * Properties myprops = new Properties(System.getProperties());
     * myprops.load(getPropertiesInputStream());
     * settings.setVariableValues(myprops);
     *
     * // Custom properties with System properties as override
     * Properties myprops = new Properties();
     * myprops.load(getPropertiesInputStream());
     * myprops.putAll(System.getProperties());
     * settings.setVariableValues(myprops);
     * </pre></code>
     *
     * @param variableValues
     *
     * @see java.util.Properties
     * @see java.lang.System
     */
    public void setVariableValues(Properties variableValues) {
        this.variableValues = variableValues;
    }

    /**
     * Adds defaults to the settings. Will not delete existing settings not
     * overlapping with the new defaults, but will overwrite existing settings
     * specified in this call.
     *
     * @param defaults
     */
    public void set(Map<Object, Object> defaults) {
        this.settings.putAll(defaults);
    }

    public void toString(PrintWriter writer) {
        try {
            writeSection("[SESSION]", writer, this.settings);
        } finally {
            writer.flush();
        }
    }

    public void toStream(OutputStream out) {
        toString(new PrintWriter(new OutputStreamWriter(out)));
    }

    private void writeSection(String sectionName, PrintWriter writer, Properties properties) {
        writer.println(sectionName);
        final Iterator<Object> p = properties.keySet().iterator();
        while (p.hasNext()) {
            final String key = (String) p.next();
            writer.print(key);
            writer.print("=");
            writer.println(properties.getProperty(key));
        }
    }

    @Override
    public String toString() {
        final StringWriter writer = new StringWriter();
        toString(new PrintWriter(writer));
        return writer.toString();
    }

    public static int[] parseSettingReconnectInterval(String raw) {
        if (raw == null || raw.length() == 0) {
            return null;
        }
        final String multiplierCharacter = raw.contains("*") ? "\\*" : "x";
        final String[] data = raw.split(";");
        final List<Integer> result = new ArrayList<Integer>();
        for (final String multi : data) {
            final String[] timesSec = multi.split(multiplierCharacter);
            int times;
            int secs;
            try {
                if (timesSec.length > 1) {
                    times = Integer.parseInt(timesSec[0]);
                    secs = Integer.parseInt(timesSec[1]);
                } else {
                    times = 1;
                    secs = Integer.parseInt(timesSec[0]);
                }
            } catch (final NumberFormatException e) {
                throw new InvalidParameterException(
                    "Invalid number '"
                        + multi
                        + "' in '"
                        + raw
                        + "'. Expected format: [<multiplier>x]<seconds>;[<multiplier>x]<seconds>;...");
            }
            for (int ii = 0; ii != times; ++ii) {
                result.add(secs);
            }
        }
        final int[] ret = new int[result.size()];
        int ii = 0;
        for (final Integer sec : result) {
            ret[ii++] = sec;
        }
        return ret;
    }

    public static Set<InetAddress> parseRemoteAddresses(String raw) {
        if (raw == null || raw.length() == 0) {
            return null;
        }
        final String[] data = raw.split(",");
        final Set<InetAddress> result = new HashSet<InetAddress>();
        for (final String multi : data) {
            try {
                result.add(InetAddress.getByName(multi));
            } catch (final UnknownHostException e) {
                log.error("Ignored unknown host : " + multi, e);
            }
        }
        return result;
    }

}