package BOB.GymManagement.Gym.Management.System.entities.workoutSession.service;

import BOB.GymManagement.Gym.Management.System.controller.Request.WorkoutSessionDTO;
import BOB.GymManagement.Gym.Management.System.controller.Response.WorkoutSessionVTO;
import BOB.GymManagement.Gym.Management.System.entities.member.service.MemberRepository;
import BOB.GymManagement.Gym.Management.System.entities.memberOffer.MemberOfferModel;
import BOB.GymManagement.Gym.Management.System.entities.memberOffer.service.MemberOfferRepository;
import BOB.GymManagement.Gym.Management.System.entities.workoutSession.WorkoutSession;
import BOB.GymManagement.Gym.Management.System.entities.workoutSession.mapper.WorkoutSessionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkoutSessionService {
    private final WorkoutSessionRepository workoutSessionRepository;
    private final MemberRepository memberRepository;
    private final MemberOfferRepository memberOfferRepository;

    @Transactional
    public WorkoutSessionVTO createSession(WorkoutSessionDTO sessionDTO) {
        // Validate member exists
        if (!memberRepository.existsById(sessionDTO.getMemberId())) {
            throw new RuntimeException("Member not found");
        }
        if(existsByMemberIdAndSessionDate(sessionDTO.getMemberId(),sessionDTO.getSessionDate())){
            throw new RuntimeException("Session already exists");
        }
        // Check if member has active offers with remaining sessions
        boolean hasActiveOffers = memberOfferRepository.existsByMemberIdAndStatusAndRemainedSessionsGreaterThan(
                sessionDTO.getMemberId(), MemberOfferModel.Status.ACTIVE, 0);

        if (!hasActiveOffers) {
            throw new RuntimeException("Member has no available sessions");
        }

        // Deduct one session from an active offer
        MemberOfferModel offerToUse = memberOfferRepository
                .findFirstByMemberIdAndStatusAndRemainedSessionsGreaterThanOrderByIdAsc(
                        sessionDTO.getMemberId(),
                        MemberOfferModel.Status.ACTIVE,
                        0
                )
                .orElseThrow(() -> new RuntimeException("No active offer found for member"));

        offerToUse.setRemainedSessions(offerToUse.getRemainedSessions() - 1);
        memberOfferRepository.save(offerToUse);

        WorkoutSession workoutSession=WorkoutSessionMapper.toWorkoutSession(sessionDTO);

        WorkoutSession saved= workoutSessionRepository.save(workoutSession);
        workoutSession.setId(saved.getId());
        return WorkoutSessionMapper.toVTO(workoutSession);
    }

    public WorkoutSessionVTO getSessionByMemberIdAndSessionDate(Integer memberId, LocalDate sessionDate) {
        return WorkoutSessionMapper.toVTO(workoutSessionRepository.findByMemberIdAndSessionDate(memberId,sessionDate));
    }
    public boolean existsByMemberIdAndSessionDate(Integer memberId,LocalDate sessionDate){
        return workoutSessionRepository.existsByMemberIdAndSessionDate(memberId,sessionDate);
    }

    public void deleteByMemberId(Integer memberId){
        try{
            workoutSessionRepository.deleteByMemberId(memberId);
        }catch (Exception e){
            throw  new RuntimeException(e);
        }
    }
    public List<WorkoutSessionVTO> getMemberSessions(Integer memberId) {
        MemberOfferModel offerToUse = memberOfferRepository
                .findFirstByMemberIdAndStatusAndRemainedSessionsGreaterThanOrderByIdAsc(
                        memberId,
                        MemberOfferModel.Status.ACTIVE,
                        0
                )
                .orElseThrow(() -> new RuntimeException("No active offer found for member"));
        return workoutSessionRepository.findByMemberId(memberId)
                .stream()
                .map(WorkoutSessionMapper::toVTO)
                .peek(vto -> vto.setRemainedSessions(offerToUse.getRemainedSessions()))
                .toList();
    }

    public List<WorkoutSessionVTO> getAllSessions() {
        return workoutSessionRepository.findAll().stream().map(WorkoutSessionMapper::toVTO).collect(Collectors.toList());
    }
    @Transactional
    public void deleteSession(Long sessionId){
        if(workoutSessionRepository.existsById(sessionId)){
            workoutSessionRepository.deleteById(sessionId);
        }else{
            throw new RuntimeException("Session id not found");
        }
    }
    public long countMemberSessionsThisMonth(Integer memberId) {
        LocalDate start = LocalDate.now().withDayOfMonth(1);
        LocalDate end = LocalDate.now().plusMonths(1).withDayOfMonth(1).minusDays(1);
        return workoutSessionRepository.countByMemberIdAndSessionDateBetween(memberId, start, end);
    }
}