package pk.rxandroiddemo;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import pk.base.debug.DebugActivity;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.observers.Observers;
import rx.schedulers.Schedulers;

public class MainActivity extends DebugActivity {

    private static final String TAG = "RxAndroidDemo";

    @Debug
    public void create_observer() {
        Observer<String> observer = Observers.create(new Action1<String>() {
            @Override
            public void call(String str) {
                T(String.format("0. 0 --> %s", str));
            }
        });

        observer.onNext("阿萨斯的");
    }

    @Debug
    public void findName() {
        String[] names = {"123", "abc", "d3", "adasd", "5dsd", "a", "dg"};
        Observable.from(names)
                .flatMap(new Func1<String, Observable<Character>>() {
                    @Override
                    public Observable<Character> call(String s) {
                        char[] chars = s.toCharArray();
                        Character[] result = new Character[chars.length];
                        for (int i = 0; i < chars.length; i++) {
                            result[i] = chars[i];
                        }
                        return Observable.from(result);
                    }
                })
                .filter(new Func1<Character, Boolean>() {
                    @Override
                    public Boolean call(Character character) {
                        return character == 'd';
                    }
                })
                .map(new Func1<Character, String>() {
                    @Override
                    public String call(Character character) {
                        return String.valueOf(character);
                    }
                })
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.i(TAG, s);
                    }
                })
                .unsubscribe();
    }

    //线程调度
    @Debug
    public void testThreadSwitch() {
        WorkThread workThread = new WorkThread();
        workThread.start();
        Handler handler = new Handler(workThread.getLooper());
        Observable.create(
                //将订阅事件注入到Observable中(只执行注册操作，没有invoke)
                new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> subscriber) {
                        for (int i = 0; i < 3; i++) {
                            try {
                                Thread.sleep(2500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            subscriber.onNext(String.format("value-%d", i));
                        }
                    }
                })
                //指定subscribe发生在IO线程中(关键词：subscribe)
                //.subscribeOn(HandlerScheduler.from(handler))
                .subscribeOn(Schedulers.io())
                        //指定订阅事件发生在主线程中(关键词：subscribe的回调)
                .observeOn(AndroidSchedulers.mainThread())
                        //订阅事件
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        //execute in main thread
                        T("onCompleted --> %s", Thread.currentThread().getName());
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String s) {
                        T("receive value is %s", s);
                    }
                });
    }

    //变换
    @Debug
    public void testChange() {
        Observable.from(new String[]{"1", "2", "3", "4"})
                .map(new Func1<String, Integer>() {
                    @Override
                    public Integer call(String s) {
                        return Integer.valueOf(s);
                    }
                })
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        L(integer);
                    }
                })
        ;
    }

    private class WorkThread extends HandlerThread {

        public WorkThread() {
            super("WorkThread");
        }
    }


}
