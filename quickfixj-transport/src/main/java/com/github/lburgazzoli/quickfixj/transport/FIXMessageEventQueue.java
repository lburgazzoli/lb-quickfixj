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

import com.github.lburgazzoli.quickfixj.transport.util.AbstractEventQueue;

/**
 *
 */
public class FIXMessageEventQueue extends AbstractEventQueue<FIXMessageEvent> {
    @Override
    public boolean process(FIXMessageEvent data) {
        data.processMessage();
        return true;
    }
}
