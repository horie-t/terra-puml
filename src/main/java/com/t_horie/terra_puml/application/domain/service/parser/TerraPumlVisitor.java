package com.t_horie.terra_puml.application.domain.service.parser;

import com.t_horie.terra_puml.application.service.parser.TerraformBaseVisitor;
import com.t_horie.terra_puml.application.service.parser.TerraformParser;

public class TerraPumlVisitor extends TerraformBaseVisitor<Void> {
    @Override
    public Void visitResourcetype(TerraformParser.ResourcetypeContext ctx) {
        System.out.println("visitResourcetype: " + ctx.getText());
        return super.visitResourcetype(ctx);
    }
}
