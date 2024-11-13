package com.t_horie.terra_puml.adapter.in.cli;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.FileWriter;

@Component
public class TerraPumlRunner implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (args.containsOption("from")) {
            System.out.println("from: " + args.getOptionValues("from").get(0));
        }
        if (args.containsOption("to")) {
            var outputPath = args.getOptionValues("to").get(0);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
                writer.write("""
                        @startuml
                        !include <awslib/AWSCommon>
                        !include <awslib/Compute/EC2>
                        
                        EC2(web, "Web Server", "PHP and Apache", "Frontend server")
                        @enduml
                        """);
            }
        }
    }
}
