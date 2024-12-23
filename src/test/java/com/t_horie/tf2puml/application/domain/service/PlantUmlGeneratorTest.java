package com.t_horie.tf2puml.application.domain.service;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlantUmlGeneratorTest {
    private final static String BASE_PATH = "src/test/resources/";
    private final static Path targetBasePath = Path.of("target/test/resources/");

    PlantUmlGenerator sut = new PlantUmlGenerator();
    @Test
    void test_ファイルをパースして単一のAWSリソースのPlantUMLファイルを生成できる() throws IOException {
        // arrange
        var inputFile = new File(BASE_PATH + "ec2_only/input/main.tf");
        // act
        var actual = sut.generateFromTerraform(inputFile, Optional.empty());
        writeToFile("ec2_only/actual/main.puml", actual);
        // assert
        var expected = Files.readString(Path.of(BASE_PATH + "ec2_only/expect/main.puml"));
        assertEquals(expected, actual);
    }

    @Test
    void test_ファイルが存在しない場合は例外が発生する() {
        // arrange
        var inputFile = new File(BASE_PATH + "ec2_only/input/not_found.tf");
        // act & assert
        var exception = org.junit.jupiter.api.Assertions.assertThrows(IOException.class, () -> {
            sut.generateFromTerraform(inputFile, Optional.empty());
        });
        assertEquals("File not found: src/test/resources/ec2_only/input/not_found.tf", exception.getMessage());
    }

    @Test
    void test_ファイルをパースして複数のAWSリソースのPlantUMLファイルを生成できる() throws IOException {
        // arrange
        var inputFile = new File(BASE_PATH + "ec2_s3/input/main.tf");
        // act
        var actual = sut.generateFromTerraform(inputFile, Optional.empty());
        writeToFile("ec2_s3/actual/main.puml", actual);
        // assert
        assertEquals(Files.readString(Path.of(BASE_PATH + "ec2_s3/expect/main.puml")), actual);
    }

    @Test
    void test_asの指定がないリソースは生成されない() throws IOException {
        // arrange
        var inputFile = new File(BASE_PATH + "vpc_network/input/main.tf");
        // act
        var actual = sut.generateFromTerraform(inputFile, Optional.empty());
        writeToFile("vpc_network/actual/main.puml", actual);
        // assert
        assertEquals(Files.readString(Path.of(BASE_PATH + "vpc_network/expect/main.puml")), actual);
    }

    @Test
    void test_ファイルがディレクトリの場合はディレクトリ内のtfファイルをすべてパースしてPlantUMLファイルを生成できる() throws IOException {
        // arrange
        var inputDir = new File(BASE_PATH + "multiple_tf/input");
        // act
        var actual = sut.generateFromTerraform(inputDir, Optional.empty());
        writeToFile("multiple_tf/actual/main.puml", actual);
        // assert
        assertEquals(Files.readString(Path.of(BASE_PATH + "multiple_tf/expect/main.puml")), actual);
    }

    @Test
    void test_layoutファイルが指定された場合はファイルの内容が追加される() throws IOException {
        // arrange
        var inputFile = new File(BASE_PATH + "ec2_s3/input/main.tf");
        var layoutFile = new File(BASE_PATH + "ec2_s3/input/layout.puml");
        // act
        var actual = sut.generateFromTerraform(inputFile, Optional.of(layoutFile));
        writeToFile("ec2_s3/actual/main_with_layout.puml", actual);
        // assert
        assertEquals(Files.readString(Path.of(BASE_PATH + "ec2_s3/expect/main_with_layout.puml")), actual);
    }

    @Test
    void test_CLBのリソースが生成される() throws IOException {
        // arrange
        var inputFile = new File(BASE_PATH + "classic_lb/input/main.tf");
        var layoutFile = new File(BASE_PATH + "classic_lb/input/layout.puml");
        // act
        var actual = sut.generateFromTerraform(inputFile, Optional.of(layoutFile));
        writeToFile("classic_lb/actual/main.puml", actual);
        // assert
        assertEquals(Files.readString(Path.of(BASE_PATH + "classic_lb/expect/main.puml")), actual);
    }

    @Test
    void test_layoutファイルが存在しない場合は例外が発生する() {
        // arrange
        var inputFile = new File(BASE_PATH + "ec2_s3/input/main.tf");
        var layoutFile = new File(BASE_PATH + "ec2_s3/input/not_found.puml");
        // act & assert
        var exception = org.junit.jupiter.api.Assertions.assertThrows(IOException.class, () -> {
            sut.generateFromTerraform(inputFile, Optional.of(layoutFile));
        });
        assertEquals("File not found: src/test/resources/ec2_s3/input/not_found.puml", exception.getMessage());
    }

    @Test
    void test_layoutファイルがディレクトリの場合は例外が発生する() {
        // arrange
        var inputFile = new File(BASE_PATH + "ec2_s3/input/main.tf");
        var layoutFile = new File(BASE_PATH + "ec2_s3/input");
        // act & assert
        var exception = org.junit.jupiter.api.Assertions.assertThrows(IOException.class, () -> {
            sut.generateFromTerraform(inputFile, Optional.of(layoutFile));
        });
        assertEquals("Not a file: src/test/resources/ec2_s3/input", exception.getMessage());
    }

    void writeToFile(String path, String content) throws IOException {
        var targetPath = targetBasePath.resolve(path);
        Files.createDirectories(targetPath.getParent());
        Files.writeString(targetPath, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }
}
