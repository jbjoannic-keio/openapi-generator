package org.openapitools.codegen.languages;

import static org.openapitools.codegen.utils.StringUtils.underscore;


import org.openapitools.codegen.*;
import org.openapitools.codegen.CodegenModel;
import org.openapitools.codegen.CodegenOperation;
import org.openapitools.codegen.CodegenParameter;
import org.openapitools.codegen.CodegenProperty;
import org.openapitools.codegen.CodegenType;
import org.openapitools.codegen.SupportingFile;
import org.openapitools.codegen.meta.features.DocumentationFeature;
import org.openapitools.codegen.meta.features.GlobalFeature;
import org.openapitools.codegen.meta.features.ParameterFeature;
import org.openapitools.codegen.meta.features.SchemaSupportFeature;
import org.openapitools.codegen.meta.features.SecurityFeature;
import org.openapitools.codegen.model.ModelMap;
import org.openapitools.codegen.model.OperationMap;
import org.openapitools.codegen.model.OperationsMap;
import org.openapitools.codegen.utils.ModelUtils;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.MapProperty;
import io.swagger.models.properties.Property;
import io.swagger.models.parameters.Parameter;

import java.io.File;
import java.util.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.servers.Server;

public class CppDrogonClientCodegen extends AbstractCppCodegen {
    public static final String PROJECT_NAME = "projectName";

    private final Logger LOGGER = LoggerFactory.getLogger(CppDrogonClientCodegen.class);

    protected boolean isAddExternalLibs = true;
    protected boolean isUseStructModel = false;
    protected String serviceName = "DefaultService";
    public static final String OPTIONAL_EXTERNAL_LIB = "addExternalLibs";
    public static final String OPTIONAL_EXTERNAL_LIB_DESC = "Add the Possibility to fetch and compile external Libraries needed by this Framework.";
    public static final String OPTION_USE_STRUCT_MODEL = "useStructModel";
    public static final String OPTION_USE_STRUCT_MODEL_DESC = "Use struct-based model template instead of get/set-based model template";
    public static final String OPTION_SERVICE_NAME = "serviceName";
    public static final String OPTION_SERVICE_NAME_DESC = "Service name to be used in the client class name. Default to 'DefaultService'.";
    public static final String HELPERS_PACKAGE_NAME = "helpersPackage";
    public static final String HELPERS_PACKAGE_NAME_DESC = "Specify the package name to be used for the helpers (e.g. org.openapitools.client.helpers).";
    protected final String PREFIX = "";
    protected String helpersPackage = "";

    /**
     * OpenApi types that shouldn't have a namespace added with getTypeDeclaration()
     * at generation time (for json::json)
     */
    private final Set<String> openAPITypesWithoutModelNamespace = new HashSet<>();

    /**
     * Http body type drogon mapping
     */
    private static final Map<String, String> DROGON_CONTENT_TYPE_MAPPING = new HashMap<>();
    static {
        DROGON_CONTENT_TYPE_MAPPING.put("application/json", "CT_APPLICATION_JSON");
        DROGON_CONTENT_TYPE_MAPPING.put("application/xml", "CT_APPLICATION_XML");
        DROGON_CONTENT_TYPE_MAPPING.put("text/plain", "CT_TEXT_PLAIN");
        DROGON_CONTENT_TYPE_MAPPING.put("text/html", "CT_TEXT_HTML");
        DROGON_CONTENT_TYPE_MAPPING.put("text/css", "CT_TEXT_CSS");
        DROGON_CONTENT_TYPE_MAPPING.put("text/csv", "CT_TEXT_CSV");
        DROGON_CONTENT_TYPE_MAPPING.put("application/octet-stream", "CT_APPLICATION_OCTET_STREAM");
        DROGON_CONTENT_TYPE_MAPPING.put("multipart/form-data", "CT_MULTIPART_FORM_DATA");
        DROGON_CONTENT_TYPE_MAPPING.put("application/x-www-form-urlencoded", "CT_APPLICATION_X_WWW_FORM_URLENCODED");
        DROGON_CONTENT_TYPE_MAPPING.put("image/png", "CT_IMAGE_PNG");
        DROGON_CONTENT_TYPE_MAPPING.put("image/jpeg", "CT_IMAGE_JPEG");
        DROGON_CONTENT_TYPE_MAPPING.put("image/gif", "CT_IMAGE_GIF");
        DROGON_CONTENT_TYPE_MAPPING.put("image/webp", "CT_IMAGE_WEBP");
        DROGON_CONTENT_TYPE_MAPPING.put("application/pdf", "CT_APPLICATION_PDF");
        DROGON_CONTENT_TYPE_MAPPING.put("application/zip", "CT_APPLICATION_ZIP");
        DROGON_CONTENT_TYPE_MAPPING.put("application/x-protobuf", "CT_APPLICATION_X_PROTOBUF");
        DROGON_CONTENT_TYPE_MAPPING.put("default", "CT_TEXT_PLAIN");
    }

