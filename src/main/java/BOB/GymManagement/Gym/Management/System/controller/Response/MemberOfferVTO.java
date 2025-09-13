package BOB.GymManagement.Gym.Management.System.controller.Response;

import BOB.GymManagement.Gym.Management.System.entities.member.MemberModel;
import BOB.GymManagement.Gym.Management.System.entities.memberOffer.MemberOfferModel;
import BOB.GymManagement.Gym.Management.System.entities.offers.OfferModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberOfferVTO {
    private Integer id;

    private MemberVTO member;

    private OfferVTO offer;

    private Integer remainedSessions;

    private MemberOfferModel.Status status ;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
}
