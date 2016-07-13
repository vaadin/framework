SVG =
RASTERIMAGES = layout-schematic

RASTERSRCIMAGES := $(foreach file, $(RASTERIMAGES), $(file).svg)
RASTERIMAGES_HI := $(foreach file, $(RASTERIMAGES), ../img/$(file)-hi.png)
SVGTRGIMAGES := $(foreach file, $(SVG), ../img/$(file).svg)

images: $(RASTERIMAGES_HI) $(SVGTRGIMAGES) FORCE
# Just do low now $(TRGIMAGES_LO)

$(RASTERIMAGES_HI): ../img/%-hi.png: %.svg
	inkscape --export-png $@ --export-dpi=300 --export-area-drawing $<

$(SVGTRGIMAGES): ../img/%.svg: %.svg FORCE
	inkscape $< --export-text-to-path -l $@

FORCE:
