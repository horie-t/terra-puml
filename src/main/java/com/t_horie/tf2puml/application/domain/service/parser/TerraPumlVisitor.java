package com.t_horie.tf2puml.application.domain.service.parser;

import com.t_horie.tf2puml.application.domain.model.AwsTfResource;
import com.t_horie.tf2puml.application.service.parser.TerraformBaseVisitor;
import com.t_horie.tf2puml.application.service.parser.TerraformParser;
import lombok.Getter;
import org.antlr.v4.runtime.RuleContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TerraPumlVisitor extends TerraformBaseVisitor<Void> {
    @Getter
    private List<AwsTfResource> awsTfResources = new ArrayList<>();
    private AwsTfResource currentAwsTfResource = null;

    @Override
    public Void visitResource(TerraformParser.ResourceContext ctx) {
        currentAwsTfResource = new AwsTfResource();

        visitResourcetype(ctx.resourcetype());
        visitBlockbody(ctx.blockbody());

        if (! currentAwsTfResource.getAlias().isEmpty()) {
            awsTfResources.add(currentAwsTfResource);
        }
        currentAwsTfResource = null;
        return null;
    }

    @Override
    public Void visitResourcetype(TerraformParser.ResourcetypeContext ctx) {
        currentAwsTfResource.setResourceType(getTextNoDoubleQuote(ctx));
        return null;
    }

    @Override
    public Void visitArgument(TerraformParser.ArgumentContext ctx) {
        switch (getTextNoDoubleQuote(ctx.identifier())) {
            case "tags":
                visitExpression(ctx.expression());
                break;
            case "Name":
                currentAwsTfResource.setLabel(getTextNoDoubleQuote(ctx.expression()));
                break;
            case "tf2puml:as":
                currentAwsTfResource.setAlias(getTextNoDoubleQuote(ctx.expression()));
                break;
            case "tf2puml:technology":
                currentAwsTfResource.setTf2pumlTechnology(getTextNoDoubleQuote(ctx.expression()));
                break;
            case "tf2puml:parent":
                currentAwsTfResource.setParent(Optional.of(getTextNoDoubleQuote(ctx.expression())));
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
