
SRCS=$(wildcard *.html *.css *.js *.png audio/*.wav audio/*/*.wav)

all: robots.manifest

robots.manifest: $(SRCS)
	echo CACHE MANIFEST > $@
	echo >> $@
	for f in $(SRCS); do echo $$f >> $@; done

