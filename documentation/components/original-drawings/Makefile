IMAGES = slider-example1 field-interface-v8 field-diagram \
	textfield-diagram component-diagram component-abstractions

SRCIMAGES := $(foreach file, $(IMAGES), $(file).svg)
TRGIMAGES_HI := $(foreach file, $(IMAGES), ../img/$(file)-hi.png)

images: $(TRGIMAGES_HI) FORCE
# Just do low now $(TRGIMAGES_LO)

$(TRGIMAGES_HI): ../img/%-hi.png: %.svg
	inkscape --export-png $@ --export-dpi=300 --export-area-page $<

FORCE:
