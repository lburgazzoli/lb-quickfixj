package com.github.lburgazzoli.quickfixj.osgi;

import com.github.lburgazzoli.quickfixj.core.IFIXContext;
import com.github.lburgazzoli.quickfixj.transport.FIXSessionHelper;

/**
 *
 */
public interface IFIXConnection {

    // *************************************************************************
    // lifecycle
    // *************************************************************************

    /**
     *
     */
    public void init();

    /**
     *
     */
    public void destroy();

    /**
     *
     */
    public void start();

    /**
     *
     */
    public void stop();

    // *************************************************************************
    //
    // *************************************************************************

    /**
     *
     * @return
     */
    public String getId();

    /**
     *
     * @return
     */
    public FIXSessionHelper getHelper();

    /**
     *
     * @return
     */
    public String getRemoteIpAddress();

}
