package BOB.GymManagement.Gym.Management.System.controller.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutSessionDTO {

    private Integer memberId;
    private LocalDate sessionDate;

}
