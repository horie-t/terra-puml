package com.t_horie.terra_puml.application.domain.service.parser;

import com.t_horie.terra_puml.application.service.parser.TerraformLexer;
import com.t_horie.terra_puml.application.service.parser.TerraformParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

public class TerraPumlVisitorTest {

    @Test
    void test_ファイルをパースしてvisitできる() {
        var path = "/ec2_only/main.tf";
        try (InputStream is = getClass().getResourceAsStream(path)) {
            var lexer = new TerraformLexer(CharStreams.fromStream(is));
            var parser = new TerraformParser(new CommonTokenStream(lexer));
            parser.setBuildParseTree(true);
            ParseTree tree = parser.file_();

            // TODO: 動作確認できたら削除する
            //System.out.println(tree.toStringTree(parser));

            var visitor = new TerraPumlVisitor();
            visitor.visit(tree);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
