package burp_magicvars;

import burp.VERSION;
import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.extension.ExtensionUnloadingHandler;
import burp_magicvars.config.MontoyaConfig;
import burp_magicvars.controller.MagicVarsConfigController;
import burp_magicvars.enums.EditorState;
import burp_magicvars.model.MagicVarsConfigModel;
import burp_magicvars.mvc.AbstractModel;
import burp_magicvars.mvc.AbstractView;
import burp_magicvars.mvc.MVC;
import burp_magicvars.ui.MagicVarsTab;
import burp_magicvars.util.Logger;
import burp_magicvars.view.MagicVarsConfigView;


public class BurpMagicVars implements BurpExtension, ExtensionUnloadingHandler {
    public static final String EXTENSION_NAME = "Burp MagicVars";
    public static final String EXTENSION_PRETTY_NAME = "Magic Variables";
    private MontoyaApi api;
    private MagicVarsTab tab;
    private MVC<MagicVarsConfigModel, MagicVarsConfigView, MagicVarsConfigController> magicVarsConfig;
    private MontoyaConfig config;

    @Override
    public void initialize(MontoyaApi api) {
        this.api = api;
        api.extension().setName(EXTENSION_NAME);
        Logger.setLogger(api.logging());
        Logger.log("INFO", String.format("%s %s loaded", EXTENSION_NAME, VERSION.getVersionStr()));
        buildMVCs();
        this.config = new MontoyaConfig(api.persistence());
        magicVarsConfig.getModel().setEditorState(EditorState.INITIAL);
        this.tab = buildTab();
        api.userInterface().registerSuiteTab(EXTENSION_PRETTY_NAME, this.tab);
        api.http().registerHttpHandler(magicVarsConfig.controller);
        api.proxy().registerRequestHandler(magicVarsConfig.controller);
        for (AbstractModel<?> model : getModels()) {
            model.load(config);
        }
        api.extension().registerUnloadingHandler(this);
    }

    public MagicVarsTab buildTab() {
        MagicVarsTab tab = new MagicVarsTab(
                magicVarsConfig.getView()
        );

        for (AbstractView<?, ?, ?> view : getViews()) {
            view.attachListeners();
        }
        magicVarsConfig.getView().setParentComponent(tab);
        return tab;
    }

    private AbstractModel<?>[] getModels() {
        return new AbstractModel[] {
                magicVarsConfig.getModel()
        };
    }

    private AbstractView<?, ?, ?>[] getViews() {
        return new AbstractView[] {
                magicVarsConfig.getView()
        };
    }

    public void buildMVCs() {
        MagicVarsConfigModel magicVarsConfigModel = new MagicVarsConfigModel();
        this.magicVarsConfig = new MVC<>(magicVarsConfigModel, new MagicVarsConfigView(magicVarsConfigModel), new MagicVarsConfigController(magicVarsConfigModel,api));
    }

    @Override
    public void extensionUnloaded() {
        for (AbstractModel<?> model : getModels()) {
            model.save(config);
        }
    }

}