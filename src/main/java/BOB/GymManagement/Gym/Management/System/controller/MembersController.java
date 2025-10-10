
package BOB.GymManagement.Gym.Management.System.controller;

import BOB.GymManagement.Gym.Management.System.controller.Request.MemberCreateDTO;
import BOB.GymManagement.Gym.Management.System.controller.Response.MemberVTO;
import BOB.GymManagement.Gym.Management.System.entities.member.MemberModel;
import BOB.GymManagement.Gym.Management.System.entities.member.mapper.MemberMapper;
import BOB.GymManagement.Gym.Management.System.entities.member.service.MemberService;
import BOB.GymManagement.Gym.Management.System.exception.DuplicatePhoneNumberException;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;import org.springframework.context.ApplicationContext;


@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MembersController {

    private final MemberService memberService;
    private static final String UPLOAD_DIR = "uploads/";
    private final ApplicationContext context;

    @PostMapping("/shutdown")
    public ResponseEntity<Map<String, String>> shutdown() {
        System.out.println("Shutdown request received - Gym Management System will shut down");

        // Create response first
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("message", "Gym Management System shutdown initiated");
        responseBody.put("status", "success");

        ResponseEntity<Map<String, String>> response = ResponseEntity.ok().body(responseBody);

        // Shutdown in separate thread after response is sent
        new Thread(() -> {
            try {
                Thread.sleep(1000); // Wait 1 second to ensure response is delivered
                System.out.println("Shutting down Gym Management System on port 8086...");

                // Proper shutdown with exit code
                int exitCode = SpringApplication.exit(context, () -> 0);
                System.out.println("Spring application exited with code: " + exitCode);
                System.exit(exitCode);

            } catch (Exception e) {
                System.err.println("Error during shutdown: " + e.getMessage());
                // Force shutdown if graceful fails
                System.exit(1);
            }
        }).start();

        return response;
    }


    @GetMapping
    public ResponseEntity<List<MemberVTO>> getAllMembers() {
        try {
            List<MemberModel> members = memberService.getAllMembers();
            List<MemberVTO> memberVTOs = members.stream()
                    .map(MemberMapper::toVTO)
                    .toList();
            return ResponseEntity.ok(memberVTOs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<MemberVTO> getMemberById(@PathVariable Integer id) {
        try {
            MemberModel member = memberService.getMemberById(id)
                    .orElseThrow(() -> new RuntimeException("Member not found with id: " + id));
            return ResponseEntity.ok(MemberMapper.toVTO(member));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    @PostMapping
    public ResponseEntity<?> createMember(@ModelAttribute MemberCreateDTO member) {
        try {
            // Handle file upload
            MultipartFile file = member.getImageFile();

            // Replace spaces with underscores in filename
            String originalFilename = file.getOriginalFilename();
            assert originalFilename != null;
            String safeFilename = originalFilename.replaceAll("\\s+", "_");

            String filename = UUID.randomUUID() + "_" + safeFilename;
            Path uploadPath = Paths.get(UPLOAD_DIR);

            // Create upload directory if it doesn't exist
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(filename);
            Files.write(filePath, file.getBytes());

            // Create member entity
            MemberModel memberModel = new MemberModel();
            memberModel.setFullName(member.getFullName());
            memberModel.setPhone(member.getPhone());
            memberModel.setStatus(member.isStatus());
            memberModel.setMembershipType(member.getMembershipType());
            memberModel.setImageUrl("/uploads/" + filename);

            // Save member
            memberModel = memberService.createMember(memberModel);

            // Create response DTO
            return ResponseEntity.status(HttpStatus.CREATED).body(MemberMapper.toVTO(memberModel));

        } catch (DuplicatePhoneNumberException e) {
            // Return specific error for duplicate phone number
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Duplicate phone number");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        } catch (Exception e) {
            // Generic error handling
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal server error");
            errorResponse.put("message", "An unexpected error occurred");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    @GetMapping("/active")
    public ResponseEntity<List<MemberVTO>> getActiveMembers() {
        try {
            List<MemberModel> activeMembers = memberService.getMembersByStatus(true);
            List<MemberVTO> memberVTOs = activeMembers.stream()
                    .map(MemberMapper::toVTO)
                    .toList();
            return ResponseEntity.ok(memberVTOs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/count-active")
    public ResponseEntity<Long> countActiveMembers() {
        try {
            long count = memberService.countActiveMembers();
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMember(@PathVariable Integer id) {
        try {
            Optional<MemberModel> member=memberService.getMemberById(id);
            if (member.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Member not found with id: " + id);
            }  String filename = extractFilenameFromUrl(member.get().getImageUrl());
            memberService.deleteMember(id);
            if (filename != null) {
                Path oldImagePath = Paths.get(UPLOAD_DIR, filename);
                if (Files.exists(oldImagePath)) {
                    Files.delete(oldImagePath);
                    System.out.println("Deleted old image: " + oldImagePath);
                }
            }

            return ResponseEntity.ok("Member deleted successfully");
        } catch (Exception e) {
            throw  new RuntimeException(e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete member");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<MemberVTO> updateMember(@PathVariable Integer id, @ModelAttribute MemberCreateDTO memberDetails, BindingResult result) {
        try {
            if (result.hasErrors()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            MemberModel existingMember = memberService.getMemberById(id)
                    .orElseThrow(() -> new RuntimeException("Member not found with id: " + id));

            MultipartFile imageFile = memberDetails.getImageFile();
            String oldImageUrl = existingMember.getImageUrl();

            if (memberDetails.getFullName() != null) {
                existingMember.setFullName(memberDetails.getFullName());
            }
            if (memberDetails.getPhone() != null) {
                MemberModel memberPh = memberService.getMemberByPhone(memberDetails.getPhone())
                        .orElseThrow(() -> new RuntimeException("Member with phone " + memberDetails.getPhone() + " not found"));
                   if(!memberPh.getId().equals(id)){
                       throw new DuplicatePhoneNumberException("Phone number " + memberPh.getPhone() + " is already used");
                }
                existingMember.setPhone(memberDetails.getPhone());
            }
            if (memberDetails.getMembershipType() != null) {
                existingMember.setMembershipType(memberDetails.getMembershipType());
            }
            existingMember.setStatus(memberDetails.isStatus());

            if (imageFile != null && !imageFile.isEmpty()) {
                try {
                    if (oldImageUrl != null && !oldImageUrl.isEmpty()) {
                        try {
                            String oldFilename = extractFilenameFromUrl(oldImageUrl);

                            if (oldFilename != null) {
                                Path oldImagePath = Paths.get(UPLOAD_DIR, oldFilename);
                                if (Files.exists(oldImagePath)) {
                                    Files.delete(oldImagePath);
                                    System.out.println("Deleted old image: " + oldImagePath);
                                }
                            }
                        } catch (IOException e) {
                            System.err.println("Failed to delete old image: " + e.getMessage());
                        }
                    }

                    String safeFilename = imageFile.getOriginalFilename().replaceAll("\\s+", "_");
                    String filename = UUID.randomUUID() + "_" + safeFilename;
                    Path uploadPath = Paths.get(UPLOAD_DIR);

                    if (!Files.exists(uploadPath)) {
                        Files.createDirectories(uploadPath);
                    }

                    Path filePath = uploadPath.resolve(filename);
                    Files.write(filePath, imageFile.getBytes());
                    existingMember.setImageUrl("/uploads/" + filename);

                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                }
            }

            memberService.update(existingMember);
            return ResponseEntity.ok(MemberMapper.toVTO(existingMember));
        } catch (Exception e) {
            System.out.println("Exception Thrown : "+e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private String extractFilenameFromUrl(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }

        try {
            // Try to parse as URI first (for http://, https:// URLs)
            URI uri = new URI(url);
            String path = uri.getPath();
            if (path.contains("/")) {
                return path.substring(path.lastIndexOf("/") + 1);
            }
            return path;
        } catch (URISyntaxException e) {
            // If it's not a valid URI, treat it as a local path
            if (url.contains("/")) {
                return url.substring(url.lastIndexOf("/") + 1);
            } else if (url.contains("\\")) {
                return url.substring(url.lastIndexOf("\\") + 1);
            }
            return url;
        }
    }
}