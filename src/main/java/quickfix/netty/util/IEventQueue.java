package quickfix.netty.util;

/**
 *
 */
public interface IEventQueue<T> {

    /**
     *
     */
    public void start();

    /**
     *
     */
    public void stop();

    /**
     *
     * @return
     */
    public boolean isRunning();

    /**
     *
     * @param data
     */
    public void put(T data);

    /**
     *
     * @param data
     * @return
     */
    public boolean process(T data);
}
