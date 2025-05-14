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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class BurpMagicVars implements BurpExtension, ExtensionUnloadingHandler {
    public static final String EXTENSION_NAME = "Burp MagicVars";
    public static final String EXTENSION_PRETTY_NAME = "Magic Variables";
    private MontoyaApi api;
    private MagicVarsTab tab;
    private MVC<MagicVarsConfigModel, MagicVarsConfigView, MagicVarsConfigController> magicVarsConfig;
    private MontoyaConfig config;
    private Thread updateCheckerThread = null;

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
        UpdateChecker updateChecker = new UpdateChecker();
        updateCheckerThread = new Thread(updateChecker);
        updateCheckerThread.start();

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
        if ( updateCheckerThread != null ) {
            try {
                updateCheckerThread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    class UpdateChecker implements Runnable {
        private String getLatestVersion() throws IOException, URISyntaxException {
            String latestVersion = null;
            URL url = new URI(VERSION.RELEASE_TAGS_URL).toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            if ( conn.getResponseCode() == 200 ) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                StringBuffer content = new StringBuffer();
                while ((line = in.readLine()) != null) {
                    content.append(line);
                }
                in.close();
                conn.disconnect();
                Pattern p = Pattern.compile("releases\\/tag\\/([^\"]+)\"");
                Matcher m = p.matcher(content);
                if ( m.find() ) {
                    latestVersion = m.group(1);
                }
            }
            return latestVersion;
        }
        @Override
        public void run() {
            try {
                String latestVersion = getLatestVersion();
                if( latestVersion != null ) {
                    if ( !VERSION.getVersionStrPlain().equals(latestVersion)) {
                        magicVarsConfig.getModel().setUpdateAvailableMessage(String.format("<html><center><a href=\"\">Magic Variables %s is available (Click to dismiss)</a></center></html>", latestVersion));
                        Logger.log("INFO", String.format("Update %s is available", latestVersion));
                    }
                }
                else {
                    Logger.log("ERROR", String.format("Error fetching updates - Could not fetch tags from %s", VERSION.RELEASE_TAGS_URL));
                }
            } catch (IOException | URISyntaxException e) {
                Logger.log("ERROR", String.format("Error fetching updates - Could not fetch tags from %s", VERSION.RELEASE_TAGS_URL));
            }
        }
    }

}