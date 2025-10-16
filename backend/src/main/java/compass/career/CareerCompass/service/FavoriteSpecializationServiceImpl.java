package compass.career.CareerCompass.service;

import compass.career.CareerCompass.dto.FavoriteSpecializationRequest;
import compass.career.CareerCompass.dto.FavoriteSpecializationResponse;
import compass.career.CareerCompass.mapper.SpecializationMapper;
import compass.career.CareerCompass.model.FavoriteSpecialization;
import compass.career.CareerCompass.model.SpecializationArea;
import compass.career.CareerCompass.model.User;
import compass.career.CareerCompass.repository.FavoriteSpecializationRepository;
import compass.career.CareerCompass.repository.SpecializationAreaRepository;
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
public class FavoriteSpecializationServiceImpl implements FavoriteSpecializationService {

    private final FavoriteSpecializationRepository favoriteSpecializationRepository;
    private final UserRepository userRepository;
    private final SpecializationAreaRepository specializationAreaRepository;

    @Override
    @Transactional
    public FavoriteSpecializationResponse addFavoriteSpecialization(Integer userId, FavoriteSpecializationRequest request) {
        // Validar máximo 5 favoritas
        if (favoriteSpecializationRepository.countByUserIdAndActiveTrue(userId) >= 5) {
            throw new IllegalArgumentException("Maximum 5 favorite specializations allowed");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        SpecializationArea specialization = specializationAreaRepository.findById(request.getSpecializationAreaId())
                .orElseThrow(() -> new EntityNotFoundException("Specialization area not found"));

        // Verificar si ya existe como favorita
        Optional<FavoriteSpecialization> existing = favoriteSpecializationRepository
                .findByUserIdAndSpecializationAreaId(userId, request.getSpecializationAreaId());

        if (existing.isPresent()) {
            FavoriteSpecialization favorite = existing.get();
            if (favorite.getActive()) {
                throw new IllegalArgumentException("Specialization already in favorites");
            }
            // Reactivar favorita
            favorite.setActive(true);
            favorite.setNotes(request.getNotes());
            FavoriteSpecialization saved = favoriteSpecializationRepository.save(favorite);
            return SpecializationMapper.toFavoriteResponse(saved);
        }

        FavoriteSpecialization favorite = SpecializationMapper.toFavoriteEntity(request, user, specialization);
        FavoriteSpecialization saved = favoriteSpecializationRepository.save(favorite);
        return SpecializationMapper.toFavoriteResponse(saved);
    }

    @Override
    @Transactional
    public void removeFavoriteSpecialization(Integer userId, Integer specializationId) {
        FavoriteSpecialization favorite = favoriteSpecializationRepository
                .findByUserIdAndSpecializationAreaId(userId, specializationId)
                .orElseThrow(() -> new EntityNotFoundException("Favorite specialization not found"));

        favorite.setActive(false);
        favoriteSpecializationRepository.save(favorite);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FavoriteSpecializationResponse> getFavoriteSpecializations(Integer userId, int page, int pageSize) {
        log.debug("Finding favorite specializations for user {} with pagination - page: {}, pageSize: {}",
                userId, page, pageSize);

        Pageable pageable = PageRequest.of(page, pageSize);

        List<FavoriteSpecializationResponse> favorites = favoriteSpecializationRepository.findByUserIdAndActiveTrue(userId, pageable).stream()
                .map(SpecializationMapper::toFavoriteResponse)
                .collect(Collectors.toList());

        if (favorites.isEmpty()) {
            throw new IllegalArgumentException("There are no favorite specializations registered for this user.");
        }

        return favorites;
    }
}
