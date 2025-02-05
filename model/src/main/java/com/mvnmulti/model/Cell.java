package com.mvnmulti.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Random;

@Data
@AllArgsConstructor
public class Cell {
    private String key;
    private String value;

    @Override
    public String toString() {
        return key + "," + value;
    }
}