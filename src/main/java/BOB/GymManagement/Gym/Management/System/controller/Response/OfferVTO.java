package BOB.GymManagement.Gym.Management.System.controller.Response;

import BOB.GymManagement.Gym.Management.System.entities.offers.OfferModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OfferVTO {
    private Integer id;
    private String title;
    private Integer numberOfSessions;
    private Integer numberOfMonths;
    private Double price;
    private OfferModel.Status status ;
    private Integer numberOfActiveMembers;
    private Integer numberOfNotActiveMembers;
    private Integer numberOfMembers;
}
