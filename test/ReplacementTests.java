import burp.api.montoya.collaborator.Collaborator;
import burp_magicvars.MagicVariable;
import burp_magicvars.enums.MagicVariableType;
import burp_magicvars.util.MagicVarsReplacer;
import burp_magicvars.util.ParameterEncoder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

public class ReplacementTests {
    Collaborator collaborator = null;
    MagicVarsReplacer magicVarsReplacer = new MagicVarsReplacer(null);
    String READ_HTTP_REQUEST = """
            HTTP/1.1 200 OK
            Date: Mon, 16 Dec 2024 20:49:42 GMT
            Server: Apache/2.4.52 (Ubuntu)
            Content-Length: 38
            Keep-Alive: timeout=5, max=92
            Connection: Keep-Alive
            Content-Type: text/html; charset=UTF-8
            
            trackedread='77777777'<br>trackedwrite='666'""";
    String WRITE_HTTP_REQUEST = """
            GET /scratch/test.php?tracked=666 HTTP/1.1
            Host: localhost
            User-Agent: Mozilla/5.0 (X11; Tenant1; Linux x86_64; rv:100.0) Gecko/20100101 Firefox/100.0
            Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8
            Accept-Language: en-CA,en-US;q=0.7,en;q=0.3
            Accept-Encoding: gzip, deflate, br
            Connection: keep-alive
            Upgrade-Insecure-Requests: 1
            Sec-Fetch-Dest: document
            Sec-Fetch-Mode: navigate
            Sec-Fetch-Site: none
            Sec-Fetch-User: ?1
            Priority: u=1""";

    @Test
    @DisplayName("Test no replacement")
    public void testReplacements() {
        String no_replace = magicVarsReplacer.processStaticVariables(null,"Blah",new ParameterEncoder());
        assertEquals(no_replace, "Blah");
    }

    @Test
    @DisplayName("Test random ip")
    public void testRandomIp() {
        // Not sure how best to test I guess if there is more than 4 words
        String test = magicVarsReplacer.getRandIpV4();
        assertTrue(test.matches("\\b(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\b"));
    }

    @Test
    @DisplayName("Test lorem ipsum sentence")
    public void testLoremIpsumSentence() {
        // Not sure how best to test I guess if there is more than 4 words
        String sentence = magicVarsReplacer.getLoremIpsumSentence();
        assertTrue(sentence.split(" ").length > 1);
    }

    @Test
    @DisplayName("Test custom static")
    public void testCustomStatic() {
        ArrayList<MagicVariable> magicVariableArrayList = new ArrayList<>();
        MagicVariable magicVariable = new MagicVariable("TEST", MagicVariableType.STATIC);
        magicVariable.initialValue = "AAAA";
        magicVariableArrayList.add(magicVariable);
        String test = magicVarsReplacer.processStaticVariables(magicVariableArrayList,"__TEST__",new ParameterEncoder());
        assertTrue(test.matches("AAAA"));
    }

    @Test
    @DisplayName("Test custom static containing builtin")
    public void testCustomStaticContainingBuiltin() {
        ArrayList<MagicVariable> magicVariableArrayList = new ArrayList<>();
        MagicVariable magicVariable = new MagicVariable("TEST", MagicVariableType.STATIC);
        magicVariable.initialValue = "<script>alert(__NERINT__)</script>";
        magicVariableArrayList.add(magicVariable);
        String test = magicVarsReplacer.processStaticVariables(magicVariableArrayList,"__TEST__",new ParameterEncoder());
        System.out.println(test);
        assertTrue(test.matches("<script>alert\\(55\\d{4,}66\\)<\\/script>"));
    }

    @Test
    @DisplayName("Test dynamic read")
    public void testDyamicRead() {
        ArrayList<MagicVariable> magicVariableArrayList = new ArrayList<>();
        MagicVariable magicVariable = new MagicVariable("TEST", MagicVariableType.DYNAMIC);
        magicVariable.readRegex = Pattern.compile("trackedread='([^']+)'");
        magicVariable.readCaptureGroup = 1;
        magicVariable.writeRegex = Pattern.compile("tracked=(\\d+)\\s");
        magicVariable.writeCaptureGroup = 1;
        magicVariableArrayList.add(magicVariable);
        String test = magicVarsReplacer.processDynamicVariables(magicVariableArrayList,READ_HTTP_REQUEST,new ParameterEncoder());
        assertTrue("77777777".matches(magicVariable.currentValue));
    }

