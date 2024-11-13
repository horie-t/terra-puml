package com.t_horie.terra_puml.application.port.in;

import java.io.File;
import java.io.IOException;

public interface GeneratePlantUmlUse {
     String generateFromTerraform(File path) throws IOException;
}
