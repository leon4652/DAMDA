package com.b210.damda.domain.timecapsule.repository;

import com.b210.damda.domain.entity.Timecapsule.Timecapsule;
import com.b210.damda.domain.entity.Timecapsule.TimecapsuleInvite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TimecapsuleInviteRepository extends JpaRepository<TimecapsuleInvite, Long> {

    
    // 타임캡슐로 타임캡슐 초대 목록 찾음
    List<TimecapsuleInvite> getTimecapsuleInviteByTimecapsule(Timecapsule timecapsule);

    // 친구 번호로 타임캡슐 초대 데이터를 찾음
    @Query("SELECT ti FROM TimecapsuleInvite ti WHERE ti.timecapsule = :timecapsule AND ti.guestUserNo = :userNo")
    Optional<TimecapsuleInvite> getTimecapsuleInviteByTimecapsuleAndGuestUserNo(@Param("timecapsule") Timecapsule timecapsule, @Param("userNo") Long userNo);
}
