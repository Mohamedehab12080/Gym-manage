package BOB.GymManagement.Gym.Management.System.controller;

import BOB.GymManagement.Gym.Management.System.controller.Request.OfferDTO;
import BOB.GymManagement.Gym.Management.System.controller.Response.OfferVTO;
import BOB.GymManagement.Gym.Management.System.entities.offers.service.OfferService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/offers")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OfferController {

    private final OfferService offerService;

    @PostMapping
    public ResponseEntity<OfferVTO> createOffer(@RequestBody OfferDTO offerDTO) {
        try {
            OfferVTO createdOffer = offerService.createOffer(offerDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdOffer);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<OfferVTO> getOfferById(@PathVariable Integer id) {
        Optional<OfferVTO> offer = offerService.getOfferById(id);
        return offer.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<OfferVTO>> getAllOffers() {
        try {
            List<OfferVTO> offers = offerService.getAllOffers();
            return ResponseEntity.ok(offers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/active")
    public ResponseEntity<List<OfferVTO>> getActiveOffers() {
        try {
            List<OfferVTO> activeOffers = offerService.getActiveOffers();
            activeOffers.forEach(offer -> {
                System.out.printf("All Members of offer: %s, Members: %d%n", offer.getTitle(), offer.getNumberOfMembers());
                System.out.printf("All Active Members of offer: %s, Members: %d%n", offer.getTitle(), offer.getNumberOfActiveMembers());
                System.out.println();
            });
            return ResponseEntity.ok(activeOffers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOffer(@PathVariable Integer id) {
        try {
            // First check if the offer exists
            Optional<OfferVTO> existingOffer = offerService.getOfferById(id);
            if (existingOffer.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            offerService.deleteOffer(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}