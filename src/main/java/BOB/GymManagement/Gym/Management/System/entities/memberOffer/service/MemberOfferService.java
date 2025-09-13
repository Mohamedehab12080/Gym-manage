package BOB.GymManagement.Gym.Management.System.entities.memberOffer.service;

import BOB.GymManagement.Gym.Management.System.controller.Request.MemberOfferDTO;
import BOB.GymManagement.Gym.Management.System.controller.Response.MemberOfferVTO;
import BOB.GymManagement.Gym.Management.System.entities.member.service.MemberRepository;
import BOB.GymManagement.Gym.Management.System.entities.memberOffer.MemberOfferModel;
import BOB.GymManagement.Gym.Management.System.entities.memberOffer.mapper.MemberOfferMapper;
import BOB.GymManagement.Gym.Management.System.entities.offers.service.OfferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class MemberOfferService {
    private final MemberOfferRepository memberOfferRepository;
    private final MemberRepository memberRepository;
    private final OfferRepository offerRepository;

    public MemberOfferVTO createMemberOffer(MemberOfferDTO memberOfferDTO) {
        // Validate member exists
        if (!memberRepository.existsById(memberOfferDTO.getMemberId())) {
            throw new RuntimeException("Member with ID " + memberOfferDTO.getMemberId() + " not found");
        }

        // Validate offer exists
        if (!offerRepository.existsById(memberOfferDTO.getOfferId())) {
            throw new RuntimeException("Offer with ID " + memberOfferDTO.getOfferId() + " not found");
        }

        MemberOfferModel memberOfferModel = MemberOfferMapper.fromDTO(memberOfferDTO);
        MemberOfferModel saved = memberOfferRepository.save(memberOfferModel);
        return MemberOfferMapper.toVTO(saved);
    }

    public List<MemberOfferVTO> getMemberOffers(Integer memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new RuntimeException("Member with ID " + memberId + " not found");
        }
        return memberOfferRepository.findByMemberId(memberId).stream()
                .map(MemberOfferMapper::toVTO)
                .collect(Collectors.toList());
    }

    public List<MemberOfferVTO> getActiveMemberOffers(Integer memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new RuntimeException("Member with ID " + memberId + " not found");
        }
        return memberOfferRepository.findByMemberIdAndStatus(memberId, MemberOfferModel.Status.ACTIVE).stream()
                .map(MemberOfferMapper::toVTO)
                .collect(Collectors.toList());
    }

    public MemberOfferVTO getMemberOfferById(Integer id) {
        return memberOfferRepository.findById(id)
                .map(MemberOfferMapper::toVTO)
                .orElseThrow(() -> new RuntimeException("Member offer with ID " + id + " not found"));
    }

    public List<MemberOfferVTO> getAllMemberOffers() {
        return memberOfferRepository.findAll().stream()
                .map(MemberOfferMapper::toVTO)
                .collect(Collectors.toList());
    }

    public MemberOfferVTO updateMemberOffer(Integer id, MemberOfferDTO memberOfferDTO) {
        MemberOfferModel existing = memberOfferRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Member offer with ID " + id + " not found"));

        // Update the fields from DTO
        if (memberOfferDTO.getRemainedSessions() != null) {
            existing.setRemainedSessions(memberOfferDTO.getRemainedSessions());
        }

        if (memberOfferDTO.getStatus() != null) {
            existing.setStatus(memberOfferDTO.getStatus());
        }

        if (memberOfferDTO.getStartDate() != null) {
            existing.setStartDate(memberOfferDTO.getStartDate());
        }

        if (memberOfferDTO.getEndDate() != null) {
            existing.setEndDate(memberOfferDTO.getEndDate());
        }

        MemberOfferModel updated = memberOfferRepository.save(existing);
        return MemberOfferMapper.toVTO(updated);
    }

    public void deleteMemberOffer(Integer id) {
        if (!memberOfferRepository.existsById(id)) {
            throw new RuntimeException("Member offer with ID " + id + " not found");
        }
        memberOfferRepository.deleteById(id);
    }

    public List<MemberOfferVTO> getMemberOffersByOfferIds(Set<Integer> offerIds) {
        return memberOfferRepository.findByOfferIdIn(offerIds).stream().map(MemberOfferMapper::toVTO).collect(Collectors.toList());
    }

    public void deleteByMemberId(Integer id) {
       try{
           memberOfferRepository.deleteByMemberId(id);
       }catch (Exception e){
           throw new RuntimeException(e);
       }
    }
}