    @Test
    @DisplayName("Test dynamic write")
    public void testDyamicWrite() {
        ArrayList<MagicVariable> magicVariableArrayList = new ArrayList<>();
        MagicVariable magicVariable = new MagicVariable("TEST", MagicVariableType.DYNAMIC);
        magicVariable.readRegex = Pattern.compile("trackedread='([^']+)'");
        magicVariable.readCaptureGroup = 1;
        magicVariable.writeRegex = Pattern.compile("tracked=(\\d+)\\s");
        magicVariable.writeCaptureGroup = 1;
        magicVariable.currentValue = "77777777";
        magicVariableArrayList.add(magicVariable);
        String test = magicVarsReplacer.processDynamicVariables(magicVariableArrayList,WRITE_HTTP_REQUEST,new ParameterEncoder());
        System.out.println(String.format("Tracked write = %s", test));
        assertTrue(test.contains("tracked=77777777"));
    }

    @Test
    @DisplayName("Test __RINT__")
    public void testRint() {
        String test = magicVarsReplacer.processStaticVariables(null,"__RINT__",new ParameterEncoder());
        assertTrue(test.matches("\\d{8}"));
    }

    @Test
    @DisplayName("Test __NERINT__ init")
    public void testNeRint() {
        String test = magicVarsReplacer.processStaticVariables(null,"__NERINT__",new ParameterEncoder());
        assertTrue(test.matches("55\\d{4}66"));
    }

    @Test
    @DisplayName("Test __NERINT__ reuse")
    public void testNeRintReuse() {
        String test = magicVarsReplacer.processStaticVariables(null,"55123466",new ParameterEncoder());
        assertTrue(test.matches("55\\d{4}66"));
    }

    @Test
    @DisplayName("Test __RANDSTR__")
    public void testRandStr() {
        String test = magicVarsReplacer.processStaticVariables(null,"__RANDSTR__",new ParameterEncoder());
        assertTrue(test.matches("[a-zA-z]{8}"));
    }

    @Test
    @DisplayName("Test __NERANDSTR__ init")
    public void testNeRandStr() {
        String test = magicVarsReplacer.processStaticVariables(null,"__NERANDSTR__",new ParameterEncoder());
        assertTrue(test.matches("XX[a-zA-z]{4}YY"));
    }

    @Test
    @DisplayName("Test __NERANDSTR__ reuse")
    public void testNeRandStrReuse() {
        String test = magicVarsReplacer.processStaticVariables(null,"XXCATZYY",new ParameterEncoder());
        assertTrue(test.matches("XX[a-zA-z]{4}YY"));
    }

    @Test
    @DisplayName("Test __UUID__")
    public void testUUID() {
        String test = magicVarsReplacer.processStaticVariables(null,"__UUID__",new ParameterEncoder());
        assertTrue(test.matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"));
    }

    @Test
    @DisplayName("Test __NEUUID__")
    public void testNeUUID() {
        String test = magicVarsReplacer.processStaticVariables(null,"__NEUUID__",new ParameterEncoder());
        assertTrue(test.matches("[0-9a-f]{8}-[0-9a-f]{2}de-adbe-ef[0-9a-f]{2}-[0-9a-f]{12}"));
    }

    @Test
    @DisplayName("Test __NEUUID__ reuse")
    public void testNeUUIDReuse() {
        String test = magicVarsReplacer.processStaticVariables(null,"72528570-22de-adbe-ef57-b4d8a157e876",new ParameterEncoder());
        assertTrue(test.matches("[0-9a-f]{8}-[0-9a-f]{2}de-adbe-ef[0-9a-f]{2}-[0-9a-f]{12}"));
    }

    @Test
    @DisplayName("Test __TIMESTAMP__")
    public void testTimeStamp() {
        String test = magicVarsReplacer.processStaticVariables(null,"__TIMESTAMP__",new ParameterEncoder());
        assertTrue(test.matches("\\d{10,}"));
    }


}

