package com.t_horie.tf2puml.application.domain.service;

import com.t_horie.tf2puml.application.domain.model.AwsPumlResource;
import com.t_horie.tf2puml.application.domain.model.AwsTfResource;
import com.t_horie.tf2puml.application.domain.service.parser.TerraPumlVisitor;
import com.t_horie.tf2puml.application.port.in.GeneratePlantUmlUseCase;
import com.t_horie.tf2puml.application.service.parser.TerraformLexer;
import com.t_horie.tf2puml.application.service.parser.TerraformParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class PlantUmlGenerator implements GeneratePlantUmlUseCase {
    /**
     * @inheritDoc
     */
    @Override
    public String generateFromTerraform(File path, Optional<File> layoutPath) throws IOException {
        /*
         * 引数のチェック
         */
        if (!path.exists()) {
            // 指定したファイルが存在しない場合
            throw new IOException("File not found: %s".formatted(path));
        }

        if (layoutPath.isPresent()) {
            if (!layoutPath.get().exists()) {
                // 指定したファイルが存在しない場合
                throw new IOException("File not found: %s".formatted(layoutPath.get()));
            } else if (!layoutPath.get().isFile()) {
                // 指定したファイルがディレクトリの場合
                throw new IOException("Not a file: %s".formatted(layoutPath.get()));
            }
        }

        /*
         * tfファイルを読み込み、AWSリソースを取得する
         */
        var tfFiles = path.isFile() ? List.of(path) : FileUtils.listFiles(path, new String[]{"tf"}, false);
        List<AwsTfResource> awsTfResources = tfFiles.stream()
                .flatMap(file -> {
                    try (FileInputStream is = new FileInputStream(file)) {
                        var lexer = new TerraformLexer(CharStreams.fromStream(is));
                        var parser = new TerraformParser(new CommonTokenStream(lexer));
                        parser.setBuildParseTree(true);
                        var tree = parser.file_();
                        var visitor = new TerraPumlVisitor();
                        visitor.visit(tree);
                        return visitor.getAwsTfResources().stream();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .sorted(new Comparator<AwsTfResource>() {
                    @Override
                    public int compare(AwsTfResource o1, AwsTfResource o2) {
                        return o1.getAlias().compareTo(o2.getAlias());
                    }
                })
                .collect(Collectors.toList());

        /*
         * pumlモデルに変換する
         */
        List<AwsPumlResource> awsPumlResources = awsTfResources.stream()
                .map(AwsPumlResource::fromTfResource)
                .toList();

        /*
         * PlantUMLのテキストを生成する
         */
        var sb = new StringBuilder();
        appendStart(sb);
        appendHeaders(sb, awsPumlResources.stream()
                .map(AwsPumlResource::getHeaderFile)
                .collect(Collectors.toCollection(TreeSet::new)));
        appendResource(sb, awsTfResources);
        if (layoutPath.isPresent()) {
            sb.append(FileUtils.readFileToString(layoutPath.get(), "UTF-8"));
        }
        appendEnd(sb);

        return sb.toString();
    }

    public void appendHeaders(StringBuilder sb, Set<String> headerFiles) {
        sb.append("!include <awslib/AWSCommon>\n");

        for (var headerFile : headerFiles) {
            sb.append("!include %s\n".formatted(headerFile));
        }
    }

    public void appendHeader(StringBuilder sb, Set<String> resourceTypes) {
        sb.append("!include <awslib/AWSCommon>\n");

        for (var resourceType : resourceTypes) {
            // see https://github.com/awslabs/aws-icons-for-plantuml/blob/main/AWSSymbols.md for include directive.
            switch (resourceType) {
                case "aws_instance":
                    sb.append("!include <awslib/Compute/EC2>\n");
                    break;
                case "aws_internet_gateway":
                    sb.append("!include <awslib/NetworkingContentDelivery/VPCInternetGateway>\n");
                    break;
                case "aws_lb":
                    sb.append("!include <awslib/NetworkingContentDelivery/ElasticLoadBalancing>\n");
                    break;
                case "aws_nat_gateway":
                    sb.append("!include <awslib/NetworkingContentDelivery/VPCNATGateway>\n");
                    break;
                case "aws_s3_bucket":
                    sb.append("!include <awslib/Storage/SimpleStorageService>\n");
                    break;
                case "aws_subnet":
                    sb.append("!include <awslib/Groups/PublicSubnet>\n");
                    break;
                case "aws_vpc":
                    sb.append("!include <awslib/Groups/VPC>\n");
                    break;

            }
        }
    }

    public void appendResource(StringBuilder sb, List<AwsTfResource> awsTfResources) {
        for (var awsPlantUml : awsTfResources) {
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
                case "aws_internet_gateway":
                    sb.append("VPCInternetGateway(%s, \"%s\", \"%s\"".formatted(
                            awsPlantUml.getAlias(),
                            awsPlantUml.getLabel(),
                            awsPlantUml.getTf2pumlTechnology()));
                    if (!awsPlantUml.getDescription().isEmpty()) {
                        sb.append(", \"%s\"".formatted(awsPlantUml.getDescription()));
                    }
                    sb.append(")\n");
                    break;
                case "aws_lb":
                    sb.append("ElasticLoadBalancing(%s, \"%s\", \"%s\"".formatted(
                            awsPlantUml.getAlias(),
                            awsPlantUml.getLabel(),
                            awsPlantUml.getTf2pumlTechnology()));
                    if (!awsPlantUml.getDescription().isEmpty()) {
                        sb.append(", \"%s\"".formatted(awsPlantUml.getDescription()));
                    }
                    sb.append(")\n");
                    break;
                case "aws_nat_gateway":
                    sb.append("VPCNATGateway(%s, \"%s\", \"%s\"".formatted(
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
                case "aws_subnet":
                    sb.append("PublicSubnetGroup(%s, \"%s\"".formatted(
                            awsPlantUml.getAlias(),
                            awsPlantUml.getLabel()));
                    if (!awsPlantUml.getDescription().isEmpty()) {
                        sb.append(", \"%s\"".formatted(awsPlantUml.getDescription()));
                    }
                    sb.append(")\n");
                    break;
                case "aws_vpc":
                    sb.append("VPCGroup(%s, \"%s\"".formatted(
                            awsPlantUml.getAlias(),
                            awsPlantUml.getLabel()));
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
