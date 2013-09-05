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

import quickfix.StringField;


public class ApplVerID extends StringField {
    static final long serialVersionUID = 20050617;
    public static final int FIELD = 1128;
    public static final String FIX27 = "0";
    public static final String FIX30 = "1";
    public static final String FIX40 = "2";
    public static final String FIX41 = "3";
    public static final String FIX42 = "4";
    public static final String FIX43 = "5";
    public static final String FIX44 = "6";
    public static final String FIX50 = "7";
    public static final String FIX50SP1 = "8";
    public static final String FIX50SP2 = "9";

    public ApplVerID() {
        super(1128);
    }

    public ApplVerID(String data) {
        super(1128, data);
    }
}
