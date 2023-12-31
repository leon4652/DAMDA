package com.b210.damda.domain.entity.Timecapsule;

import com.b210.damda.domain.dto.Timecapsule.TimecapsuleCardDTO;
import com.b210.damda.domain.dto.Timecapsule.TimecapsuleOpenCardDTO;
import com.b210.damda.domain.entity.User.User;
import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Getter
public class TimecapsuleCard {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long timecapsuleCardNo;

    @ManyToOne
    @JoinColumn(name="timecapsule_no")
    private Timecapsule timecapsule;

    private String imagePath;

    @ManyToOne
    @JoinColumn(name="user_no")
    private User user;

    private Timestamp createTime;

    public TimecapsuleCard(){
    }

    public TimecapsuleOpenCardDTO toTimecapsuleOpenCardDTO(){
        return TimecapsuleOpenCardDTO.builder()
                .timecapsuleCardNo(this.timecapsuleCardNo)
                .imagePath(this.imagePath)
                .userNo(this.user.getUserNo())
                .build();
    }

    public void createCard(Timecapsule timecapsule, User user, String fileUri, Timestamp createTime){
        this.timecapsule = timecapsule;
        this.user = user;
        this.imagePath = fileUri;
        this.createTime = createTime;
    }
}
