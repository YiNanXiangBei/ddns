package org.yinan.ddns.monitor.entity;

/**
 * @author yinan
 * @date 19-8-6
 */
public class Message {
    private String name;

    private Object value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("{");
        sb.append("\"name\" : ").append("\"")
                .append(name).append("\",");
        sb.append("\"value\" : ").append(value);
        sb.append("}");
        return sb.toString();
    }

    public Message(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public String toArray() {
        final StringBuffer sb = new StringBuffer("[");
        sb.append(name);
        sb.append(",").append(value);
        sb.append("]");
        return sb.toString();
    }
}
