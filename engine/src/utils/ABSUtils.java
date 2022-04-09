package utils;

import jaxb.generated.AbsDescriptor;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;

public class ABSUtils {

    private final static String JAXB_XML_ABS_PACKAGE = "engine.jaxb.generated";
    public static int tryParseIntAndValidateRange(String value, int min, int max) {
        try {
            int intValue = Integer.parseInt(value);
            System.out.println();
            if (intValue > max || intValue < min)
                return -1;
            return intValue;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public static AbsDescriptor deserializeFrom(InputStream in) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(JAXB_XML_ABS_PACKAGE);
        Unmarshaller u = jc.createUnmarshaller();
        return (AbsDescriptor) u.unmarshal(in);
    }
}
