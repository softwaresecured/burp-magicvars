package burp_magicvars.util;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.message.HttpHeader;
import burp.api.montoya.http.message.params.HttpParameterType;
import com.fasterxml.jackson.core.io.JsonStringEncoder;

public class ParameterEncoder {
    private MontoyaApi api = null;
    private HttpParameterType parameterType = null;
    private String contentType = "";

    public ParameterEncoder() {

    }

    public ParameterEncoder(MontoyaApi api, HttpParameterType parameterType, HttpHeader contentTypeHeader) {
        this.api = api;
        this.parameterType = parameterType;
        if ( contentTypeHeader != null ) {
            contentType = contentTypeHeader.value();
        }
    }

    public String encodeParameter(String value ) {
        if ( parameterType == null || api == null || contentType == null ) {
            return value;
        }
        String encodedValue = value;
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
        if ( contentType.matches("(?i).*json.*")) {
            encodedValue = new String(JsonStringEncoder.getInstance().quoteAsString(value));
        }
        return encodedValue;
    }
}
