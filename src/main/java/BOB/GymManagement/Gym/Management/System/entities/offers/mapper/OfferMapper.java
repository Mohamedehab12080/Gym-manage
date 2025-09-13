package BOB.GymManagement.Gym.Management.System.entities.offers.mapper;

import BOB.GymManagement.Gym.Management.System.controller.Request.OfferDTO;
import BOB.GymManagement.Gym.Management.System.controller.Response.OfferVTO;
import BOB.GymManagement.Gym.Management.System.entities.offers.OfferModel;

public class OfferMapper {
    public static OfferVTO toVTO(OfferModel offerModel) {

        return OfferVTO.builder().id(offerModel.getId())
                .title(offerModel.getTitle())
                .price(offerModel.getPrice())
                .numberOfSessions(offerModel.getNumberOfSessions())
                .numberOfMonths(offerModel.getNumberOfMonths())
                .status(offerModel.getStatus()).build();
    }
    public static OfferModel toOfferModel(OfferVTO offerModel) {

        return OfferModel.builder().id(offerModel.getId())
                .title(offerModel.getTitle())
                .price(offerModel.getPrice())
                .numberOfSessions(offerModel.getNumberOfSessions())
                .numberOfMonths(offerModel.getNumberOfMonths())
                .status(offerModel.getStatus()).build();
    }

    public static OfferDTO toDTO(OfferModel offerModel) {
        return OfferDTO.builder()
                .title(offerModel.getTitle())
                .price(offerModel.getPrice())
                .numberOfSessions(offerModel.getNumberOfSessions())
                .numberOfMonths(offerModel.getNumberOfMonths())
                .status(offerModel.getStatus()).build();
    }
    public static OfferModel toOfferModel(OfferDTO offerDTO) {

        return OfferModel.builder()
                .title(offerDTO.getTitle())
                .price(offerDTO.getPrice())
                .numberOfSessions(offerDTO.getNumberOfSessions())
                .numberOfMonths(offerDTO.getNumberOfMonths())
                .status(offerDTO.getStatus()).build();
    }
}
