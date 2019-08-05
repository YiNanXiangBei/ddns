package org.yinan.ddns.monitor.entity;

/**
 * @author yinan
 * @date 19-8-4
 */
public class Meter extends AbstractMe {
    private Long count;

    private Double m15_rate;

    private Double m1_rate;

    private Double m5_rate;

    private Double mean_rate;

    private String units;

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Double getM15_rate() {
        return m15_rate;
    }

    public void setM15_rate(Double m15_rate) {
        this.m15_rate = m15_rate;
    }

    public Double getM1_rate() {
        return m1_rate;
    }

    public void setM1_rate(Double m1_rate) {
        this.m1_rate = m1_rate;
    }

    public Double getM5_rate() {
        return m5_rate;
    }

    public void setM5_rate(Double m5_rate) {
        this.m5_rate = m5_rate;
    }

    public Double getMean_rate() {
        return mean_rate;
    }

    public void setMean_rate(Double mean_rate) {
        this.mean_rate = mean_rate;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Meter{");
        sb.append("count=").append(count);
        sb.append(", m15_rate=").append(m15_rate);
        sb.append(", m1_rate=").append(m1_rate);
        sb.append(", m5_rate=").append(m5_rate);
        sb.append(", mean_rate=").append(mean_rate);
        sb.append(", units='").append(units).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
