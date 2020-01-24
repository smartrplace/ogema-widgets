package org.ogema.widgets.pushover.model;

import org.json.JSONObject;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.model.prototypes.Data;

/**
 *
 * @author jlapp
 */
public interface ReceiptInfo extends Data {
    
    BooleanResource acknowledged();
    
    TimeResource acknowledgedAt();
    
    StringResource acknowledgedBy();
    
    StringResource acknowledgedByDevice();
    
    TimeResource lastDeliveredAt();
    
    BooleanResource expired();
    
    TimeResource expiresAt();
    
    BooleanResource calledBack();
    
    TimeResource calledBackAt();
    
    default boolean isExpired() {
        return expired().isActive() && expired().getValue();
    }
    
    default boolean isAcknowledged() {
        return acknowledged().isActive() && acknowledged().getValue();
    }
    
    default void store(JSONObject receiptInfo) {
        ((BooleanResource) acknowledged().create()).setValue(receiptInfo.getInt("acknowledged") == 1);
        ((TimeResource) acknowledgedAt().create()).setValue(receiptInfo.getLong("acknowledged_at"));
        ((StringResource) acknowledgedBy().create()).setValue(receiptInfo.getString("acknowledged_by"));
        ((StringResource) acknowledgedByDevice().create()).setValue(receiptInfo.getString("acknowledged_by_device"));
        ((TimeResource) lastDeliveredAt().create()).setValue(receiptInfo.getLong("last_delivered_at"));
        ((BooleanResource) expired().create()).setValue(receiptInfo.getInt("expired") == 1);
        ((TimeResource) expiresAt().create()).setValue(receiptInfo.getLong("expires_at"));
        ((BooleanResource) calledBack().create()).setValue(receiptInfo.getInt("called_back") == 1);
        ((TimeResource) calledBackAt().create()).setValue(receiptInfo.getLong("called_back_at"));
        activate(true);
    }
    
}
