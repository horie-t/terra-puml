package com.t_horie.terra_puml.application.domain.service;

import com.t_horie.terra_puml.application.domain.model.AwsPlantUml;
import com.t_horie.terra_puml.application.domain.service.parser.TerraPumlListener;
import com.t_horie.terra_puml.application.domain.service.parser.TerraPumlVisitor;
import com.t_horie.terra_puml.application.port.in.GeneratePlantUmlUse;
import com.t_horie.terra_puml.application.service.parser.TerraformLexer;
import com.t_horie.terra_puml.application.service.parser.TerraformParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class PlantUmlGenerator implements GeneratePlantUmlUse {
    @Override
    public String generateFromTerraform(File path) throws IOException {
        var is = new FileInputStream(path);
        var lexer = new TerraformLexer(CharStreams.fromStream(is));
        var parser = new TerraformParser(new CommonTokenStream(lexer));
        parser.setBuildParseTree(true);
        var tree = parser.file_();
        var visitor = new TerraPumlVisitor();
        visitor.visit(tree);

        var sb = new StringBuilder();
        appendStart(sb);
        appendHeader(sb, visitor.getAwsPlantUmls().stream()
                .map(AwsPlantUml::getResourceType)
                .collect(Collectors.toSet()));
        appendResource(sb, visitor.getAwsPlantUmls());
        appendEnd(sb);

        return sb.toString();
    }

    public void appendHeader(StringBuilder sb, Set<String> resourceTypes) {
        sb.append("!include <awslib/AWSCommon>\n");

        for (var resourceType : resourceTypes) {
            // see https://github.com/awslabs/aws-icons-for-plantuml/blob/main/AWSSymbols.md for include directive.
            switch (resourceType) {
                case "aws_instance":
                    sb.append("!include <awslib/Compute/EC2>\n");
                    break;
                case "aws_s3_bucket":
                    sb.append("!include <awslib/Storage/SimpleStorageService>\n");
                    break;
            }
        }
    }

    public void appendResource(StringBuilder sb, List<AwsPlantUml> awsPlantUmls) {
        for (var awsPlantUml : awsPlantUmls) {
            switch (awsPlantUml.getResourceType()) {
                case "aws_instance":
                    sb.append("EC2(%s, \"%s\", \"%s\"".formatted(
                            awsPlantUml.getAlias(),
                            awsPlantUml.getLabel(),
                            awsPlantUml.getTf2pumlTechnology()));
                    if (!awsPlantUml.getDescription().isEmpty()) {
                        sb.append(", \"%s\"".formatted(awsPlantUml.getDescription()));
                    }
                    sb.append(")\n");
                    break;
                case "aws_s3_bucket":
                    sb.append("SimpleStorageService(%s, \"%s\", \"%s\"".formatted(
                            awsPlantUml.getAlias(),
                            awsPlantUml.getLabel(),
                            awsPlantUml.getTf2pumlTechnology()));
                    if (!awsPlantUml.getDescription().isEmpty()) {
                        sb.append(", \"%s\"".formatted(awsPlantUml.getDescription()));
                    }
                    sb.append(")\n");
                    break;
            }
        }
    }

    private void appendStart(StringBuilder sb) {
        sb.append("@startuml\n");
    }

    private void appendEnd(StringBuilder sb) {
        sb.append("@enduml\n");
    }
}
