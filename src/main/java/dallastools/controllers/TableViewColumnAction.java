package dallastools.controllers;

import dallastools.controllers.notifications.LangProperties;
import dallastools.controllers.notifications.LangSource;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * Created by dimmaryanto on 08/10/15.
 */
@Component
public class TableViewColumnAction implements MessageSourceAware {

    private Hyperlink updateLink;
    private Hyperlink deleteLink;
    private Button detailLink;
    private ToggleButton onOff;
    private CheckBox checklist;
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

    public Hyperlink getUpdateLink() {
        return updateLink;
    }

    public Hyperlink getDeleteLink() {
        return deleteLink;
    }

    public Button getDetailLink() {
        return detailLink;
    }

    public ToggleButton getOnOff() {
        return onOff;
    }

    public CheckBox getChecklist() {
        return checklist;
    }

    public Node getSingleButtonTableModel(String textOfButton) {
        this.detailLink = new Button();
        this.detailLink.setPrefSize(70, 20);
        this.detailLink.setText(messageSource.getMessage(textOfButton, null, Locale.getDefault()));
        this.detailLink.setTextAlignment(TextAlignment.CENTER);
        return this.detailLink;
    }

    public Node getSingleHyperlinkTableModel(String textOfLink) {
        this.deleteLink = new Hyperlink();
        this.deleteLink.setText(messageSource.getMessage(textOfLink, null, Locale.getDefault()));
        return deleteLink;
    }

    public Node getMasterDetailTableModel() {
        HBox box = new HBox(5);
        box.setAlignment(Pos.CENTER);

        this.deleteLink = new Hyperlink();
        this.deleteLink.setText(messageSource.getMessage(lang.getSources(LangProperties.DELETE), null, Locale.getDefault()));
        this.deleteLink.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.TRASH_ALT));
        this.deleteLink.setTextFill(Color.RED);

        this.detailLink = new Button();
        this.detailLink.setText(messageSource.getMessage(lang.getSources(LangProperties.VIEW), null, Locale.getDefault()));
        this.detailLink.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.FOLDER_ALTPEN_ALT));

        this.updateLink = new Hyperlink();
        this.updateLink.setText(messageSource.getMessage(lang.getSources(LangProperties.UPDATE), null, Locale.getDefault()));
        this.updateLink.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.EDIT));
        this.updateLink.setTextFill(Color.YELLOWGREEN);

        box.getChildren().add(detailLink);
        box.getChildren().add(updateLink);
        box.getChildren().add(deleteLink);

        return box;
    }

    public Node getDefautlTableModel() {
        HBox box = new HBox(5);
        box.setAlignment(Pos.CENTER);

        updateLink = new Hyperlink();
        updateLink.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.EDIT));
        updateLink.setText(messageSource.getMessage(lang.getSources(LangProperties.UPDATE), null, Locale.getDefault()));
        updateLink.setTextFill(Color.YELLOWGREEN);

        deleteLink = new Hyperlink();
        deleteLink.setText(messageSource.getMessage(lang.getSources(LangProperties.DELETE), null, Locale.getDefault()));
        deleteLink.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.TRASH_ALT));
        deleteLink.setTextFill(Color.RED);

        box.getChildren().add(updateLink);
        box.getChildren().add(deleteLink);
        return box;
    }


}
