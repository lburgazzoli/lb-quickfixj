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

import com.github.lburgazzoli.quickfixj.core.IFIXContext;

/**
 * Creates a message store that stores messages in a file. Compatibility note: The file formats are not compatible with
 * QF C++/JNI. If you upgrading from the QuickFIX JNI, you must delete your old session state files.)
 */
public class CachedFileStoreFactory extends FileStoreFactory {

    /**
     * Create the factory with configuration in session settings.
     * 
     * @param settings
     */
    public CachedFileStoreFactory(IFIXContext context,SessionSettings settings) {
        super(context,settings);
    }

    /**
     * Creates a file-based message store.
     * 
     * @param sessionID
     *            session ID for the message store.
     */
    public MessageStore create(SessionID sessionID) {
        try {
            boolean syncWrites = false;
            if (settings.isSetting(SETTING_FILE_STORE_SYNC)) {
                syncWrites = settings.getBool(SETTING_FILE_STORE_SYNC);
            }
            return new CachedFileStore(context,settings.getString(SETTING_FILE_STORE_PATH), sessionID, syncWrites);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
