package BOB.GymManagement.Gym.Management.System.controller.Request;

import BOB.GymManagement.Gym.Management.System.entities.member.MemberModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class MemberCreateDTO {
    private String fullName;

    private String phone;

    private LocalDate joinDate = LocalDate.now();

    private MemberModel.MembershipType membershipType = MemberModel.MembershipType.NORMAL;

    private boolean status = true;
    private MultipartFile imageFile;
}
