package BOB.GymManagement.Gym.Management.System.entities.offers.service;

import BOB.GymManagement.Gym.Management.System.controller.Request.OfferDTO;
import BOB.GymManagement.Gym.Management.System.controller.Response.MemberOfferVTO;
import BOB.GymManagement.Gym.Management.System.controller.Response.OfferVTO;
import BOB.GymManagement.Gym.Management.System.entities.memberOffer.service.MemberOfferService;
import BOB.GymManagement.Gym.Management.System.entities.offers.OfferModel;
import BOB.GymManagement.Gym.Management.System.entities.offers.mapper.OfferMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OfferService {
    private final OfferRepository offerRepository;
    private final MemberOfferService memberOfferService;

    public OfferVTO createOffer(OfferDTO offerDTO) {
        OfferModel offerModel =OfferMapper.toOfferModel(offerDTO);
        OfferModel saved = offerRepository.save(offerModel);
        offerModel.setId(saved.getId());
        return OfferMapper.toVTO(offerModel);
    }

    public Optional<OfferVTO> getOfferById(Integer id) {
        OfferModel offerModel = offerRepository.findById(id).orElse(null);
        assert offerModel != null;
        return Optional.ofNullable(OfferMapper.toVTO(offerModel));
    }
    public List<OfferVTO> getAllOffers() {
        return processOffers(offerRepository.findAll());
    }

    public List<OfferVTO> getActiveOffers() {
        return processOffers(offerRepository.findByStatus(OfferModel.Status.ACTIVE));
    }

    private List<OfferVTO> processOffers(List<OfferModel> offerModels) {
        if (offerModels.isEmpty()) {
            return Collections.emptyList();
        }

        // Convert to VTO and create lookup map
        List<OfferVTO> offerVTOS = offerModels.stream()
                .map(OfferMapper::toVTO)
                .collect(Collectors.toList());

        Map<Integer, OfferVTO> offerMap = createOfferMap(offerVTOS);
        Set<Integer> offerIds = offerMap.keySet();

        // Fetch member offers for relevant offers only
        List<MemberOfferVTO> memberOfferVTOS = memberOfferService.getMemberOffersByOfferIds(offerIds);

        // Create statistics map
        Map<Integer, OfferStatistics> offerStatsMap = new HashMap<>();
        offerIds.forEach(id -> offerStatsMap.put(id, new OfferStatistics()));

        // Process member offers
        processMemberOffers(memberOfferVTOS, offerMap, offerStatsMap);

        // Apply statistics to offers
        applyStatisticsToOffers(offerVTOS, offerStatsMap);

        return offerVTOS;
    }

    private Map<Integer, OfferVTO> createOfferMap(List<OfferVTO> offers) {
        return offers.stream()
                .collect(Collectors.toMap(OfferVTO::getId, Function.identity()));
    }

    private void processMemberOffers(List<MemberOfferVTO> memberOfferVTOS,
                                     Map<Integer, OfferVTO> offerMap,
                                     Map<Integer, OfferStatistics> offerStatsMap) {
        for (MemberOfferVTO memberOffer : memberOfferVTOS) {
            Integer offerId = memberOffer.getOffer().getId();
            OfferStatistics stats = offerStatsMap.get(offerId);
            OfferVTO correspondingOffer = offerMap.get(offerId);

            if (stats != null && correspondingOffer != null) {
                stats.totalMembers++;

                if (memberOffer.getRemainedSessions() < correspondingOffer.getNumberOfSessions()) {
                    stats.activeMembers++;
                }
            }
        }
    }

    private void applyStatisticsToOffers(List<OfferVTO> offers, Map<Integer, OfferStatistics> statsMap) {
        for (OfferVTO offer : offers) {
            OfferStatistics stats = statsMap.get(offer.getId());
            if (stats != null) {
                offer.setNumberOfActiveMembers(stats.activeMembers);
                offer.setNumberOfNotActiveMembers(stats.totalMembers - stats.activeMembers);
                offer.setNumberOfMembers(stats.totalMembers);
            }
        }
    }

    // Statistics helper class - consider making it a record if using Java 16+
    private static class OfferStatistics {
        int activeMembers = 0;
        int totalMembers = 0;
    }

    public void deleteOffer(Integer id) {
        offerRepository.deleteById(id);
    }
}