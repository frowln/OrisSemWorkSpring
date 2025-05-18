package kpfu.itis.kasimov.repositories;

import kpfu.itis.kasimov.models.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {
    List<Review> findByCourse_Id(Integer courseId);
    List<Review> findByUser_Id(Integer userId);
}

