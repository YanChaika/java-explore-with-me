package org.example.ewm.user;

import org.example.ewm.user.dto.NewUserRequest;
import org.example.ewm.user.dto.UserDto;
import org.example.ewm.user.dto.UserShortDto;
import org.example.ewm.user.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserMapper {

    public static UserDto toUserDto(User user) {
        return new UserDto(user.getId(), user.getEmail(), user.getName());
    }

    public static List<UserDto> toUsersDto(List<User> users) {
        List<UserDto> usersDto = new ArrayList<>();
        for (User user : users) {
            usersDto.add(toUserDto(user));
        }
        return usersDto;
    }

    public static User fromUserDto(UserDto userDto) {
        return new User(userDto.getId(), userDto.getEmail(), userDto.getName());
    }

    public static User fromNewUserRequest(NewUserRequest newUserRequest) {
        return new User(null, newUserRequest.getEmail(), newUserRequest.getName());
    }

    public static UserShortDto toUserShortDto(User user) {
        return new UserShortDto(
                user.getId(),
                user.getName()
        );
    }
}
