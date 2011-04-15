########################################################
## ENVIRONMENT : TST
########################################################

# True if the loaded ui configuration should be cached or not. False indicates that it will be dynamically
# reloaded to reflect changes. 
env.cache.ui.configuration=true
env.cache.resources=true

# If a resource cache entry corresponds to no resources found, this is the maximum number of times
# that attempts will be made to refresh the entry (and thus conclude that the resources really, 
# really cannot be found). This allows for some level of recovery if the original request could not
# be resolved due to a transient file system error.
# env.cache.resources.not.found.refresh.count.update.milliseconds will limit the rate at which
# the refresh count is updated.
env.cache.resources.not.found.max.refresh.count=5

# Used in conjunction with env.cache.resources.not.found.max.refresh.count, this value is
# the minimum time delay to wait before updating the refresh count. For example, if this is 
# set to 60000 milliseconds, we effectively allow a maximum of one refresh to be counted every 
# 60 seconds, regardless of how many attempts were truly made. This ensures that the recovery 
# attempts will span a non-trivial amount of time.
env.cache.resources.not.found.refresh.count.update.milliseconds=120000

# True if you want MBeans registered for filling the caches with arbitrary numbers of elements.
# Useful for analysing memory consumption.
env.cache.resources.fillers.enabled=false

# Period (in milliseconds) over which to log stats about the resource caches. 
env.cache.stats.logging.period.milliseconds=1200000

# Allow browser/proxy caching of UI resource files. Always true for non-development environments.
env.downstream.caching.enabled=true

# True if you want CSS and JavaScript resources to be bundled.
env.bundle.resources=true

# True if you want the resolved resource graph to be logged. 
env.debug.resource.graph=false

# Path to the Graphics Magick executable if you are using the GraphicsMagickImageTransformationFactoryBean.
# TODO: need Graphics Magick installed for this environment.
env.image.transformation.graphics.magick.path=gm