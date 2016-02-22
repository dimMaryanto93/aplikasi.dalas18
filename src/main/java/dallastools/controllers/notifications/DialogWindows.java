package dallastools.controllers.notifications;

import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Created by dimmaryanto on 06/10/15.
 */
@Component
public class DialogWindows implements MessageSourceAware {

    private final Integer SUCCESS_INDICATOR = 500;
    private final Integer LOADING_DATA = 100;

    private String title;
    private String header;
    private String message;
    private MessageSource messageSource;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void showError(Throwable ex) {
        Dialogs.create()
                .lightweight()
                .title(title)
                .masthead(header)
                .message(message)
                .showException(ex);
    }

    public Optional<ButtonType> showConfirmation() {
        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle(getTitle());
        dialog.setHeaderText(getHeader());
        dialog.setContentText(getMessage());
        dialog.initModality(Modality.APPLICATION_MODAL);
        return dialog.showAndWait();
    }

    public void showWarningDialog() {
        Alert dialog = new Alert(Alert.AlertType.WARNING);
        dialog.setTitle(getTitle());
        dialog.setHeaderText(getHeader());
        dialog.setContentText(getMessage());
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.show();
    }

    /**
     * @param title              {scene.xxx.text} diambil dari /lang/language.properties
     * @param value              value selected
     * @param propertyColumnName {scene.xxx.placeholder.xxx.id} diambil dari /lang/language.properties
     * @param id                 get value
     * @return
     */
    public Optional<ButtonType> confirmDelete(String title, Object value, String propertyColumnName, Object id) {
        setTitle(messageSource.getMessage(title, null, Locale.getDefault()));
        setHeader(messageSource.getMessage(new LangSource(LangProperties.QUESTION_REMOVE_WITH_PARAMS).toString(), new Object[]{
                getTitle(), value,
                messageSource.getMessage(propertyColumnName, null, Locale.getDefault()), id
        }, Locale.getDefault()));
        setMessage(messageSource.getMessage(
                new LangSource(LangProperties.QUESTION_ARE_YOU_SURE).toString()
                , null, Locale.getDefault()));
        return showConfirmation();
    }

    /**
     * @param emptyList bisa digunakan untuk tableView, listView, comboBox dan lain-lain
     * @param items     data yang diload dari database dan array
     * @param title     judul dari form {scene.xxx.text} diambil dari /lang/language.properties
     */
    public void loading(List emptyList, List items, String title) {
        setTitle(messageSource.getMessage(title, null, Locale.getDefault()));
        setHeader(messageSource.getMessage(
                new LangSource(LangProperties.PROGRESS_LOADING_WITH_PARAM).toString(),
                new Object[]{getTitle()}, Locale.getDefault()));
        setMessage("");
        loading(emptyList, items);
    }

    /**
     * @param task
     * @param title
     */
    public void loading(Task task, String title) {
        setTitle(messageSource.getMessage(title, null, Locale.getDefault()));
        setHeader(messageSource.getMessage(
                new LangSource(LangProperties.PROGRESS_LOADING_WITH_PARAM).toString(),
                new Object[]{getTitle()}, Locale.getDefault()));
        setMessage("");
        loading(task);
    }

    /**
     * @param title {scene.xxx.text} diambil dari /lang/language.properties
     * @param ex    throwing sebuah exception
     */
    public void errorLoading(String title, Throwable ex) {
        setTitle(messageSource.getMessage(title, null, Locale.getDefault()));
        setHeader(messageSource.getMessage(new LangSource(LangProperties.ERROR_LOADING_WITH_PARAM).toString(),
                new Object[]{getTitle()}, Locale.getDefault()));
        setMessage(ex.getMessage());
        showError(ex);
    }

    /**
     * @param title {scene.xxx.text} diambil dari /lang/language.properties
     * @param ex    throwing sebuah exception
     */
    public void errorSave(String title, Throwable ex) {
        setTitle(messageSource.getMessage(title, null, Locale.getDefault()));
        setHeader(messageSource.getMessage(new LangSource(LangProperties.ERROR_SAVE_WITH_PARAM).toString(), new Object[]{
                getTitle()
        }, Locale.getDefault()));
        setMessage(ex.getMessage());
        showError(ex);
    }

