package au.com.sensis.mobile.crf.config;

import au.com.sensis.mobile.crf.config.DeploymentVersion.Platform;

/**
 * Test data factory for {@link DeploymentVersion}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class DeploymentVersionTestData {

    public DeploymentVersion createDevDeploymentVersion() {
        final DeploymentVersion deploymentVersion = new DeploymentVersion();
        deploymentVersion.setPlatform(Platform.DEVELEOPMENT);
        return deploymentVersion;
    }

    public DeploymentVersion createProdDeploymentVersion() {
        final DeploymentVersion deploymentVersion = new DeploymentVersion();
        deploymentVersion.setPlatform(Platform.PRODUCTION);
        return deploymentVersion;
    }
}
