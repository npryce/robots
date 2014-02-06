
target?=dev

ACTIONS=step-forward step-backward turn-clockwise turn-anticlockwise

WAVS=$(wildcard src/audio/*.wav src/audio/*/*.wav)
SRCS=$(wildcard src/*.html src/*.css src/*.js src/*.png src/*.ttf) $(WAVS)
OUTDIR=built/$(target)

BUILT:=$(SRCS:src/%=$(OUTDIR)/%) \
	   $(WAVS:src/%.wav=$(OUTDIR)/%.mp3) \
	   $(ACTIONS:%=$(OUTDIR)/icons/%.svg)

all: $(OUTDIR)/robots.manifest

$(OUTDIR)/%.css: src/%.css node_modules/autoprefixer/bin/autoprefixer
	node_modules/autoprefixer/bin/autoprefixer -o $@ $<

$(OUTDIR)/%.mp3: src/%.wav
	lame -h -b 192 $< $@

$(OUTDIR)/icons/%.svg: artwork/actions.svg tools/svgx
	@mkdir -p $(dir $@)
	tools/svgx $< $* > $@

$(OUTDIR)/%: src/%
	@mkdir -p $(dir $@)
	cp $< $@

$(OUTDIR)/robots.manifest: $(BUILT)
	@mkdir -p $(dir $@)
	echo CACHE MANIFEST > $@
	echo -n "# " >> $@
	cat $^ | md5sum -b | cut -d " " -f 1 >> $@
	echo >> $@
	for f in $(BUILT:$(OUTDIR)/%=%); do echo $$f >> $@; done

check: all node_modules/karma/bin/karma node_modules/phantomjs/package.json
	./node_modules/karma/bin/karma start --browsers PhantomJS --no-auto-watch --single-run

check-continually: all node_modules/karma/bin/karma node_modules/phantomjs/package.json
	./node_modules/karma/bin/karma start --browsers PhantomJS --auto-watch

clean: 
	rm -rf built/

distclean: clean
	rm -rf node_modules/

again: clean all

.PHONY: all clean distclean again check


SCANNED_FILES=$(SRCS)
continually:
	@while true; do \
	  clear; \
	  if not make all; \
	  then \
	      notify-send --icon=error --category=blog --expire-time=250 "Robots build broken"; \
	  fi; \
	  date; \
	  inotifywait -r -qq -e modify -e delete $(SCANNED_FILES); \
	done


served: all
	./node_modules/.bin/http-server $(OUTDIR) -p 8765 -c-1

# Install build tools

node_modules/%: package.json
	npm install # npm install for $@
	touch $@

