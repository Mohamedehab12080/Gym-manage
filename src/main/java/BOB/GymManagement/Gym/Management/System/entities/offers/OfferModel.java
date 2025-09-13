package BOB.GymManagement.Gym.Management.System.entities.offers;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "offers")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OfferModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;
    private Integer numberOfSessions;
    private Integer numberOfMonths;
    private Double price;

    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;

    public enum Status {
        ACTIVE, CANCELLED
    }
}