package pk.rxandroiddemo.student;

/**
 * @author zijiao
 * @version 2016/2/3
 * @Mark 学生课程
 */
public class Course {

    public String name;

    public Course(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Course{" +
                "name='" + name + '\'' +
                '}';
    }
}
