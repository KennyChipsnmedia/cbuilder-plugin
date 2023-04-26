package io.jenkins.plugins.cbuilder;

import com.cwctravel.hudson.plugins.extended_choice_parameter.ExtendedChoiceParameterDefinition;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import hudson.DescriptorExtensionList;
import hudson.Extension;
import hudson.model.*;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ExtendChoiceParameterResolver extends ExtendedChoiceParameterDefinition {
    private ParameterValue parameterValue;

    private ExtendChoiceParameterResolver(String name, String type, String value, String projectName, String propertyFile, String groovyScript, String groovyScriptFile, String bindings, String groovyClasspath, String propertyKey, String defaultValue, String defaultPropertyFile, String defaultGroovyScript, String defaultGroovyScriptFile, String defaultBindings, String defaultGroovyClasspath, String defaultPropertyKey, String descriptionPropertyValue, String descriptionPropertyFile, String descriptionGroovyScript, String descriptionGroovyScriptFile, String descriptionBindings, String descriptionGroovyClasspath, String descriptionPropertyKey, String javascriptFile, String javascript, boolean saveJSONParameterToFile, boolean quoteValue, int visibleItemCount, String description, String multiSelectDelimiter) {
        super(name, type, value, projectName, propertyFile, groovyScript, groovyScriptFile, bindings, groovyClasspath, propertyKey, defaultValue, defaultPropertyFile, defaultGroovyScript, defaultGroovyScriptFile, defaultBindings, defaultGroovyClasspath, defaultPropertyKey, descriptionPropertyValue, descriptionPropertyFile, descriptionGroovyScript, descriptionGroovyScriptFile, descriptionBindings, descriptionGroovyClasspath, descriptionPropertyKey, javascriptFile, javascript, saveJSONParameterToFile, quoteValue, visibleItemCount, description, multiSelectDelimiter);
    }


    private ExtendChoiceParameterResolver(ExtendedChoiceParameterDefinition expd) {
        this(
            expd.getName(),
            expd.getType(),
            expd.getValue(),
            expd.getProjectName(),
            expd.getPropertyFile(),
            expd.getGroovyScript(),
            expd.getGroovyScriptFile(),
            expd.getBindings(),
            expd.getGroovyClasspath(),
            expd.getPropertyKey(),
            expd.getDefaultValue(),
            expd.getDefaultPropertyFile(),
            expd.getDefaultGroovyScript(),
            expd.getDefaultGroovyScriptFile(),
            expd.getDefaultBindings(),
            expd.getDefaultGroovyClasspath(),
            expd.getDefaultPropertyKey(),
            expd.getDescriptionPropertyValue(),
            expd.getDescriptionPropertyFile(),
            expd.getDescriptionGroovyScript(),
            expd.getDescriptionGroovyScriptFile(),
            expd.getDescriptionBindings(),
            expd.getDescriptionGroovyClasspath(),
            expd.getDescriptionPropertyKey(),
            expd.getJavascriptFile(),
            expd.getJavascript(),
            expd.isSaveJSONParameterToFile(),
            expd.isQuoteValue(),
            expd.getVisibleItemCount(),
            expd.getDescription(),
            expd.getMultiSelectDelimiter()
        );

    }

    public ExtendChoiceParameterResolver(ExtendedChoiceParameterDefinition expd, ParameterValue parameterValue) {
        this(expd);
        this.parameterValue = parameterValue;
    }

    public Map<String, Boolean> getBuiltMap() {
        Map<String, Boolean> builtMap = new HashMap<>();

        try {
            Arrays.stream(parameterValue.getValue().toString().split(",")).filter(v -> v.trim().length() > 0).forEach(v -> builtMap.put(v, true));
        }
        catch (NullPointerException e) {
            // do nothing
        }

        return builtMap;
    }

    public ParameterValue getParameterValue() {
        return parameterValue;
    }



    @Extension
    public static class DescriptorImpl extends ParameterDescriptor {
        @NonNull
        @Override
        public String getDisplayName() {
            return "ExtendChoiceParameterResolver";
        }
    }

}
