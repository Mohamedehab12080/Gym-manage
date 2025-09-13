package BOB.GymManagement.Gym.Management.System.entities.workoutSession;

import BOB.GymManagement.Gym.Management.System.entities.member.MemberModel;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

// WorkoutSession.java
@Entity
@Table(name = "workout_sessions")
@Data
public class WorkoutSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private MemberModel member;

    @Column(name="session_date",nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate sessionDate;
}