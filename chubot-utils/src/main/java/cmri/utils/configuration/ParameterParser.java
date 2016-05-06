package cmri.utils.configuration;

import cmri.utils.lang.StringHelper;

import java.util.HashSet;
import java.util.Set;

/**
 * Parameter expression is like "${user.home}/stock", its key can include English letters, number, dot, underline.
 * Created by zhuyin on 3/6/15.
 */
public class ParameterParser {
    /**
     * For example, for "${user.home}/stock", its key is "user.home", and region is "${user.home}".
     */
    private static final String keyRegex = "\\$\\{([a-zA-Z\\d\\._]+)\\}";
    private static final String regionRegex = "\\$\\{[a-zA-Z\\d\\._]+\\}";

    /**
     * @return sorted regions.
     */
    public Set<String> parseRegions(String parameter) {
        return new HashSet<>(StringHelper.parseCollectionByRegex(parameter, regionRegex, 0));
    }

    public String parseKey(String region) {
        return StringHelper.parseRegex(region, keyRegex, 1);
    }

    public String getReal(String parameter) {
        if (parameter == null) {
            return null;
        }
        String real = parameter;
        for (String region : parseRegions(parameter)) {
            String key = parseKey(region);
            String property = ConfigManager.get(key);
            if (property == null) {
                throw new RuntimeException("cannot find property " + region);
            }
            real = real.replace(region, property);
        }
        return real;
    }
}
