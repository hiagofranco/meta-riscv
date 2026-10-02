#!/bin/sh
# Grow the root partition and filesystem to the end of the SD card.
# The caller (systemd unit or init script) disables itself on success, so
# this only runs once.

set -e

ROOT_DEV=$(findmnt -n -o SOURCE /)
PKNAME=$(lsblk -n -o PKNAME "$ROOT_DEV")
[ -n "$PKNAME" ] || exit 0
DISK=/dev/$PKNAME
PARTNUM=$(cat "/sys/class/block/${ROOT_DEV##*/}/partition")

echo ", +" | sfdisk -N "$PARTNUM" --no-reread "$DISK"
partx -u "$DISK"
resize2fs "$ROOT_DEV"
