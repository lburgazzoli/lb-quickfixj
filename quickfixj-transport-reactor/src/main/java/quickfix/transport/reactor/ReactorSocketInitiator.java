package quickfix.transport.reactor;

import quickfix.transport.AbstractTransport;
import quickfix.transport.FIXSessionHelper;

/**
 *
 */
public class ReactorSocketInitiator extends AbstractTransport {
    /**
     * c-tor
     *
     * @param session
     */
    public ReactorSocketInitiator(FIXSessionHelper session) {
        super(session);
    }

    @Override
    public void run() {
    }
}
