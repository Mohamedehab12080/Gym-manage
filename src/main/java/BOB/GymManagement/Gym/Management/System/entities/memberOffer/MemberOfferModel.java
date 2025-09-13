package BOB.GymManagement.Gym.Management.System.entities.memberOffer;

import BOB.GymManagement.Gym.Management.System.entities.member.MemberModel;
import BOB.GymManagement.Gym.Management.System.entities.offers.OfferModel;
import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import org.springframework.format.annotation.DateTimeFormat;

import java.lang.reflect.Member;
import java.time.LocalDate;

@Entity
@Table(name = "member_offer")
@Getter
@Setter
public class MemberOfferModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private MemberModel member;

    @ManyToOne
    @JoinColumn(name = "offer_id")
    private OfferModel offer;

    private Integer remainedSessions;

    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;

    @Column(nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @Column(nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    public enum Status {
        ACTIVE("نشط"),
        EXPIRED("منتهي"),
        CANCELLED("ملغي");

        private final String arabicName;

        Status(String arabicName) {
            this.arabicName = arabicName;
        }

        public String getArabicName() {
            return arabicName;
        }

        @JsonCreator
        public static Status fromString(String value) {
            for (Status status : Status.values()) {
                if (status.name().equalsIgnoreCase(value)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Unknown status: " + value);
        }

        public static Status fromArabicName(String arabicName) {
            for (Status status : Status.values()) {
                if (status.arabicName.equals(arabicName)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("No status found with Arabic name: " + arabicName);
        }
    }
}