package BOB.GymManagement.Gym.Management.System.controller;

import BOB.GymManagement.Gym.Management.System.controller.Request.WorkoutSessionDTO;
import BOB.GymManagement.Gym.Management.System.controller.Response.WorkoutSessionVTO;
import BOB.GymManagement.Gym.Management.System.entities.workoutSession.service.WorkoutSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/workout-sessions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Allow requests from your frontend
public class WorkoutSessionsController {

    private final WorkoutSessionService workoutSessionService;

    @PostMapping
    public ResponseEntity<WorkoutSessionVTO> createSession(@RequestBody WorkoutSessionDTO sessionDTO) {
        try {
            WorkoutSessionVTO createdSession = workoutSessionService.createSession(sessionDTO);

            return ResponseEntity.ok(createdSession);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @DeleteMapping("/{sessionId}")
    public ResponseEntity<Map<String, String>> deleteWorkoutSession(@PathVariable Long sessionId) {
        workoutSessionService.deleteSession(sessionId);
        return ResponseEntity.ok(Map.of("message", "Deleted Success"));
    }


    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<WorkoutSessionVTO>> getMemberSessions(@PathVariable Integer memberId) {
        try {
            List<WorkoutSessionVTO> sessions = workoutSessionService.getMemberSessions(memberId);

            return ResponseEntity.ok(sessions);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<WorkoutSessionVTO>> getAllSessions() {
        try {
            List<WorkoutSessionVTO> sessions = workoutSessionService.getAllSessions();
            return ResponseEntity.ok(sessions);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/member/{memberId}/count-this-month")
    public ResponseEntity<Long> countMemberSessionsThisMonth(@PathVariable Integer memberId) {
        try {
            long sessionCount = workoutSessionService.countMemberSessionsThisMonth(memberId);
            return ResponseEntity.ok(sessionCount);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}