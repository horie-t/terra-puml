package com.t_horie.terra_puml.application.service;

import com.t_horie.terra_puml.application.domain.service.PlantUmlGenerator;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlantUmlGeneratorTest {
    PlantUmlGenerator sut = new PlantUmlGenerator();
    @Test
    void test_ファイルをパースしてPlantUMLを生成できる() throws IOException {
        // arrange
        var inputFile = new File("src/test/resources/ec2_only/input/main.tf");
        // act
        var actual = sut.generateFromTerraform(inputFile);
        // assert
        assertEquals(Files.readString(Path.of("src/test/resources/ec2_only/expect/main.puml")), actual);
    }
}
