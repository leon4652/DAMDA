package com.b210.damda.domain.dto;

import com.b210.damda.domain.entity.TimecapsuleCriteria;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Builder
@Getter @Setter
public class SaveTimecapsuleListDTO {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long timecapsuleNo;
    private String type;
    private String title;
    private String startDate;
    private String endDate;
    private int capsuleIconNo;
    private int curCard;
    private int goalCard;
    //조건 정보
    private SaveCapsuleCriteriaDTO criteria;

}
