/*
 * Copyright 2014 lb
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
package com.github.lburgazzoli.quickfixj.transport;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FIXCodecHelper {

    public static final String MESSAGE_REX =
        "(8=FIX[T]?\\.[0-9]\\.[0-9])\\x01(9=[0-9]+)\\x01([0-9].*=*).*\\x01(10=[0-9]+)\\x01";

    public static final int    MSG_MIN_BYTES     = 30;
    public static final int    MSG_CSUM_LEN      = 7;
    public static final byte   BYTE_EQUALS       = '=';
    public static final char   CHAR_EQUALS       = '=';
    public static final byte   BYTE_SOH          = 0x01;
    public static final char   CHAR_SOH          = (char)0x01;
    public static final char   CHAR_PIPE         = '|';
    public static final byte   BYTE_BEGIN_STRING = '8';
    public static final byte   BYTE_BODY_LENGTH  = '9';


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
