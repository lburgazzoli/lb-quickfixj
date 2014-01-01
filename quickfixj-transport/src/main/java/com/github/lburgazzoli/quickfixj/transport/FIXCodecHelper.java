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

package com.github.lburgazzoli.quickfixj.transport;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FIXCodecHelper {

    public static final String MESSAGE_REX =
        "(8=FIX[T]?\\.[0-9]\\.[0-9])\\x01(9=[0-9]+)\\x01([0-9].*=*).*\\x01(10=[0-9]+)\\x01";

    public static final int  MSG_MIN_BYTES    = 30;
    public static final int  MSG_CSUM_LEN      = 7;
    public static final char CHAR_EQUALS       = '=';
    public static final byte BYTE_SOH          = 0x01;
    public static final char CHAR_SOH          = BYTE_SOH;
    public static final byte BYTE_BEGIN_STRING = '8';
    public static final byte BYTE_BODY_LENGTH  = '9';


    // *************************************************************************
    //
    // *************************************************************************

    /**
     *
     * @param data
     * @return
     */
    public static Matcher getCodecMatcher(CharSequence data) {
        Pattern p = Pattern.compile(MESSAGE_REX);
        Matcher m = p.matcher(data);

        return m;
    }
}