    /**
     * @param title
     * @param value
     * @param ex
     */
    public void errorSave(String title, Object value, Throwable ex) {
        setTitle(messageSource.getMessage(title, null, Locale.getDefault()));
        setHeader(messageSource.getMessage(new LangSource(LangProperties.ERROR_SAVE_WITH_PARAM).toString()
                , new Object[]{value}, Locale.getDefault()));
        setMessage(ex.getMessage());
        showError(ex);
    }

    /**
     * @param title          {scene.xxx.text} diambil dari /lang/language.properties
     * @param columnProperty {scene.xxx.placeholder.xxx.id} diambil dari /lang/language.properties
     * @param key            get value
     * @param ex             throwing sebuah exception
     */
    public void errorRemoved(String title, String columnProperty, Object key, Throwable ex) {
        setTitle(messageSource.getMessage(title, null, Locale.getDefault()));
        setHeader(messageSource.getMessage(new LangSource(LangProperties.ERROR_REMOVE_WITH_PARAMS).toString(),
                new Object[]{getTitle(),
                        messageSource.getMessage(columnProperty, null, Locale.getDefault()), key}, Locale.getDefault()));
        setMessage(ex.getMessage());
        showError(ex);
    }

    /**
     * @param title          {scene.xxx.text} diambil dari /lang/language.properties
     * @param columnProperty {scene.xxx.placeholder.xxx.id} diambil dari /lang/language.properties
     * @param key            get value
     * @param ex             throwing sebuah exception
     */
    public void errorUpdate(String title, String columnProperty, Object key, Throwable ex) {
        setTitle(messageSource.getMessage(title, null, Locale.getDefault()));
        setHeader(messageSource.getMessage(new LangSource(LangProperties.ERROR_UPDATE_WITH_PARAMS).toString(),
                new Object[]{getTitle(),
                        messageSource.getMessage(columnProperty, null, Locale.getDefault()), key}, Locale.getDefault()));
        setMessage(ex.getMessage());
        showError(ex);
    }

    public void errorPrint(String title, Throwable ex) {
        setTitle(messageSource.getMessage(title, null, Locale.getDefault()));
        setHeader(messageSource.getMessage(new LangSource(LangProperties.ERROR_PRINT_WITH_PARAM).toString(), new Object[]{
                getTitle()
        }, Locale.getDefault()));
        setMessage(ex.getMessage());
        showError(ex);
    }

    private Task<Object> getWorker(List emptyList, List getItems) {
        return new Task<Object>() {
            @Override
            protected void succeeded() {
                try {
                    for (int i = 0; i < 100; i++) {
                        Thread.sleep(10);
                        updateProgress(i, 99);
                        updateMessage(messageSource.getMessage(new LangSource(LangProperties.PROGRESS_FINISHED_WITH_PARAM).toString(),
                                new Object[]{i}, Locale.getDefault()));
                    }
                    super.succeeded();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected Object call() throws Exception {
                Integer row = getItems.size();
                for (int i = 0; i < row; i++) {
                    int jml = i + 1;
                    updateProgress(i, getItems.size() - 1);
                    updateMessage(messageSource.getMessage(new LangSource(LangProperties.PROGRESS_GETTING_WITH_PARAMS).toString(),
                            new Object[]{jml, row}, Locale.getDefault()));
                    emptyList.add(getItems.get(i));
                    Thread.sleep(LOADING_DATA);
                }
                succeeded();
                return null;
            }
        };
    }

    public void loading(Task aTask) {
        Task<Object> worker = aTask;
        Thread th = new Thread(worker);
        th.setDaemon(true);
        th.start();
        Dialogs.create()
                .lightweight()
                .title(title)
                .masthead(header)
                .message("")
                .styleClass(Dialog.STYLE_CLASS_CROSS_PLATFORM)
                .showWorkerProgress(worker);
    }

    public void loading(List emptyList, List items) {
        Task<Object> worker = getWorker(emptyList, items);
        Thread th = new Thread(worker);
        th.setDaemon(true);
        th.start();
        Dialogs.create()
                .lightweight()
                .title(title)
                .masthead(header)
                .message("").
                styleClass(Dialog.STYLE_CLASS_CROSS_PLATFORM).
                showWorkerProgress(worker);

    }


    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }
}
