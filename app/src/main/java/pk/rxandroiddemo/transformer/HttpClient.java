package pk.rxandroiddemo.transformer;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import pk.rxandroiddemo.net.LoginInfo;
import pk.rxandroiddemo.net.User;
import pk.rxandroiddemo.net.UserApi;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author zijiao
 * @version 2016/2/3
 * @Mark
 */
public class HttpClient {

    private UserApi mUserApi;
    private Executor mExecutor;
    private static final Handler mHandler = new Handler(Looper.getMainLooper());

    public HttpClient() {
        this.mUserApi = new UserApi();
        this.mExecutor = Executors.newCachedThreadPool();
    }

    /**
     * 传统的回调方式代码结构
     *
     * @param loginInfo 登录信息
     * @param result    回调接口
     */
    public void execute(final LoginInfo loginInfo, final OnResult<User> result) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    //正常执行流程
                    final User user = mUserApi.login(loginInfo);
                    //通过handler post到主线程中去
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            result.onResult(user);
                        }
                    });
                } catch (final Exception e) {
                    //发生异常时
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            result.onError(e);
                        }
                    });
                }
            }
        });
    }

    /**
     * 能改造实现方式时，可以在该层修改返回值
     * 回调事件只需要将返回的Observable<User>进行设置订阅者即可
     *
     * @param loginInfo 登录信息
     * @return User的结果观察者
     */
    public Observable<User> execute(final LoginInfo loginInfo) {
        return Observable.create(
                new Observable.OnSubscribe<User>() {
                    @Override
                    public void call(Subscriber<? super User> subscriber) {
                        try {
                            User user = mUserApi.login(loginInfo);
                            subscriber.onNext(user);
                            subscriber.onCompleted();
                        } catch (Exception e) {
                            subscriber.onError(e);
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
