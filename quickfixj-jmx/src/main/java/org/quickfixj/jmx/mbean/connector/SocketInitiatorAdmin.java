
package org.quickfixj.jmx.mbean.connector;

import org.quickfixj.jmx.JmxExporter;
import org.quickfixj.jmx.mbean.JmxSupport;
import org.quickfixj.jmx.mbean.session.SessionJmxExporter;
import org.quickfixj.jmx.openmbean.TabularDataAdapter;
import quickfix.ext.IFIXContext;
import quickfix.transport.mina.initiator.AbstractSocketInitiator;
import quickfix.transport.mina.initiator.IoSessionInitiator;

import javax.management.ObjectName;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.TabularData;
import java.io.IOException;
import java.util.ArrayList;


class SocketInitiatorAdmin extends ConnectorAdmin implements SocketInitiatorAdminMBean {

    private final TabularDataAdapter tabularDataAdapter = new TabularDataAdapter();

    private final AbstractSocketInitiator initiator;

    protected SocketInitiatorAdmin(final IFIXContext context,JmxExporter jmxExporter, AbstractSocketInitiator connector,
            ObjectName connectorName, SessionJmxExporter sessionExporter) {
        super(context,jmxExporter, connector, connectorName, connector.getSettings(), sessionExporter);
        initiator = (AbstractSocketInitiator) connector;
    }

    public TabularData getEndpoints() throws IOException {
        try {
            return tabularDataAdapter.fromBeanList("Endpoints", "Endpoint", "sessionID",
                    new ArrayList<IoSessionInitiator>(initiator.getInitiators()));
        } catch (OpenDataException e) {
            throw JmxSupport.toIOException(e);
        }
    }

    public int getQueueSize() {
        return initiator.getQueueSize();
    }
}
