package Team4.TobeHonest.controller;

import Team4.TobeHonest.domain.Member;
import Team4.TobeHonest.dto.contributor.ContributorDTO;
import Team4.TobeHonest.dto.item.ItemInfoDTO;
import Team4.TobeHonest.dto.wishitem.FirstWishItem;
import Team4.TobeHonest.dto.wishitem.WishItemDetail;
import Team4.TobeHonest.dto.wishitem.WishItemResponseDTO;
import Team4.TobeHonest.exception.DuplicateWishItemException;
import Team4.TobeHonest.exception.ItemNotInWishlistException;
import Team4.TobeHonest.service.ContributorService;
import Team4.TobeHonest.service.ItemService;
import Team4.TobeHonest.service.MemberService;
import Team4.TobeHonest.service.WishItemService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wishlist")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class WishlistController {

    private final WishItemService wishItemService;
    private final ContributorService contributorService;
    private final ItemService itemService;
    private final MemberService memberService;

    //    위시리스트 정보들(사진, progress정도.. 추가 정보필요하면 FrirstWishItem수정하기)
    //모든 위시리스트 타입 상관없이 달라하기..
    @GetMapping("/all/{memberId}")
    public List<FirstWishItem> inquireWishList(@PathVariable Long memberId) {
        return wishItemService.findAllWishList(memberId);
    }


    //현재 펀딩 진행 중인 위시리스트
    @GetMapping("/progress/{memberId}")
    public List<FirstWishItem> inquireWishListInProgress(@PathVariable Long memberId) {
        return wishItemService.findWishListInProgress(memberId);
    }

    @GetMapping("/completed/{memberId}")
    public List<FirstWishItem> inquireWishListCompleted(@PathVariable Long memberId) {
        return wishItemService.findWishListCompleted(memberId);
    }

    @GetMapping("/used/{memberId}")
    public List<FirstWishItem> inquireWishListUsed(@PathVariable Long memberId) {
        return wishItemService.findWishListUsed(memberId);
    }



    @GetMapping("/details/{memberId}/{wishItemId}")
    public WishItemResponseDTO seeWishItemDetail(@PathVariable Long memberId, @PathVariable Long wishItemId,
                                                 @AuthenticationPrincipal UserDetails userDetails) {

        WishItemResponseDTO response = new WishItemResponseDTO();
        List<WishItemDetail> wishItemDetail = wishItemService.findWishItemDetail(wishItemId);
        response.setWishItemDetail(wishItemDetail);
        String userEmail = userDetails.getUsername();

        Member member = memberService.findByEmail(userEmail);

//        만약 로그인한 멤버라면... 추가정보도 제공
        List<ContributorDTO> contributor;

        if (member.getId().equals(memberId)) {
            contributor = contributorService.findContributor(wishItemId);
            response.setContributor(contributor);
        }
        return response;
    }

    @PostMapping("/add/{itemId}")
    public ResponseEntity<String> addWishItem(@PathVariable Long itemId,
                                              @AuthenticationPrincipal UserDetails userDetails) {
        String userEmail = userDetails.getUsername();
        Member member = memberService.findByEmail(userEmail);


        ItemInfoDTO byItemID = itemService.findByItembyID(itemId);
        wishItemService.addWishList(member, byItemID);
        return ResponseEntity.status(HttpStatus.OK).body("위시아이템" + byItemID.getName() + " 추가완료");
    }

    @DeleteMapping("/delete/{itemId}")
    public ResponseEntity<String> deleteWishItem(@PathVariable Long itemId,
                                                 @AuthenticationPrincipal UserDetails userDetails) {
        String userEmail = userDetails.getUsername();
        Member member = memberService.findByEmail(userEmail);


        ItemInfoDTO itemInfoDTO = itemService.findByItembyID(itemId);
        wishItemService.deleteWishListByItemId((Member) userDetails, itemInfoDTO.getId());
        return ResponseEntity.status(HttpStatus.OK).body("위시아이템" + itemInfoDTO.getName() + " 추가완료");
    }

    @PostMapping("/use/{wishItemId}")
    public ResponseEntity<String> wishItemUse(@PathVariable Long wishItemId,
                                                 @AuthenticationPrincipal UserDetails userDetails){
        String userEmail = userDetails.getUsername();
        wishItemService.useWishItem(userEmail, wishItemId);

        return ResponseEntity.status(HttpStatus.OK).body("충전완료");
    }

}
