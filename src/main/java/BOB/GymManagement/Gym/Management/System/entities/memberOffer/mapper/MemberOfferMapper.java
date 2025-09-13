package BOB.GymManagement.Gym.Management.System.entities.memberOffer.mapper;

import BOB.GymManagement.Gym.Management.System.controller.Request.MemberOfferDTO;
import BOB.GymManagement.Gym.Management.System.controller.Response.MemberOfferVTO;
import BOB.GymManagement.Gym.Management.System.entities.member.MemberModel;
import BOB.GymManagement.Gym.Management.System.entities.member.mapper.MemberMapper;
import BOB.GymManagement.Gym.Management.System.entities.memberOffer.MemberOfferModel;
import BOB.GymManagement.Gym.Management.System.entities.offers.OfferModel;
import BOB.GymManagement.Gym.Management.System.entities.offers.mapper.OfferMapper;

public class MemberOfferMapper {
    // Mapping methods
    public static MemberOfferDTO toDTO(MemberOfferModel entity) {
        if (entity == null) {
            return null;
        }

        return MemberOfferDTO.builder()
                .memberId(entity.getMember().getId())
                .offerId(entity.getOffer().getId())
                .remainedSessions(entity.getRemainedSessions())
                .status(entity.getStatus())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .build();
    }

    public static MemberOfferModel fromDTO(MemberOfferDTO dto) {
        if (dto == null) {
            return null;
        }

        MemberOfferModel entity = new MemberOfferModel();
        MemberModel membModel=new MemberModel();
        membModel.setId(dto.getMemberId());
        entity.setMember(membModel);
        OfferModel offerModel=new OfferModel();
        offerModel.setId(dto.getOfferId());
        entity.setOffer(offerModel);
        entity.setRemainedSessions(dto.getRemainedSessions());
        entity.setStatus(dto.getStatus());
        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());

        return entity;
    }

    // Add these methods to your MemberOfferModel class
    public static MemberOfferVTO toVTO(MemberOfferModel entity) {
        if (entity == null) {
            return null;
        }

        return MemberOfferVTO.builder()
                .id(entity.getId())
                .member(MemberMapper.toVTO(entity.getMember()))  // Convert MemberModel to MemberVTO
                .offer(OfferMapper.toVTO(entity.getOffer()))     // Convert OfferModel to OfferVTO
                .remainedSessions(entity.getRemainedSessions())
                .status(entity.getStatus())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .build();
    }



}
