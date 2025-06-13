package kpfu.itis.kasimov.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import kpfu.itis.kasimov.models.Review;
import kpfu.itis.kasimov.repositories.ReviewRepository;
import kpfu.itis.kasimov.dto.ReviewDTO;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ReviewService {
    private final ReviewRepository reviewRepository;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public ReviewDTO findById(Integer id) {
        return ReviewDTO.valueOf(reviewRepository.findById(id).orElseThrow());
    }

    public List<Review> findByCourseId(Integer courseId) {
        return reviewRepository.findByCourse_Id(courseId);
    }

    public void save(Review review) {
        reviewRepository.save(review);
    }

    public List<Review> findAll() {
        return reviewRepository.findAll();
    }

    public Optional<Review> findReviewById(Integer id) {
        return reviewRepository.findById(id);
    }

    public void delete(Integer id) {
        reviewRepository.deleteById(id);
    }

}
