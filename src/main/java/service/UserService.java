package service;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import model.User;
import entity.UserEntity;
import repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    public UserEntity getUser(String name) {
        return userRepository.findByName(name);
    }
    public UserEntity addUser(User user) {
        UserEntity userEntity = new  UserEntity();
        userEntity.setName(user.getName());
        return userRepository.save(userEntity);
    }
    public void removeUser(String name) {
        userRepository.delete(getUser(name));
    }
    public UserEntity updateUser(String name, User updatedFields) {
        UserEntity existing = userRepository.findByName(name);
        if (existing == null) {
            return null;
        }

        if (updatedFields.getName() != null && !updatedFields.getName().isBlank()) {
            existing.setName(updatedFields.getName());
        }

        return userRepository.save(existing);
    }
}
