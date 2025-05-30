package kpfu.itis.kasimov.repositories;

import kpfu.itis.kasimov.models.UserLesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserLessonRepository extends JpaRepository<UserLesson, Integer> {
    List<UserLesson> findByUser_Id(Integer userId);
    List<UserLesson> findByLesson_Id(Integer lessonId);
    Optional<UserLesson> findByUser_IdAndLesson_Id(Integer userId, Integer lessonId);
    @Query("SELECT COUNT(u) FROM UserLesson u WHERE u.user.id = :userId AND u.lesson.course.id = :courseId AND u.completed = true")
    int countCompletedLessons(@Param("userId") Integer userId, @Param("courseId") Integer courseId);
}
