package BOB.GymManagement.Gym.Management.System.controller.Response;

import BOB.GymManagement.Gym.Management.System.entities.member.MemberModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberVTO {
    private Integer id;

    private String fullName;

    private String phone;

    private LocalDate joinDate ;

    private MemberModel.MembershipType membershipType ;

    private boolean status ;
    private byte[] imageData;
    private String imageUrl;
}
