package kpfu.itis.kasimov.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import kpfu.itis.kasimov.models.Course;
import kpfu.itis.kasimov.repositories.CourseRepository;
import kpfu.itis.kasimov.dto.CourseDTO;

import java.util.List;

@Service
@Transactional
public class CourseService {
    private final CourseRepository courseRepository;

    @Autowired
    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public Course findById(Integer id) {
        return courseRepository.findById(id).orElse(null);
    }

    public List<CourseDTO> findByTeacherId(Integer teacherId) {
        return courseRepository.findByTeacherId(teacherId).stream().map(CourseDTO::valueOf).toList();
    }

    public void save(Course course) {
        courseRepository.save(course);
    }

    public void deleteById(Integer id) {
        courseRepository.deleteById(id);
    }

    public List<CourseDTO> searchByName(String query) {
        return courseRepository.findByNameContaining(query).stream().map(CourseDTO::valueOf).toList();
    }

    public List<Course> findAll() {
        return courseRepository.findAll();
    }


}
