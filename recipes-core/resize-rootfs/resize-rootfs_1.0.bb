SUMMARY = "Script to grow the root partition"
DESCRIPTION = "Grow the root partition and filesystem to fill the SD card on \
               first boot"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://resize-rootfs.sh \
           file://resize-rootfs.init \
           file://resize-rootfs.service \
           "

S = "${UNPACKDIR}"

# Support both systemd and sysvinit
inherit systemd update-rc.d

SYSTEMD_SERVICE:${PN} = "resize-rootfs.service"
SYSTEMD_AUTO_ENABLE = "enable"

# After checkroot.sh (S06), which remounts the rootfs read-write
INITSCRIPT_NAME = "resize-rootfs"
INITSCRIPT_PARAMS = "start 10 S ."

RDEPENDS:${PN} = "e2fsprogs-resize2fs \
                  util-linux-findmnt \
                  util-linux-lsblk \
                  util-linux-partx \
                  util-linux-sfdisk \
                  "

do_configure[noexec] = "1"
do_compile[noexec] = "1"

# This install both files for systemd and sysvinit.
# The systemd.bbclass [1] inherited here makes sure sysvinit will be removed in
# case systemd is being used, therefore recipes inherting both systemd and
# sysvinit do not have to remove the files manually (or gate with an if).
#
# [1] https://github.com/openembedded/openembedded-core/blob/master/meta/classes-recipe/systemd.bbclass#L282
do_install() {
    install -d ${D}${sbindir}
    install -m 0755 ${UNPACKDIR}/resize-rootfs.sh ${D}${sbindir}/resize-rootfs

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/resize-rootfs.service \
                    ${D}${systemd_system_unitdir}/

    install -d ${D}${sysconfdir}/init.d
    install -m 0755 ${UNPACKDIR}/resize-rootfs.init \
                    ${D}${sysconfdir}/init.d/resize-rootfs

    sed -i -e 's,@SBINDIR@,${sbindir},g' \
        ${D}${systemd_system_unitdir}/resize-rootfs.service \
        ${D}${sysconfdir}/init.d/resize-rootfs
}

COMPATIBLE_MACHINE = "milkv-duo-common"
