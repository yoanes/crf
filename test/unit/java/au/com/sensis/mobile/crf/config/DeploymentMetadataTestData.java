package au.com.sensis.mobile.crf.config;

import au.com.sensis.mobile.crf.config.DeploymentMetadata.Platform;

/**
 * Test data factory for {@link DeploymentMetadata}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class DeploymentMetadataTestData {

    public DeploymentMetadata createDevDeploymentMetadata() {
        final DeploymentMetadata deploymentMetadata = new DeploymentMetadata();
        deploymentMetadata.setPlatform(Platform.DEVELEOPMENT);
        return deploymentMetadata;
    }

    public DeploymentMetadata createProdDeploymentMetadata() {
        final DeploymentMetadata deploymentMetadata = new DeploymentMetadata();
        deploymentMetadata.setPlatform(Platform.PRODUCTION);
        return deploymentMetadata;
    }
}
