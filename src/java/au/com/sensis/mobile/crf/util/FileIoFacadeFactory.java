package au.com.sensis.mobile.crf.util;




/**
 * Factory to return a {@link FileIoFacade} singleton.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public final class FileIoFacadeFactory {
    private static FileIoFacade fileIoFacadeSingleton;

    static {
        restoreDefaultFileIoFacadeSingleton();
    }

    private FileIoFacadeFactory() {

    }

    /**
     * @return the fileIoFacadeSingleton
     */
    public static FileIoFacade getFileIoFacadeSingleton() {
        return fileIoFacadeSingleton;
    }

    /**
     * Change the default {@link FileIoFacade} singleton. Only to be called
     * during unit testing.
     *
     * @param fileIoFacadeSingleton
     *            the fileIoFacadeSingleton to use.
     */
    public static void changeDefaultFileIoFacadeSingleton(
            final FileIoFacade fileIoFacadeSingleton) {
        FileIoFacadeFactory.fileIoFacadeSingleton = fileIoFacadeSingleton;
    }

    /**
     * Restore the default {@link FileIoFacade} singleton. Only to be called
     * during unit testing.
     */
    public static void restoreDefaultFileIoFacadeSingleton() {
        // We actually instantiate a new instance since it contains no state.
        FileIoFacadeFactory.fileIoFacadeSingleton = new FileIoFacadeBean();
    }
}
