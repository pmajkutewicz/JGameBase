#!/bin/bash

NAME="jgamebase"
VERSION="0.64-6"

# Make sure only root can run our script
if [[ $EUID -ne 0 ]]; then
   echo "This script must be run as root" 1>&2
   exit 1
fi

# find the directory this script is in
CMD="$(cd "${0%/*}" 2>/dev/null; echo "$PWD"/"${0##*/}")"
BASE=$( dirname "${CMD}" )

# change dir
cd "${BASE}" || exit 1

SRC="${NAME}-src${VERSION}"
BIN="${NAME}${VERSION}"
RES="${BASE}/src_ressources"
JSDOC="${RES}/jsdoc"
HP="${RES}/homepage"

DEBBASE="${BASE}/${NAME}_${VERSION}_all"
DEBIAN="${DEBBASE}/DEBIAN"
DEB_MAIN="${DEBBASE}/usr/share/games/${NAME}"
DEB_MAN="${DEBBASE}/usr/share/man/man6"
DEB_APPL="${DEBBASE}/usr/share/applications"
DEB_DOC="${DEBBASE}/usr/share/doc/${NAME}"
DEB_EXECDIR="${DEBBASE}/usr/games"
DEB_EXEC="${DEB_EXECDIR}/${NAME}"
DEB_ICON="${DEBBASE}/usr/share/pixmaps"
DEB_MENU="${DEBBASE}/usr/share/menu"

echo "SRC=${SRC}"
echo "BIN=${BIN}"
echo "DEBBASE=${DEBBASE}"

############################################
## CREATE C64 OVERLAY
############################################

OVRNAME="c64"
#OVR="${OVRNAME}.overlay.$(date +%Y%m%d)"
OVR="${OVRNAME}.overlay.$(ls -tlr --time-style=+%Y%m%d ${BASE}/C64lite/Scripts/* | tail -1 | awk '{ print $6 }')"
echo "overlay_version=$(ls -tlr --time-style=+%Y%m%d ${BASE}/C64lite/Scripts/* | tail -1 | awk '{ print $6 }')" >"${BASE}/C64lite/Settings.cfg"
echo "OVR=${OVR}"

# delete old work directories
if [ -e "${OVR}" ]; then
  rm -r "${OVR}"
fi

# create new work directories
mkdir -p "${OVR}"

# delete old archives
if [ -e "${OVR}.zip" ]; then
  rm "${OVR}.zip"
fi

# copy to source and binary work directories
mkdir -p "${OVR}/Scripts"

