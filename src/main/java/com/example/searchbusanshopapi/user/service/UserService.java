package com.example.searchbusanshopapi.user.service;

import com.example.searchbusanshopapi.api.ShopConfig;
import com.example.searchbusanshopapi.favorite.model.Favorite;
import com.example.searchbusanshopapi.infra.exception.Errorcode;
import com.example.searchbusanshopapi.infra.exception.RegistedUsernameException;
import com.example.searchbusanshopapi.infra.exception.UserNotFoundException;
import com.example.searchbusanshopapi.shop.dto.ShopDTO;
import com.example.searchbusanshopapi.user.dto.UserDTO;
import com.example.searchbusanshopapi.user.model.User;
import com.example.searchbusanshopapi.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional(rollbackOn = Exception.class)//Exception, 모든 예외에 대하여 롤백처리
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ShopConfig shopConfig;
    private final PasswordEncoder passwordEncoder;

    public List<User> findUsers(){
        List<User> users = userRepository.findAll();
        if(users.size() == 0) {
            throw new UserNotFoundException(Errorcode.USER_NOT_FOUND_IN_DB);
        }
        return users;
    }

    @PostMapping("/users")
    public void joinUser(UserDTO userDTO) {
        User getUser = userRepository.findByUsername(userDTO.getUsername());
        if(getUser != null) {
            throw new RegistedUsernameException(Errorcode.ALREADY_REGISTED_USERNAME_IN_DB, "이미 존재하는 아이디 입니다.");
        }

        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setRole("ROLE_USER");
        userRepository.save(user);
    }

    public void deleteUser(Long userId) {
        User user;
        try {
            user = userRepository.findById(userId).get();
        } catch (NoSuchElementException e) {
            throw new UserNotFoundException(Errorcode.USER_NOT_FOUND_IN_DB, userId);
        }
        userRepository.delete(user);

    }

    public void save(ShopDTO shopDTO, Long userId) throws Exception{
        JSONObject jsonObject = shopConfig.request(shopDTO);
        List<Favorite> favorites = jsonRestaurantToFavorites(jsonObject);
        User user = userRepository.findById(userId).get();
        if(user == null){
            throw new Exception("넌 아이디가 없어! 난 서비스단이야");
        }
        user.getFavorites().addAll(favorites);
        userRepository.save(user);
    }

    /**
     * jsonObject타입의 가게리스트를 List<Favorite>타입의 자바 오브젝트로 바꿔줍니다. //vo객체로 이동필요
     * @param jsonObject 원래형태
     * @return 자바오브젝트로 변환
     */
    private List<Favorite> jsonRestaurantToFavorites(JSONObject jsonObject){
        JSONObject jsonMap =(JSONObject)jsonObject.get("getGoodPriceStore");
        JSONArray jsonList = (JSONArray)jsonMap.get("item");

        List<Favorite> favorites = new ArrayList<>();
        Favorite favorite = null;

        for(Object element : jsonList) {
            HashMap<String, String> restaurntFiled = (HashMap<String, String>)element;
            favorite = new Favorite.FavoriteBuilder()
                    .setOwner(restaurntFiled.get("mNm"))
                    .setCategori(restaurntFiled.get("cn"))
                    .setLocale(restaurntFiled.get("locale"))
                    .setOwner(restaurntFiled.get("mNm"))
                    .setShopName(restaurntFiled.get("sj"))
                    .setImg(restaurntFiled.get("imgFile1"))
                    .build();
            favorites.add(favorite);
        }
        return favorites;
    }

    /**
     * 유저 업데이트
     * @param userId 수정할 유저식별자
     * @param userDTO 수정내용이 담긴 객체
     */
    public void updateUser(Long userId, UserDTO userDTO) {
        User user = userRepository.findById(userId).get();
        user.setUsername(userDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        userRepository.save(user);
    }
}
