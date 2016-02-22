package dallastools.controllers.notifications;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * Created by dimmaryanto on 18/12/15.
 */
@Component
public class ValidatorMessages implements MessageSourceAware {
    private MessageSource messageSource;

    private LangSource lang;

    @Autowired
    public void setLang(LangSource lang) {
        this.lang = lang;
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * @param columnProperty model property {scene.xxx.placeholder.xxx} diambil dari file /lang/language.properties
     * @param min
     * @param max
     * @return
     */
    public String validatorMinMax(String columnProperty, Integer min, Integer max) {
        return messageSource.getMessage(lang.getSources(LangProperties.MIN_MAX_WITH_PARAMS),
                new Object[]{
                        messageSource.getMessage(columnProperty, null, Locale.getDefault()),
                        min,
                        max
                }, Locale.getDefault());
    }

    /**
     * @param columnProperty model property {scene.xxx.placeholder.xxx} diambil dari file /lang/language.properties
     * @return
     */
    public String validatorNotNull(String columnProperty) {
        return messageSource.getMessage(lang.getSources(LangProperties.NULL_WITH_PARAM), new Object[]{
                messageSource.getMessage(columnProperty, null, Locale.getDefault())
        }, Locale.getDefault());
    }

    /**
     * @param columnProperty model property {scene.xxx.placeholder.xxx} diambil dari file /lang/language.properties
     * @return
     */
    public String validatorEmpty(String columnProperty) {
        return messageSource.getMessage(lang.getSources(LangProperties.EMPTY_WITH_PARAM), new Object[]{
                messageSource.getMessage(columnProperty, null, Locale.getDefault())
        }, Locale.getDefault());
    }

    /**
     * @param columnProperty model property {scene.xxx.placeholder.xxx} diambil dari file /lang/language.properties
     * @return
     */
    public String validatorNotSelected(String columnProperty) {
        return messageSource.getMessage(lang.getSources(LangProperties.NOT_SELECTED_WITH_PARAM), new Object[]{
                messageSource.getMessage(columnProperty, null, Locale.getDefault())
        }, Locale.getDefault());
    }

    /**
     * @param value number only
     * @return
     */
    public String validatorMin(Number value) {
        return messageSource.getMessage(lang.getSources(LangProperties.MIN_WITH_PARAM), new Object[]{
                value
        }, Locale.getDefault());
    }

    public String validatorDateNotEqualsNow() {
        return messageSource.getMessage(lang.getSources(LangProperties.DATE_NOT_EQUAL_NOW), null, Locale.getDefault());
    }
}
