package Team4.TobeHonest.controller;


import Team4.TobeHonest.domain.Member;
import Team4.TobeHonest.dto.friendWIth.FriendWithSpecifyName;
import Team4.TobeHonest.exception.DuplicateFriendException;
import Team4.TobeHonest.exception.NoMemberException;
import Team4.TobeHonest.service.FriendService;
import Team4.TobeHonest.service.MemberService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/members")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class MemberController {

    private final MemberService memberService;
    private final FriendService friendService;

    @GetMapping("{memberId}")
    @ResponseBody
    public String memberInformation(@PathVariable Long memberId) {
        return "" + memberId;
    }

    @GetMapping("/friends")
    //로그인은 인터셉터에서 처리 해 준다고 생각..
    public List<FriendWithSpecifyName> findAllFriends(@AuthenticationPrincipal UserDetails userDetails) {
        /*Authentication 객체가 SecurityContext에 저장됩니다.
            그리고 이 SecurityContext는 HttpSession에 SPRING_SECURITY_CONTEXT라는 키로 저장*/

        //수정해야함 ==> member 엔티티가 controller에 노출되는것을 해결해보자
        Member member = (Member) userDetails;
        return friendService.findAllFriendsProfile(member);

    }

    @GetMapping("/friends/add/{friendId}")
    public ResponseEntity<String> addFriend(@PathVariable Long friendId,
                                            @AuthenticationPrincipal UserDetails userDetails) {
        Member member = (Member) userDetails;
        Member friend;
        try {
            friend = memberService.findByID(friendId);
            friendService.addFriendList(member, friend);
        } catch (DuplicateFriendException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
        catch (NoMemberException noMemberException){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(noMemberException.getMessage());
        }

        return ResponseEntity.status(HttpStatus.OK).body("friend added: " + friend.getName());
    }

    /*@GetMapping("/friends/delete/{friendId}")
    public ResponseEntity<String> deleteFriend(@PathVariable Long friendId,
                                            @AuthenticationPrincipal UserDetails userDetails) {
        Member member = (Member) userDetails;
        Member friend;
        try {
            friend = memberService.findByID(friendId);
            friendService.addFriendList(member, friend);
        } catch (DuplicateFriendException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
        catch (NoMemberException noMemberException){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(noMemberException.getMessage());
        }

        return ResponseEntity.status(HttpStatus.OK).body("friend added: " + friend.getName());

    }*/



}
