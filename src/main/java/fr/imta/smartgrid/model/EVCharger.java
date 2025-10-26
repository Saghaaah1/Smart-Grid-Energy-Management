package fr.imta.smartgrid.model;

import io.vertx.core.json.JsonObject;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "ev_charger")
@PrimaryKeyJoinColumn(name = "id")
public class EVCharger extends Consumer {
    private Integer voltage;
    private Integer maxAmp;

    @Column(name = "connector_type")
    private String type;

    public Integer getVoltage() {
        return voltage;
    }

    public void setVoltage(Integer voltage) {
        this.voltage = voltage;
    }

    public Integer getMaxAmp() {
        return maxAmp;
    }

    public void setMaxAmp(Integer maxAmp) {
        this.maxAmp = maxAmp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    protected void extendSpecificFields(JsonObject json) {
    json.put("max_power", this.getMaxPower());
    json.put("type", this.getType());
    json.put("voltage", this.getVoltage());
    json.put("maxAmp", this.getMaxAmp());
    }


}
