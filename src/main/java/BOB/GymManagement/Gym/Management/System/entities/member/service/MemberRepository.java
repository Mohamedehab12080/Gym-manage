package BOB.GymManagement.Gym.Management.System.entities.member.service;

import BOB.GymManagement.Gym.Management.System.entities.member.MemberModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<MemberModel, Integer> {
    Optional<MemberModel> findByPhone(String phone);
    List<MemberModel> findByMembershipType(MemberModel.MembershipType type);
    List<MemberModel> findByStatus(boolean status);
    List<MemberModel> findByJoinDateBetween(LocalDate start, LocalDate end);
    long countByStatus(boolean status);
}
