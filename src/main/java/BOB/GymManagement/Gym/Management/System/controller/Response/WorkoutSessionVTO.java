package BOB.GymManagement.Gym.Management.System.controller.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutSessionVTO {

    private Long id;
    private MemberVTO member;
    private LocalDate sessionDate;
    private Integer remainedSessions;

}
