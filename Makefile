
target?=dev

SRCS:=$(wildcard src/*.html src/*.css src/*.js src/*.png src/audio/*.wav src/audio/*/*.wav)
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

check: all node_modules/karma/bin/karma node_modules/phantomjs/package.json
	./node_modules/karma/bin/karma start --browsers PhantomJS --no-auto-watch --single-run

clean: 
	rm -rf built/

distclean: clean
	rm -rf node_modules

again: clean all

.PHONY: all clean distclean again check


SCANNED_FILES=$(SRCS)
continually:
	@while true; do \
	  clear; \
	  if not make all; \
	  then \
	      notify-send --icon=error --category=blog --expire-time=250 "Deft build broken"; \
	  fi; \
	  date; \
	  inotifywait -r -qq -e modify -e delete $(SCANNED_FILES); \
	done


# Install build tools

node_modules/%: package.json
	npm install # npm install for $@
	touch $@

