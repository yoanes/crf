package au.com.sensis.mobile.crf.config;

import au.com.sensis.mobile.crf.config.DeploymentMetadata.Platform;

/**
 * Test data factory for {@link DeploymentMetadata}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class DeploymentMetadataTestData {

    public DeploymentMetadata createDevDeploymentMetadata() {
        final DeploymentMetadata deploymentMetadata =
                new DeploymentMetadata(Platform.DEVELEOPMENT, "0.1.5");
        return deploymentMetadata;
    }

    public DeploymentMetadata createProdDeploymentMetadata() {
        final DeploymentMetadata deploymentMetadata =
                new DeploymentMetadata(Platform.PRODUCTION, "0.1.5");
        return deploymentMetadata;
    }
}
