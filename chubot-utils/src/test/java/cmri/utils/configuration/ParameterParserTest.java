package cmri.utils.configuration;

import junit.framework.TestCase;

import java.util.Set;

/**
 * Created by zhuyin on 3/6/15.
 * <p/>
 * Standard System Properties
 * <p/>
 * Key	Meaning
 * "file.separator"	Character that separates components of a file path. This is "/" on UNIX and "\" on Windows.
 * "java.class.path"	Path used to find directories and JAR archives containing class files. Elements of the class path are separated by a platform-specific character specified in the path.separator property.
 * "java.home"	Installation directory for Java Runtime Environment (JRE)
 * "java.vendor"	JRE vendor name
 * "java.vendor.url"	JRE vender URL
 * "java.version"	JRE version number
 * "line.separator"	Sequence used by operating system to separate lines in text files
 * "os.arch"	Operating system architecture
 * "os.name"	Operating system name
 * "os.version"	Operating system version
 * "path.separator"	Path separator character used in java.class.path
 * "user.dir"	User working directory
 * "user.home"	User home directory
 * "user.name"	User account name
 * <p/>
 * 所谓的 system porperty，system 指的是 JRE (runtime)system，不是指 OS。
 * <p/>
 * java -D<name>=<value> set a system property  设置系统属性
 */
public class ParameterParserTest extends TestCase {
    ParameterParser parser;
    String parameter;

    @Override
    protected void setUp() {
        System.setProperty("a_Z_Y.1", "zhuZhenYing");
        parameter = "${user.home}/stock/${a_Z_Y.1}";

        parser = new ParameterParser();
    }

    public void testParseRegions() {
        Set<String> regions = parser.parseRegions(parameter);
        assertEquals("[${a_Z_Y.1}, ${user.home}]", regions.toString());
    }

    public void testParseKey() {
        assertEquals("os.name", parser.parseKey("${os.name}"));
        assertEquals(null, parser.parseKey("{os.name}"));
    }

    public void testGetRealName() {
        String real = parser.getReal(parameter);
        System.out.print(real);
    }
}
