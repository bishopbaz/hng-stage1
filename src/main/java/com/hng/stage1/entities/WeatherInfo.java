package com.hng.stage1.entities;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class WeatherInfo {

    private String city;
    private double temperatureC;

}
