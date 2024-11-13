package com.t_horie.terra_puml.application.domain.service;

import com.t_horie.terra_puml.application.domain.service.parser.TerraPumlListener;
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

@Component
public class PlantUmlGenerator implements GeneratePlantUmlUse {
    @Override
    public String generateFromTerraform(File path) throws IOException {
        var is = new FileInputStream(path);
        var lexer = new TerraformLexer(CharStreams.fromStream(is));
        var parser = new TerraformParser(new CommonTokenStream(lexer));
        ParseTreeWalker.DEFAULT.walk(new TerraPumlListener(), parser.file_());

        var sb = new StringBuilder();
        appendStart(sb);
        appendHeader(sb);
        appendResource(sb);
        appendEnd(sb);

        return sb.toString();
    }

    public void appendHeader(StringBuilder sb) {
        sb.append("!include <awslib/AWSCommon>\n");
        sb.append("!include <awslib/Compute/EC2>\n");
    }

    public void appendResource(StringBuilder sb) {
        sb.append("EC2(web, \"Web Server\", \"PHP and Apache\", \"Frontend server\")\n");
    }

    private void appendStart(StringBuilder sb) {
        sb.append("@startuml\n");
    }

    private void appendEnd(StringBuilder sb) {
        sb.append("@enduml\n");
    }
}
