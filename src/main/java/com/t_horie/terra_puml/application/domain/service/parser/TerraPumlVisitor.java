package com.t_horie.terra_puml.application.domain.service.parser;

import com.t_horie.terra_puml.application.domain.model.AwsPlantUml;
import com.t_horie.terra_puml.application.service.parser.TerraformBaseVisitor;
import com.t_horie.terra_puml.application.service.parser.TerraformParser;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class TerraPumlVisitor extends TerraformBaseVisitor<Void> {
    @Getter
    private List<AwsPlantUml> awsPlantUmls = new ArrayList<>();
    private AwsPlantUml currentAwsPlantUml = null;

    @Override
    public Void visitResource(TerraformParser.ResourceContext ctx) {
        currentAwsPlantUml = new AwsPlantUml();

        visitResourcetype(ctx.resourcetype());
        visitBlockbody(ctx.blockbody());

        awsPlantUmls.add(currentAwsPlantUml);
        currentAwsPlantUml = null;
        return null;
    }

    @Override
    public Void visitResourcetype(TerraformParser.ResourcetypeContext ctx) {
        currentAwsPlantUml.setResourceType(ctx.getText().replaceAll("\"", ""));
        return null;
    }

    @Override
    public Void visitArgument(TerraformParser.ArgumentContext ctx) {
        switch (ctx.identifier().getText()) {
            case "tags":
                visitExpression(ctx.expression());
                break;
            case "Name":
                currentAwsPlantUml.setLabel(ctx.expression().getText().replaceAll("\"", ""));
                break;
            case "asPlantUML":
                currentAwsPlantUml.setAlias(ctx.expression().getText().replaceAll("\"", ""));
                break;
        }

        return null;
    }
}
