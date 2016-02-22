package dallastools.config;

import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by dimmaryanto on 9/24/15.
 */
@Configuration
@ComponentScan(basePackages = {"dallastools.controllers", "dallastools.services"})
@Import(value = DatabaseConfigurer.class)
@EnableTransactionManagement
public class ApplicationConfigurer {

    // untuk menaktifkan placeholder
    @Bean
    public static PropertySourcesPlaceholderConfigurer sourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource source = new ResourceBundleMessageSource();
        source.setBasename("lang.language");
        return source;
    }

    @Bean
    public ResourceBundle resourceBundle() {
        return ResourceBundle.getBundle("lang.language", Locale.getDefault());
    }

    @Bean
    public Stage primaryStage() {
        Stage stage = new Stage();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/stage/icons/logo/x24.png")));
        return stage;
    }

    @Bean
    public Stage secondStage() {
        Stage stage = new Stage();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/stage/icons/logo/x24.png")));
        return stage;
    }

    @Bean
    public HashMap hashMap() {
        return new HashMap();
    }

    @Bean
    public NumberFormat currencyInstance() {
        NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.getDefault());
        return nf;
    }

    @Bean
    public NumberFormat numberInstance() {
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.getDefault());
        return nf;
    }


}
