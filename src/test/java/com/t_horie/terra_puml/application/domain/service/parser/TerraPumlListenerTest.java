package com.t_horie.terra_puml.application.domain.service.parser;

import com.t_horie.terra_puml.application.service.parser.TerraformLexer;
import com.t_horie.terra_puml.application.service.parser.TerraformParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

public class TerraPumlListenerTest {

    @Test
    void test_ファイルをパースしてwalkできる() {
        var path = "/ec2_only/input/main.tf";
        try (InputStream is = getClass().getResourceAsStream(path)) {
            var lexer = new TerraformLexer(CharStreams.fromStream(is));
            var parser = new TerraformParser(new CommonTokenStream(lexer));
            ParseTreeWalker.DEFAULT.walk(new TerraPumlListener(), parser.file_());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
