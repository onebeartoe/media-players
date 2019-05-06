
cd ~/Versioning/owner/github/onebeartoe/media-players/jukebox-client/src/main/circuit-python

CIRCUITPY_PATH=/media/roberto/CIRCUITPY/

rsync --exclude 'lib/' \
	  --exclude 'deploy.sh' \
	  --exclude 'secrets.py' \
	  --delete --recursive --verbose \
	  . $CIRCUITPY_PATH
