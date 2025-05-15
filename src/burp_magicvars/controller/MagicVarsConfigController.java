package burp_magicvars.controller;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.core.ToolType;
import burp.api.montoya.http.handler.*;
import burp.api.montoya.http.message.params.HttpParameter;
import burp.api.montoya.http.message.params.HttpParameterType;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.message.responses.HttpResponse;
import burp.api.montoya.proxy.http.InterceptedRequest;
import burp.api.montoya.proxy.http.ProxyRequestHandler;
import burp.api.montoya.proxy.http.ProxyRequestReceivedAction;
import burp.api.montoya.proxy.http.ProxyRequestToBeSentAction;
import burp_magicvars.MagicVariable;
import burp_magicvars.config.MagicVariableListExport;
import burp_magicvars.enums.EditorState;
import burp_magicvars.enums.MagicVariableType;
import burp_magicvars.event.MagicVarsConfigControllerEvent;
import burp_magicvars.event.MagicVarsReplacementEvent;
import burp_magicvars.model.MagicVarsConfigModel;
import burp_magicvars.mvc.AbstractController;
import burp_magicvars.util.ParameterEncoder;
import burp_magicvars.util.Logger;
import burp_magicvars.util.MagicVarsReplacer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;


public class MagicVarsConfigController extends AbstractController<MagicVarsConfigControllerEvent, MagicVarsConfigModel> implements HttpHandler, ProxyRequestHandler {
    private MontoyaApi api;
    private MagicVarsReplacer magicVarsReplacer;

    public MagicVarsConfigController(MagicVarsConfigModel model, MontoyaApi api) {
        super(model);
        this.api = api;
        magicVarsReplacer = new MagicVarsReplacer(api.collaborator());
        magicVarsReplacer.addListener(this);
    }

    protected void handleEvent(MagicVarsReplacementEvent event, Object previous, Object next) {
        switch (event) {
            case MagicVarsReplacementEvent.REPLACEMENT_MADE:
            case MagicVarsReplacementEvent.VALUE_READ:
            case MagicVarsReplacementEvent.VALUE_WRITE:
                getModel().addMagicVarToTable(getModel().getMagicVariableByName((String)next));
                break;
            case MagicVarsReplacementEvent.PROCESSING_ERROR:
                break;
        }
    }

