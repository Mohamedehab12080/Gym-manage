package BOB.GymManagement.Gym.Management.System.entities.memberOffer.service;

import BOB.GymManagement.Gym.Management.System.controller.Response.MemberOfferVTO;
import BOB.GymManagement.Gym.Management.System.entities.memberOffer.MemberOfferModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface MemberOfferRepository extends JpaRepository<MemberOfferModel,Integer> {

    List<MemberOfferModel> findByMemberId(Integer memberId);
    List<MemberOfferModel> findByOfferId(Integer offerId);
    List<MemberOfferModel> findByStatus(MemberOfferModel.Status status);
    List<MemberOfferModel> findByEndDateBeforeAndStatus(LocalDate date, MemberOfferModel.Status status);

    List<MemberOfferModel> findByMemberIdAndStatus(Integer memberId, MemberOfferModel.Status status);

    boolean existsByMemberIdAndStatusAndRemainedSessionsGreaterThan(Integer memberId, MemberOfferModel.Status status, Integer remainedSessionsIsGreaterThan);

    Optional<MemberOfferModel> findFirstByMemberIdAndStatusAndRemainedSessionsGreaterThanOrderByIdAsc(
            Integer memberId,
            MemberOfferModel.Status status,
            Integer remainedSessions
    );

    List<MemberOfferModel> findByOfferIdIn(Set<Integer> offerIds);
    void deleteByMemberId(Integer id);
}