    /**
     * HTTP status code to Drogon status code mapping
     */
    private static final Map<String, String> DROGON_STATUS_CODE_MAPPING = new HashMap<>();
    static {
        DROGON_STATUS_CODE_MAPPING.put("default", "kUnknown");
        DROGON_STATUS_CODE_MAPPING.put("0", "kUnknown");
        DROGON_STATUS_CODE_MAPPING.put("100", "k100Continue");
        DROGON_STATUS_CODE_MAPPING.put("101", "k101SwitchingProtocols");
        DROGON_STATUS_CODE_MAPPING.put("102", "k102Processing");
        DROGON_STATUS_CODE_MAPPING.put("103", "k103EarlyHints");
        DROGON_STATUS_CODE_MAPPING.put("200", "k200OK");
        DROGON_STATUS_CODE_MAPPING.put("201", "k201Created");
        DROGON_STATUS_CODE_MAPPING.put("202", "k202Accepted");
        DROGON_STATUS_CODE_MAPPING.put("203", "k203NonAuthoritativeInformation");
        DROGON_STATUS_CODE_MAPPING.put("204", "k204NoContent");
        DROGON_STATUS_CODE_MAPPING.put("205", "k205ResetContent");
        DROGON_STATUS_CODE_MAPPING.put("206", "k206PartialContent");
        DROGON_STATUS_CODE_MAPPING.put("207", "k207MultiStatus");
        DROGON_STATUS_CODE_MAPPING.put("208", "k208AlreadyReported");
        DROGON_STATUS_CODE_MAPPING.put("226", "k226IMUsed");
        DROGON_STATUS_CODE_MAPPING.put("300", "k300MultipleChoices");
        DROGON_STATUS_CODE_MAPPING.put("301", "k301MovedPermanently");
        DROGON_STATUS_CODE_MAPPING.put("302", "k302Found");
        DROGON_STATUS_CODE_MAPPING.put("303", "k303SeeOther");
        DROGON_STATUS_CODE_MAPPING.put("304", "k304NotModified");
        DROGON_STATUS_CODE_MAPPING.put("305", "k305UseProxy");
        DROGON_STATUS_CODE_MAPPING.put("306", "k306Unused");
        DROGON_STATUS_CODE_MAPPING.put("307", "k307TemporaryRedirect");
        DROGON_STATUS_CODE_MAPPING.put("308", "k308PermanentRedirect");
        DROGON_STATUS_CODE_MAPPING.put("400", "k400BadRequest");
        DROGON_STATUS_CODE_MAPPING.put("401", "k401Unauthorized");
        DROGON_STATUS_CODE_MAPPING.put("402", "k402PaymentRequired");
        DROGON_STATUS_CODE_MAPPING.put("403", "k403Forbidden");
        DROGON_STATUS_CODE_MAPPING.put("404", "k404NotFound");
        DROGON_STATUS_CODE_MAPPING.put("405", "k405MethodNotAllowed");
        DROGON_STATUS_CODE_MAPPING.put("406", "k406NotAcceptable");
        DROGON_STATUS_CODE_MAPPING.put("407", "k407ProxyAuthenticationRequired");
        DROGON_STATUS_CODE_MAPPING.put("408", "k408RequestTimeout");
        DROGON_STATUS_CODE_MAPPING.put("409", "k409Conflict");
        DROGON_STATUS_CODE_MAPPING.put("410", "k410Gone");
        DROGON_STATUS_CODE_MAPPING.put("411", "k411LengthRequired");
        DROGON_STATUS_CODE_MAPPING.put("412", "k412PreconditionFailed");
        DROGON_STATUS_CODE_MAPPING.put("413", "k413RequestEntityTooLarge");
        DROGON_STATUS_CODE_MAPPING.put("414", "k414RequestURITooLarge");
        DROGON_STATUS_CODE_MAPPING.put("415", "k415UnsupportedMediaType");
        DROGON_STATUS_CODE_MAPPING.put("416", "k416RequestedRangeNotSatisfiable");
        DROGON_STATUS_CODE_MAPPING.put("417", "k417ExpectationFailed");
        DROGON_STATUS_CODE_MAPPING.put("418", "k418ImATeapot");
        DROGON_STATUS_CODE_MAPPING.put("421", "k421MisdirectedRequest");
        DROGON_STATUS_CODE_MAPPING.put("422", "k422UnprocessableEntity");
        DROGON_STATUS_CODE_MAPPING.put("423", "k423Locked");
        DROGON_STATUS_CODE_MAPPING.put("424", "k424FailedDependency");
        DROGON_STATUS_CODE_MAPPING.put("425", "k425TooEarly");
        DROGON_STATUS_CODE_MAPPING.put("426", "k426UpgradeRequired");
        DROGON_STATUS_CODE_MAPPING.put("428", "k428PreconditionRequired");
        DROGON_STATUS_CODE_MAPPING.put("429", "k429TooManyRequests");
        DROGON_STATUS_CODE_MAPPING.put("431", "k431RequestHeaderFieldsTooLarge");
        DROGON_STATUS_CODE_MAPPING.put("451", "k451UnavailableForLegalReasons");
        DROGON_STATUS_CODE_MAPPING.put("500", "k500InternalServerError");
        DROGON_STATUS_CODE_MAPPING.put("501", "k501NotImplemented");
        DROGON_STATUS_CODE_MAPPING.put("502", "k502BadGateway");
        DROGON_STATUS_CODE_MAPPING.put("503", "k503ServiceUnavailable");
        DROGON_STATUS_CODE_MAPPING.put("504", "k504GatewayTimeout");
        DROGON_STATUS_CODE_MAPPING.put("505", "k505HTTPVersionNotSupported");
        DROGON_STATUS_CODE_MAPPING.put("506", "k506VariantAlsoNegotiates");
        DROGON_STATUS_CODE_MAPPING.put("507", "k507InsufficientStorage");
        DROGON_STATUS_CODE_MAPPING.put("508", "k508LoopDetected");
        DROGON_STATUS_CODE_MAPPING.put("510", "k510NotExtended");
        DROGON_STATUS_CODE_MAPPING.put("511", "k511NetworkAuthenticationRequired");
    }

