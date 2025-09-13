package BOB.GymManagement.Gym.Management.System.entities.member.service;

import BOB.GymManagement.Gym.Management.System.entities.member.MemberModel;
import BOB.GymManagement.Gym.Management.System.entities.memberOffer.service.MemberOfferService;
import BOB.GymManagement.Gym.Management.System.entities.workoutSession.service.WorkoutSessionService;
import BOB.GymManagement.Gym.Management.System.exception.DuplicatePhoneNumberException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final MemberOfferService memberOfferService;
    private final WorkoutSessionService workoutSessionService;
    public MemberModel createMember(MemberModel member) {
        Optional<MemberModel> existingMember = getMemberByPhone(member.getPhone());

        if (existingMember.isPresent()) {
            throw new DuplicatePhoneNumberException("Phone number " + member.getPhone() + " is already used by another member");
        }

        return memberRepository.save(member);
    }

    public MemberModel update(MemberModel member){
        return memberRepository.save(member);
    }

    public Optional<MemberModel> getMemberByPhone(String phone) {
        return memberRepository.findByPhone(phone);
    }
    public Optional<MemberModel> getMemberById(Integer id) {
        return memberRepository.findById(id);
    }

    public List<MemberModel> getAllMembers() {
        return memberRepository.findAll();
    }

    public List<MemberModel> getMembersByStatus(boolean status) {
        return memberRepository.findByStatus(status);
    }

    @Transactional
    public void deleteMember(Integer id) {
        workoutSessionService.deleteByMemberId(id);
        memberOfferService.deleteByMemberId(id);
        memberRepository.deleteById(id);
    }

    public long countActiveMembers() {
        return memberRepository.countByStatus((true));
    }
}