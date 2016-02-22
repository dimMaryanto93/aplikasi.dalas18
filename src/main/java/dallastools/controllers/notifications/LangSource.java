package dallastools.controllers.notifications;

import org.springframework.stereotype.Component;

/**
 * Created by dimmaryanto on 24/12/15.
 */
@Component
public class LangSource {
    private LangProperties sources;

    public LangSource() {
    }

    public LangSource(LangProperties sources) {
        this.sources = sources;
    }

    public String getSources(LangProperties source) {
        return source.getValue();
    }

    @Override
    public String toString() {
        return sources.getValue();
    }
}
