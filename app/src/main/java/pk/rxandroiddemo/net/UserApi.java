package pk.rxandroiddemo.net;

/**
 * @author zijiao
 * @version 2016/2/3
 * @Mark
 */
public class UserApi {

    /**
     * 模拟网络请求
     * @param loginInfo
     * @return
     */
    public User login(LoginInfo loginInfo) {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String username = loginInfo.username;
        String password = loginInfo.password;
        if ("wzj".equals(username) && "123".equals(password)) {
            return new User(username, null, 12, '男');
        }
        return null;
    }

}
