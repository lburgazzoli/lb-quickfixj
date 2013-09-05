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
package quickfix.field;

import quickfix.IntField;


public class SessionStatus extends IntField {
    static final long serialVersionUID = 20050617;
    public static final int FIELD = 1409;
    public static final int SESSION_ACTIVE = 0;
    public static final int SESSION_PASSWORD_CHANGED = 1;
    public static final int SESSION_PASSWORD_DUE_TO_EXPIRE = 2;
    public static final int NEW_SESSION_PASSWORD_DOES_NOT_COMPLY_WITH_POLICY = 3;
    public static final int SESSION_LOGOUT_COMPLETE = 4;
    public static final int INVALID_USERNAME_OR_PASSWORD = 5;
    public static final int ACCOUNT_LOCKED = 6;
    public static final int LOGONS_ARE_NOT_ALLOWED_AT_THIS_TIME = 7;
    public static final int PASSWORD_EXPIRED = 8;

    public SessionStatus() {
        super(1409);
    }

    public SessionStatus(int data) {
        super(1409, data);
    }
}