package com.example.searchbusanshopapi.user.controller;

import com.example.searchbusanshopapi.infra.exception.Errorcode;
import com.example.searchbusanshopapi.infra.exception.InValidRequestParameterException;
import com.example.searchbusanshopapi.infra.exception.RegistedUsernameException;
import com.example.searchbusanshopapi.infra.exception.UserNotFoundException;
import com.example.searchbusanshopapi.user.dto.UserDTO;
import com.example.searchbusanshopapi.user.model.User;
import com.example.searchbusanshopapi.user.repository.UserRepository;
import com.example.searchbusanshopapi.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.security.InvalidParameterException;
import java.util.List;


/**
 * 유저에 관한 CRUD의 모든내용들을 처리합니다.
 *
 */
@Api(description = "유저에관한 CRUD을 제공합니다.")
@RestController
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;

    /**
     * 서버에 저장된 모든 user를 출력합니다.
     * ADMIN권한을 가진 이용자만 접근할수있습니다.
     *
     * @return
     */
    @ApiOperation("모든 유저 조회")
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

    /**
     * 유저 1개를 찾습니다.
     * @param userId 1이상의 값만 들어올수있습니다. @positive
     * @return
     */
    @ApiOperation(value = "유저 조회(id)")
    @GetMapping("/users/{userId}")
    public ResponseEntity<User> findUser(
            @Positive @PathVariable Long userId) {
        User user;
        try {
            user = userRepository.findById(userId).get();
        }catch (Exception e) {
            //유저를 찾을수없다면 예외를 던집니다.
            e.printStackTrace();
            throw new UserNotFoundException(Errorcode.USER_NOT_FOUND_IN_DB, userId);
        }
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    /**
     * 유저를 생성합니다.
     * @param userDTO
     * @param bindingResult
     * @return
     * @throws RegistedUsernameException
     */
    @ApiOperation(value = "유저 생성")
    @PostMapping("/users")
    public ResponseEntity joinUser(@Validated @RequestBody UserDTO userDTO, BindingResult bindingResult) throws RegistedUsernameException {

        //username, password에 공백을 넣었을경우 들어갑니다.
        if(bindingResult.hasErrors()){
            throw new InValidRequestParameterException("입력항목에는 공백이 허용되지 않습니다.");
        }
        try {
            userService.joinUser(userDTO);
        } catch (RegistedUsernameException e) {
            //이미 등록되어있는 username일경우 던집니다.
            e.printStackTrace();
            throw e;
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    /**
     * user삭제 + favorite데이터도 함께 삭제 됩니다.
     * @param userId 삭제할 식별자(최소 1이상)
     * @return
     * @throws Exception 삭제하고자하는 아이디가 없을때
     */
    @ApiOperation(value = "유저 삭제")
    @DeleteMapping("/users/{userId}")
    public ResponseEntity deleteUser(@Positive @PathVariable Long userId) {
        try{
            userService.deleteUser(userId);
        }catch (UserNotFoundException e) {
            e.printStackTrace();
            throw e;
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 유저를 업데이트합니다.
     * @param userId 업데이트할 식별자(최소 1이상)
     * @param userDTO 수정할 내용입니다.(공백을 허용하지 않습니다.)
     * @param bindingResult
     * @return
     */
    @ApiOperation(value = "유저 수정")
    @PutMapping("/users/{userId}")
    public ResponseEntity updateUser(@Positive @PathVariable Long userId,
                                     @Validated @RequestBody UserDTO userDTO,
                                     BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            throw new InValidRequestParameterException("입력항목에는 공백이 허용되지 않습니다.");
        }
        userService.updateUser(userId, userDTO);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
