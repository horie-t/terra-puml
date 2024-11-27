package com.t_horie.terra_puml.application.port.in;

import java.io.File;
import java.io.IOException;

/**
 * PlantUMLを生成するUseCase
 */
public interface GeneratePlantUmlUseCase {
     /**
      * TerraformのファイルからPlantUMLを生成する
      * @param path Terraformのファイル
      * @return
      * @throws IOException
      */
     String generateFromTerraform(File path) throws IOException;
}
