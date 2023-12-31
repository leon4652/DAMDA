package com.b210.damda.util.schedular;


import com.b210.damda.domain.timecapsule.repository.TimecapsuleRepository;
import com.b210.damda.domain.user.repository.UserRepository;
import com.b210.damda.util.serverSentEvent.service.EventStreamService;
import com.b210.damda.util.serverSentEvent.service.FriendEventService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.FluxSink;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class SSEScheduledComponent {

    private final EventStreamService eventStreamService;
    private final UserRepository userRepository;


    @Scheduled(fixedRate = 60000) // 테스터
    public void scheduledTask() {
        log.info("스케줄링된 작업 실행: {}", System.currentTimeMillis());
        /*
1. 일정 시간마다 스케줄러가 시행된다.
2. lastResponseTimes Map을 순회하여 현재 시간과 비교하여 오래 '연결이 없는' userNo를 찾아낸다.
3. 해당 userNo를 장시 미접속 로그로 판단하고, disconnectStream(userNo)로 시행한다.
4. 해당 메서드 실행 시 모든 스트림과 저장 내역이 종료된다. 만약 접속 중인 클라이언트라면, FE에서 자동으로 재연결을 수행한다.(end-of-stream event)
         */

        Map<Long, FluxSink<ServerSentEvent<JsonNode>>> nowUserFluxSinkMap = EventStreamService.userFluxSinkMap;
        // 방법 1: 향상된 for 루프 사용
        log.warn("[Schedular] 유저 접속 관련(메모리 관련) 스케줄러 작동");
        for (Map.Entry<Long, LocalDateTime> entry : EventStreamService.lastResponseTimes.entrySet()) {
            Long userNo = entry.getKey();
            String userName = userRepository.findUserNicknameByUserNo(userNo);
            LocalDateTime userLastResponse = entry.getValue(); //유저의 마지막 응답 시간
            Duration duration = Duration.between(userLastResponse, LocalDateTime.now().plusHours(9)); //시간 차이
            log.info("+ User Info : {}, {} ", userNo, userName);
            log.info("+== Time Diff : {} | Last Response : {}", duration, userLastResponse);
            //2분 이상 미접속 시 강제 로그아웃 진행
            if(duration.toMinutes() >= 2) {
                log.warn("[Disconnect]{} 님이 부재중입니다, 로그아웃을 진행합니다. 시간 차이 : {} 분", userNo, duration.toMinutes());
                eventStreamService.disconnectStream(userNo); //장기 미접속으로 인한 삭제 로직 진행
            }

        }
    }


}
