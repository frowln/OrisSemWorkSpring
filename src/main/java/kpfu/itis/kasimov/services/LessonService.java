package kpfu.itis.kasimov.services;

import kpfu.itis.kasimov.models.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import kpfu.itis.kasimov.models.Lesson;
import kpfu.itis.kasimov.repositories.LessonRepository;
import kpfu.itis.kasimov.dto.LessonDTO;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LessonService {
    private final LessonRepository lessonRepository;

    @Autowired
    public LessonService(LessonRepository lessonRepository) {
        this.lessonRepository = lessonRepository;
    }

    public Optional<Lesson> findById(Integer id) {
        return lessonRepository.findById(id);
    }

    public List<Lesson> findByCourseId(Integer courseId) {
        return lessonRepository.findByCourse_Id(courseId);
    }

    public void save(Lesson lesson) {
        lessonRepository.save(lesson);
    }

    public void deleteById(Integer id) {
        lessonRepository.deleteById(id);
    }

    public Optional<Lesson> findLessonById(Integer id) {
        return lessonRepository.findById(id);
    }
}
