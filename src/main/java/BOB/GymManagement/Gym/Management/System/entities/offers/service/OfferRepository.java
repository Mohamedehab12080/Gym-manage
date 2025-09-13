package BOB.GymManagement.Gym.Management.System.entities.offers.service;

import BOB.GymManagement.Gym.Management.System.entities.offers.OfferModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OfferRepository extends JpaRepository<OfferModel,Integer> {
    List<OfferModel> findByStatus(OfferModel.Status status);

    List<OfferModel> findByTitleContainingIgnoreCase(String title);

    List<OfferModel> findByTitleContaining(String title);
}
