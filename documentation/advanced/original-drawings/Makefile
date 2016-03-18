IMAGES = three-layer-architecture

SRCIMAGES := $(foreach file, $(IMAGES), $(file).svg)
TRGIMAGES_HI := $(foreach file, $(IMAGES), ../img/$(file)-hi.png)
TRGIMAGES_LO := $(foreach file, $(IMAGES), ../img/$(file)-lo.png)

images: $(TRGIMAGES_HI) FORCE
# Just do low now $(TRGIMAGES_LO) 

$(TRGIMAGES_HI): ../img/%-hi.png: %.svg
	inkscape --export-png $@ --export-dpi=150 --export-area-drawing $<

FORCE:
