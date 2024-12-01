package com.t_horie.tf2puml.application.port.in;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

/**
 * PlantUMLを生成するUseCase
 */
public interface GeneratePlantUmlUseCase {
     /**
      * TerraformのファイルからPlantUMLを生成する
      *
      * @param path       Terraformのファイル
      * @param layoutPath
      * @return
      * @throws IOException
      */
     String generateFromTerraform(File path, Optional<File> layoutPath) throws IOException;
}
