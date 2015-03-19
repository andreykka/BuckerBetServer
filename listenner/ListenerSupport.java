package listenner;

/**
 * Created by gandy on 19.03.15.
 */
public interface ListenerSupport<T> {

    /**
     * register new listener
     */
    public void addListener(T listener);

    /**
     * removed already added listener
     */
    public void removeListener(T listener);

}
