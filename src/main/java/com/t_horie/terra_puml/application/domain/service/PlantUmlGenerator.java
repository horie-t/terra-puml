package com.t_horie.terra_puml.application.domain.service;

import com.t_horie.terra_puml.application.port.in.GeneratePlantUmlUse;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class PlantUmlGenerator implements GeneratePlantUmlUse {
    @Override
    public String generateFromTerraform(File path) {
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
