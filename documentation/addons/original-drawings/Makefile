IMAGES = widgetset-modes widgetset-mode-cdn widgetset-mode-local \
 widgetset-mode-fetch addon-architecture

SRCIMAGES := $(foreach file, $(IMAGES), $(file).svg)
TRGIMAGES := $(foreach file, $(IMAGES), ../img/$(file).png)

images: $(TRGIMAGES) FORCE
# Just do low now $(TRGIMAGES_LO)

$(TRGIMAGES): ../img/%.png: %.svg
	inkscape --export-png $@ --export-dpi=300 --export-area-page $<

FORCE:
