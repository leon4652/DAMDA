package com.b210.damda.domain.dto.serverSentEvent.timecapsule;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CheckMyExpiredTimecapsuleDTO {
    String content;
    Long timecapsuleNo;
    String type;
    String title;
    String date;
}
