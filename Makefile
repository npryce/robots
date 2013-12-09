
target?=dev

SRCS=$(wildcard src/*.html src/*.css src/*.js src/*.png src/audio/*.wav src/audio/*/*.wav)
OUTDIR=built/$(target)

BUILT=$(SRCS:src/%=$(OUTDIR)/%)

all: $(OUTDIR)/robots.manifest

$(OUTDIR)/%.css: src/%.css node_modules/autoprefixer/bin/autoprefixer
	node_modules/autoprefixer/bin/autoprefixer -o $@ $<

$(OUTDIR)/%: src/%
	@mkdir -p $(dir $@)
	cp $< $@

$(OUTDIR)/robots.manifest: $(BUILT)
	@mkdir -p $(dir $@)
	echo CACHE MANIFEST > $@
	echo >> $@
	for f in $(BUILT:$(OUTDIR)/%=%); do echo $$f >> $@; done

check: all
	./node_modules/karma/bin/karma start --browsers PhantomJS --no-auto-watch --single-run

clean: 
	rm -rf built/

distclean: clean
	rm -rf node_modules

again: clean all

.PHONY: all clean distclean again check


# Install build tools

node_modules/autoprefixer/bin/autoprefixer:
	npm install autoprefixer

