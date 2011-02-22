package au.com.sensis.mobile.crf.presentation.tag;

import java.io.IOException;
import java.util.Date;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import au.com.sensis.mobile.crf.service.ImageResourceBean;
import au.com.sensis.mobile.crf.service.Resource;
import au.com.sensis.mobile.crf.service.ResourceResolverEngine;
import au.com.sensis.wireless.common.volantis.devicerepository.api.Device;

/**
 * Facade to an image tag that uses the Content Rendering Framework to resolve
 * the resource path set into {@link #setSrc(String)}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class ImageTag extends AbstractTag {

    private String src;

    /**
     * Name of the (optional) device property that specifies an extra ratio to apply to a
     * scaled image. This originated from the iphone 4 retina display. Even though the device
     * repository pixel width of the screen is 320, we actually have to apply an extra ratio
     * for the (real) 640 pixel wide retina display.
     */
    public static final String IMAGE_RATIO_DEVICE_PROPERTY_NAME = "custom.crf.image.ratio";

    /**
     * {@inheritDoc}
     */
    @Override
    // TODO: validate that abstract path has correct extension.
    public final void doTag() throws JspException, IOException {
        validatePathAttribute(getSrc());

        final Resource resource =
            getResourceResolverEngine().getResource(getDevice(),
                    getSrc());



        if (resource != null) {
            doTagWhenResourceFound(resource);
        } else {
            doTagWhenResourceNotFound();
        }
    }

    private void doTagWhenResourceFound(final Resource resource) throws IOException, JspException {
        if (resource.newPathEndsWithDotNull()) {
            writeBodyContent();
        } else {
            writeSingleImageTag(getJspContext().getOut(), resource);
        }
    }

    private void doTagWhenResourceNotFound() throws IOException {
        writeSingleBrokenImageTag(getJspContext().getOut());
    }

    private void writeSingleImageTag(final JspWriter jspWriter,
            final Resource resource) throws IOException {

        final ImageResourceBean imageResource = (ImageResourceBean) resource;

        writeSingleImageTag(jspWriter, imageResource.getNewPath(), imageResource.getImageWidth(),
                imageResource.getImageHeight());
    }

    private void writeSingleBrokenImageTag(final JspWriter jspWriter) throws IOException {
        jspWriter.print("<img ");

        jspWriter.print("src=\"" + getTagDependencies().getClientPathPrefix()
                + getTagDependencies().getDeploymentMetadata().getVersion() + "/" + getSrc()
                + "\" ");

        writeDynamicTagAttributes(jspWriter);

        jspWriter.print("/>");
    }

    private void writeSingleImageTag(final JspWriter jspWriter,
            final String imageSrc, final int imageWidth, final int imageHeight) throws IOException {
        jspWriter.print("<img ");

        jspWriter.print("src=\"" + getTagDependencies().getClientPathPrefix()
                + imageSrc + getUniqueRequestParam() + "\" ");

        writeImageWidth(jspWriter, imageWidth, imageHeight);

        writeDynamicTagAttributes(jspWriter);

        jspWriter.print("/>");
    }

    private void writeImageWidth(final JspWriter jspWriter, final int imageWidth,
            final int imageHeight) throws IOException {

        if ((imageWidth != 0) && (imageHeight != 0)) {

            int width = imageWidth;
            int height = imageHeight;

            // imageRatio is non-null for some devices like iPhone 4. This
            // device has a high-res
            // screen and we actually use images twice the size that they should
            // be displayed -
            // so we therefore need to halve the size that's displayed.
            final Integer imageRatio = getImageRatioNotNull(getDevice());
            width /= imageRatio;
            height /= imageRatio;

            jspWriter.print("width=\"" + width + "\" ");
            jspWriter.print("height=\"" + height + "\" ");
        }
    }

    private void writeDynamicTagAttributes(final JspWriter jspWriter) throws IOException {
        for (final DynamicTagAttribute attribute : getDynamicAttributes()) {
            jspWriter.print(attribute.getLocalName() + "=\""
                    + attribute.getValue() + "\" ");
        }
    }

    private void writeBodyContent() throws JspException, IOException {
        if (getJspBody() != null) {
            getJspBody().invoke(getJspContext().getOut());
        }
    }

    private ImageTagDependencies getTagDependencies() {
        final PageContext pc = (PageContext) getJspContext();

        final WebApplicationContext webApplicationContext =
            WebApplicationContextUtils.getRequiredWebApplicationContext(pc
                    .getServletContext());
        return (ImageTagDependencies) webApplicationContext
        .getBean(ImageTagDependencies.BEAN_NAME);
    }

    private String getUniqueRequestParam() {

        String uniqueRequestParam = StringUtils.EMPTY;

        if (!getTagDependencies().getDeploymentMetadata().isDownstreamCachingEnabled()) {
            uniqueRequestParam = "?" + new Date().getTime();
        }

        return uniqueRequestParam;
    }

    /**
     * @return the {@link ResourceResolverEngine}.
     */
    private ResourceResolverEngine getResourceResolverEngine() {
        return getTagDependencies().getResourceResolverEngine();
    }

    /**
     * @return the src
     */
    public final String getSrc() {
        return src;
    }

    /**
     * @param src the src to set.
     */
    public final void setSrc(final String src) {
        this.src = src;
    }

    private Integer getImageRatioNotNull(final Device device) {
        Integer imageRatio = device.getPropertyAsInteger(IMAGE_RATIO_DEVICE_PROPERTY_NAME);
        if (imageRatio == null) {
            imageRatio = 1;
        }
        return imageRatio;
    }
}
