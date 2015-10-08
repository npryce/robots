Building
--------

On a Mac, install Homebrew and then run the bootstrap.sh scripts to install essential tools.

Ensure you have the following programs installed: make, python (2.x), git, lame, espeak.
On Ubuntu Linux these can be installed with:

    sudo apt-get install git build-essential lame python espeak

Get the code by typing:

    git clone https://github.com/npryce/robots.git
    cd robots

To build, use the command:

    make

Output ends up in built/dev/

Running
-------

Open built/dev/robots.html in a modern web browser (Firefox, Chrome, Safari).

For example, type this command:

    firefox built/dev/robots.html

