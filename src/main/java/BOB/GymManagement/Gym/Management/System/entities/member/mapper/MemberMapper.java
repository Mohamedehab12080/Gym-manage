package BOB.GymManagement.Gym.Management.System.entities.member.mapper;

import BOB.GymManagement.Gym.Management.System.controller.Response.MemberVTO;
import BOB.GymManagement.Gym.Management.System.entities.member.MemberModel;

public class MemberMapper {

    public static MemberVTO toVTO(MemberModel memberModel) {
        MemberVTO memberVTO = new MemberVTO();
        memberVTO.setId(memberModel.getId());
        memberVTO.setFullName(memberModel.getFullName());
        memberVTO.setPhone(memberModel.getPhone());
        memberVTO.setStatus(memberModel.isStatus());
        memberVTO.setMembershipType(memberModel.getMembershipType());
        memberVTO.setJoinDate(memberModel.getJoinDate());

        // Handle image URL properly
        if (memberModel.getImageUrl() != null) {
            // If it's already a full URL, use it as is
            if (memberModel.getImageUrl().startsWith("http")) {
                memberVTO.setImageUrl(memberModel.getImageUrl());
            } else {
                // If it's a relative path, construct the full URL
                String imageUrl = "http://localhost:8086" + memberModel.getImageUrl();
                memberVTO.setImageUrl(imageUrl);
            }
        } else {
            memberVTO.setImageUrl(null);
        }

        return memberVTO;
    }
}
