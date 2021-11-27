package com.example.searchbusanshopapi.user.controller;

import com.example.searchbusanshopapi.infra.exception.Errorcode;
import com.example.searchbusanshopapi.infra.exception.RegistedUsernameException;
import com.example.searchbusanshopapi.infra.exception.UserNotFoundException;
import com.example.searchbusanshopapi.user.dto.UserDTO;
import com.example.searchbusanshopapi.user.model.User;
import com.example.searchbusanshopapi.user.repository.UserRepository;
import com.example.searchbusanshopapi.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;

    @GetMapping("/users")
    public ResponseEntity findUsers(){
        List<User> users;
        try {
            users = userService.findUsers();
        } catch (UserNotFoundException e) {
            e.printStackTrace();
            throw e;
        }
        return ResponseEntity.ok().body(users);
    }
    @GetMapping("/users/{userId}")
    public ResponseEntity<User> findUser(@PathVariable Long userId) {
        User user;
        try {
            user = userRepository.findById(userId).get();
        }catch (Exception e) {
            e.printStackTrace();
            throw new UserNotFoundException(Errorcode.USER_NOT_FOUND_IN_DB, userId);
        }
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }
    @PostMapping("/users")
    public ResponseEntity joinUser(@RequestBody UserDTO userDTO) throws RegistedUsernameException {

        try {
            userService.joinUser(userDTO);
        } catch (RegistedUsernameException e) {
            e.printStackTrace();
            throw e;
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    /**
     * user삭제 + favorite데이터도 함께 삭제 됩니다.
     * @param userId 삭제할 식별자
     * @return
     * @throws Exception 삭제하고자하는 아이디가 없을때
     */
    @DeleteMapping("/users/{userId}")
    public ResponseEntity deleteUser(@PathVariable Long userId) {
        try{
            userService.deleteUser(userId);
        }catch (UserNotFoundException e) {
            e.printStackTrace();
            throw e;
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    @PutMapping("/users/{userId}")
    public ResponseEntity updateUser(@PathVariable Long userId,
                                     @RequestBody UserDTO userDTO){
        userService.updateUser(userId, userDTO);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
