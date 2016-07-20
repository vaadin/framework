SVG =
RASTERIMAGES = architecture

RASTERSRCIMAGES := $(foreach file, $(RASTERIMAGES), $(file).svg)
RASTERIMAGES := $(foreach file, $(RASTERIMAGES), ../img/$(file).png)
SVGTRGIMAGES := $(foreach file, $(SVG), ../img/$(file).svg)

images: $(RASTERIMAGES) $(SVGTRGIMAGES) FORCE

$(RASTERIMAGES): ../img/%.png: %.svg
	inkscape --export-png $@ --export-dpi=300 --export-area-page $<

$(SVGTRGIMAGES): ../img/%.svg: %.svg FORCE
	inkscape $< --export-text-to-path -l $@

FORCE:
