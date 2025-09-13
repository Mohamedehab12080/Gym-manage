package BOB.GymManagement.Gym.Management.System.entities.workoutSession.service;

import BOB.GymManagement.Gym.Management.System.entities.workoutSession.WorkoutSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface WorkoutSessionRepository extends JpaRepository<WorkoutSession, Long> {

    List<WorkoutSession> findByMemberId(Integer member_Id);
    List<WorkoutSession> findBySessionDate(LocalDate date);
    boolean existsByMemberIdAndSessionDate(Integer member_id, LocalDate date);
    boolean existsById(Long id);
    @Transactional
    @Modifying
    @Query("DELETE FROM WorkoutSession w WHERE w.member.id = :memberId")
    void deleteByMemberId(@Param("memberId") Integer memberId);
    WorkoutSession findByMemberIdAndSessionDate(Integer member_Id, LocalDate date);

    Integer countByMemberIdAndSessionDateBetween(Integer memberId, LocalDate start, LocalDate end);

    Integer countByMemberId(Integer memberId);
}


