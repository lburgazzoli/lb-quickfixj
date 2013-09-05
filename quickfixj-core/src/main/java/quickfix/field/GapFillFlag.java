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

import quickfix.BooleanField;


public class GapFillFlag extends BooleanField {
    static final long serialVersionUID = 20050617;
    public static final int FIELD = 123;
    public static final boolean SEQUENCE_RESET_IGNORE_MSG_SEQ_NUM = false;
    public static final boolean GAP_FILL_MESSAGE_MSG_SEQ_NUM_FIELD_VALID = true;

    public GapFillFlag() {
        super(123);
    }

    public GapFillFlag(boolean data) {
        super(123, data);
    }
}