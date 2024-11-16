package com.t_horie.terra_puml.application.domain.service.parser;

import com.t_horie.terra_puml.application.service.parser.TerraformLexer;
import com.t_horie.terra_puml.application.service.parser.TerraformParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TerraPumlVisitorTest {

    @Test
    void test_ファイルをパースしてvisitできる() {
        var path = "/ec2_only/input/main.tf";
        try (InputStream is = getClass().getResourceAsStream(path)) {
            // arrange
            var lexer = new TerraformLexer(CharStreams.fromStream(is));
            var parser = new TerraformParser(new CommonTokenStream(lexer));
            parser.setBuildParseTree(true);
            ParseTree tree = parser.file_();
            var visitor = new TerraPumlVisitor();

            // act
            visitor.visit(tree);

            // assert
            var awsPlantUml = visitor.getAwsPlantUmls().get(0);
            assertEquals("aws_instance", awsPlantUml.getResourceType());
            assertEquals("web", awsPlantUml.getAlias());
            assertEquals("WebServer", awsPlantUml.getLabel());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
