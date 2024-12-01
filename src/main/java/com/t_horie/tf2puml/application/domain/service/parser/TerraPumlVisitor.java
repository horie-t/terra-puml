package com.t_horie.tf2puml.application.domain.service.parser;

import com.t_horie.tf2puml.application.domain.model.AwsPlantUml;
import com.t_horie.tf2puml.application.service.parser.TerraformBaseVisitor;
import com.t_horie.tf2puml.application.service.parser.TerraformParser;
import lombok.Getter;
import org.antlr.v4.runtime.RuleContext;

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

        if (! currentAwsPlantUml.getAlias().isEmpty()) {
            awsPlantUmls.add(currentAwsPlantUml);
        }
        currentAwsPlantUml = null;
        return null;
    }

    @Override
    public Void visitResourcetype(TerraformParser.ResourcetypeContext ctx) {
        currentAwsPlantUml.setResourceType(getTextNoDoubleQuote(ctx));
        return null;
    }

    @Override
    public Void visitArgument(TerraformParser.ArgumentContext ctx) {
        switch (getTextNoDoubleQuote(ctx.identifier())) {
            case "tags":
                visitExpression(ctx.expression());
                break;
            case "Name":
                currentAwsPlantUml.setLabel(getTextNoDoubleQuote(ctx.expression()));
                break;
            case "tf2puml:as":
                currentAwsPlantUml.setAlias(getTextNoDoubleQuote(ctx.expression()));
                break;
        }

        return null;
    }

    private String getTextNoDoubleQuote(RuleContext ctx) {
        return ctx.getText().replaceAll("\"", "");
    }

    /*
     * Ignore Section: below definitions are not used in this project
     */
    @Override
    public Void visitTerraform(TerraformParser.TerraformContext ctx) {
        return null;
    }

    @Override
    public Void visitData(TerraformParser.DataContext ctx) {
        return null;
    }

    @Override
    public Void visitProvider(TerraformParser.ProviderContext ctx) {
        return null;
    }

    @Override
    public Void visitOutput(TerraformParser.OutputContext ctx) {
        return null;
    }
}
