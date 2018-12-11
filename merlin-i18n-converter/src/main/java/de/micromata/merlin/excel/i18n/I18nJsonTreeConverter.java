package de.micromata.merlin.excel.i18n;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class I18nJsonTreeConverter {
    private static Logger log = LoggerFactory.getLogger(I18nJsonTreeConverter.class);

    @Getter
    private Dictionary dictionary;
    @Setter
    private String carriageReturn = "\n";
    @Setter
    private boolean keysOnly;

    /**
     * If false (default) all dictionary will be written. If true, only "" will be written for every language.
     */
    @Setter
    private boolean writeEmptyTranslations = false;

    public I18nJsonTreeConverter() {
        this.dictionary = new Dictionary();
    }

    public I18nJsonTreeConverter(Dictionary translations) {
        this.dictionary = translations;
    }

    public void importTranslations(Reader reader, String lang) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(reader);
        traverse(lang, rootNode, null);
    }

    private void traverse(String lang, JsonNode node, String parentKey) {
        Iterator<Map.Entry<String, JsonNode>> it = node.fields();
        while (it.hasNext()) {
            Map.Entry<String, JsonNode> childEntry = it.next();
            String key = childEntry.getKey();
            JsonNode child = childEntry.getValue();
            if (child.getNodeType() == JsonNodeType.OBJECT) {
                traverse(lang, child, buildKey(parentKey, key));
            } else {
                dictionary.addTranslation(lang, buildKey(parentKey, key), child.textValue());
            }
        }
    }

    private String buildKey(String parentKey, String key) {
        if (StringUtils.isNotBlank(parentKey)) {
            return parentKey + "." + key;
        }
        return key;
    }


    public void write(String lang, Writer writer) throws IOException {
        Node root = buildNodes();
        StringBuilder sb = new StringBuilder();
        write(lang, sb, root);
        sb.append(carriageReturn).append("}").append(carriageReturn);
        writer.write(sb.toString());
    }

    private void write(String lang, StringBuilder sb, Node node) {
        if (node.keyPart != null) {
            for (int i = 0; i < node.level; i++) sb.append("  ");
            sb.append("\"").append(node.keyPart).append("\" : ");
        }
        if (node.childs == null) {
            String translation = escapeJson(dictionary.getTranslation(lang, node.i18nKey));
            sb.append("\"").append(translation).append("\"");
            return;
        }
        sb.append("{");
        for (Map.Entry<String, Node> entry : node.childs.entrySet()) {
            sb.append(carriageReturn);
            write(lang, sb, entry.getValue());
        }
    }

    private String escapeJson(String text) {
        if (text == null) {
            return "";
        }
        return StringEscapeUtils.escapeJson(text);
    }

    private Node buildNodes() {
        Node root = new Node(null, 0);
        for (String i18nKey : dictionary.getKeys()) {
            String[] keyParts = StringUtils.split(i18nKey, '.');
            addNodes(root, i18nKey, keyParts, 0);
        }
        return root;
    }

    private void addNodes(Node parent, String i18nKey, String[] keyParts, int level) {
        String keyPart = keyParts[level];
        Node node = parent.ensureAndGetChild(keyPart);
        if (level < keyParts.length - 1) {
            addNodes(node, i18nKey, keyParts, level + 1);
        } else {
            node.i18nKey = i18nKey;
        }
    }

    private class Node implements Comparable<Node> {
        String keyPart;
        String i18nKey;
        int level;
        Map<String, Node> childs;

        Node(String keyPart, int level) {
            this.keyPart = keyPart;
            this.level = level;
        }

        Node ensureAndGetChild(String keyPart) {
            if (childs == null) {
                childs = new TreeMap<String, Node>();
            }
            Node child = childs.get(keyPart);
            if (child == null) {
                child = new Node(keyPart, level + 1);
                childs.put(keyPart, child);
            }
            return child;
        }

        @Override
        public int compareTo(Node o) {
            return keyPart.compareTo(o.keyPart);
        }
    }
}
