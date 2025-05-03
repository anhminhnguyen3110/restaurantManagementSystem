package com.restaurant.validators;

import com.restaurant.events.ErrorEvent;
import com.restaurant.pubsub.ErrorPubSubService;
import com.restaurant.pubsub.PubSubService;

import java.util.List;

public interface Validator<C, U> {
    List<String> validateCreate(C dto);

    List<String> validateUpdate(U dto);

    default boolean triggerCreateErrors(C dto) {
        List<String> errors = validateCreate(dto);
        if (!errors.isEmpty()) {
            PubSubService pub = ErrorPubSubService.getInstance();
            StringBuilder msg = new StringBuilder("Validation errors:");
            for (String e : errors) msg.append("\n").append(e);
            pub.publish(new ErrorEvent(msg.toString()));
            return false;
        }
        return true;
    }

    default boolean triggerUpdateErrors(U dto) {
        List<String> errors = validateUpdate(dto);
        if (!errors.isEmpty()) {
            PubSubService pub = ErrorPubSubService.getInstance();
            StringBuilder msg = new StringBuilder("Validation errors:");
            for (String e : errors) msg.append("\n").append(e);
            pub.publish(new ErrorEvent(msg.toString()));
            return false;
        }
        return true;
    }
}
