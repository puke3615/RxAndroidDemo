package pk.rxandroiddemo.transformer;

/**
 * @author zijiao
 * @version 2016/2/3
 * @Mark
 */
public interface OnResult<T> {

    void onResult(T result);

    void onError(Throwable throwable);

}
