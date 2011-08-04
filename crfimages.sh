#!/bin/bash
# CRF wrapper for gimages.sh. Mimicks what a webapp that uses CRF would create in their root
# dir. However, for CRF, you probably don't ever actually want to run this except for testing,
# since it will create a _lot_ of images that we don't really need in the CRF showcase.

./src/scripts/image-generation/gimages.sh -r showcase/web/uiresources -i5 -m5 -p "$1"
