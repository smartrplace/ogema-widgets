package org.ogema.widgets.pushover.model;

import de.iwes.widgets.api.messaging.Message;
import java.time.ZonedDateTime;
import java.util.Optional;
import org.ogema.core.model.simple.StringResource;
import org.ogema.model.prototypes.Data;

/**
 *
 * @author jlapp
 */
public interface EmergencyMessage extends Data {
    
    StringResource receipt();
    
    StringResource sendTime();
    
    StringResource appToken();
    
    StringResource title();
    
    StringResource message();
    
    ReceiptInfo receiptInfo();
    
    default void storeMessage(String receipt, Message msg, String apiToken) {
        ((StringResource)receipt().create()).setValue(receipt);
        receipt().activate(false);
        ((StringResource)sendTime().create()).setValue(ZonedDateTime.now().toString());
        sendTime().activate(false);
        ((StringResource)appToken().create()).setValue(apiToken);
        appToken().activate(false);
        ((StringResource)title().create()).setValue(msg.title(null));
        title().activate(false);
        ((StringResource)message().create()).setValue(msg.message(null));
        message().activate(false);
        activate(false);
    }
    
    default Optional<ZonedDateTime> getSendTime() {
        return sendTime().isActive() ? Optional.of(ZonedDateTime.parse(sendTime().getValue())) : Optional.empty();
    }
    
    default boolean receiptExpired() {
        return getSendTime().map(t -> t.plusWeeks(1).isBefore(ZonedDateTime.now())).orElse(Boolean.FALSE);
    }
    
}
