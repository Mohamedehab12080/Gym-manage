package BOB.GymManagement.Gym.Management.System.controller.Request;

import BOB.GymManagement.Gym.Management.System.entities.offers.OfferModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OfferDTO {
    private String title;
    private Integer numberOfSessions;
    private Integer numberOfMonths;
    private Double price;
    private OfferModel.Status status ;
}
