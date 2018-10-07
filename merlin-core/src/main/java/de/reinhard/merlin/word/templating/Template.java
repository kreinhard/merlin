package de.reinhard.merlin.word.templating;

import de.reinhard.merlin.persistency.FileDescriptor;
import de.reinhard.merlin.persistency.FileDescriptorInterface;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A template refers a template file, optional a template definition file and contains some meta data (such as statistics
 * about variables and their usage).
 */
public class Template implements Cloneable, FileDescriptorInterface {
    private Logger log = LoggerFactory.getLogger(Template.class);
    private TemplateStatistics statistics;
    private TemplateDefinition templateDefinition;
    private String templateDefinitionId;
    private FileDescriptor fileDescriptor;

    public Template() {
        statistics = new TemplateStatistics(this);
    }

    public TemplateDefinition getTemplateDefinition() {
        return templateDefinition;
    }

    public TemplateStatistics getStatistics() {
        return statistics;
    }

    /**
     * Please use {@link #assignTemplateDefinition(TemplateDefinition)} for updating statistics (unused variables etc.) or
     * don't forget to call {@link #updateStatistics()}.
     *
     * @param templateDefinition
     */
    public void setTemplateDefinition(TemplateDefinition templateDefinition) {
        this.templateDefinition = templateDefinition;
    }

    public void assignTemplateDefinition(TemplateDefinition templateDefinition) {
        setTemplateDefinition(templateDefinition);
        updateStatistics();
    }

    /**
     * Analyzes used variables by this template and compares it to the defined variables in the given templateDefinition.
     */
    public void updateStatistics() {
        statistics.updateStatistics();
    }

    public FileDescriptor getFileDescriptor() {
        return fileDescriptor;
    }

    public void setFileDescriptor(FileDescriptor fileDescriptor) {
        this.fileDescriptor = fileDescriptor;
    }

    public String getTemplateDefinitionId() {
        if (templateDefinition != null) {
            return templateDefinition.getId();
        }
        return templateDefinitionId;
    }

    public void setTemplateDefinitionId(String templateDefinitionId) {
        if (this.templateDefinition != null) {
            throw new IllegalArgumentException("You shouldn't try to set a template definition id if a template definition object is already assigned.");
        }
        this.templateDefinitionId = templateDefinitionId;
    }

    @Override
    public String toString() {
        ToStringBuilder tos = new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE);
        tos.append("fileDescriptor", fileDescriptor);
        tos.append("templateDefinitionId", getTemplateDefinitionId());
        tos.append("statistics", statistics);
        return tos.toString();
    }

    /**
     * Creates a template definition from all used variables. This may be used, if not template definition is
     * explicitly set.
     *
     * @return
     */
    public TemplateDefinition createAutoTemplateDefinition() {
        TemplateDefinition autoTemplateDefinition = new TemplateDefinition();
        autoTemplateDefinition.setId(this.getFileDescriptor().getFilename());
        autoTemplateDefinition.setDescription("Pseudo template, generated by analyzing the template.");
        autoTemplateDefinition.setAutoGenerated(true);
        if (CollectionUtils.isNotEmpty(statistics.getUsedVariables())) {
            for (String variable : statistics.getUsedVariables()) {
                if (autoTemplateDefinition.getVariableDefinition(variable, false) == null) {
                    // Not yet registered.
                    autoTemplateDefinition.add(new VariableDefinition(VariableType.STRING, variable));
                }
            }
        }
        return autoTemplateDefinition;
    }

    @Override
    public Object clone() {
        Template template = null;
        try {
            template = (Template) super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new UnsupportedOperationException(this.getClass().getCanonicalName() + " isn't cloneable: " + ex.getMessage(), ex);
        }
        template.fileDescriptor = (FileDescriptor)this.fileDescriptor.clone();
        template.statistics = (TemplateStatistics)this.statistics.clone();
        return template;
    }
}