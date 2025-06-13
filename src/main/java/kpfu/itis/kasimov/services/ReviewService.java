package kpfu.itis.kasimov.services;

import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    @Transactional(readOnly = true)
    public ReviewDTO findById(Integer id) {
        return ReviewDTO.valueOf(reviewRepository.findById(id).orElseThrow());
    }

    @Transactional(readOnly = true)
    public List<Review> findByCourseId(Integer courseId) {
        return reviewRepository.findByCourse_Id(courseId);
    }

    public void save(Review review) {
        reviewRepository.save(review);
    }

    @Transactional(readOnly = true)
    public List<Review> findAll() {
        return reviewRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Review> findReviewById(Integer id) {
        return reviewRepository.findById(id);
    }

    public void delete(Integer id) {
        reviewRepository.deleteById(id);
    }

}