    /**
     * int32_t (for integer)
     */
    private static final String INT32_T = "std::int32_t";

    /**
     * int64_t (for long)
     */
    private static final String INT64_T = "std::int64_t";

    /**
     * json::json (for object, AnyType)
     */
    private static final String JSON_JSON = "Json::Value";

    /**
     * std:string (for date, DateTime, string, file, binary, UUID, URI, ByteArray)
     */
    private static final String STD_STRING = "std::string";

    /**
     * std:map (for map)
     */
    private static final String STD_MAP = "std::map";

    /**
     * std:set (for set)
     */
    private static final String STD_SET = "std::set";

    /**
     * std:vector (for array)
     */
    private static final String STD_VECTOR = "std::vector";


    @Override
    public CodegenType getTag() {
        return CodegenType.CLIENT;
    }

    @Override
    public String getName() {
        return "cpp-drogon-client";
    }

    @Override
    public String getHelp() {
        return "Generates C++ drogon clients.";
    }

    public CppDrogonClientCodegen() {
        super();


        modifyFeatureSet(features -> features
        .includeDocumentationFeatures(DocumentationFeature.Readme)
        .securityFeatures(EnumSet.noneOf(SecurityFeature.class))
        .excludeGlobalFeatures(
                GlobalFeature.XMLStructureDefinitions,
                GlobalFeature.Callbacks,
                GlobalFeature.LinkObjects,
                GlobalFeature.ParameterStyling,
                GlobalFeature.MultiServer)
        .excludeSchemaSupportFeatures(
                SchemaSupportFeature.Polymorphism)
        .excludeParameterFeatures(
                        ParameterFeature.Cookie));

        if (StringUtils.isEmpty(modelNamePrefix)) {
            modelNamePrefix = PREFIX;
        }

        helpersPackage = "org.openapitools.client.helpers";
        apiPackage = "org.openapitools.client.api";
        modelPackage = "org.openapitools.client.model";

        apiTemplateFiles.put("client-source.mustache", ".cpp");
        apiTemplateFiles.put("client-header.mustache", ".h");
        apiTemplateFiles.put("response-header.mustache", ".h");

        embeddedTemplateDir = "cpp-drogon";

        cliOptions.clear();
        addSwitch(OPTIONAL_EXTERNAL_LIB, OPTIONAL_EXTERNAL_LIB_DESC, this.isAddExternalLibs);
        addOption(HELPERS_PACKAGE_NAME, HELPERS_PACKAGE_NAME_DESC, this.helpersPackage);
        addOption(RESERVED_WORD_PREFIX_OPTION, RESERVED_WORD_PREFIX_DESC, this.reservedWordPrefix);
        addSwitch(OPTION_USE_STRUCT_MODEL, OPTION_USE_STRUCT_MODEL_DESC, this.isUseStructModel);
        addOption(OPTION_SERVICE_NAME, OPTION_SERVICE_NAME_DESC, this.serviceName);
        addOption(VARIABLE_NAME_FIRST_CHARACTER_UPPERCASE_OPTION,
                VARIABLE_NAME_FIRST_CHARACTER_UPPERCASE_DESC,
                Boolean.toString(this.variableNameFirstCharacterUppercase));

        setupSupportingFiles();

        languageSpecificPrimitives = new HashSet<>(
            Arrays.asList("int", "char", "bool", "long", "float", "double", INT32_T, INT64_T));

        typeMapping = new HashMap<>();
        typeMapping.put("date", STD_STRING);
        typeMapping.put("DateTime", STD_STRING);
        typeMapping.put("string", STD_STRING);
        typeMapping.put("integer", INT32_T);
        typeMapping.put("long", INT64_T);
        typeMapping.put("boolean", "bool");
        typeMapping.put("array", STD_VECTOR);
        typeMapping.put("map", STD_MAP);
        typeMapping.put("set", STD_SET);
        typeMapping.put("file", STD_STRING);
        typeMapping.put("object", JSON_JSON);
        typeMapping.put("binary", STD_STRING);
        typeMapping.put("number", "double");
        typeMapping.put("UUID", STD_STRING);
        typeMapping.put("URI", STD_STRING);
        typeMapping.put("ByteArray", STD_STRING);
        typeMapping.put("AnyType", JSON_JSON);

        super.importMapping = new HashMap<>();
        importMapping.put(STD_VECTOR, "#include <vector>");
        importMapping.put(STD_MAP, "#include <map>");
        importMapping.put(STD_SET, "#include <set>");
        importMapping.put(STD_STRING, "#include <string>");
        importMapping.put(JSON_JSON, "#include <json/json.h>");

        // Json::Value doesn't belong to model package
        this.openAPITypesWithoutModelNamespace.add(JSON_JSON);
    }

