package org.openapitools.codegen.cpp.drogon;

import org.openapitools.codegen.*;
import org.openapitools.codegen.languages.CppDrogonServerCodegen;
import io.swagger.models.*;
import io.swagger.models.properties.*;

import org.testng.Assert;
import org.testng.annotations.Test;

@SuppressWarnings("static-method")
public class CppDrogonServerCodegenModelTest {

    @Test(description = "convert a simple java model")
    public void simpleModelTest() {
        final Model model = new ModelImpl()
                .description("a sample model")
                .property("id", new LongProperty())
                .property("name", new StringProperty())
                .required("id")
                .required("name");
        final DefaultCodegen codegen = new CppDrogonServerCodegen();

        // TODO: Complete this test.
        Assert.fail("Not implemented.");
    }

}

