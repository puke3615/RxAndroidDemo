package pk.rxandroiddemo;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import pk.base.debug.DebugActivity;
import pk.base.debug.Helper;
import pk.rxandroiddemo.net.LoginInfo;
import pk.rxandroiddemo.net.User;
import pk.rxandroiddemo.net.UserApi;
import pk.rxandroiddemo.student.Course;
import pk.rxandroiddemo.student.Student;
import pk.rxandroiddemo.transformer.Client;
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
    public void ThreadSwitch() {
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
                            subscriber.onNext(String.format("value %d", i));
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
    public void Change() {
        final Integer number = 1234;
        Observable.just(number)
                //map变换：Func1中两个泛型类型  T:入参(变换前), R:出参(变换后)
                //call中完成整个变换过程
                .map(new Func1<Integer, String>() {
                    @Override
                    public String call(Integer num) {
                        return String.format("【%d】", num);
                    }
                })
                        //订阅事件，用于对变换结果的回调，内部实际调用了 subscribe(Subscriber)
                        // 将Action1的call方法注入到Subscribe的onNext方法中去
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String str) {
                        T("变换之前： %d\n变换之后： %s", number, str);
                    }
                })
        ;
    }

    //输出学生的名称
    @Debug
    public void print_studentName() {
        Student student = new Student("胡巴");
        Observable.just(student)
                .map(new Func1<Student, String>() {

                         @Override
                         public String call(Student student) {
                             return student == null ? null : student.name;
                         }
                     }
                )
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        T("Error: ", e.getMessage());
                    }

                    @Override
                    public void onNext(String s) {
                        T("Next: %s", s);
                    }
                });
    }

    //输出学生所选课程(正常输出)
    @Debug
    public void print_student_course_normal() {
        String[] courseNames = {"lol", "dota", "tx3", "cross fire"};
        final List<Course> courses = new ArrayList<>();
        //构造课程对象
        Observable.from(courseNames)
                .map(new Func1<String, Course>() {

                    @Override
                    public Course call(String s) {
                        return new Course(s);
                    }
                })
                .subscribe(new Action1<Course>() {
                    @Override
                    public void call(Course course) {
                        courses.add(course);
                    }
                });
        Student huba = new Student("胡巴", courses);
        Observable.just(huba)
                .subscribe(new Action1<Student>() {
                    @Override
                    public void call(Student student) {
                        List<Course> courses = student == null ? null : student.courses;
                        if (courses != null && courses.size() != 0) {
                            StringBuilder builder = new StringBuilder("Next: ");
                            for (Course course : courses) {
                                String courseName = course == null ? null : course.name;
                                builder.append("\n")
                                        .append(courseName);
                            }
                            T(builder);
                        }
                    }
                });
    }

    //输出学生所选课程(flatMap输出)
    @Debug
    public void print_student_course_flatMap() {
        final List<Course> courses = new ArrayList<>();
        //构造课程对象
        Observable.from(new String[]{"lol", "dota", "tx3", "cross fire"})
                .map(new Func1<String, Course>() {

                    @Override
                    public Course call(String s) {
                        return new Course(s);
                    }
                })
                .subscribe(new Action1<Course>() {
                    @Override
                    public void call(Course course) {
                        courses.add(course);
                    }
                });
        Student huba = new Student("胡巴", courses);

        Observable.just(huba)
                .flatMap(new Func1<Student, Observable<Course>>() {

                    @Override
                    public Observable<Course> call(Student student) {
                        return Observable.from(student.courses);
                    }
                })
                .subscribe(new Action1<Course>() {
                    @Override
                    public void call(Course course) {
                        L(course);
                        T(course);
                    }
                });
    }

    //模拟网络请求
    @Debug
    public void Net() {
        final UserApi userApi = new UserApi();
        Helper.getData(mContext, new Helper.IR() {
            @Override
            public void onInput(final String[] r) {
                Observable.create(
                        new Observable.OnSubscribe<User>() {
                            @Override
                            public void call(final Subscriber<? super User> subscriber) {
                                LoginInfo loginInfo = new LoginInfo(r[0], r[1]);
                                User user = userApi.login(loginInfo);
                                subscriber.onNext(user);
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<User>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                T("Error： %s", e.getMessage());
                            }

                            @Override
                            public void onNext(User user) {
                                T("Next: %s", user);
                            }
                        });

            }
        }, "username", "password");
    }

    //传统方式
    @Debug
    public void net_login() {
        Client client = new Client();
        client.login();
    }


    //改造方式
    @Debug
    public void net_loginInner() {
        Client client = new Client();
        client.loginInner();
    }


    //兼容方式
    @Debug
    public void net_loginOuter() {
        Client client = new Client();
        client.loginOuter();
    }



    private class WorkThread extends HandlerThread {

        public WorkThread() {
            super("WorkThread");
        }
    }


}
