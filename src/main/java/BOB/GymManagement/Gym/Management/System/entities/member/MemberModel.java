package BOB.GymManagement.Gym.Management.System.entities.member;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
@Entity
@Table(name = "member")
@Getter
@Setter
public class MemberModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String fullName;

    @Column(length = 20)
    private String phone;

    @Column(nullable = false, updatable = false)
    private LocalDate joinDate = LocalDate.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private MembershipType membershipType = MembershipType.NORMAL;

    private boolean status = true;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    public enum MembershipType {
        NORMAL, PREMIUM, VIP
    }
}