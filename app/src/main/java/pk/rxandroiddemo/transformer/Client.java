package pk.rxandroiddemo.transformer;


import pk.base.util.ToastUtil;
import pk.rxandroiddemo.net.LoginInfo;
import pk.rxandroiddemo.net.User;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author zijiao
 * @version 2016/2/3
 * @Mark 测试传统的回调方式向RxJava的转变
 */
public class Client {

    private HttpClient httpClient = new HttpClient();
    private LoginInfo loginInfo = new LoginInfo("wzj", "123");

    /**
     * 传统的回调方式
     */
    public void login() {
        httpClient.execute(loginInfo, new OnResult<User>() {
            @Override
            public void onResult(User result) {
                T(result);
            }

            @Override
            public void onError(Throwable throwable) {
                T(throwable.getMessage());
            }
        });

    }

    /**
     * 在Api层的内部进行改造，返回Observable<User>对象
     */
    public void loginInner() {
        httpClient.execute(loginInfo)
                .subscribe(new Observer<User>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        T(e.getMessage());
                    }

                    @Override
                    public void onNext(User user) {
                        T(user);
                    }
                });
    }

    /**
     * 在外部改造，用兼容替换更替，减少使用成本
     */
    public void loginOuter() {
        getUser(loginInfo)
                .subscribe(new Observer<User>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        T(e.getMessage());
                    }

                    @Override
                    public void onNext(User user) {
                        T(user);
                    }
                });

    }

    /**
     * 外层兼容
     *
     * @param loginInfo
     * @return
     */
    private Observable<User> getUser(final LoginInfo loginInfo) {
        return Observable.create(
                new Observable.OnSubscribe<User>() {
                    @Override
                    public void call(final Subscriber<? super User> subscriber) {
                        httpClient.execute(loginInfo,
                                new OnResult<User>() {
                                    @Override
                                    public void onResult(User result) {
                                        try {
                                            subscriber.onNext(result);
                                            subscriber.onCompleted();
                                        } catch (Exception e) {
                                            subscriber.onError(e);
                                        }
                                    }

                                    @Override
                                    public void onError(Throwable throwable) {
                                        subscriber.onError(throwable);
                                    }
                                });
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static void T(Object s) {
        ToastUtil.show(s);
    }

    public static void T(String format, Object... values) {
        T(String.format(format, values));
    }

}
