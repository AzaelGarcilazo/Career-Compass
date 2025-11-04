package compass.career.careercompass.service;

import compass.career.careercompass.dto.FavoriteCareerRequest;
import compass.career.careercompass.dto.FavoriteCareerResponse;

import java.util.List;

public interface FavoriteCareerService {
    FavoriteCareerResponse addFavoriteCareer(Integer userId, FavoriteCareerRequest request);
    void removeFavoriteCareer(Integer userId, Integer careerId);
    List<FavoriteCareerResponse> getFavoriteCareers(Integer userId, int page, int pageSize);
}
