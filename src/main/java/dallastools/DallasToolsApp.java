package dallastools;

import dallastools.actions.HomeAction;
import dallastools.config.ApplicationConfigurer;
import dallastools.controllers.AutheticationLevel;
import dallastools.controllers.dataselections.LanguageSelector;
import dallastools.models.masterdata.Account;
import dallastools.models.other.Level;
import dallastools.services.ServiceOfAccount;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.scene.control.ChoiceDialog;
import javafx.stage.Stage;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Created by dimmaryanto on 9/24/15.
 */
public class DallasToolsApp extends Application {

    private static ApplicationContext context;
    private final List<LanguageSelector> languages = new ArrayList<LanguageSelector>() {
        {
            add(new LanguageSelector("Indonesia", new Locale("in", "ID")));
            add(new LanguageSelector("English", Locale.getDefault()));
        }
    };

    public static void main(String[] args) {
        launch(DallasToolsApp.class, args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        ChoiceDialog<LanguageSelector> dialog = new ChoiceDialog<>(null, languages);
        dialog.setTitle("DallasToolApp");
        dialog.setHeaderText("Pilih bahasa yang akan digunakan?");
        Optional<LanguageSelector> pilih = dialog.showAndWait();

        setUserAgentStylesheet(STYLESHEET_CASPIAN);
        Task<Object> worker = getTask();
        worker.setOnFailed(event -> {
            Dialogs.create().title("DallasToolsApp")
                    .masthead("Aplikasi akan dihentikan")
                    .message("Tidak dapat menjalan applikasi, silahkan hubungi developer!")
                    .styleClass(Dialog.STYLE_CLASS_CROSS_PLATFORM)
                    .showException(worker.getException());
            System.exit(0);
        });

        worker.setOnSucceeded(event -> {
            HomeAction home = context.getBean(HomeAction.class);
            home.doLogout();
            try {
                ServiceOfAccount serviceAccount = context.getBean(ServiceOfAccount.class);
                Boolean adminAvailabel = false;
                Iterator<Account> values = serviceAccount.findAll().iterator();
                while (values.hasNext()) {
                    Account account = values.next();
                    if (account.getLevel().equals(AutheticationLevel.getValue(Level.ADMIM))&& account.getUsername().equals("admin")) {
                        adminAvailabel = true;
                        System.out.println("admin is available");
                    }
                }
                if (!adminAvailabel) {
                    Account anAccount = new Account();
                    anAccount.setUsername("admin");
                    anAccount.setPasswd("admin");
                    anAccount.setActive(true);
                    anAccount.setFullname("Administrator");
                    anAccount.setLevel(AutheticationLevel.getValue(Level.ADMIM));
                    anAccount.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
                    serviceAccount.save(anAccount);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        if (pilih.isPresent()) {
            Locale.setDefault(dialog.getSelectedItem().getLocale());
            worker.run();
            dialog.close();
        } else {
            System.exit(0);
        }


    }

    private Task<Object> getTask() {
        return new Task<Object>() {
            Integer workDone;
            Integer workMax;

            public void setWorkDone(Integer workDone) {
                this.workDone = workDone;
            }

            public void setWorkMax(Integer workMax) {
                this.workMax = workMax;
            }

            @Override
            protected void setException(Throwable t) {
                super.setException(t);
            }

            protected Object call() throws Exception {
                context = new AnnotationConfigApplicationContext(ApplicationConfigurer.class);
                String[] names = context.getBeanDefinitionNames();
                setWorkMax(context.getBeanDefinitionCount());
                for (int i = 0; i < workMax; i++) {
                    setWorkDone(i);
                    updateProgress(workDone, workMax - 1);
                    updateMessage("Memuat Konfigurasi : " + names[workDone]);
                    System.out.println(getMessage());
                    Thread.sleep(1);
                }
                succeeded();
                return null;
            }

        };
    }
}
