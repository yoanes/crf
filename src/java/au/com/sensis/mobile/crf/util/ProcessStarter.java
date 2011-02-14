package au.com.sensis.mobile.crf.util;

import java.io.IOException;
import java.util.List;

/**
 * Simple interface to start a process.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public interface ProcessStarter {

    /**
     * Start a {@link Process} to run the given command line. The semantics of the command line
     * are identical to what is accepted by {@link ProcessBuilder}.
     *
     * @param commandLine Command line to run.
     * @return The started process.
     * @throws IOException Thrown if the process could not be started.
     */
    Process start(List<String> commandLine) throws IOException;
}
