package hudson.plugins.testlink.util;

import hudson.EnvVars;
import hudson.Util;
import hudson.util.VariableResolver;

/**
 * Helper methods for Jenkins.
 *  
 * @author s2o
 *
 */
public class JenkinsHelper {
	
	
	  /**
     * Expands a text variable like BUILD-$VAR replacing the $VAR part with a environment variable that matches its
     * name, minus $.
     * 
     * @param variableResolver Jenkins Build Variable Resolver.
     * @param envVars Jenkins Build Environment Variables.
     * @param variable Variable value (includes mask).
     * @return Expanded test project name job configuration property.
     */
    public static String expandVariable(VariableResolver<String> variableResolver, EnvVars envVars, String variable) {
        return Util.replaceMacro(envVars.expand(variable), variableResolver);
    }


}
