package com.example.playgame.entity.enums;

import lombok.Getter;

@Getter
public enum BucketType {
    BUYLIST("BUYLIST"),
    WISHLIST("WISHLIST");

    private final String value;

    BucketType(String value){
        this.value = value;
    }
}
