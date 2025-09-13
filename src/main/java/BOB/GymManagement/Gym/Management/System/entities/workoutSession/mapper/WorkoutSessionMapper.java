package BOB.GymManagement.Gym.Management.System.entities.workoutSession.mapper;

import BOB.GymManagement.Gym.Management.System.controller.Request.WorkoutSessionDTO;
import BOB.GymManagement.Gym.Management.System.controller.Response.MemberVTO;
import BOB.GymManagement.Gym.Management.System.controller.Response.WorkoutSessionVTO;
import BOB.GymManagement.Gym.Management.System.entities.member.MemberModel;
import BOB.GymManagement.Gym.Management.System.entities.workoutSession.WorkoutSession;

public class WorkoutSessionMapper {

    public static WorkoutSessionVTO toVTO(WorkoutSession workoutSession) {
        WorkoutSessionVTO workoutSessionVTO = new WorkoutSessionVTO();
        workoutSessionVTO.setId(workoutSession.getId());
        workoutSessionVTO.setSessionDate(workoutSession.getSessionDate());
        MemberVTO memberVTO = new MemberVTO();
        memberVTO.setId(workoutSession.getMember().getId());
        workoutSessionVTO.setMember(memberVTO);
        return workoutSessionVTO;
    }
    public static WorkoutSession toWorkoutSession(WorkoutSessionVTO workoutSessionVTO) {
        WorkoutSession workoutSession = new WorkoutSession();
        workoutSession.setId(workoutSessionVTO.getId());
        workoutSession.setSessionDate(workoutSessionVTO.getSessionDate());
        MemberModel memberModel = new MemberModel();
        memberModel.setId(workoutSessionVTO.getMember().getId());
        workoutSession.setMember(memberModel);
        return workoutSession;
    }

    public static WorkoutSession toWorkoutSession(WorkoutSessionDTO workoutSessionDTO) {
        WorkoutSession workoutSession = new WorkoutSession();
        workoutSession.setSessionDate(workoutSessionDTO.getSessionDate());
        MemberModel memberModel = new MemberModel();
        memberModel.setId(workoutSessionDTO.getMemberId());
        workoutSession.setMember(memberModel);
        return workoutSession;
    }
}
