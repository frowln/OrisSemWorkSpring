package kpfu.itis.kasimov.services;

import kpfu.itis.kasimov.models.Course;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class LessonService {

    private final LessonRepository lessonRepository;

    @Transactional(readOnly = true)
    public Optional<Lesson> findById(Integer id) {
        return lessonRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Lesson> findByCourseId(Integer courseId) {
        return lessonRepository.findByCourse_Id(courseId);
    }

    public void save(Lesson lesson) {
        lessonRepository.save(lesson);
    }

    public void deleteById(Integer id) {
        lessonRepository.deleteById(id);
    }
}
