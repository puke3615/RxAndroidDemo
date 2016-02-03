package pk.rxandroiddemo.net;

/**
 * @author zijiao
 * @version 2016/2/3
 * @Mark
 */
public class LoginInfo {

    public String username;
    public String password;

    public LoginInfo(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public String toString() {
        return "LoginInfo{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

}
