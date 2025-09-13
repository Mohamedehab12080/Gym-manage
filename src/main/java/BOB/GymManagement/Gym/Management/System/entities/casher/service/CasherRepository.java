package BOB.GymManagement.Gym.Management.System.entities.casher.service;


import BOB.GymManagement.Gym.Management.System.entities.casher.CasherModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CasherRepository extends JpaRepository<CasherModel,Integer> {
    Optional<CasherModel> findByUserName(String userName);
    Optional<CasherModel> findByPhone(String phone);
    List<CasherModel> findByType(CasherModel.Type type);
}
