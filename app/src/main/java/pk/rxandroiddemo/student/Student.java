package pk.rxandroiddemo.student;

import java.util.List;

/**
 * @author zijiao
 * @version 2016/2/3
 * @Mark 学生
 */
public class Student {

    public String name;
    public List<Course> courses;

    public Student(String name) {
        this.name = name;
    }

    public Student(String name, List<Course> courses) {
        this.name = name;
        this.courses = courses;
    }

    @Override
    public String toString() {
        return "Student{" +
                "name='" + name + '\'' +
                ", courses=" + courses +
                '}';
    }
}
