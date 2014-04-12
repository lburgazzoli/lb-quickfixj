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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 */
public class FileSystemSessionSettingsBuilder implements SessionSettingsBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileSystemSessionSettingsBuilder.class);

    private final File file;

    /**
     *
     * @param file
     */
    public FileSystemSessionSettingsBuilder(File file) {
        this.file = file;
    }

    /**
     *
     * @param path
     */
    public FileSystemSessionSettingsBuilder(String path) {
        this.file = new File(path);
    }

    /**
     *
     * @param path
     * @param name
     */
    public FileSystemSessionSettingsBuilder(String path, String name) {
        this.file = new File(path,name);
    }

    /**
     *
     * @param path
     * @param name
     */
    public FileSystemSessionSettingsBuilder(File path, String name) {
        this.file = new File(path,name);
    }

    /**
     *
     * @return
     */
    @Override
    public SessionSettings build() {
        Properties props = new Properties();
        try(InputStream in = new FileInputStream(this.file)) {
            props.load(in);
        } catch (IOException e) {
            LOGGER.warn("IOException",e);
        }

        return new SessionSettings(props);
    }
}
