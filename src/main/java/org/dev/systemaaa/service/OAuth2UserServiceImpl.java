package org.dev.systemaaa.service;

import lombok.RequiredArgsConstructor;
import org.dev.systemaaa.model.entity.User;
import org.dev.systemaaa.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OAuth2UserServiceImpl extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) {
        OAuth2User oAuth2User = super.loadUser(request);

        String email = oAuth2User.getAttribute("email");
        String name  = oAuth2User.getAttribute("name");

        // name и email могут быть null — цепочка fallback'ов
        String username;
        if (name != null) {
            username = name.replaceAll("\\s+", "_").toLowerCase();
        } else if (email != null) {
            username = email.split("@")[0];
        } else {
            // крайний случай: ни name, ни email не пришли от провайдера
            username = "oauth_user_" + System.currentTimeMillis();
        }

        userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setUsername(username);
            newUser.setPassword("");      // пароля нет — OAuth пользователь
            newUser.setEnabled(true);     // email уже проверил Google
            newUser.setProvider("google");
            return userRepository.save(newUser);
        });

        return oAuth2User;
    }
}