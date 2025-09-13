package BOB.GymManagement.Gym.Management.System.controller;

import BOB.GymManagement.Gym.Management.System.entities.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class PageController {

    private final MemberService memberService;

    @GetMapping("/")
    public String homePage(Model model) {
        model.addAttribute("title", "Home Page");
        model.addAttribute("message", "Welcome to our application!");
        return "index"; // This will resolve to src/main/resources/templates/index.html
    }

    // Add this method to handle the /members page
    @GetMapping("/members")
    public String membersPage(Model model) {
        model.addAttribute("title", "Members Management");
        // You can add members data if needed
        // model.addAttribute("members", memberService.getAllMembers());
        return "members"; // This will resolve to src/main/resources/templates/members.html
    }

    @GetMapping("/offers")
    public String offersPage(Model model) {
        model.addAttribute("title", "Offers Management");
        return "offers";
    }

    @GetMapping("/navigation")
    public String navigationPage(Model model) {
        return "navigation";
    }

    @GetMapping("/member-profile")
    public String memberProfilePage(@RequestParam(required = false) Integer id, Model model) {
        if (id != null) {
            model.addAttribute("memberId", id);
        }
        return "member-profile"; // This will resolve to src/main/resources/templates/member-profile.html
    }
}