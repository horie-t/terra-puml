package com.t_horie.tf2puml.application.domain.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Map.entry;

@Data
public class AwsPumlResource {
    private String pumlMacro = "";
    private String alias = "";
    private String label = "";
    private String technology = "";
    private String description = "";
    private Optional<String> parent = Optional.empty();
    private List<AwsPumlResource> children = new ArrayList<>();

    public String getHeaderFile() {
        return pumlMacroToIncludeFileName.get(pumlMacro);
    }

    public static AwsPumlResource fromTfResource(AwsTfResource tfResource) {
        var pumlResource = new AwsPumlResource();
        pumlResource.setPumlMacro(getPumlMacro(tfResource.getResourceType()));
        pumlResource.setAlias(tfResource.getAlias());
        pumlResource.setLabel(tfResource.getLabel());
        pumlResource.setTechnology(tfResource.getTf2pumlTechnology());
        pumlResource.setDescription(tfResource.getDescription());
        pumlResource.setParent(tfResource.getParent());
        return pumlResource;
    }

    // see https://github.com/awslabs/aws-icons-for-plantuml/blob/main/AWSSymbols.md for include directive.
    private static final Map<String, String> tfResourceTypeToPumlMacro = Map.ofEntries(
            entry("aws_instance", "EC2"),
            entry("aws_internet_gateway", "VPCInternetGateway"),
            entry("aws_lb", "ElasticLoadBalancing"),
            entry("aws_nat_gateway", "VPCNATGateway"),
            entry("aws_s3_bucket", "SimpleStorageService"),
            entry("aws_subnet", "PublicSubnetGroup"),
            entry("aws_vpc", "VPCGroup")
    );

    // see https://github.com/awslabs/aws-icons-for-plantuml/blob/main/AWSSymbols.md for include directive.
    private static final Map<String, String> pumlMacroToIncludeFileName = Map.ofEntries(
            entry("EC2", "<awslib/Compute/EC2>"),
            entry("VPCInternetGateway", "<awslib/NetworkingContentDelivery/VPCInternetGateway>"),
            entry("ElasticLoadBalancing", "<awslib/NetworkingContentDelivery/ElasticLoadBalancing>"),
            entry("VPCNATGateway", "<awslib/NetworkingContentDelivery/VPCNATGateway>"),
            entry("SimpleStorageService", "<awslib/Storage/SimpleStorageService>"),
            entry("PublicSubnetGroup", "<awslib/Groups/PublicSubnet>"),
            entry("VPCGroup", "<awslib/Groups/VPC>")
    );

    private static String getPumlMacro(String tfResource) {
        return tfResourceTypeToPumlMacro.get(tfResource);
    }
}
