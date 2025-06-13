package kpfu.itis.kasimov.services;

import kpfu.itis.kasimov.models.Course;
import kpfu.itis.kasimov.models.User;
import kpfu.itis.kasimov.repositories.CourseRepository;
import kpfu.itis.kasimov.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import kpfu.itis.kasimov.models.UserCourse;
import kpfu.itis.kasimov.repositories.UserCourseRepository;
import kpfu.itis.kasimov.services.UserCourseService;
import kpfu.itis.kasimov.dto.UserCourseDTO;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class UserCourseService {

    private final UserCourseRepository userCourseRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    public void enroll(Integer userId, Integer courseId) {
        User user = userRepository.findById(userId).orElseThrow();
        Course course = courseRepository.findById(courseId).orElseThrow();
        UserCourse userCourse = new UserCourse();
        userCourse.setUser(user);
        userCourse.setCourse(course);
        userCourseRepository.save(userCourse);
    }

    public void leave(Integer userId, Integer courseId) {
        userCourseRepository.deleteByUser_IdAndCourse_Id(userId, courseId);
    }

    public boolean isEnrolled(Integer userId, Integer courseId) {
        return userCourseRepository.findByUser_IdAndCourse_Id(userId, courseId).isPresent();
    }

    public void removeStudent(Integer studentId, Integer courseId) {
        userCourseRepository.deleteByUser_IdAndCourse_Id(studentId, courseId);
    }

    @Transactional(readOnly = true)
    public List<Course> getEnrolledCourses(Integer userId) {
        return userCourseRepository.findByUser_Id(userId).stream()
                .map(uc -> uc.getCourse()).toList();
    }

    public int countEnrolled(Integer courseId) {
        return userCourseRepository.countByCourse_Id(courseId);
    }

    @Transactional(readOnly = true)
    public List<User> findStudentsByCourseId(Integer courseId) {
        return userCourseRepository.findByCourseId(courseId)
                .stream()
                .map(UserCourse::getUser)
                .collect(Collectors.toList());
    }

    public void removeStudentFromCourse(Integer courseId, Integer studentId) {
        Optional<UserCourse> userCourseOpt = userCourseRepository.findByUserIdAndCourseId(studentId, courseId);
        userCourseOpt.ifPresent(userCourseRepository::delete);
    }


}
