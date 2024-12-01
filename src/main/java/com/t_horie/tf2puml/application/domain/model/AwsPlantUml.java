package com.t_horie.tf2puml.application.domain.model;

import lombok.Data;

@Data
public class AwsPlantUml {
    private String resourceType = "";
    private String alias = "";
    private String label = "";
    private String tf2pumlTechnology = "";
    private String description = "";
}
