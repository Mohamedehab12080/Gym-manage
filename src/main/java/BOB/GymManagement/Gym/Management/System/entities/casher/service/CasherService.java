package BOB.GymManagement.Gym.Management.System.entities.casher.service;

import BOB.GymManagement.Gym.Management.System.entities.casher.CasherModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CasherService {
    private final CasherRepository casherRepository;

    public CasherModel createCasher(CasherModel casher) {
        return casherRepository.save(casher);
    }

    public Optional<CasherModel> getCasherById(Integer id) {
        return casherRepository.findById(id);
    }

    public List<CasherModel> getAllCashers() {
        return casherRepository.findAll();
    }

    public List<CasherModel> getCashersByType(CasherModel.Type type) {
        return casherRepository.findByType(type);
    }

    public void deleteCasher(Integer id) {
        casherRepository.deleteById(id);
    }

    public Optional<CasherModel> authenticate(String username, String password) {
        Optional<CasherModel> casher = casherRepository.findByUserName(username);
        if (casher.isPresent() && casher.get().getPassword().equals(password)) {
            return casher;
        }
        return Optional.empty();
    }
}