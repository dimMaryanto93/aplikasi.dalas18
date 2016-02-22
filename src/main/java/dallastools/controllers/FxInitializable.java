package dallastools.controllers;

import javafx.fxml.Initializable;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSourceAware;

/**
 * Created by dimmaryanto on 16/10/15.
 */
public interface FxInitializable extends Initializable, ApplicationContextAware, MessageSourceAware {

    void doClose();

}