    @Override
    protected void handleEvent(MagicVarsConfigControllerEvent event, Object previous, Object next) {
        switch (event) {
            case MagicVarsConfigControllerEvent.NEW:
                getModel().setLastVariableId(getModel().getCurrentVariableId());
                getModel().clearCurrentVariable();
                getModel().setCurrentVariableMagicVariableType(MagicVariableType.STATIC);
                getModel().setEditorState(EditorState.CREATE);
                break;
            case MagicVarsConfigControllerEvent.SAVE:
                getModel().saveCurrentVariable();
                getModel().setEditorState(EditorState.EDIT);
                break;
            case MagicVarsConfigControllerEvent.CANCEL:
                getModel().editMagicVariableById(getModel().getLastVariableId());
                break;
            case MagicVarsConfigControllerEvent.DELETE:
                getModel().removeVariable(getModel().getCurrentVariableId());
                break;
            case MagicVarsConfigControllerEvent.MOVE_UP:
                getModel().moveCurrentVariableUp();
                break;
            case MagicVarsConfigControllerEvent.MOVE_DOWN:
                getModel().moveCurrentVariableDown();
                break;
            case MagicVarsConfigControllerEvent.UPDATE_NAME:
                getModel().setCurrentVariableName((String)next);
                break;
            case MagicVarsConfigControllerEvent.UPDATE_DESCRIPTION:
                getModel().setCurrentVariableDescription((String)next);
                break;
            case MagicVarsConfigControllerEvent.UPDATE_VARIABLE_TYPE:
                getModel().setCurrentVariableMagicVariableType(MagicVariableType.byName((String)next));
                break;
            case MagicVarsConfigControllerEvent.TOGGLE_ENABLED:
                getModel().setCurrentVariableEnabled((boolean)next);
                break;
            case MagicVarsConfigControllerEvent.UPDATE_INITIAL_VALUE:
                getModel().setCurrentVariableInitialValue((String)next);
                break;
            case MagicVarsConfigControllerEvent.UPDATE_READ_CAPTURE_GROUP:
                getModel().setCurrentVariableReadCaptureGroup(textFieldAsInteger((String)next));
                break;
            case MagicVarsConfigControllerEvent.UPDATE_WRITE_CAPTURE_GROUP:
                getModel().setCurrentVariableWriteCaptureGroup(textFieldAsInteger((String)next));
                break;
            case MagicVarsConfigControllerEvent.UPDATE_PATH_SCOPE:
                getModel().setCurrentVariablePathScopeRegex((String)next);
                break;
            case MagicVarsConfigControllerEvent.UPDATE_READ_REGEX:
                getModel().setCurrentVariableReadRegex((String)next);
                break;
            case MagicVarsConfigControllerEvent.UPDATE_WRITE_REGEX:
                getModel().setCurrentVariableWriteRegex((String)next);
                break;
            case MagicVarsConfigControllerEvent.LEFT_VARIABLE_MARKER_CHANGED:
                getModel().setLeftVariableMarker((String)next);
                magicVarsReplacer.setLeftVariableMarker(getModel().getLeftVariableMarker());
                break;
            case MagicVarsConfigControllerEvent.RIGHT_VARIABLE_MARKER_CHANGED:
                getModel().setRightVariableMarker((String)next);
                magicVarsReplacer.setRightVariableMarker(getModel().getRightVariableMarker());
                break;
            case MagicVarsConfigControllerEvent.ROW_SELECTION_UPDATE:
                if ( (Integer)next >= 0 ) {
                    getModel().editMagicVariableById((String) getModel().getCustomVariablesModel().getValueAt((Integer)next,0));
                    getModel().setEditorState(EditorState.EDIT);
                    getModel().setCurrentSelectedIdx((Integer)next);
                }
                if ( getModel().getCustomVariablesModel().getRowCount() == 0 ) {
                    getModel().setEditorState(EditorState.INITIAL);
                }
                break;
            case MagicVarsConfigControllerEvent.VARIABLES_TABLE_MODEL_CHANGED:
                getModel().syncOrder();
                break;
            case MagicVarsConfigControllerEvent.IMPORT_VARIABLES:
                JFileChooser importDialog = new JFileChooser();
                importDialog.setSelectedFile(new File("export.json"));
                if (importDialog.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                    try {
                        File file = importDialog.getSelectedFile();
                        byte[] content = Files.readAllBytes(file.toPath());
                        if ( content != null ) {
                            getModel().importVariablesFromJSON(new String(content));
                        }

                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                }
                break;
            case MagicVarsConfigControllerEvent.EXPORT_VARIABLES:
                JFileChooser exportDialog = new JFileChooser();
                exportDialog.setSelectedFile(new File("export.json"));
                if (exportDialog.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                    File file = exportDialog.getSelectedFile();
                    try {
                        Files.writeString(file.getAbsoluteFile().toPath(), getModel().exportVariablesAsJSON());
                    } catch (IOException e) {
                        Logger.log("ERROR", String.format("Exception while saving: %s", e.getMessage()));
                    }
                }
                break;
            case TOGGLE_SOURCE_INTRUDER:
                getModel().toggleTrafficSource(ToolType.INTRUDER,(boolean) next);
                break;
            case TOGGLE_SOURCE_REPEATER:
                getModel().toggleTrafficSource(ToolType.REPEATER,(boolean) next);
                break;
            case TOGGLE_SOURCE_SCANNER:
                getModel().toggleTrafficSource(ToolType.SCANNER,(boolean) next);
                break;
            case TOGGLE_SOURCE_PROXY:
                getModel().toggleTrafficSource(ToolType.PROXY,(boolean) next);
                break;
            case TOGGLE_SOURCE_EXTENSIONS:
                getModel().toggleTrafficSource(ToolType.EXTENSIONS,(boolean) next);
                break;
            case DISMISS_UPDATE:
                getModel().setUpdateAvailableMessage(null);
        }
    }


    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if ( event.getSource() instanceof MagicVarsReplacer) {
            handleEvent(MagicVarsReplacementEvent.valueOf(event.getPropertyName()), event.getOldValue(), event.getNewValue());
        }
        else {
            handleEvent(MagicVarsConfigControllerEvent.valueOf(event.getPropertyName()), event.getOldValue(), event.getNewValue());
        }

    }

