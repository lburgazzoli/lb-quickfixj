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

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.logicalcobwebs.proxool.ProxoolDataSource;
import org.quickfixj.QFJException;
import org.slf4j.LoggerFactory;

class JdbcUtil {

    static final String CONNECTION_POOL_ALIAS = "quickfixj";

    private static Map<String, ProxoolDataSource> dataSources = new ConcurrentHashMap<String, ProxoolDataSource>();
    private static int dataSourceCounter = 1;
    
    static DataSource getDataSource(SessionSettings settings, SessionID sessionID)
            throws ConfigError, FieldConvertError {
        if (settings.isSetting(sessionID, JdbcSetting.SETTING_JDBC_DS_NAME)) {
            String jndiName = settings.getString(sessionID, JdbcSetting.SETTING_JDBC_DS_NAME);
            try {
                return (DataSource) new InitialContext().lookup(jndiName);
            } catch (NamingException e) {
                throw new ConfigError(e);
            }
        } else {
            String jdbcDriver = settings.getString(sessionID, JdbcSetting.SETTING_JDBC_DRIVER);
            String connectionURL = settings.getString(sessionID,
                    JdbcSetting.SETTING_JDBC_CONNECTION_URL);
            String user = settings.getString(sessionID, JdbcSetting.SETTING_JDBC_USER);
            String password = settings.getString(sessionID, JdbcSetting.SETTING_JDBC_PASSWORD);

            return getDataSource(jdbcDriver, connectionURL, user, password, true);
        }
    }

    /**
     * This is typically called from a single thread, but just in case we are synchronizing modification
     * of the cache. The cache itself is thread safe.
     */
    static synchronized DataSource getDataSource(String jdbcDriver, String connectionURL, String user, String password, boolean cache) {
        String key = jdbcDriver + "#" + connectionURL + "#" + user + "#" + password;
        ProxoolDataSource ds = cache ? dataSources.get(key) : null;
        
        if (ds == null) {
            ds = new ProxoolDataSource(JdbcUtil.CONNECTION_POOL_ALIAS + "-" + dataSourceCounter++);
            
            ds.setDriver(jdbcDriver);
            ds.setDriverUrl(connectionURL);

            // Bug in Proxool 0.9RC2. Must set both delegate properties and individual setters. :-(
            ds.setDelegateProperties("user=" + user + ","
                    + (password != null && !"".equals(password) ? "password=" + password : ""));
            ds.setUser(user);
            ds.setPassword(password);
            
            // TODO JDBC Make these configurable
            setMaximumActiveTime(ds, 5000);
            ds.setMaximumConnectionLifetime(28800000);
            ds.setMaximumConnectionCount(10);
            ds.setSimultaneousBuildThrottle(10);

            if (cache) {
                dataSources.put(key, ds);
            }
        }
        return ds;
    }

    private static void setMaximumActiveTime(ProxoolDataSource ds, long ms) {
        // This is a hack for Proxool support in Java 4. The Proxool library changed
        // the argument type for setMaximumActiveTime from int to long. The retrotranslated
        // library was still referencing the long setter and it wasn't defined in the
        // Java 4-compatible Proxool library. Therefore, we are using reflection to
        // workaround the problem until Java 4 support is dropped.
        // TODO Use normal setter when Java 4 support is dropped.
        String methodName = "setMaximumActiveTime";
        Method setter = null;
        try {
            setter = ds.getClass().getMethod(methodName, long.class);
        } catch (NoSuchMethodException e) {
            try {
                setter = ds.getClass().getMethod(methodName, int.class);
            } catch (NoSuchMethodException e1) {
                // ignore
            }
        }

        if (setter != null) {
            try {
                setter.invoke(ds, (int) ms);
            } catch (Exception e) {
                throw new QFJException(e);
            }
        } else {
            LoggerFactory.getLogger(LogUtil.class).warn(
                    "Couldn't set maximum active time on Proxool data source");
        }
    }

    static void close(SessionID sessionID, Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                LogUtil.logThrowable(sessionID, e.getMessage(), e);
            }
        }
    }

    static void close(SessionID sessionID, PreparedStatement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                LogUtil.logThrowable(sessionID, e.getMessage(), e);
            }
        }
    }

    static void close(SessionID sessionID, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                LogUtil.logThrowable(sessionID, e.getMessage(), e);
            }
        }
    }
    

    static boolean determineSessionIdSupport(DataSource dataSource, String tableName) throws SQLException {
        Connection connection = dataSource.getConnection();
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            String columnName = "sendersubid";
            return isColumn(metaData, tableName.toUpperCase(), columnName.toUpperCase())
                    || isColumn(metaData, tableName, columnName);
        } finally {
            connection.close();
        }
    }

    
    private static boolean isColumn(DatabaseMetaData metaData, String tableName, String columnName)
            throws SQLException {
        ResultSet columns = metaData.getColumns(null, null, tableName, columnName);
        try {
            return columns.next();
        } finally {
            columns.close();
        }
    }

    static String getIDWhereClause(boolean isExtendedSessionID) {
        return isExtendedSessionID
                ? ("beginstring=? and sendercompid=? and sendersubid=? and senderlocid=? and "
                        + "targetcompid=? and targetsubid=? and targetlocid=? and session_qualifier=? ")
                : "beginstring=? and sendercompid=? and targetcompid=? and session_qualifier=? ";

    }
    
    static String getIDColumns(boolean isExtendedSessionID) {
        return isExtendedSessionID
                ? "beginstring,sendercompid,sendersubid,senderlocid,targetcompid,targetsubid,targetlocid,session_qualifier"
                : "beginstring,sendercompid,targetcompid,session_qualifier";

    }

    static String getIDPlaceholders(boolean isExtendedSessionID) {
        return isExtendedSessionID ? "?,?,?,?,?,?,?,?" : "?,?,?,?";

    }

    static int setSessionIdParameters(SessionID sessionID, PreparedStatement query, int offset, boolean isExtendedSessionID, String defaultSqlValue) throws SQLException {
        if (isExtendedSessionID) {
            query.setString(offset++, getSqlValue(sessionID.getBeginString(), defaultSqlValue));
            query.setString(offset++, getSqlValue(sessionID.getSenderCompID(), defaultSqlValue));
            query.setString(offset++, getSqlValue(sessionID.getSenderSubID(), defaultSqlValue));
            query.setString(offset++, getSqlValue(sessionID.getSenderLocationID(), defaultSqlValue));
            query.setString(offset++, getSqlValue(sessionID.getTargetCompID(), defaultSqlValue));
            query.setString(offset++, getSqlValue(sessionID.getTargetSubID(), defaultSqlValue));
            query.setString(offset++, getSqlValue(sessionID.getTargetLocationID(), defaultSqlValue));
            query.setString(offset++, getSqlValue(sessionID.getSessionQualifier(), defaultSqlValue));
        } else {
            query.setString(offset++, getSqlValue(sessionID.getBeginString(), defaultSqlValue));
            query.setString(offset++, getSqlValue(sessionID.getSenderCompID(), defaultSqlValue));
            query.setString(offset++, getSqlValue(sessionID.getTargetCompID(), defaultSqlValue));
            query.setString(offset++, getSqlValue(sessionID.getSessionQualifier(), defaultSqlValue));
        }
        return offset;
    }

    private static String getSqlValue(String javaValue, String defaultSqlValue) {
        return !SessionID.NOT_SET.equals(javaValue) ? javaValue : defaultSqlValue;
    }

}