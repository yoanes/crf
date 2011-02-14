package au.com.sensis.mobile.crf.util;

import java.io.IOException;
import java.util.List;

/**
 * Default {@link ProcessStarter} that uses a {@link ProcessBuilder}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class ProcessBuilderStarterBean implements ProcessStarter {

    /**
     * {@inheritDoc}
     */
    @Override
    public Process start(final List<String> commandLine) throws IOException {
        final ProcessBuilder processBuilder = new ProcessBuilder(commandLine);
        return processBuilder.start();
    }

}
