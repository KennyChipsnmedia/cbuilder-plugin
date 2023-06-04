package io.jenkins.plugins.cbuilder;

import com.cwctravel.hudson.plugins.extended_choice_parameter.ExtendedChoiceParameterDefinition;
import com.sonyericsson.rebuild.RebuildParameterPage;
import com.sonyericsson.rebuild.RebuildParameterProvider;
import hudson.Extension;
import hudson.Util;
import hudson.model.ChoiceParameterDefinition;
import hudson.model.ParameterDefinition;
import hudson.model.ParameterValue;
import io.jenkins.plugins.changer.parameter.DownstreamPriorityDefinition;
import jenkins.model.Jenkins;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.tools.ant.filters.StringInputStream;
import org.biouno.unochoice.*;
import org.biouno.unochoice.util.Utils;

import java.io.StringReader;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Extension(optional = true)
public class CbuildParameterProvider extends RebuildParameterProvider {
    private static final Logger LOGGER = Logger.getLogger(CbuildParameterProvider.class.getName());

    @Override
    public RebuildParameterPage getRebuildPage(ParameterDefinition definition, ParameterValue value) {

        if(definition == null || value == null) {
            return null;
        }

        if(value.getValue() == null) {
            LOGGER.log(Level.INFO, "Kenny ParameterValue name:" + value.getName() + " value:" + value.getValue());
            return null;
        }

        List<String> valueList = Arrays.stream(value.getValue().toString().split(",")).collect(Collectors.toList());

        if(definition instanceof ExtendedChoiceParameterDefinition) {

            ExtendChoiceParameterResolver ecpr = new ExtendChoiceParameterResolver((ExtendedChoiceParameterDefinition)definition, value);
            String path = "/" + ecpr.getClass().getName().replace('.', '/').replace('$', '/');
            String page = path + "/index.jelly";
            return new RebuildParameterPage(ecpr.getClass(), ecpr.getDescriptor().getValuePage(), ecpr);
        }
        else if(definition instanceof ChoiceParameterDefinition) {
            ParameterDefinition cpd = definition.copyWithDefaultValue(value);
            String page = cpd.getDescriptor().getValuePage();
            return new RebuildParameterPage(definition.getClass(), page, cpd);
        }
        else if(definition instanceof AbstractScriptableParameter) {
            String selected = ":selected";

            // Just for logging.
            /*
            if(definition instanceof ChoiceParameter) {
                LOGGER.log(Level.INFO, "ChoiceParameter value:" + value.getValue());
            }
            else if(definition instanceof DynamicReferenceParameter) {
                LOGGER.log(Level.INFO, "DynamicReferenceParameter value:" + value.getValue());
            }
            else if(definition instanceof CascadeChoiceParameter) {
                LOGGER.log(Level.INFO, "CascadeChoiceParameter value:" + value.getValue());
                LOGGER.log(Level.INFO, "Kenny debug CascadeChoiceParameter choices");
                Map<Object, Object> choices = ((CascadeChoiceParameter) definition).getChoices();
                choices.entrySet().forEach(entry -> {
                    LOGGER.log(Level.INFO, "CascadeChoiceParameter key:" + entry.getKey() + " value:" + entry.getValue());
                });
            }
            else {
                LOGGER.log(Level.SEVERE, "Unkwnon AbstractUnoChoiceParameter");
            }
            */


            // getChoicesToRebuild is made to rebuild parameter, setRebulid(false) will be done at active choice parameter class or index.jelly


            // special case for textarea in DynamicReferenceParameter
            if (definition instanceof DynamicReferenceParameter) {
                // for debug
                LOGGER.log(Level.FINEST, "DynamicReferenceParameter value:" + value.getValue());

                if(value.getValue() != null) {
                    String newValue = StringEscapeUtils.escapeHtml4(value.getValue().toString()).replaceAll("\\\\n", "\n");
                    LOGGER.log(Level.FINEST, "oldValue:" + value.getValue() + " newValue:" + newValue);
                    DynamicReferenceParameter d2 = (DynamicReferenceParameter)definition;
                    Map<Object, Object> choices = d2.getChoicesToRebuild();
                    String choicesAsString = d2.getChoicesAsString();
                    String choiceType = d2.getChoiceType();
                    LOGGER.log(Level.FINEST, "choicesAsString:{0} choiceType:{1}", new Object[]{choicesAsString, choiceType});

                    // now only dealing with ET_FORMATTED_HTML format
                    if(choiceType.equals("ET_FORMATTED_HTML")) {
                        LOGGER.log(Level.FINEST, "DynamicReferenceParameter choiceType ET_FORMATTED_HTML found");
                        choices.clear();
                        choices.put(definition.getName(), newValue);
                        return new RebuildParameterPage(definition.getClass(), definition.getDescriptor().getValuePage(), definition);
                    }
                }

            }

            // normal choices
            Map<Object, Object> choices = ((AbstractScriptableParameter) definition).getChoicesToRebuild();
            Map<Object, Object> freshChoices = new LinkedHashMap<Object, Object>();
            Map<Object, Object> newChoices = new LinkedHashMap<Object, Object>();

            choices.entrySet().forEach(entry -> {
                String nk = Utils.escapeSelectedAndDisabled(entry.getKey());
                String nv = Utils.escapeSelectedAndDisabled(entry.getValue());
                freshChoices.put(nk, nv);
            });

            // Kenny, choices may not selected by default, but do this to refresh all choices
            freshChoices.entrySet().forEach(entry -> {
                if(valueList.contains(entry.getKey().toString())) {
                    String k = entry.getKey() + selected;
                    String v = entry.getValue() + selected;
                    newChoices.put(k, v);
                }
                else {
                    newChoices.put(entry.getKey(), entry.getValue());
                }
            });

            choices.clear();
            newChoices.entrySet().forEach(entry -> {
                choices.put(entry.getKey(), entry.getValue());
            });

            return new RebuildParameterPage(definition.getClass(), definition.getDescriptor().getValuePage(), definition);
        }
        else if(definition instanceof DownstreamPriorityDefinition) {
            ParameterDefinition cpd = definition.copyWithDefaultValue(value);
            String page = cpd.getDescriptor().getValuePage();
            return new RebuildParameterPage(definition.getClass(), page, cpd);
        }

        else {
            return null;
        }

    }

}
