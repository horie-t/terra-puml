package com.t_horie.terra_puml.adapter.in.cli;

import com.t_horie.terra_puml.application.port.in.GeneratePlantUmlUseCase;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Optional;

@Component
public class TerraPumlRunner implements ApplicationRunner {
    private final GeneratePlantUmlUseCase generatePlantUmlUse;

    public TerraPumlRunner(GeneratePlantUmlUseCase generatePlantUmlUse) {
        this.generatePlantUmlUse = generatePlantUmlUse;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (!args.containsOption("from") || args.getOptionValues("from").size() != 1
                || !args.containsOption("to") || args.getOptionValues("to").size() != 1) {
            System.err.println("Usage: --from=<path> --to=<path>");
        }

        var fromPath = args.getOptionValues("from").getFirst();
        var toPath = args.getOptionValues("to").getFirst();
        var layoutPath = args.getOptionValues("layout").stream().findFirst().map(File::new);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(toPath))) {
            writer.write(generatePlantUmlUse.generateFromTerraform(new File(fromPath), layoutPath));
        }
    }
}
