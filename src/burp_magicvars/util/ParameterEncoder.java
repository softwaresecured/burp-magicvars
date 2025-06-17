package burp_magicvars.util;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.message.HttpHeader;
import burp.api.montoya.http.message.params.HttpParameterType;
import com.fasterxml.jackson.core.io.JsonStringEncoder;

import java.util.regex.Matcher;

public class ParameterEncoder {
    private MontoyaApi api = null;
    private HttpParameterType parameterType = null;
    private String contentType = null;

    public ParameterEncoder() {

    }

    public ParameterEncoder( MontoyaApi api, HttpParameterType parameterType ) {
        this.api = api;
        this.parameterType = parameterType;
    }

    public ParameterEncoder(MontoyaApi api, HttpParameterType parameterType, HttpHeader contentTypeHeader) {
        this.api = api;
        this.parameterType = parameterType;
        if ( contentTypeHeader != null ) {
            contentType = contentTypeHeader.value();
        }
    }

    public String encodeParameter(String value ) {
        long startTime = System.currentTimeMillis();
        String encodedValue = value;
        if ( parameterType != null ) {
            switch ( parameterType ) {
                case URL:
                    encodedValue = api.utilities().urlUtils().encode(value);
                    break;
                case BODY:
                    if ( contentType.matches("(?i).*x-www-form-urlencoded.*")) {
                        encodedValue = api.utilities().urlUtils().encode(value);
                    }
                    break;
                case XML:
                    break;
                case XML_ATTRIBUTE:
                    break;
                case JSON:
                    break;
            }
        }

        if ( contentType != null ) {
            if ( contentType.matches("(?i).*json.*")) {
                encodedValue = new String(JsonStringEncoder.getInstance().quoteAsString(value));
            }
        }
        Logger.perf(startTime,"Encode parameter");
        return Matcher.quoteReplacement(encodedValue);
    }
}
