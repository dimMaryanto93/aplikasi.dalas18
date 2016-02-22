package dallastools.controllers.notifications;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * Created by dimmaryanto on 23/12/15.
 */
@Component
@Deprecated
public class ActionMessages implements MessageSourceAware {
    private MessageSource messageSource;

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    private String getValue(String property) {
        return messageSource.getMessage(property, null, Locale.getDefault());
    }

    public String checked() {
        return getValue("javafx.action.checkbox.checked");
    }

    public String unchecked() {
        return getValue("javafx.action.checkbox.unchecked");
    }

    public String repayment() {
        return getValue("javafx.action.repayment");
    }

    public String not() {
        return getValue("javafx.notification.warning.not");
    }

    public String notYet() {
        return getValue("javafx.action.not.yet");
    }

    public String remove() {
        return getValue("javafx.action.remove");
    }

    public String deleted() {
        return getValue("javafx.action.delete");
    }

    public String beingProcessed() {
        return getValue("javafx.action.being.processed");
    }

    public String hasBeenProcessed() {
        return getValue("javafx.notification.warning.has.been.processed");
    }

    public String payoff() {
        return getValue("javafx.action.payoff");
    }

    public String payment() {
        return getValue("javafx.action.payment");
    }
}
