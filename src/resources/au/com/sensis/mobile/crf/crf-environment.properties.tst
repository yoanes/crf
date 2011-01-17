########################################################
## ENVIRONMENT : TST
########################################################

# True if the loaded ui configuration should be cached or not. False indicates that it will be dynamically
# reloaded to reflect changes. 
env.cache.ui.configuration=true
env.cache.resources=true

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