package com.collaball.domain.user.service;

import com.collaball.domain.user.dto.UpdateBioRequest;
import com.collaball.domain.user.dto.UserResponse;
import com.collaball.domain.user.entity.User;
import com.collaball.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public UserResponse updateBio(User currentUser, UpdateBioRequest request) {
        currentUser.updateBio(request.bio());
        userRepository.save(currentUser);
        return UserResponse.from(currentUser);
    }
}
