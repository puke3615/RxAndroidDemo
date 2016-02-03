package pk.rxandroiddemo.net;

/**
 * @author zijiao
 * @version 2016/2/3
 * @Mark
 */
public class User {

    public String username;
    public String password;
    public int age;
    public char sex;

    public User(String username, String password, int age, char sex) {
        this.username = username;
        this.password = password;
        this.age = age;
        this.sex = sex;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", age=" + age +
                ", sex=" + sex +
                '}';
    }
}