    private void setupSupportingFiles() {
        supportingFiles.clear();
        String serviceNameLower = serviceName.toLowerCase().replaceAll("[^a-zA-Z0-9]", "");
        String serviceFolder = serviceNameLower + "/";


        supportingFiles.add(new SupportingFile("helpers-header-client.mustache", serviceFolder + "model", modelNamePrefix + "HelpersClient.h"));
        supportingFiles.add(
            new SupportingFile("helpers-header-string.mustache", serviceFolder + "model", modelNamePrefix + "HelpersString.h"));
        supportingFiles.add(
            new SupportingFile("helpers-source-string.mustache", serviceFolder + "model", modelNamePrefix + "HelpersString.cpp"));
        supportingFiles.add(new SupportingFile("helpers-header-json.mustache", serviceFolder + "model", modelNamePrefix + "HelpersJson.h"));
        supportingFiles.add(new SupportingFile("helpers-header.mustache", serviceFolder + "model", modelNamePrefix + "Helpers.h"));
        supportingFiles.add(new SupportingFile("helpers-source.mustache", serviceFolder + "model", modelNamePrefix + "Helpers.cpp"));
        supportingFiles.add(new SupportingFile("cmake.mustache", serviceFolder, "CMakeLists.txt"));
        supportingFiles.add(new SupportingFile("README.mustache", serviceFolder, "README.md"));
    }



    @Override
    public void processOpts() {
        super.processOpts();
        if (additionalProperties.containsKey(HELPERS_PACKAGE_NAME)) {
            helpersPackage = (String) additionalProperties.get(HELPERS_PACKAGE_NAME);
        }
        if (additionalProperties.containsKey("modelNamePrefix")) {
            additionalProperties().put("prefix", modelNamePrefix);
            setupSupportingFiles();
        }
        if (additionalProperties.containsKey(RESERVED_WORD_PREFIX_OPTION)) {
            reservedWordPrefix = (String) additionalProperties.get(RESERVED_WORD_PREFIX_OPTION);
        }

        if (additionalProperties.containsKey(OPTION_SERVICE_NAME)) {
            serviceName = (String) additionalProperties.get(OPTION_SERVICE_NAME);
        }

        // Update package names to use serviceName
        String serviceNameLower = serviceName.toLowerCase().replaceAll("[^a-zA-Z0-9]", "");
        modelPackage = "org.openapitools.client." + serviceNameLower + ".model";
        apiPackage = "org.openapitools.client." + serviceNameLower + ".api";
        helpersPackage = "org.openapitools.client." + serviceNameLower + ".helpers";

        additionalProperties.put("serviceName", serviceName);
        additionalProperties.put("serviceNameLower", serviceNameLower);
        additionalProperties.put("modelNamespaceDeclarations", modelPackage.split("\\."));
        additionalProperties.put("modelNamespace", modelPackage.replaceAll("\\.", "::"));
        additionalProperties.put("apiNamespaceDeclarations", apiPackage.split("\\."));
        additionalProperties.put("apiNamespace", apiPackage.replaceAll("\\.", "::"));
        additionalProperties.put("helpersNamespaceDeclarations", helpersPackage.split("\\."));
        additionalProperties.put("helpersNamespace", helpersPackage.replaceAll("\\.", "::"));
        additionalProperties.put(RESERVED_WORD_PREFIX_OPTION, reservedWordPrefix);

        // Reconfigure supporting files with the updated serviceName
        setupSupportingFiles();

        if (additionalProperties.containsKey(OPTIONAL_EXTERNAL_LIB)) {
            setAddExternalLibs(convertPropertyToBooleanAndWriteBack(OPTIONAL_EXTERNAL_LIB));
        } else {
            additionalProperties.put(OPTIONAL_EXTERNAL_LIB, isAddExternalLibs);
        }

        setupModelTemplate();
    }

    @Override
    public void addOperationToGroup(String tag,
            String resourcePath,
            Operation operation,
            CodegenOperation op,
            Map<String, List<CodegenOperation>> operations) {

        /* Use first tag as controller name; fall back to “default” */
        String baseTag = (tag == null || tag.trim().isEmpty())
                ? "default"
                : sanitizeTag(tag);

        /* Tell the templates which controller they belong to */
        op.baseName = baseTag;

        /* Accumulate operations under the same controller */
        operations.computeIfAbsent(baseTag, k -> new ArrayList<>()).add(op);
    }

