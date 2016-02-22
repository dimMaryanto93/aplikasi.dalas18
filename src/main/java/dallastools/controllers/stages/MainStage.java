package dallastools.controllers.stages;

import dallastools.actions.HomeAction;
import dallastools.controllers.notifications.LangProperties;
import dallastools.controllers.notifications.LangSource;
import javafx.stage.Stage;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * Created by dimmaryanto on 9/24/15.
 */
@Component
public class MainStage implements ApplicationContextAware, MessageSourceAware {

    private Stage primaryStage;

    private StageRunner stageRunner;
    private LangSource lang;
    private MessageSource messageSource;
    private ApplicationContext applicationContext;

    @Autowired
    public void setLang(LangSource lang) {
        this.lang = lang;
    }

    @Autowired
    public void setStageRunner(StageRunner stageRunner) {
        this.stageRunner = stageRunner;
    }

    @Autowired
    public void setPrimaryStage(@Qualifier(value = "primaryStage") Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    private void setTitle(String value) {
        this.primaryStage.setTitle(value);
    }

    @Bean
    public HomeAction homeAction() {
        HomeAction home = (HomeAction) stageRunner.getController(getClass(), this.primaryStage, "/stage/scene/home.fxml");
        setTitle(messageSource.getMessage(lang.getSources(LangProperties.SYSTEM_INFORMATION_TRANSACTION_OF_INCOME_AND_EXPENDITUR),
                null, Locale.getDefault()));
        this.primaryStage.setResizable(true);
        this.primaryStage.centerOnScreen();
        this.primaryStage.show();
        return home;
    }

    @Override
    public void setApplicationContext(ApplicationContext arg0) throws BeansException {
        this.applicationContext = arg0;
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }
}
