package BOB.GymManagement.Gym.Management.System.controller;

import BOB.GymManagement.Gym.Management.System.controller.Request.MemberOfferDTO;
import BOB.GymManagement.Gym.Management.System.controller.Response.MemberOfferVTO;
import BOB.GymManagement.Gym.Management.System.entities.memberOffer.service.MemberOfferService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/member-offers")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MemberOffersController {

    private final MemberOfferService memberOfferService;

    // Create a new member-offer association
    @PostMapping
    public ResponseEntity<MemberOfferVTO> createMemberOffer(@RequestBody MemberOfferDTO memberOfferDTO) {
        try {
            MemberOfferVTO createdOffer = memberOfferService.createMemberOffer(memberOfferDTO);
            return ResponseEntity.ok(createdOffer);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Get all member-offer associations for a specific member
    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<MemberOfferVTO>> getMemberOffers(@PathVariable Integer memberId) {
        try {
            List<MemberOfferVTO> offers = memberOfferService.getMemberOffers(memberId);
            return ResponseEntity.ok(offers);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Get active member-offer associations for a specific member
    @GetMapping("/member/{memberId}/active")
    public ResponseEntity<List<MemberOfferVTO>> getActiveMemberOffers(@PathVariable Integer memberId) {
        try {
            List<MemberOfferVTO> activeOffers = memberOfferService.getActiveMemberOffers(memberId);
            return ResponseEntity.ok(activeOffers);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Get a specific member-offer association by ID
    @GetMapping("/{id}")
    public ResponseEntity<MemberOfferVTO> getMemberOfferById(@PathVariable Integer id) {
        try {
            MemberOfferVTO memberOffer = memberOfferService.getMemberOfferById(id);
            return ResponseEntity.ok(memberOffer);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Delete a member-offer association
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMemberOffer(@PathVariable Integer id) {
        try {
            memberOfferService.deleteMemberOffer(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Update a member-offer association
    @PutMapping("/{id}")
    public ResponseEntity<MemberOfferVTO> updateMemberOffer(@PathVariable Integer id, @RequestBody MemberOfferDTO memberOfferDTO) {
        try {
            MemberOfferVTO updatedOffer = memberOfferService.updateMemberOffer(id, memberOfferDTO);
            return ResponseEntity.ok(updatedOffer);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Get all member-offer associations
    @GetMapping
    public ResponseEntity<List<MemberOfferVTO>> getAllMemberOffers() {
        try {
            List<MemberOfferVTO> allOffers = memberOfferService.getAllMemberOffers();
            return ResponseEntity.ok(allOffers);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}