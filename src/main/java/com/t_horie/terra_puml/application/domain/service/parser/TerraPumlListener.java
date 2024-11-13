package com.t_horie.terra_puml.application.domain.service.parser;

import com.t_horie.terra_puml.application.service.parser.TerraformBaseListener;
import com.t_horie.terra_puml.application.service.parser.TerraformParser;

public class TerraPumlListener extends TerraformBaseListener {
    @Override
    public void enterResourcetype(TerraformParser.ResourcetypeContext ctx) {
        System.out.println("enterResourcetype: " + ctx.getText());
    }

    @Override
    public void enterName(TerraformParser.NameContext ctx) {
        System.out.println("enterName: " + ctx.getText());
    }
}
