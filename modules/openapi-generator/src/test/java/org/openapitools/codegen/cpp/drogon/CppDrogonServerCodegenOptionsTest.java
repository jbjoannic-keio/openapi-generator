package org.openapitools.codegen.cpp.drogon;

import org.openapitools.codegen.AbstractOptionsTest;
import org.openapitools.codegen.CodegenConfig;
import org.openapitools.codegen.languages.CppDrogonServerCodegen;
import org.openapitools.codegen.options.CppDrogonServerCodegenOptionsProvider;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class CppDrogonServerCodegenOptionsTest extends AbstractOptionsTest {
    private CppDrogonServerCodegen codegen = mock(CppDrogonServerCodegen.class, mockSettings);

    public CppDrogonServerCodegenOptionsTest() {
        super(new CppDrogonServerCodegenOptionsProvider());
    }

    @Override
    protected CodegenConfig getCodegenConfig() {
        return codegen;
    }

    @SuppressWarnings("unused")
    @Override
    protected void verifyOptions() {
        // TODO: Complete options using Mockito
        // verify(codegen).someMethod(arguments)
    }
}

