package com.t_horie.terra_puml.application.domain.model;

import lombok.Data;

@Data
public class AwsPlantUml {
    private String resourceType;
    private String alias;
    private String label;
    private String technology;
    private String description;
}
