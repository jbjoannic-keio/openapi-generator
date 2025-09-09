package org.openapitools.codegen.languages;

import org.openapitools.codegen.*;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.MapProperty;
import io.swagger.models.properties.Property;
import io.swagger.models.parameters.Parameter;

import java.io.File;
import java.util.*;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CppDrogonClientCodegen extends DefaultCodegen implements CodegenConfig {
    public static final String PROJECT_NAME = "projectName";

    private final Logger LOGGER = LoggerFactory.getLogger(CppDrogonClientCodegen.class);

    public CodegenType getTag() {
        return CodegenType.CLIENT;
    }

    public String getName() {
        return "cpp-drogon";
    }

    public String getHelp() {
        return "Generates a cpp-drogon client.";
    }

    public CppDrogonClientCodegen() {
        super();

        outputFolder = "generated-code" + File.separator + "cpp-drogon";
        modelTemplateFiles.put("model.mustache", ".zz");
        apiTemplateFiles.put("api.mustache", ".zz");
        embeddedTemplateDir = templateDir = "cpp-drogon";
        apiPackage = "Apis";
        modelPackage = "Models";
        supportingFiles.add(new SupportingFile("README.mustache", "", "README.md"));
        // TODO: Fill this out.
    }
}
