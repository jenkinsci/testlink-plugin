package hudson.plugins.testlink.result;

import br.eti.kinoshita.testlinkjavaapi.model.CustomField;
import br.eti.kinoshita.testlinkjavaapi.model.TestPlan;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.Serializable;
import java.util.List;

/**
 * Created by saravm1 on 11.01.2016.
 */
public class TestPlanWrapper implements Serializable{

    private TestPlan testplan;

    public List<CustomField> getCustomFields(){
        throw new NotImplementedException();
    }
}
