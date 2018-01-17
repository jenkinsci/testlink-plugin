package hudson.plugins.testlink.result;

import net.sf.json.JSONObject;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.jvnet.hudson.test.For;
import org.jvnet.hudson.test.JenkinsRule;
import org.kohsuke.stapler.RequestImpl;
import org.mockito.Mock;

import javax.management.Descriptor;

@For(ResultSeeker.class)
@Ignore
public class ResultSeekerTest {

    @ClassRule
    public static JenkinsRule j = new JenkinsRule();

    @ClassRule
    public static TemporaryFolder f = new TemporaryFolder();

    @Test
    public void test() throws Exception {

        for (hudson.model.Descriptor<? extends ResultSeeker> d : ResultSeeker.all()) {
            System.out.println("Trying " + d.getDisplayName());
            ResultSeeker s = d.newInstance(null, new JSONObject());
        }
    }
}
