
target?=dev

VERSION:=$(shell git describe --tags --always --dirty='+')
BUILDTIME:=$(shell date -u +%Y%m%dT%H:%M:%SZ)

ACTIONS=step-forward step-backward step-left step-right \
		turn-clockwise turn-anticlockwise \
		pick-up put-down shoot

SRCS=$(wildcard src/*.html src/*.css src/*.js src/*.png src/*.ttf)
OUTDIR=built/$(target)

BUILT:=$(SRCS:src/%=$(OUTDIR)/%) \
	   $(ACTIONS:%=$(OUTDIR)/icons/%.svg) \
       $(ACTIONS:%=$(OUTDIR)/audio/actions/%.wav) \
       $(ACTIONS:%=$(OUTDIR)/audio/actions/%.mp3)

md5:=$(shell which md5sum || which md5)

all: $(OUTDIR)/robots.manifest

$(OUTDIR)/%.css: src/%.css node_modules/.bin/postcss
	@mkdir -p $(dir $@)
	node_modules/.bin/postcss --use autoprefixer --output $@ $<

$(OUTDIR)/%.mp3: $(OUTDIR)/%.wav
	@mkdir -p $(dir $@)
	lame -h -b 192 $< $@

$(OUTDIR)/icons/%.svg: artwork/actions.svg tools/svgx
	@mkdir -p $(dir $@)
	tools/svgx $< $* > $@

$(OUTDIR)/audio/actions/%.wav:
	@mkdir -p $(dir $@)
	echo "$*" | tr - ' ' | espeak -v english_rp --stdin -w $@

$(OUTDIR)/%.html: src/%.html built/version-$(VERSION).txt
	@mkdir -p $(dir $@)
	sed -e "s/{{version}}/$(VERSION)/g" -e "s/{{buildtime}}/$(BUILDTIME)/g" $< > $@

built/version-$(VERSION).txt:
	@mkdir -p $(dir $@)
	touch $@

$(OUTDIR)/%: src/%
	@mkdir -p $(dir $@)
	cp $< $@

$(OUTDIR)/robots.manifest: $(BUILT)
	@mkdir -p $(dir $@)
	echo CACHE MANIFEST > $@
	printf "# " >> $@
	cat $^ | $(md5) $(md5flags) | cut -d " " -f 1 >> $@
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

tmp:
	echo $(md5) $(md5flags)
