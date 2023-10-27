package Team4.TobeHonest.dto.message;


import Team4.TobeHonest.enumer.MessageType;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
//내가 전송 하는 message
public class SendMessageDTO {

    private Long wishItemId;
    private Long senderId;
    private Long receiverId;
    private String title;
    private String contents;
    private MessageType messageType;
    //펀딩한 금액..
    private Integer fundMoney;
    //이미지와 json은 병렬화 할 수 없다..
    private List<MultipartFile> images = new ArrayList<>();




}
