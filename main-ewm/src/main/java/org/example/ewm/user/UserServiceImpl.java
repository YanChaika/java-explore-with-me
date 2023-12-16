package org.example.ewm.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ewm.exception.ConflictException;
import org.example.ewm.exception.NotFoundException;
import org.example.ewm.user.dto.NewUserRequest;
import org.example.ewm.user.dto.UserDto;
import org.example.ewm.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<UserDto> findUsersById(List<Long> ids, int from, int size) {
        log.info("Поиск пользователей");
        Pageable pageRequest = PageRequest.of(from, size);
        List<User> users;
        Page<User> userPage;
        if (ids.get(0) == 0) {
            log.info("Получение всех пользователей");
            userPage = userRepository.findAll(pageRequest);
            users = userPage.toList();
        } else {
            log.info("Получение пользователей по id");
            users = userRepository.findAllById(ids);
        }
        return UserMapper.toUsersDto(users);
    }

    @Override
    @Transactional
    public UserDto createUser(NewUserRequest userDto) {
        log.info("Добавление пользователя");
        if (!(userDto.getEmail().contains("@"))) {
            throw new RuntimeException("Email: " + userDto.getEmail() + "некорректный");
        }
        if (userRepository.findByName(userDto.getName()) != null) {
            throw new ConflictException("Пользователь с таким именем уже существует");
        }
        User user = UserMapper.fromNewUserRequest(userDto);
        User userWithId = userRepository.save(user);
        return UserMapper.toUserDto(userWithId);

    }

    @Override
    @Transactional
    public void deleteUserById(Long userId) {
        log.info("Удаление пользователя");
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь с id" + userId + " не найден");
        } else {
            userRepository.deleteById(userId);
        }
    }
}
