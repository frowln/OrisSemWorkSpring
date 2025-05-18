package kpfu.itis.kasimov.repositories;

import kpfu.itis.kasimov.models.UserProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserProgressRepository extends JpaRepository<UserProgress, Long> {
    Optional<UserProgress> findByUser_IdAndCourse_Id(Integer userId, Integer courseId);
    List<UserProgress> findByUser_Id(Integer userId);
}

