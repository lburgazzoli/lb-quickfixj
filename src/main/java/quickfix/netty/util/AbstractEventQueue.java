package quickfix.netty.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.netty.FIXMessageEvent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 */
public abstract class AbstractEventQueue<T> implements IEventQueue<T>, Runnable{
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractEventQueue.class);

    private final BlockingQueue<T> m_queue;
    private final AtomicBoolean m_running;
    private Thread m_thread;

    /**
     * c-tor
     */
    public AbstractEventQueue() {
        m_queue = new LinkedBlockingQueue<T>();
        m_thread = null;
        m_running = new AtomicBoolean(false);
    }

    /**
     *
     */
    @Override
    public void start() {
        if(!m_running.get()) {
            m_running.set(true);

            m_thread = new Thread(this);
            m_thread.start();
        }
    }

    /**
     *
     */
    @Override
    public void stop() {
        if(m_running.get()) {
            m_running.set(false);
            m_thread = null;
        }
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isRunning() {
        return m_running.get();
    }

    /**
     *
     * @param data
     */
    @Override
    public void put(T data) {
        try {
            m_queue.put(data);
        } catch (InterruptedException e) {
            LOGGER.warn("Exception",e);
        }
    }


    /**
     *
     */
    @Override
    public void run() {
        while(m_running.get()) {
            try {
                T data = m_queue.poll(1000L, TimeUnit.MILLISECONDS);
                if (data != null && m_running.get()) {
                    if(!process(data)) {
                        m_running.set(false);
                        return;
                    }
                }
            } catch(InterruptedException e) {
            }
        }
    }
}
