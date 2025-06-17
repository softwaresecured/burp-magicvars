package burp_magicvars.util;

import burp.Constants;
import burp.api.montoya.logging.Logging;

public class Logger {
    private static Logging logger = null;

    public static void perf(long startTime, String message ) {
        if( System.currentTimeMillis()-startTime > Constants.PERF_TIME_CUTOFF ) {
            log("PERF", String.format("%d msec - %s", System.currentTimeMillis()-startTime,message));
        }
    }

    public static void log(String status, String message) {
        if (logger != null) {
            switch (status) {
                case "ERROR":
                    logger.raiseErrorEvent(message);
                    break;
                case "WARN":
                case "INFO":
                    logger.raiseInfoEvent(message);
                    break;
                case "PERF":
                    if (Constants.PERF_LOGGING_ENABLED ) {
                        logger.raiseDebugEvent(String.format("PERF - %s", message));
                    }
                    break;
                case "DEBUG":
                default:
                    logger.raiseDebugEvent(message);
                    break;
            }
            
        } else {
            System.out.println(String.format("[%s] %s", status, message));
        }
    }

    public static void setLogger(Logging logger) {
        Logger.logger = logger;
    }
}
