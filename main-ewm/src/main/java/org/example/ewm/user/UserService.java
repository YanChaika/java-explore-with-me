package org.example.ewm.user;

import org.example.ewm.user.dto.NewUserRequest;
import org.example.ewm.user.dto.UserDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface UserService {

    List<UserDto> findUsersById(List<Long> ids, int from, int size);

    UserDto createUser(NewUserRequest newUserRequest);

    void deleteUserById(Long userId);
}
