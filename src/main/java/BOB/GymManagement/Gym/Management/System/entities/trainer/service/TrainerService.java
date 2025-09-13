package BOB.GymManagement.Gym.Management.System.entities.trainer.service;

import BOB.GymManagement.Gym.Management.System.entities.trainer.TrainerModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TrainerService {
    private final TrainerRepository trainerRepository;

    public TrainerModel createTrainer(TrainerModel trainer) {
        return trainerRepository.save(trainer);
    }

    public Optional<TrainerModel> getTrainerById(Integer id) {
        return trainerRepository.findById(id);
    }

    public List<TrainerModel> getAllTrainers() {
        return trainerRepository.findAll();
    }

    public List<TrainerModel> getActiveTrainers() {
        return trainerRepository.findByActive(true);
    }

    public void deleteTrainer(Integer id) {
        trainerRepository.deleteById(id);
    }
}