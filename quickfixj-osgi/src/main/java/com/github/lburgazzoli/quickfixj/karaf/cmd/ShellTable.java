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

package com.github.lburgazzoli.quickfixj.karaf.cmd;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;

/**
 *
 */
public class ShellTable {

    public static final int MAX_COL_SIZE = 56;

    private final List<String> m_header = Lists.newArrayList();
    private final List<List<String>> m_content = Lists.newArrayList();

    private int maxColSize = MAX_COL_SIZE;

    /**
     * c-tor
     */
    public ShellTable() {
    }

    /**
     * c-tor
     *
     * @param args
     */
    public ShellTable(String... args) {
        for(String s : args) {
            m_header.add(s);
        }
    }

    /**
     *
     * @param max
     */
    public void setMxColSize(int max) {
        maxColSize = max;
    }

    /**
     *
     * @param args
     */
    public void setHeader(String... args) {
        for(String s : args) {
            m_header.add(s);
        }
    }
    /**
     *
     * @param arg
     */
    public void addHeader(String arg) {
        m_header.add(arg);
    }

    /**
     *
     * @return
     */
    public List<String> addRow() {
        List<String> row = Lists.newArrayList();
        m_content.add(row);

        return row;
    }
   /**
     * @param args
     */
    public void addRow(Object... args) {
        List<String> row = addRow();
        for(Object arg : args) {
            row.add(ObjectUtils.toString(arg));
        }
    }

    /**
     *
     */
    public void print() {
        int[] sizes = new int[m_header.size()];
        updateSizes(sizes, m_header);
        for(List<String> row : m_content) {
            updateSizes(sizes,row);
        }

        String headerLine = getRow(sizes, m_header," | ");
        System.out.println(headerLine);
        System.out.println(underline(headerLine.length()));
        for(List<String> row : m_content) {
            System.out.println(getRow(sizes, row, " | "));
        }
    }

    /**
     *
     */
    public void clear() {
        m_content.clear();
    }
    /**
     *
     * @param length
     * @return
     */
    private String underline(int length) {
        char[] exmarks = new char[length];
        Arrays.fill(exmarks, '-');

        return new String(exmarks);
    }
    /**
     *
     * @param sizes
     * @param row
     * @param separator
     * @return
     */
    private String getRow(int[] sizes,List<String> row,String separator) {
        StringBuilder line = new StringBuilder();
        int c = 0;
        for(String cell : row) {
            if(cell == null) {
                cell = "";
            }

            if(cell.length() > maxColSize) {
                cell = cell.substring(0, maxColSize - 1);
            }

            cell = cell.replaceAll("\n","");
            line.append(String.format("%-" + sizes[c] + "s",cell));
            if(c + 1 < row.size()) {
                line.append(separator);
            }

            c++;
        }

        return line.toString();
    }

    /**
     *
     * @param sizes
     * @param row
     */
    private void updateSizes(int[] sizes,List<String> row) {
        int c = 0;
        for(String cellContent : row) {
            int cellSize = cellContent != null ? cellContent.length() : 0;
            cellSize = Math.min(cellSize, maxColSize);
            if(cellSize > sizes[c]) {
                sizes[c] = cellSize;
            }

            c++;
        }
    }

}