    private int textFieldAsInteger( String value ) {
        int i = 0;
        if ( value != null ) {
            i = Integer.parseInt(value);
        }
        return i;
    }

    /*
        Responses
     */
    public HttpResponse processMagicVariables(HttpResponseReceived response ) {
        if ( !api.scope().isInScope(response.initiatingRequest().url())) {
            return response;
        }
        magicVarsReplacer.processDynamicVariables(
                getModel().getMagicVariables(),
                response.toString(),
                null
        );
        return response;
    }

    /*
        Requests
     */

    public HttpRequest processMagicVariables(HttpRequest request ) {
        /*
            Design considerations:
                - https://github.com/PortSwigger/burp-extensions-montoya-api/issues/103
         */

        // Ignore if not in scope
        if ( !api.scope().isInScope(request.url())) {
            return request;
        }

        HttpRequest modifiedRequest = request;
        ArrayList<HttpParameter> modifiedParameters = new ArrayList<HttpParameter>();

        /*
            Handle replacements for anything that isn't json/xml/formdata
         */
        for (HttpParameter p : request.parameters() ) {
            String param  = p.value();
            if ( p.type().equals(HttpParameterType.JSON) || p.type().equals(HttpParameterType.XML) || p.type().equals(HttpParameterType.XML_ATTRIBUTE )) {
                continue;
            }
            else {
                param = magicVarsReplacer.processStaticVariables(getModel().getMagicVariables(),param, new ParameterEncoder(api,p.type(),request.header("content-type")));
                param = magicVarsReplacer.processDynamicVariables(getModel().getMagicVariables(),param, new ParameterEncoder(api,p.type(),request.header("content-type")));
                if ( !p.value().equals(param)) {
                    modifiedParameters.add(HttpParameter.parameter(p.name(), param, p.type()));
                }
            }

        }
        // Merge the updated parameters into a new request if there are any changes
        if (!modifiedParameters.isEmpty()) {
            modifiedRequest = modifiedRequest.withUpdatedParameters(modifiedParameters);
        }

        /*
            Do full body replacement for text/json/xml/formdata or null content-type
         */
        String contentType = request.header("content-type") == null ? "" : request.header("content-type").value();
        if ( request.header("content-type") == null || contentType.matches("(?i).*(text|xml|json|x-www-form-urlencoded).*")) {
            if ( request.body().length() > 0 ) {
                String preProcess = modifiedRequest.bodyToString();
                // Statics
                String postProcess = magicVarsReplacer.processStaticVariables(
                        getModel().getMagicVariables(),
                        preProcess,
                        new ParameterEncoder(api,null,request.header("content-type"))
                );
                // Dynamics
                postProcess = magicVarsReplacer.processDynamicVariables(
                        getModel().getMagicVariables(),
                        postProcess,
                        new ParameterEncoder()
                );
                if ( preProcess != postProcess ) {
                    modifiedRequest = modifiedRequest.withBody(postProcess);
                }
            }
        }

        return modifiedRequest;
    }


    @Override
    public RequestToBeSentAction handleHttpRequestToBeSent(HttpRequestToBeSent request) {
        if ( getModel().getEnabledToolSources().contains(request.toolSource().toolType().toolName())) {
            return RequestToBeSentAction.continueWith(processMagicVariables((HttpRequest)request));
        }
        return RequestToBeSentAction.continueWith(request);
    }

    @Override
    public ResponseReceivedAction handleHttpResponseReceived(HttpResponseReceived response) {
        return ResponseReceivedAction.continueWith(processMagicVariables(response));
    }

    @Override
    public ProxyRequestReceivedAction handleRequestReceived(InterceptedRequest request) {
        if ( getModel().getEnabledToolSources().contains("Proxy")) {
            return ProxyRequestReceivedAction.continueWith(processMagicVariables((HttpRequest)request));
        }
        return ProxyRequestReceivedAction.continueWith(request);
    }

    @Override
    public ProxyRequestToBeSentAction handleRequestToBeSent(InterceptedRequest request) {
        return null;
    }
}
