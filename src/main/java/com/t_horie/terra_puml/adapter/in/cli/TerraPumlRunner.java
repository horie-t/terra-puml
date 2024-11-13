package com.t_horie.terra_puml.adapter.in.cli;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class TerraPumlRunner implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (args.containsOption("from")) {
            System.out.println("from: " + args.getOptionValues("from").get(0));
        }
        if (args.containsOption("to")) {
            System.out.println("to: " + args.getOptionValues("to").get(0));
        }
    }
}
