package au.com.sensis.mobile.crf.config;

/**
 * Holds either a {@link Group} or a {@link GroupImport}. One and only one of
 * these can be non-null.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public interface GroupOrImport {

    /**
     * @return the encapsulated {@link Group}.
     */
    Group getGroup();

    /**
     * @return true if {@link #getGroup()} is non-null.
     */
    boolean isGroup();

    /**
     * @return the encapsulated {@link GroupImport}.
     */
    GroupImport getGroupImport();

    /**
     * @return true if {@link #getGroupImport()} is non-null.
     */
    boolean isGroupImport();
}
