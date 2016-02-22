package dallastools.controllers;

import javafx.scene.image.Image;
import org.springframework.stereotype.Component;

/**
 * Created by dimmaryanto on 13/11/15.
 */
@Component
public class ImageController {

    public final Image statusPaid = getImageResource("/stage/icons/indicator-paid.png");
    public final Image statusInRange = getImageResource("/stage/icons/indicator-in-range.png");
    public final Image statusExpired = getImageResource("/stage/icons/indicator-out-of-range.png");
    public final Image taskChecked = getImageResource("/stage/icons/task_checked.png");
    public final Image taskProgressed = getImageResource("/stage/icons/task_progressed.png");
    public final Image statusHint = getImageResource("/stage/icons/indicator-hint.png");
    public final Image statusMessage = getImageResource("/stage/icons/indicator-message.png");

    private Image getImageResource(String resource) {
        return new Image(getClass().getResourceAsStream(resource));
    }
}
