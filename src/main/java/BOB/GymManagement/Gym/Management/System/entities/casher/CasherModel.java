package BOB.GymManagement.Gym.Management.System.entities.casher;

import jakarta.persistence.*;
import lombok.Data;
@Entity
@Table(name = "casher")
@Data
public class CasherModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 50)
    private String userName;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false, unique = true, length = 20)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Type type = Type.USER;

    public enum Type {
        ADMIN, USER
    }
}