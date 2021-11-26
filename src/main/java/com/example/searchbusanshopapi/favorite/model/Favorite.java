package com.example.searchbusanshopapi.favorite.model;

import com.example.searchbusanshopapi.user.model.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Table(name = "favorite")
@Getter
@Setter
@Entity
public class Favorite {

    @Id
    @Column(name = "favorite_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)//mysql 기본전략, auto로 사용시 db벤더에 맞는 전략제공
    private Long id;

    @Column(name = "owner")
    private String owner; //가게주인
    @Column(name = "shop_name")
    private String shopName; //가게이름
    @Column(name = "locale")
    private String locale;  //지역
    @Column(name = "categori")
    private String categori; //가게분류
    @Column(name = "img")
    private String img; //가게이미지

    @JsonBackReference//순환참조 방어
    @ManyToOne()
    @JoinColumn(name = "user_id", insertable = false, updatable = false) //readOnly
    private User user;

    public static class FavoriteBuilder {

        private String owner;
        private String shopName;
        private String locale;
        private String categori;
        private String img;

        public FavoriteBuilder setOwner(String owner){
            this.owner = owner;
            return this;
        }

        public FavoriteBuilder setShopName(String shopName){
            this.shopName = shopName;
            return this;
        }

        public FavoriteBuilder setLocale(String locale) {
            this.locale = locale;
            return this;
        }

        public FavoriteBuilder setCategori(String categori) {
            this.categori = categori;
            return this;
        }

        public FavoriteBuilder setImg(String img) {
            this.img = img;
            return this;
        }

        public Favorite build(){
            Favorite favorite = new Favorite();
            favorite.setOwner(this.owner);
            favorite.setShopName(this.shopName);
            favorite.setLocale(this.locale);
            favorite.setCategori(this.categori);
            favorite.setImg(this.img);
            return favorite;
        }
    }
}