    private void setupModelTemplate() {
        if (additionalProperties.containsKey(OPTION_USE_STRUCT_MODEL))
            isUseStructModel = convertPropertyToBooleanAndWriteBack(OPTION_USE_STRUCT_MODEL);

        if (isUseStructModel) {
            LOGGER.info("Using struct-based model template");
            modelTemplateFiles.put("model-struct-header.mustache", ".h");
            modelTemplateFiles.put("model-struct-source.mustache", ".cpp");
        } else {
            LOGGER.info("Using get/set-based model template");
            modelTemplateFiles.put("model-header.mustache", ".h");
            modelTemplateFiles.put("model-source.mustache", ".cpp");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toModelImport(String name) {
        // Do not reattempt to add #include on an already solved #include
        if (name.startsWith("#include")) {
            return null;
        }

        if (importMapping.containsKey(name)) {
            return importMapping.get(name);
        } else {
            return "#include \"" + name + ".h\"";
        }
    }

    @Override
    public CodegenModel fromModel(String name, Schema model) {
        // Exchange import directives from core model with ours
        CodegenModel codegenModel = super.fromModel(name, model);

        Set<String> oldImports = codegenModel.imports;
        codegenModel.imports = new HashSet<>();

        for (String imp : oldImports) {
            String newImp = toModelImport(imp);

            if (newImp != null && !newImp.isEmpty()) {
                codegenModel.imports.add(newImp);
            }
        }

        if (!codegenModel.isEnum
                && codegenModel.anyOf.size() > 1
                && codegenModel.anyOf.contains(STD_STRING)
                && !codegenModel.anyOf.contains("AnyType")
                && codegenModel.interfaces.size() == 1) {
            codegenModel.vendorExtensions.put("x-is-string-enum-container", true);
        }
        return codegenModel;
    }

    @Override
    public CodegenOperation fromOperation(String path, String httpMethod, Operation operation, List<Server> servers) {
        CodegenOperation op = super.fromOperation(path, httpMethod, operation, servers);

        if (operation.getResponses() != null && !operation.getResponses().isEmpty()) {
            ApiResponse apiResponse = findMethodResponse(operation.getResponses());

            if (apiResponse != null) {
                Schema response = ModelUtils.getSchemaFromResponse(openAPI, apiResponse);
                if (response != null) {
                    CodegenProperty cm = fromProperty("response", response, false);
                    op.vendorExtensions.put("x-codegen-response", cm);
                    if ("HttpContent".equals(cm.dataType)) {
                        op.vendorExtensions.put("x-codegen-response-ishttpcontent", true);
                    }
                }
            }

            // Add Drogon status code mapping with support for multiple content types per
            // response
            List<Map<String, Object>> responseTuples = new ArrayList<>();
            for (Map.Entry<String, ApiResponse> entry : operation.getResponses().entrySet()) {
                String statusCode = DROGON_STATUS_CODE_MAPPING.get(entry.getKey());
                ApiResponse response = entry.getValue();

                Map<String, Object> respInfo = new HashMap<>();
                List<HashMap<String, Object>> contentInfoList = new ArrayList<>();
                Set<String> variantsDataTypes = new HashSet<>();
                if (response.getContent() != null && !response.getContent().isEmpty()) {
                    // Handle each content type for this status code

                    respInfo.put("statusCode", statusCode);

                    for (Map.Entry<String, io.swagger.v3.oas.models.media.MediaType> contentEntry : response
                            .getContent().entrySet()) {
                        String mediaType = contentEntry.getKey();
                        String contentType = DROGON_CONTENT_TYPE_MAPPING.getOrDefault(mediaType, "CT_TEXT_PLAIN");

                        Schema responseSchema = contentEntry.getValue().getSchema();

                        HashMap<String, Object> contentInfo = new HashMap<>();
                        contentInfo.put("contentType", contentType);
                        contentInfo.put("mediaType", mediaType);

                        if (responseSchema != null) {
                            CodegenProperty cp = fromProperty("response", responseSchema, false);
                            contentInfo.put("dataType", cp.dataType);
                        } else {
                            contentInfo.put("dataType", null);
                        }
                        String statusAndDataType = statusCode + "_" + contentType;
                        if (!variantsDataTypes.contains(statusAndDataType)) {
                            variantsDataTypes.add(statusAndDataType);
                            contentInfo.put("isVariantDuplicated", false);
                        } else {
                            contentInfo.put("isVariantDuplicated", true);
                        }

                        contentInfoList.add(contentInfo);
                    }
                    respInfo.put("contentInfos", contentInfoList);
                } else {
                    // Handle responses without content (like 204 No Content)
                    HashMap<String, Object> contentInfo = new HashMap<>();
                    respInfo.put("statusCode", statusCode);
                    contentInfo.put("contentType", "CT_TEXT_PLAIN");
                    contentInfo.put("mediaType", "text/plain");
                    contentInfo.put("dataType", null);
                    contentInfo.put("isVariantDuplicated", false);
                    contentInfoList.add(contentInfo);
                }
                respInfo.put("contentInfos", contentInfoList);
                responseTuples.add(respInfo);
            }
            op.vendorExtensions.put("x-codegen-response-variants", responseTuples);

            // Build a flat list of response alternatives to make Mustache separators
            // trivial
            // add also headers if any
            List<Map<String, Object>> flatHeaders = new ArrayList<>();
            List<Map<String, Object>> flatVariants = new ArrayList<>();
            Set<String> seen = new HashSet<>();
            Map<String, List<Map<String, String>>> statusCodeHeaders = new HashMap<>();

            // First pass: collect headers by status code
            for (Map.Entry<String, ApiResponse> entry : operation.getResponses().entrySet()) {
                String statusCode = DROGON_STATUS_CODE_MAPPING.getOrDefault(entry.getKey(), "kUnknown");
                ApiResponse response = entry.getValue();

                if (response.getHeaders() != null && !response.getHeaders().isEmpty()) {
                    op.vendorExtensions.put("x-codegen-response-has-headers", true);

                    List<Map<String, String>> headersList = new ArrayList<>();
                    for (Map.Entry<String, io.swagger.v3.oas.models.headers.Header> headerEntry : response
                            .getHeaders().entrySet()) {
                        Map<String, String> headers = new HashMap<>();
                        String headerName = headerEntry.getKey();
                        // Sanitize header name to be used as variable name
                        String sanitizedHeaderName = headerName.replaceAll("[-\\s]", "_");
                        io.swagger.v3.oas.models.headers.Header header = headerEntry.getValue();
                        String headerType = getSchemaType(header.getSchema());
                        headers.put("headerName", headerName);
                        headers.put("sanitizedHeaderName", sanitizedHeaderName);
                        headers.put("headerType", headerType);
                        headersList.add(headers);
                    }
                    statusCodeHeaders.put(statusCode, headersList);
                }
            }

            for (Map.Entry<String, List<Map<String, String>>> entry : statusCodeHeaders.entrySet()) {
                String statusCode = entry.getKey();
                List<Map<String, String>> headersList = entry.getValue();
                Map<String, Object> alt = new HashMap<>();
                alt.put("statusCode", statusCode);
                alt.put("headers", headersList);
                alt.put("hasHeaders", !headersList.isEmpty());
                String dedupKey = statusCode + "|" + (headersList.toString());
                if (seen.add(dedupKey)) {
                    flatHeaders.add(alt);
                }
            }

            // Second pass: build flat variants with headers per status code
            for (Map.Entry<String, ApiResponse> entry : operation.getResponses().entrySet()) {
                String statusCode = DROGON_STATUS_CODE_MAPPING.getOrDefault(entry.getKey(), "kUnknown");
                ApiResponse response = entry.getValue();
                List<Map<String, String>> headersList = statusCodeHeaders.getOrDefault(statusCode, new ArrayList<>());

                if (response.getContent() != null && !response.getContent().isEmpty()) {
                    for (Map.Entry<String, io.swagger.v3.oas.models.media.MediaType> contentEntry : response
                            .getContent().entrySet()) {
                        String mediaType = contentEntry.getKey();
                        String contentType = DROGON_CONTENT_TYPE_MAPPING.getOrDefault(mediaType, "CT_TEXT_PLAIN");
                        Schema<?> responseSchema = contentEntry.getValue().getSchema();

                        Map<String, Object> alt = new HashMap<>();
                        alt.put("statusCode", statusCode);
                        alt.put("hasContentType", true);
                        alt.put("contentType", contentType);
                        alt.put("headers", headersList);
                        alt.put("hasHeaders", !headersList.isEmpty());

                        if (responseSchema != null) {
                            CodegenProperty cp = fromProperty("response", responseSchema, false);
                            alt.put("hasBody", true);
                            alt.put("dataType", cp.dataType);
                            // if content type is string -> mark it because drogon will copy it
                            if (STD_STRING.equals(cp.dataType)) {
                                alt.put("isString", true);
                            }
                        } else {
                            // should not happen with content, but be defensive
                            alt.put("hasBody", false);
                        }

                        String dedupKey = statusCode + "|" + contentType + "|" + alt.getOrDefault("dataType", "");
                        if (seen.add(dedupKey)) {
                            flatVariants.add(alt);
                        }
                    }
                } else {
                    Map<String, Object> alt = new HashMap<>();
                    alt.put("statusCode", statusCode);
                    alt.put("hasBody", false);
                    alt.put("hasContentType", false);
                    alt.put("headers", headersList);
                    alt.put("hasHeaders", !headersList.isEmpty());

                    String dedupKey = statusCode + "|nocontent|";
                    if (seen.add(dedupKey)) {
                        flatVariants.add(alt);
                    }
                }
            }
            op.vendorExtensions.put("x-codegen-response-variants-headers", flatHeaders);
            op.vendorExtensions.put("x-codegen-response-variants-flat", flatVariants);

        }

        // Build pathSimple and pathComplete for Drogon
        String pathSimple = path;
        StringBuilder pathComplete = new StringBuilder(path);

        int pathParamIndex = 1;
        int queryParamIndex = pathParamIndex;

        // Replace path parameters with {1}, {2}, etc.
        for (CodegenParameter param : op.pathParams) {
            pathSimple = pathSimple.replaceAll("\\{" + param.paramName + "}", "");
            pathComplete = new StringBuilder(
                    pathComplete.toString().replaceAll("\\{" + param.paramName + "}", "{" + pathParamIndex + "}"));
            pathParamIndex++;
        }

        // Add query parameters to pathComplete
        queryParamIndex = pathParamIndex;
        boolean firstQueryParam = true;
        for (CodegenParameter param : op.queryParams) {
            if (firstQueryParam) {
                pathComplete.append("?");
                firstQueryParam = false;
            } else {
                pathComplete.append("&");
            }
            pathComplete.append(param.paramName).append("={").append(queryParamIndex).append("}");
            queryParamIndex++;
        }

        String pathForDrogon = path;
        int idx = 1;
        for (CodegenParameter p : op.pathParams) {
            pathForDrogon = pathForDrogon.replace("{" + p.paramName + "}", "{" + idx + "}");
            idx++;
        }

        op.vendorExtensions.put("x-codegen-path-simple", pathSimple);
        op.vendorExtensions.put("x-codegen-path-complete", pathComplete.toString());
        op.vendorExtensions.put("x-codegen-drogon-path", pathForDrogon);

        return op;
    }

    /**
     * Extract the primary status code from operation responses
     *
     * @param responses The operation responses
     * @return The primary status code as string
     */
    private String getPrimaryStatusCode(io.swagger.v3.oas.models.responses.ApiResponses responses) {
        // Priority order: 200, 201, 2xx success codes, then others
        if (responses.containsKey("200"))
            return "200";
        if (responses.containsKey("201"))
            return "201";
        if (responses.containsKey("202"))
            return "202";
        if (responses.containsKey("204"))
            return "204";

        // Look for other 2xx codes
        for (String code : responses.keySet()) {
            if (code.matches("2\\d\\d")) {
                return code;
            }
        }

        // Look for 4xx codes
        for (String code : responses.keySet()) {
            if (code.matches("4\\d\\d")) {
                return code;
            }
        }

        // Look for 5xx codes
        for (String code : responses.keySet()) {
            if (code.matches("5\\d\\d")) {
                return code;
            }
        }

        // Look for other specific codes
        for (String code : responses.keySet()) {
            if (!code.equals("default") && code.matches("\\d+")) {
                return code;
            }
        }

        return "default";
    }

    @Override
    public OperationsMap postProcessOperationsWithModels(OperationsMap objs, List<ModelMap> allModels) {
        OperationMap operations = objs.getOperations();
        String classname = operations.getClassname();
        operations.put("classnameSnakeUpperCase", underscore(classname).toUpperCase(Locale.ROOT));
        operations.put("classnameSnakeLowerCase", underscore(classname).toLowerCase(Locale.ROOT));
        List<CodegenOperation> operationList = operations.getOperation();
        for (CodegenOperation op : operationList) {
            op.vendorExtensions.put("operationIdSnakeCase", underscore(op.operationId));
            postProcessSingleOperation(operations, op);
        }
        return objs;
    }

    private void postProcessSingleOperation(OperationMap operations, CodegenOperation op) {
        if (op.vendorExtensions == null) {
            op.vendorExtensions = new HashMap<>();
        }

        if (op.bodyParam != null) {
            if (op.bodyParam.vendorExtensions == null) {
                op.bodyParam.vendorExtensions = new HashMap<>();
            }

            boolean isStringOrDate = op.bodyParam.isString || op.bodyParam.isDate;
            // Ensure fully-qualified model type for single-model request bodies (not
            // containers)
            // Detect containers by looking at the C++ type (e.g., "std::vector<...>",
            // "std::map<...>", "std::set<...>")
            String dt = op.bodyParam.dataType;
            boolean isStdContainer = dt != null && (dt.startsWith("std::vector<") ||
                    dt.startsWith("std::map<") ||
                    dt.startsWith("std::set<"));
            if (!op.bodyParam.isPrimitiveType && !isStdContainer) {
                if (dt != null && !dt.startsWith("std::") && !dt.contains("::")) {
                    op.bodyParam.dataType = prefixWithNameSpaceIfNeeded(dt);
                }
            }

        }

        boolean consumeJson = false;
        if (op.consumes != null) {
            Predicate<Map<String, String>> isMediaTypeJson = consume -> (consume.get("mediaType") != null
                    && consume.get("mediaType").equals("application/json"));
            consumeJson = op.consumes.stream().anyMatch(isMediaTypeJson);
        }
        op.vendorExtensions.put("x-codegen-drogon-consumes-json", consumeJson);

        op.httpMethod = op.httpMethod.substring(0, 1).toUpperCase(Locale.ROOT)
                + op.httpMethod.substring(1).toLowerCase(Locale.ROOT);

        boolean isParsingSupported = true;
        for (CodegenParameter param : op.allParams) {
            boolean paramSupportsParsing = (!param.isFormParam && !param.isFile && !param.isCookieParam);
            isParsingSupported = isParsingSupported && paramSupportsParsing;

            postProcessSingleParam(param);
        }
        op.vendorExtensions.put("x-codegen-drogon-is-parsing-supported", isParsingSupported);

        // Check if any one of the operations needs a model, then at API file level, at
        // least one model has to be included.
        Predicate<String> importNotInImportMapping = hdr -> !importMapping.containsKey(hdr);
        if (op.imports.stream().anyMatch(importNotInImportMapping)) {
            operations.put("hasModelImport", true);
        }
    }

    /**
     * postProcessSingleParam - Modifies a single parameter, adjusting generated
     * data types for Header and Query parameters.
     */
    private void postProcessSingleParam(CodegenParameter param) {
        // For query and header params, wrap in std::optional<> only when NOT required.
        if (param.isQueryParam || param.isHeaderParam) {
            boolean makeOptional = !param.required;
            String dataTypeWithNamespace = param.isPrimitiveType
                    ? param.dataType
                    : prefixWithNameSpaceIfNeeded(param.dataType);

            if (makeOptional) {
                param.dataType = "std::optional<" + dataTypeWithNamespace + ">";
                param.isOptional = true;
                if (!param.isPrimitiveType) {
                    param.baseType = "std::optional<" + param.baseType + ">";
                }
            } else {
                // Ensure model namespace for non-primitive when required
                if (!param.isPrimitiveType) {
                    param.dataType = dataTypeWithNamespace;
                }
            }
        }
    }

    @Override
    public String toModelFilename(String name) {
        String modelName = toModelName(name);
        return modelName;
    }

    @Override
    public String apiFilename(String templateName, String tag) {

        String result = super.apiFilename(templateName, tag);

        if (templateName.endsWith("response-header.mustache")) {
            result = responseFilenameFromApiFilename(result, ".h");
        }
        return result;
    }

    private String responseFilenameFromApiFilename(String filename, String suffix) {
        String result = filename.substring(0, filename.length() - suffix.length()) + "Response" + suffix;
        return result;
    }


    @Override
    public String toApiFilename(String name) {
        return toApiName(name);
    }

    /**
     * Optional - type declaration. This is a String which is used by the
     * templates to instantiate your types. There is typically special handling
     * for different property types
     *
     * @return a string value used as the `dataType` field for model templates,
     *         `returnType` for api templates
     */
    @Override
    public String getTypeDeclaration(Schema p) {
        String openAPIType = getSchemaType(p);

        if (ModelUtils.isArraySchema(p)) {
            Schema inner = ModelUtils.getSchemaItems(p);
            return getSchemaType(p) + "<" + getTypeDeclaration(inner) + ">";
        }
        if (ModelUtils.isMapSchema(p)) {
            Schema inner = ModelUtils.getAdditionalProperties(p);
            return getSchemaType(p) + "<std::string, " + getTypeDeclaration(inner) + ">";
        } else if (ModelUtils.isByteArraySchema(p)) {
            return STD_STRING;
        }
        if (ModelUtils.isStringSchema(p)
                || ModelUtils.isDateSchema(p)
                || ModelUtils.isDateTimeSchema(p) || ModelUtils.isFileSchema(p)
                || languageSpecificPrimitives.contains(openAPIType)) {
            return toModelName(openAPIType);
        }

        return prefixWithNameSpaceIfNeeded(openAPIType);
    }

    /**
     * Prefix an open API type with a namespace or not, depending of its current
     * type and if it is on a list to avoid it.
     *
     * @param openAPIType Open API Type.
     * @return type prefixed with the namespace or not.
     */
    private String prefixWithNameSpaceIfNeeded(String openAPIType) {
        // Some types might not support namespace
        if (this.openAPITypesWithoutModelNamespace.contains(openAPIType) || openAPIType.startsWith("std::")) {
            return openAPIType;
        } else {
            String namespace = (String) additionalProperties.get("modelNamespace");
            return namespace + "::" + openAPIType;
        }
    }

    @Override
    public String toDefaultValue(Schema p) {
        if (ModelUtils.isStringSchema(p)) {
            if (p.getDefault() != null) {
                return "\"" + p.getDefault().toString() + "\"";
            } else {
                return "\"\"";
            }
        } else if (ModelUtils.isBooleanSchema(p)) {
            if (p.getDefault() != null) {
                return p.getDefault().toString();
            } else {
                return "false";
            }
        } else if (ModelUtils.isDateSchema(p)) {
            if (p.getDefault() != null) {
                return "\"" + p.getDefault().toString() + "\"";
            } else {
                return "\"\"";
            }
        } else if (ModelUtils.isDateTimeSchema(p)) {
            if (p.getDefault() != null) {
                return "\"" + p.getDefault().toString() + "\"";
            } else {
                return "\"\"";
            }
        } else if (ModelUtils.isNumberSchema(p)) {
            if (ModelUtils.isFloatSchema(p)) { // float
                if (p.getDefault() != null) {
                    // We have to ensure that our default value has a decimal point,
                    // because in C++ the 'f' suffix is not valid on integer literals
                    // i.e. 374.0f is a valid float but 374 isn't.
                    String defaultStr = p.getDefault().toString();
                    if (defaultStr.indexOf('.') < 0) {
                        return defaultStr + ".0f";
                    } else {
                        return defaultStr + "f";
                    }
                } else {
                    return "0.0f";
                }
            } else { // double
                if (p.getDefault() != null) {
                    return p.getDefault().toString();
                } else {
                    return "0.0";
                }
            }
        } else if (ModelUtils.isIntegerSchema(p)) {
            if (ModelUtils.isLongSchema(p)) { // long
                if (p.getDefault() != null) {
                    return p.getDefault().toString() + "L";
                } else {
                    return "0L";
                }
            } else { // integer
                if (p.getDefault() != null) {
                    return p.getDefault().toString();
                } else {
                    return "0";
                }
            }
        } else if (ModelUtils.isByteArraySchema(p)) {
            if (p.getDefault() != null) {
                return "\"" + p.getDefault().toString() + "\"";
            } else {
                return "\"\"";
            }
        } else if (ModelUtils.isMapSchema(p)) {
            String inner = getSchemaType(ModelUtils.getAdditionalProperties(p));
            return "std::map<std::string, " + inner + ">()";
        } else if (ModelUtils.isArraySchema(p)) {
            String inner = getSchemaType(ModelUtils.getSchemaItems(p));
            if (!languageSpecificPrimitives.contains(inner)) {
                inner = "std::shared_ptr<" + inner + ">";
            }
            return "std::vector<" + inner + ">()";
        } else if (!StringUtils.isEmpty(p.get$ref())) {
            return "std::make_shared<" + toModelName(ModelUtils.getSimpleRef(p.get$ref())) + ">()";
        }

        return "nullptr";
    }

    /**
     * Location to write model files. You can use the modelPackage() as defined
     * when the class is instantiated
     */
    @Override
    public String modelFileFolder() {
        String serviceNameLower = serviceName.toLowerCase().replaceAll("[^a-zA-Z0-9]", "");
        return (outputFolder + "/" + serviceNameLower + "/model").replace("/", File.separator);
    }

    /**
     * Location to write api files. You can use the apiPackage() as defined when
     * the class is instantiated
     */
    @Override
    public String apiFileFolder() {
        String serviceNameLower = serviceName.toLowerCase().replaceAll("[^a-zA-Z0-9]", "");
        return (outputFolder + "/" + serviceNameLower + "/api").replace("/", File.separator);
    }

    /**
     * Optional - OpenAPI type conversion. This is used to map OpenAPI types in
     * a `Schema` into either language specific types via `typeMapping` or
     * into complex models if there is not a mapping.
     *
     * @return a string value of the type or complex model for this property
     */
    @Override
    public String getSchemaType(Schema p) {
        String openAPIType = super.getSchemaType(p);
        String type = null;
        if (typeMapping.containsKey(openAPIType)) {
            type = typeMapping.get(openAPIType);
            if (languageSpecificPrimitives.contains(type))
                return toModelName(type);
        } else
            type = openAPIType;
        return toModelName(type);
    }

    @Override
    public String getTypeDeclaration(String str) {
        return toModelName(str);
    }

    /**
     * Specify whether external libraries will be added during the generation
     *
     * @param value the value to be set
     */
    public void setAddExternalLibs(boolean value) {
        isAddExternalLibs = value;
    }
}
