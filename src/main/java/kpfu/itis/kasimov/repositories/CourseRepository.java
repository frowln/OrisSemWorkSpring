package kpfu.itis.kasimov.repositories;

import kpfu.itis.kasimov.models.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {
    List<Course> findByTeacherId(Integer teacherId);
    List<Course> findByNameContaining(String query);
}

