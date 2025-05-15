package burp_magicvars.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class RegexUtil {
    public static boolean validateRegex( String regex ) {
        try {
            Pattern.compile(regex);
            return true;
        } catch ( PatternSyntaxException e ) {}

        return false;
    }

    public static Pattern compile(String regex) {
        if (regex == null || regex.length() == 0) return null;

        try {
            return Pattern.compile(regex);
        } catch(Exception e) {
            return null;
        }
    }

    public static int getMatchGroupCount( String regex ) {
        if ( validateRegex(regex)) {
            return Pattern.compile(regex).matcher("").groupCount();
        }
        return 0;
    }

    public static boolean matches(String value, String regex ) {
        Pattern p = Pattern.compile(regex,Pattern.MULTILINE);
        Matcher m = p.matcher(value);
        return m.find();
    }
}
