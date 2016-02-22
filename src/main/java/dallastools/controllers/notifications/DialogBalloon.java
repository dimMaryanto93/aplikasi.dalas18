package dallastools.controllers.notifications;

import javafx.geometry.Pos;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * Created by dimmaryanto on 06/10/15.
 */
@Component
public class DialogBalloon implements MessageSourceAware {

    private MessageSource messageSource;
    private String title;
    private String message;
    private LangSource lang;

    @Autowired
    public void setLang(LangSource lang) {
        this.lang = lang;
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @param title judul form {scene.xxx.text} diambil dari file /lang/language.properties
     */
    public void sucessedRemoved(String title) {
        setTitle(messageSource.getMessage(title, null, Locale.getDefault()));
        setMessage(messageSource.getMessage(lang.getSources(LangProperties.SUCESSED_REMOVE_WITH_PARAM),
                new Object[]{getTitle()}, Locale.getDefault()));
        showInformation();
    }

    /**
     * @param title
     * @param value
     */
    public void sucessedRemoved(String title, Object value) {
        setTitle(messageSource.getMessage(title, null, Locale.getDefault()));
        setMessage(messageSource.getMessage(lang.getSources(LangProperties.SUCESSED_REMOVE_WITH_PARAM)
                , new Object[]{value}, Locale.getDefault()));
        showInformation();
    }

    /**
     * @param title
     * @param columnProperty
     * @param value
     */
    public void sucessedUpdated(String title, String columnProperty, Object value) {
        setTitle(messageSource.getMessage(title, null, Locale.getDefault()));
        setMessage(messageSource.getMessage(lang.getSources(LangProperties.SUCESSED_UPDATE_WITH_PARAM),
                new Object[]{getTitle(), messageSource.getMessage(columnProperty, null, Locale.getDefault()), value}, Locale.getDefault()));
        showInformation();
    }

    /**
     * @param title
     * @param columnProperty
     * @param value
     * @param from
     * @param to
     */
    public void sucessedUpdated(String title, String columnProperty, Object value, Number from, Number to) {
        setTitle(messageSource.getMessage(title, null, Locale.getDefault()));
        setMessage(messageSource.getMessage(lang.getSources(LangProperties.SUCESSED_UPDATE_WITH_PARAMS),
                new Object[]{
                        messageSource.getMessage(columnProperty, null, Locale.getDefault()),
                        value,
                        from,
                        to
                }, Locale.getDefault()));
        showInformation();
    }

    /**
     * @param title judul form {scene.xxx.text} diambil dari file /lang/language.properties
     */
    public void sucessedSave(String title) {
        setTitle(messageSource.getMessage(title, null, Locale.getDefault()));
        setMessage(messageSource.getMessage(lang.getSources(LangProperties.SUCESSED_SAVE_WITH_PARAM), new Object[]{getTitle()}, Locale.getDefault()));
        showInformation();
    }

    /**
     * @param title
     * @param value
     */
    public void sucessedSave(String title, String value) {
        setTitle(messageSource.getMessage(title, null, Locale.getDefault()));
        setMessage(messageSource.getMessage(lang.getSources(LangProperties.SUCESSED_SAVE_WITH_PARAM), new Object[]{value}, Locale.getDefault()));
        showInformation();
    }

    /**
     * @param title
     * @param columnProperty
     * @param value
     */
    public void sucessedSave(String title, String columnProperty, Object value) {
        informationMessage(title, lang.getSources(LangProperties.SUCESSED_SAVE_WITH_PARAMS),
                new Object[]{
                        messageSource.getMessage(title, null, Locale.getDefault()),
                        messageSource.getMessage(columnProperty, null, Locale.getDefault()),
                        value
                });
    }

    /**
     * @param title
     * @param message
     * @param params
     */
    public void warningMessage(String title, String message, Object[] params) {
        setTitle(messageSource.getMessage(title, null, Locale.getDefault()));
        setMessage(messageSource.getMessage(message, params, Locale.getDefault()));
        showWarning();
    }

    /**
     * @param title
     * @param message
     * @param params
     */
    private void informationMessage(String title, String message, Object[] params) {
        setTitle(messageSource.getMessage(title, null, Locale.getDefault()));
        setMessage(messageSource.getMessage(message, params, Locale.getDefault()));
        showInformation();
    }

    /**
     * @param title
     * @param value
     */
    public void warningPaidMessage(String title, Object value) {
        warningMessage(title, "javafx.notification.warning.paid", new Object[]{value});
    }

    public void warningSendingMessage(String title, String columnProperty, Object value) {
        warningMessage(title, columnProperty, new Object[]{value});
    }

    /**
     * @param title
     * @param value
     */
    public void warningExpiredMessage(String title, Number value) {
        warningMessage(title, "javafx.notification.warning.expired", new Object[]{value});
    }

    public void warningCantRemovedSendMessage(String title, Object value) {
        warningMessage(title, "javafx.notification.warning.cant.remove.sent", new Object[]{value});
    }

    public void warningNotNullMessage(String title, Object value) {
        warningMessage(title, "javafx.notification.warning.notnull", new Object[]{value});
    }

    public void warningHasBeenProcessed(String title, Object value) {
        warningMessage(title, "javafx.action.has.been.processed", new Object[]{value});
    }

    public void warningEmptyMessage(String title, String columnProperty) {
        setTitle(messageSource.getMessage(title, null, Locale.getDefault()));
        setMessage(messageSource.getMessage(lang.getSources(LangProperties.IS_EMPTY_WITH_PARAM), new Object[]{
                messageSource.getMessage(columnProperty, null, Locale.getDefault())
        }, Locale.getDefault()));
        showWarning();
    }

    public void warningAuthetication(String columnProperty) {
        setTitle(messageSource.getMessage(lang.getSources(LangProperties.DATA_AN_ACCOUNT), null, Locale.getDefault()));
        setMessage(messageSource.getMessage(columnProperty, null, Locale.getDefault()));
        showWarning();
    }

    public void warningNotEnough(String title, String value) {
        setTitle(messageSource.getMessage(title, null, Locale.getDefault()));
        setMessage(messageSource.getMessage(lang.getSources(LangProperties.NOT_ENOUGH_WITH_PARAM), new Object[]{
                messageSource.getMessage(value, null, Locale.getDefault())
        }, Locale.getDefault()));
        showWarning();
    }

    public void warningNotEnoughSending(String title, String value) {
        setTitle(messageSource.getMessage(title, null, Locale.getDefault()));
        setMessage(messageSource.getMessage("javafx.notification.warning.not-enough-sending-item", new Object[]{
                value
        }, Locale.getDefault()));
        showWarning();
    }


    public void showInformation() {
        Notifications.create()
                .title(getTitle())
                .text(getMessage())
                .hideAfter(Duration.seconds(2.5))
                .hideCloseButton()
                .position(Pos.BOTTOM_RIGHT)
                .showInformation();
    }

    public void showWarning() {
        Notifications.create()
                .title(getTitle())
                .text(getMessage())
                .position(Pos.BOTTOM_RIGHT)
                .hideAfter(Duration.seconds(5))
                .hideCloseButton()
                .showWarning();
    }
}