cp "${BASE}/C64lite/Scripts/README.TXT" "${OVR}/Scripts" 2>/dev/null
cp "${BASE}/C64lite/Scripts"/*.sh "${OVR}/Scripts" 2>/dev/null
cp "${BASE}/C64lite/Scripts"/*.js "${OVR}/Scripts" 2>/dev/null
cp -r "${BASE}/C64lite/Scripts"/*conf* "${OVR}/Scripts" 2>/dev/null
cp -r "${BASE}/C64lite/Scripts"/*prop* "${OVR}/Scripts" 2>/dev/null
cp -r "${BASE}/C64lite/Scripts"/*.cfg "${OVR}/Scripts" 2>/dev/null
cp -r "${BASE}/C64lite/Scripts"/*.ini "${OVR}/Scripts" 2>/dev/null
cp -r "${BASE}/C64lite/Scripts"/*rc "${OVR}/Scripts" 2>/dev/null

cp "${BASE}/C64lite/Scripts/includes" "${OVR}/Scripts" 2>/dev/null
cp "${BASE}/C64lite/Scripts/jsidplay.jar" "${OVR}/Scripts" 2>/dev/null
cp "${BASE}/C64lite/Scripts/jswingc64.jar" "${OVR}/Scripts" 2>/dev/null

mkdir -p "${OVR}/Gfx"
cp "${BASE}/C64lite/Gfx"/*.png "${OVR}/Gfx" 2>/dev/null

cp "${BASE}/C64lite/GameEmulators.ini" "${OVR}"
cp "${BASE}/C64lite/MusicEmulators.ini" "${OVR}"
# cp "${BASE}/C64lite/url.csv" "${OVR}"

# build overlay
cd "${OVR}" || exit 1
zip -r "${OVR}.zip" * >/dev/null

# change dir
cd "${BASE}" || exit 1

chown frank:frank "${BASE}/${OVR}/${OVR}.zip"

# move created files to parent dir
mv "${BASE}/${OVR}/${OVR}.zip" "${BASE}/Overlays"

# remove work directories
rm -r "${BASE}/${OVR}"

#############################
## OVERLAY END
#############################


# delete old work directories
if [ -e "${SRC}" ]; then
  rm -r "${SRC}"
fi

if [ -e "${BIN}" ]; then
  rm -r "${BIN}"
fi

if [ -e "${DEBBASE}" ]; then
  rm -r "${DEBBASE}"
fi

# create new work directories
mkdir -p "${SRC}"
mkdir -p "${BIN}"
mkdir -p "${DEBBASE}"
mkdir -p "${DEBIAN}"
mkdir -p "${DEB_MAIN}"
mkdir -p "${DEB_MAN}"
mkdir -p "${DEB_APPL}"
mkdir -p "${DEB_DOC}"
mkdir -p "${DEB_EXECDIR}"
mkdir -p "${DEB_ICON}"
mkdir -p "${DEB_MENU}"

### update date and remove debug ###
### NOT WORKING ###
#sed -i "s/^\(.*\)public[\t ]\+static[\t ]\+final[\t ]\+String[\t ]\+DATE_DAY[ ]*=[ ]*\"[0-9]\{2\}\".*;\(.*\)$/\1public static final String DATE_DAY   = \"$(date +%d)\";\2/" Const.java
#sed -i "s/^\(.*\)public[\t ]\+static[\t ]\+final[\t ]\+String[\t ]\+DATE_MONTH[ ]*=[ ]*\"[0-9]\{2\}\".*;\(.*\)$/\1public static final String DATE_MONTH = \"$(date +%m)\";\2/" Const.java
#sed -i "s/^\(.*\)public[\t ]\+static[\t ]\+final[\t ]\+String[\t ]\+DATE_YEAR[ ]*=[ ]*\"[0-9]\{4\}\".*;\(.*\)$/\1public static final String DATE_YEAR  = \"$(date +%Y)\";\2/" Const.java
#sed -i "s/^\(.*\)public[\t ]\+static[\t ]\+final[\t ]\+boolean[\t ]\+DEBUG[ ]*=[ ]*true.*;\(.*\)$/\1public static final boolean DEBUG = false;\2/" Const.java

# create javadoc
ant -f javadoc.xml

# delete old jar
rm jgamebase.jar
# create new jar
ant

###################################
# create homepage & javascript docu

# homepage
cd "${HP}" || exit 1

if [ -e "${HP}/htdocs" ]; then
  rm -r "${HP}/htdocs"
fi
mkdir -p "${HP}/htdocs"

# create footer
cat "${HP}/source/footer1.html" >"${HP}/source/footer.html"
echo "V${VERSION}" >>"${HP}/source/footer.html"
cat "${HP}/source/footer2.html" >>"${HP}/source/footer.html"

# build homepage
LANG=C ; rebol webcontent4all.r >/dev/null

# jsdocs
cd "${JSDOC}" || exit 1

if [ -e "${JSDOC}/out/jsdoc" ]; then
  rm -r "${JSDOC}/out/jsdoc"
fi
mkdir -p "${JSDOC}/out/jsdoc"

java -jar jsrun.jar app/run.js -a -t=templates/jsdoc "${BASE}/Includes/includes.js" >/dev/null

# add javascript docu to homepage
mkdir -p "${HP}/htdocs/symbols"
cp "${JSDOC}/out/jsdoc/symbols/_global_.html" "${HP}/htdocs/symbols"
cp "${JSDOC}/out/jsdoc/symbols/Array.html" "${HP}/htdocs/symbols"
cp "${JSDOC}/out/jsdoc/symbols/String.html" "${HP}/htdocs/symbols"

# copy homepage to docu
if [ -e "${BASE}/Docs" ]; then
  rm -r "${BASE}/Docs"
fi
mkdir -p "${BASE}/Docs"

cp -r "${HP}/htdocs/"* "${BASE}/Docs"

# build homepage again with picture footer
cd "${HP}" || exit 1

if [ -e "${HP}/htdocs" ]; then
  rm -r "${HP}/htdocs"
fi
mkdir -p "${HP}/htdocs"

# create footer
cat "${HP}/source/footer1_pics.html" >"${HP}/source/footer.html"
echo $( LANG=en_EN.UTF-8; date +"%d. %B %Y" ). >>"${HP}/source/footer.html"
cat "${HP}/source/footer2.html" >>"${HP}/source/footer.html"

# build homepage
rebol webcontent4all.r >/dev/null

# add javascript docu to homepage
mkdir -p "${HP}/htdocs/symbols"
cp "${JSDOC}/out/jsdoc/symbols/_global_.html" "${HP}/htdocs/symbols"
cp "${JSDOC}/out/jsdoc/symbols/Array.html" "${HP}/htdocs/symbols"
cp "${JSDOC}/out/jsdoc/symbols/String.html" "${HP}/htdocs/symbols"

#####################################

# change dir
cd "${BASE}" || exit 1

# delete old archives
if [ -e "${SRC}.tar.gz" ]; then
  rm "${SRC}.tar.gz"
fi
if [ -e "${SRC}.zip" ]; then
  rm "${SRC}.zip"
fi

if [ -e "${BIN}.tar.gz" ]; then
  rm "${BIN}.tar.gz"
fi
if [ -e "${BIN}.zip" ]; then
  rm "${BIN}.zip"
fi

# write version on splash screen
convert -font "${RES}/C64.ttf" -fill lightblue -pointsize 12 -draw "text 390,120 \"V${VERSION}\"" "${RES}/GameBase_no_version.png" "${BASE}/Artwork/SplashScreens/GameBase.png"

# copy menu entry
cp "${RES}/${NAME}" "${DEB_MENU}"

# copy man page
cp "${RES}/${NAME}.6" "${DEB_MAN}"
gzip -9 "${DEB_MAN}/${NAME}.6" >/dev/null

# create "executable"
echo '#!/bin/sh' >"${DEB_EXEC}"
echo >>"${DEB_EXEC}"
echo "DIR=\"/usr/share/games/${NAME}\"" >>"${DEB_EXEC}"
echo 'cd "${DIR}"' >>"${DEB_EXEC}"
echo >>"${DEB_EXEC}"
echo "BINARY=\"${NAME}.sh\"" >>"${DEB_EXEC}"
echo 'exec "${DIR}/${BINARY}" "$@"' >>"${DEB_EXEC}"

# copy to source and binary work directories
cp -r Artwork "${BIN}"
cp -r Artwork "${DEB_MAIN}"
#bin
cp -r C64lite "${BIN}"
cp -r C64lite "${DEB_MAIN}"
rm -r "${BIN}/C64lite/Extras/Additional" 2>/dev/null
rm -r "${DEB_MAIN}/C64lite/Extras/Additional" 2>/dev/null
rm -r "${BIN}/C64lite/Extras/Books" 2>/dev/null
rm -r "${DEB_MAIN}/C64lite/Extras/Books" 2>/dev/null
rm -r "${BIN}/C64lite/Extras/Carts" 2>/dev/null
rm -r "${DEB_MAIN}/C64lite/Extras/Carts" 2>/dev/null
rm -r "${BIN}/C64lite/Extras/Coverdisks" 2>/dev/null
rm -r "${DEB_MAIN}/C64lite/Extras/Coverdisks" 2>/dev/null
rm -r "${BIN}/C64lite/Extras/Covertapes" 2>/dev/null
rm -r "${DEB_MAIN}/C64lite/Extras/Covertapes" 2>/dev/null
rm -r "${BIN}/C64lite/Extras/Disks" 2>/dev/null
rm -r "${DEB_MAIN}/C64lite/Extras/Disks" 2>/dev/null
rm -r "${BIN}/C64lite/Extras/Docs" 2>/dev/null
rm -r "${DEB_MAIN}/C64lite/Extras/Docs" 2>/dev/null
rm -r "${BIN}/C64lite/Extras/Listings" 2>/dev/null
rm -r "${DEB_MAIN}/C64lite/Extras/Listings" 2>/dev/null
rm -r "${BIN}/C64lite/Extras/Magcover" 2>/dev/null
rm -r "${DEB_MAIN}/C64lite/Extras/Magcover" 2>/dev/null
rm -r "${BIN}/C64lite/Extras/Maps" 2>/dev/null
rm -r "${DEB_MAIN}/C64lite/Extras/Maps" 2>/dev/null
rm -r "${BIN}/C64lite/Extras/Missing" 2>/dev/null
rm -r "${DEB_MAIN}/C64lite/Extras/Missing" 2>/dev/null
rm -r "${BIN}/C64lite/Extras/mp3s" 2>/dev/null
rm -r "${DEB_MAIN}/C64lite/Extras/mp3s" 2>/dev/null
rm -r "${BIN}/C64lite/Extras/PD-Disks" 2>/dev/null
rm -r "${DEB_MAIN}/C64lite/Extras/PD-Disks" 2>/dev/null
rm -r "${BIN}/C64lite/Extras/Photos" 2>/dev/null
rm -r "${DEB_MAIN}/C64lite/Extras/Photos" 2>/dev/null
rm -r "${BIN}/C64lite/Extras/Tapes" 2>/dev/null
rm -r "${DEB_MAIN}/C64lite/Extras/Tapes" 2>/dev/null
rm -r "${BIN}/C64lite/Extras/Tips" 2>/dev/null
rm -r "${DEB_MAIN}/C64lite/Extras/Tips" 2>/dev/null

rm -r "${BIN}/C64lite/Games"
rm -r "${DEB_MAIN}/C64lite/Games"

##########################
#cp -r DEBIAN "${DEBBASE}"
cp -r "${RES}/control" "${DEBIAN}"
cp -r "${RES}/postinst" "${DEBIAN}"
cp -r "${RES}/postrm" "${DEBIAN}"
chown root:root -R "${DEBIAN}"
chmod a-st -R "${DEBIAN}"
chmod 0755 -R "${DEBIAN}"

cp -r Docs "${BIN}"
cp -r Docs "${DEB_MAIN}"
cp -r Docs/* "${DEB_DOC}"

cp -r Includes "${BIN}"
cp -r Includes "${DEB_MAIN}"

cp -r javadoc "${SRC}"

cp -r lib "${BIN}"
cp -r lib "${DEB_MAIN}"

cp -r nbproject "${SRC}"

cp -r Overlays "${BIN}"
cp -r Overlays "${DEB_MAIN}"

cp -r src "${SRC}"
cp -r "${RES}" "${SRC}"
cp -r utilities "${SRC}"

cp build.xml "${SRC}"

cp db.ini "${BIN}"
cp db.ini "${DEB_MAIN}"

#derby.log

# copy to normal, not source release (fixes export to *.mdb)
#cp Empty.mdb "${SRC}"
cp Empty.mdb "${BIN}"
cp Empty.mdb "${DEB_MAIN}"

# copy to normal, not source release (fixes export to *.mdb)
#cp Empty_with_defaults.mdb "${SRC}"
cp Empty_with_defaults.mdb "${BIN}"
cp Empty_with_defaults.mdb "${DEB_MAIN}"

cp global.ini "${BIN}"
cp global.ini "${DEB_MAIN}"

cp hibernate.cfg.xml "${BIN}"
cp hibernate.cfg.xml "${DEB_MAIN}"

cp hibernateImport.cfg.xml "${BIN}"
cp hibernateImport.cfg.xml "${DEB_MAIN}"

cp History.txt "${BIN}"
cp History.txt "${DEB_MAIN}"
cp History.txt "${DEB_DOC}/changelog"
gzip -9 "${DEB_DOC}/changelog"

cp jgamebase.bat "${BIN}"

cp "jGameBase.desktop" "${BIN}"
chmod a+x "${BIN}/jGameBase.desktop"
cp "jGameBase.desktop" "${DEB_APPL}"
chmod a+x "${DEB_APPL}/jGameBase.desktop"

cp jgamebase.jar "${BIN}"
cp jgamebase.jar "${DEB_MAIN}"

cp jgamebase.sh "${BIN}"
cp jgamebase.sh "${DEB_MAIN}"

cp License.txt "${SRC}"
cp License.txt "${BIN}"
cp License.txt "${DEB_DOC}/copyright"

#cp manifest.mf "${SRC}"

cp README.TXT "${BIN}"
cp README.TXT "${DEB_MAIN}"
cp README.TXT "${DEB_DOC}"

cp release.sh "${SRC}"

cp toolbox.sh "${BIN}"
cp toolbox.sh "${DEB_MAIN}"

cp "${BASE}/Artwork/ProgramIcons/jgamebase.png" "${DEB_ICON}"
cp "${RES}/jgamebase.xpm" "${DEB_ICON}"

chown root:root -R "${DEBBASE}/usr"
chmod a-st -R "${DEBBASE}/usr"
chmod u+w,a+r,a-x,a+X -R "${DEBBASE}/usr"
find . -type f -name "*.sh" -exec chmod a+x '{}' \;

chmod a+x "${DEB_EXEC}"

# create archives
echo
echo "TAR ${SRC}.tar.gz"
tar cvzf "${SRC}.tar.gz" "${SRC}" >/dev/null

echo
echo "ZIP ${SRC}.zip"
zip -r "${SRC}.zip" "${SRC}" >/dev/null

echo
echo "TAR ${BIN}.tar.gz"
tar cvzf "${BIN}.tar.gz" "${BIN}" >/dev/null

echo
echo "ZIP ${BIN}.zip"
zip -r "${BIN}.zip" "${BIN}" >/dev/null

echo
echo "DEB ${DEBBASE}.deb"
dpkg --build ${DEBBASE} >/dev/null

chown -R frank:frank /home/frank/workspace/jgamebase/*
chown frank:frank "${BASE}/Artwork/SplashScreens/GameBase.png"

# remove work directories
rm -r "${SRC}"
rm -r "${BIN}"
rm -r "${DEBBASE}"

# move created files to parent dir
mv "${SRC}.tar.gz" ".."
mv "${SRC}.zip" ".."
mv "${BIN}.tar.gz" ".."
mv "${BIN}.zip" ".."
mv "${DEBBASE}.deb" ..

#lintian "${DEBBASE}.deb" | lintian-info

