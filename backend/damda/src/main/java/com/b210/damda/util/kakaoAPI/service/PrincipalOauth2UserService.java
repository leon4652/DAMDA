package com.b210.damda.util.kakaoAPI.service;


import com.b210.damda.domain.entity.Items.Items;
import com.b210.damda.domain.entity.Items.ItemsMapping;
import com.b210.damda.domain.entity.User.*;

import com.b210.damda.domain.entity.theme.Theme;
import com.b210.damda.domain.entity.theme.ThemeMapping;
import com.b210.damda.domain.shop.repository.ItemsMappingRepository;
import com.b210.damda.domain.shop.repository.ItemsRepository;
import com.b210.damda.domain.shop.repository.ThemeMappingRepository;
import com.b210.damda.domain.shop.repository.ThemeRepository;
import com.b210.damda.domain.user.repository.UserCoinGetLogRepository;
import com.b210.damda.domain.user.repository.UserEventRepository;
import com.b210.damda.domain.user.repository.UserLogRepository;
import com.b210.damda.domain.user.repository.UserRepository;
import com.b210.damda.domain.user.service.UserService;
import com.b210.damda.util.kakaoAPI.repository.KakaoLogRepository;
import com.b210.damda.util.refreshtoken.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;


import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    private final KakaoLogRepository kakaoLogRepository;
    private final BCryptPasswordEncoder encoder;
    private final UserService userService;
    private final UserLogRepository userLogRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final ItemsMappingRepository itemsMappingRepository;
    private final ItemsRepository itemsRepository;
    private final ThemeMappingRepository themeMappingRepository;
    private final ThemeRepository themeRepository;
    private final UserEventRepository userEventRepository;
    private final UserCoinGetLogRepository userCoinGetLogRepository;

    private final int dailyCheckCoin = 500;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException{
        OAuth2User oAuth2User = super.loadUser(userRequest);
        //log.info("getAttributes : {}", oAuth2User.getAttributes());

        OAuth2UserInfo oAuth2UserInfo = null;

        String provider = userRequest.getClientRegistration().getRegistrationId();

        if(provider.equals("kakao")){
            oAuth2UserInfo  = new KakaoUserInfo((Map) oAuth2User.getAttributes());
        }

        String providerId = oAuth2UserInfo.getProviderId();
        String email = oAuth2UserInfo.getEmail();
        String loginId = provider + "_" + providerId;
        String nickname = oAuth2UserInfo.getName();
        String profileImage = oAuth2UserInfo.getImagePath();
        if(profileImage == null){
            profileImage = "https://damda.s3.ap-northeast-2.amazonaws.com/user-profileImage/profile.jpg";
        }

        Optional<User> optionalUser = userRepository.findByKakaoEmail(email);
        User user = null;

        if(optionalUser.isEmpty()){
            //카카오 유저 생성
            user = User.builder()
                    .accountType("KAKAO")
                    .email(email)
                    .userPw(encoder.encode(loginId))
                    .nickname(nickname)
                    .profileImage(profileImage)
                    .build();
            
            user = userRepository.save(user);

            // 3번 아이템 꺼냄
            Items items = itemsRepository.findByItemNo(3L).get();

            // 스티커 무료 제공
            ItemsMapping itemsMapping = new ItemsMapping(user, items);
            itemsMappingRepository.save(itemsMapping);

            // 1번 테마 꺼냄
            Theme theme = themeRepository.findById(1L).get();

            ThemeMapping themeMapping = new ThemeMapping(user, theme);
            themeMappingRepository.save(themeMapping);
            KakaoLog kakaoLog = new KakaoLog();
            kakaoLog.createKakaoLog(userRequest.getAccessToken().getTokenValue(), user);
            kakaoLogRepository.save(kakaoLog);

        }
        else{
            //유저 불러오기
            user = optionalUser.get();
            KakaoLog kakaolog = kakaoLogRepository.findByUserUserNo(user.getUserNo());
            kakaolog.updateKakaoLog(userRequest.getAccessToken().getTokenValue());
            kakaoLogRepository.save(kakaolog);
        }
        
        // 카카오 유저 로그인 로그 저장
        UserLog userLog = new UserLog();
        userLog.setUser(user);
        userLogRepository.save(userLog);

        // 유저 출석체크
        UserEvent byUser = userEventRepository.findByUser(user);
        boolean check = false;

        if(byUser == null){
            byUser = new UserEvent(user);
            userEventRepository.save(byUser);
            // 코인 개수 변경
            user.updatePlusCoin(dailyCheckCoin);
            check = true;
        }else if(!byUser.getIsCheck()){
            byUser.updateIsCheck();
            // 코인 개수 변경
            user.updatePlusCoin(dailyCheckCoin);
            check = true;
        }

        if(check){
            // 코인 얻은 로그 생성
            UserCoinGetLog userCoinGetLog = UserCoinGetLog.builder()
                    .user(user)
                    .getCoin(dailyCheckCoin)
                    .type("CHECK").build();

            userCoinGetLogRepository.save(userCoinGetLog);
        }

        //유저 정보를 필요할때 그때 사용한다.
        return new PrincipalDetails(user, oAuth2User.getAttributes(), userRequest.getAccessToken().getTokenValue());

    }

}
