/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensilex.dev;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import org.opensilex.OpenSilex;
import org.opensilex.OpenSilexModule;
import org.opensilex.cli.MainCommand;

public class DevModule extends OpenSilexModule {

    public final static String CONFIG_FILE_PATH = "./src/main/resources/config/opensilex.yml";

    private static OpenSilex devInstance = null;

    public static OpenSilex getOpenSilexDev() throws Exception {
        return getOpenSilexDev(OpenSilex.getDefaultBaseDirectory());
    }

    public static OpenSilex getOpenSilexDev(Path baseDirectory) throws Exception {
        if (devInstance == null) {
            Map<String, String> args = new HashMap<String, String>() {
                {
                    put(OpenSilex.PROFILE_ID_ARG_KEY, OpenSilex.DEV_PROFILE_ID);
                    put(OpenSilex.DEBUG_ARG_KEY, "true");
                }
            };

            if (baseDirectory == null) {
                baseDirectory = OpenSilex.getDefaultBaseDirectory();
            }

            args.put(OpenSilex.BASE_DIR_ARG_KEY, baseDirectory.toFile().getCanonicalPath());
            args.put(OpenSilex.CONFIG_FILE_ARG_KEY, getConfig(baseDirectory));

            devInstance = OpenSilex.createInstance(args);
        }
        return devInstance;
    }

    public static void run(String[] args) throws Exception {
        run(null, args);
    }

    public static void run(Path baseDirectory, String[] args) throws Exception {
        OpenSilex instance = getOpenSilexDev(baseDirectory);
        MainCommand.run(args, instance);
    }

    private static String getConfig(Path baseDirectory) {
        return baseDirectory.resolve(DevModule.CONFIG_FILE_PATH).toFile().getAbsolutePath();
    }

    public static boolean isWindows() {
        return System.getProperty("os.name").startsWith("Windows");
    }
}
