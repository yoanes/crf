package au.com.sensis.mobile.crf.presentation;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.HttpServletBean;

import au.com.sensis.mobile.crf.service.FileIoFacadeFactory;
import au.com.sensis.mobile.crf.service.MappedResourcePath;
import au.com.sensis.mobile.crf.service.ResourceResolverEngine;
import au.com.sensis.wireless.common.volantis.devicerepository.api.Device;
import au.com.sensis.wireless.web.mobile.MobileContext;

/**
 * Maps abstract image paths ( eg. /resources/images/common/background.image) to
 * concrete, device specific image paths (eg. common/iphone/background.png),
 * then writes the image to the {@link HttpServletResponse}. The actual path
 * mapping is performed by delegating to a
 * {@link ResourceResolverEngine}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class ImageServlet extends HttpServletBean {

    private static final long serialVersionUID = 1L;
    private String imageServletCollaboratorsMementoBeanName;
    private ImageServletCollaboratorsMemento imageServletCollaboratorsMemento;

    /**
     * Lookup the {@link WebApplicationContext} and retrieve the
     * {@link ImageServletCollaboratorsMemento} from it.
     *
     * {@inheritDoc}
     */
    @Override
    public void init(final ServletConfig config) throws ServletException {
        super.init(config);

        final WebApplicationContext webApplicationContext =
                WebApplicationContextUtils
                        .getRequiredWebApplicationContext(config
                                .getServletContext());
        setImageServletCollaboratorsMemento((ImageServletCollaboratorsMemento) webApplicationContext
                .getBean(getImageServletCollaboratorsMementoBeanName()));

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doGet(final HttpServletRequest request,
            final HttpServletResponse response) throws ServletException,
            IOException {

        if (requestedResourcePathHasCorrectPrefix(request)) {

            final MappedResourcePath mappedResourcePath =
                    getImageServletCollaboratorsMemento().getResourceResolverEngine()
                            .getResourcePath(getDevice(request),
                                    getRequestedResourcePath(request));

            if (mappedResourcePath != null) {
                setResponseHeaders(response, mappedResourcePath);
                writeImageToResponse(response, mappedResourcePath);
            } else {
                setFileNotFoundResponseStatus(response);
            }

        } else {
            throw new ServletException(
                    "Requests for abstract images should have a servlet path starting with '"
                    + getImageServletCollaboratorsMemento()
                            .getImagesClientPathPrefix()
                    + "'. However, servlet path is '" + request.getServletPath()
                    + "'");
        }

    }

    private boolean requestedResourcePathHasCorrectPrefix(final HttpServletRequest req) {
        return (req.getServletPath() != null)
                && req.getServletPath().startsWith(
                        getImageServletCollaboratorsMemento()
                                .getImagesClientPathPrefix());
    }

    private void setFileNotFoundResponseStatus(final HttpServletResponse resp) {
        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    private void writeImageToResponse(final HttpServletResponse resp,
            final MappedResourcePath mappedResourcePath) throws IOException {
        FileIoFacadeFactory.getFileIoFacadeSingleton()
                .writeFileAndCloseStream(
                        mappedResourcePath.getNewResourceFile(),
                        resp.getOutputStream());
    }

    private void setResponseHeaders(final HttpServletResponse resp,
            final MappedResourcePath mappedResourcePath) {
        resp.setContentType(getMimeType(mappedResourcePath));
        resp.setContentLength(mappedResourcePath.getFileLengthAsInt());
        resp.setDateHeader("Last-Modified", mappedResourcePath
                .getNewResourceFile().lastModified());
    }

    private String getMimeType(final MappedResourcePath mappedResourcePath) {
        return getServletContext().getMimeType(
                mappedResourcePath.getNewResourceFile().getPath());
    }

    private String getRequestedResourcePath(final HttpServletRequest req) {
        return StringUtils.substringAfter(req.getServletPath(),
                getImageServletCollaboratorsMemento()
                        .getImagesClientPathPrefix());
    }

    private Device getDevice(final HttpServletRequest req) {
        final MobileContext context =
                (MobileContext) req.getSession().getAttribute(
                        MobileContext.MOBILE_CONTEXT_KEY);
        return context.getDevice();
    }

    /**
     * @param beanName
     *            Name of the {@link ImageServletCollaboratorsMemento} bean to
     *            obtain from the Spring context.
     */
    public void setImageServletCollaboratorsMementoBeanName(
            final String beanName) {
        imageServletCollaboratorsMementoBeanName = beanName;
    }

    private String getImageServletCollaboratorsMementoBeanName() {
        return imageServletCollaboratorsMementoBeanName;
    }

    private ImageServletCollaboratorsMemento getImageServletCollaboratorsMemento() {
        return imageServletCollaboratorsMemento;
    }

    private void setImageServletCollaboratorsMemento(
            final ImageServletCollaboratorsMemento imageServletCollaboratorsMemento) {
        this.imageServletCollaboratorsMemento =
                imageServletCollaboratorsMemento;
    }

    /**
     * Simple memento encapsulating the singleton collaborators of this
     * {@link ImageServlet} An instance of this memento will be programmatically
     * retrieved from the Spring context.
     */
    public static class ImageServletCollaboratorsMemento {

        private final ResourceResolverEngine resourceResolverEngine;
        private final String imagesClientPathPrefix;

        /**
         * Constructor.
         *
         * @param resourceResolverEngine
         *            {@link ResourceResolverEngine} to use
         *            to resolve abstract image paths to real paths.
         * @param imagesClientPathPrefix
         *            Client side prefix for all image paths.
         *            eg. "/resources/images".
         */
        public ImageServletCollaboratorsMemento(
                final ResourceResolverEngine resourceResolverEngine,
                final String imagesClientPathPrefix) {
            this.resourceResolverEngine = resourceResolverEngine;
            this.imagesClientPathPrefix = imagesClientPathPrefix;
        }

        /**
         * @return resourceResolverEngine
         *         {@link ResourceResolverEngine} to use to
         *         resolve abstract image paths to real paths.
         */
        public ResourceResolverEngine getResourceResolverEngine() {
            return resourceResolverEngine;
        }

        /**
         * @return imagesClientPathPrefix Client side prefix for all image paths.
         *         eg. "/resources/images".
         */
        public String getImagesClientPathPrefix() {
            return imagesClientPathPrefix;
        }
    }

}
