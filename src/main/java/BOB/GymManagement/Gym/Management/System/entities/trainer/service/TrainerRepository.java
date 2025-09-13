package BOB.GymManagement.Gym.Management.System.entities.trainer.service;

import BOB.GymManagement.Gym.Management.System.entities.trainer.TrainerModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainerRepository extends JpaRepository<TrainerModel,Integer> {

    Optional<TrainerModel> findByEmail(String email);
    Optional<TrainerModel> findByPhone(String phone);
    List<TrainerModel> findByActive(boolean active);
}
