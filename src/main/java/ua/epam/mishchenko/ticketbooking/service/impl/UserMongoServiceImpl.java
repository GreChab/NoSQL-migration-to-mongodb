package ua.epam.mishchenko.ticketbooking.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ua.epam.mishchenko.ticketbooking.dto.UserDto;
import ua.epam.mishchenko.ticketbooking.model.UserMongo;
import ua.epam.mishchenko.ticketbooking.repository.UserMongoRepository;
import ua.epam.mishchenko.ticketbooking.service.UserService;

import java.util.ArrayList;
import java.util.List;

@Profile(value = "mongo")
@Service
@RequiredArgsConstructor
public class UserMongoServiceImpl implements UserService {

    private final UserMongoRepository userRepository;

    @Override
    public UserDto getUserById(String userId) {
        try {
            UserMongo user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Can not to get a user by id: " + userId));
            return UserDto.fromUserMongoToUserDto(user);
        } catch (RuntimeException e) {
            return null;
        }
    }

    @Override
    public UserDto getUserByEmail(String email) {
        try {
            if (email.isEmpty()) {
                return null;
            }
            var user = userRepository.getByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Can not to get an user by email: " + email));
            return UserDto.fromUserMongoToUserDto(user);
        } catch (RuntimeException e) {
            return null;
        }
    }

    @Override
    public List<UserDto> getUsersByName(String name, int pageSize, int pageNum) {
        try {
            if (name.isEmpty()) {
                return new ArrayList<>();
            }
            var usersByName = userRepository.getAllByName(PageRequest.of(pageNum - 1, pageSize), name);

            return usersByName.getContent()
                    .stream()
                    .map(UserDto::fromUserMongoToUserDto)
                    .toList();
        } catch (RuntimeException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public UserDto createUser(UserDto user) {
        try {
            if (isUserNull(user)) {
                return null;
            }

            var savedUser = userRepository.save(UserDto.fromUserDtotoUserMongo(user));
            return UserDto.fromUserMongoToUserDto(savedUser);
        } catch (RuntimeException e) {
            return null;
        }
    }


    private boolean userExistsById(UserDto user) {
        return userRepository.existsById(user.getId());
    }

    private boolean userExistsByEmail(UserDto user) {
        return userRepository.existsByEmail(user.getEmail());
    }

    /**
     * Is user null boolean.
     *
     * @param user the user
     * @return the boolean
     */
    private boolean isUserNull(UserDto user) {
        return user == null;
    }

    @Override
    public UserDto updateUser(UserDto user) {
        try {
            if (isUserNull(user)) {
                return null;
            }
            if (!userExistsById(user)) {
                throw new RuntimeException("This user does not exist");
            }
            if (userExistsByEmail(user)) {
                throw new RuntimeException("This email already exists");
            }

            var savedUser = userRepository.save(UserDto.fromUserDtotoUserMongo(user));
            return UserDto.fromUserMongoToUserDto(savedUser);
        } catch (RuntimeException e) {
            return null;
        }
    }

    @Override
    public boolean deleteUser(String userId) {
        try {
            userRepository.deleteById(userId);
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }
}
