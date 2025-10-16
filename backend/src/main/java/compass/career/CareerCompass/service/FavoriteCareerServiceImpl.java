package compass.career.CareerCompass.service;

import compass.career.CareerCompass.dto.FavoriteCareerRequest;
import compass.career.CareerCompass.dto.FavoriteCareerResponse;
import compass.career.CareerCompass.mapper.CareerMapper;
import compass.career.CareerCompass.model.Career;
import compass.career.CareerCompass.model.FavoriteCareer;
import compass.career.CareerCompass.model.User;
import compass.career.CareerCompass.repository.CareerRepository;
import compass.career.CareerCompass.repository.FavoriteCareerRepository;
import compass.career.CareerCompass.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FavoriteCareerServiceImpl implements FavoriteCareerService {

    private final FavoriteCareerRepository favoriteCareerRepository;
    private final UserRepository userRepository;
    private final CareerRepository careerRepository;

    @Override
    @Transactional
    public FavoriteCareerResponse addFavoriteCareer(Integer userId, FavoriteCareerRequest request) {
        // Validar máximo 10 favoritas
        if (favoriteCareerRepository.countByUserIdAndActiveTrue(userId) >= 10) {
            throw new IllegalArgumentException("Maximum 10 favorite careers allowed");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Career career = careerRepository.findById(request.getCareerId())
                .orElseThrow(() -> new EntityNotFoundException("Career not found"));

        // Verificar si ya existe como favorita
        Optional<FavoriteCareer> existing = favoriteCareerRepository.findByUserIdAndCareerId(userId, request.getCareerId());

        if (existing.isPresent()) {
            FavoriteCareer favorite = existing.get();
            if (favorite.getActive()) {
                throw new IllegalArgumentException("Career already in favorites");
            }
            // Reactivar favorita
            favorite.setActive(true);
            favorite.setNotes(request.getNotes());
            FavoriteCareer saved = favoriteCareerRepository.save(favorite);
            return CareerMapper.toFavoriteResponse(saved);
        }

        FavoriteCareer favorite = CareerMapper.toFavoriteEntity(request, user, career);
        FavoriteCareer saved = favoriteCareerRepository.save(favorite);
        return CareerMapper.toFavoriteResponse(saved);
    }

    @Override
    @Transactional
    public void removeFavoriteCareer(Integer userId, Integer careerId) {
        FavoriteCareer favorite = favoriteCareerRepository.findByUserIdAndCareerId(userId, careerId)
                .orElseThrow(() -> new EntityNotFoundException("Favorite career not found"));

        favorite.setActive(false);
        favoriteCareerRepository.save(favorite);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FavoriteCareerResponse> getFavoriteCareers(Integer userId, int page, int pageSize) {
        log.debug("Finding favorite careers for user {} with pagination - page: {}, pageSize: {}",
                userId, page, pageSize);

        Pageable pageable = PageRequest.of(page, pageSize);

        List<FavoriteCareerResponse> favorites = favoriteCareerRepository.findByUserIdAndActiveTrue(userId, pageable).stream()
                .map(CareerMapper::toFavoriteResponse)
                .collect(Collectors.toList());

        if (favorites.isEmpty()) {
            throw new IllegalArgumentException("There are no favorite careers registered for this user.");
        }

        return favorites;
    }
}
