package kpfu.itis.kasimov.repositories;

import kpfu.itis.kasimov.models.User;
import kpfu.itis.kasimov.models.UserCourse;
import kpfu.itis.kasimov.models.UserCourseKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserCourseRepository extends JpaRepository<UserCourse, UserCourseKey> {
    List<UserCourse> findByUser_Id(Integer userId);
    List<UserCourse> findByCourse_Id(Integer courseId);
    void deleteByUser_IdAndCourse_Id(Integer userId, Integer courseId);
    Optional<UserCourse> findByUser_IdAndCourse_Id(Integer userId, Integer courseId);
    int countByCourse_Id(Integer courseId);
    List<UserCourse> findByCourseId(Integer courseId);
    Optional<UserCourse> findByUserIdAndCourseId(Integer userId, Integer courseId);
}

