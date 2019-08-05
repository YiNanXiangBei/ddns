package org.yinan.ddns.monitor.entity;

/**
 * @author yinan
 * @date 19-8-4
 */
public class Counter extends AbstractMe {

    private Long count;

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Counter{");
        sb.append("count=").append(count);
        sb.append('}');
        return sb.toString();
    }
}
