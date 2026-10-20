#!/usr/bin/env bash
# Captures README screenshots of the sample app on a running emulator.
# The sample shows bundled demo content only (a demo feed, a demo product and the release notes and reviews of a
# made-up app), so nothing is loaded from the network. It opens each screen from the `scene` extra and logs
# SCENE_READY once the screen has settled. A scene that logs SCENE_FAILED or never becomes ready fails the run.
# Every capture is also checked for the expected text and for a blank image.
#
#   bash scripts/screenshots.sh tablet   # pixel_tablet in landscape: release notes beside a grid of reviews
#   bash scripts/screenshots.sh phone    # pixel_7: collapsed feed, an expanded post and the fade style
set -euo pipefail
source "$(dirname "$0")/screenshot-lib.sh"

device="${1:-phone}"

# The text each scene must show; the capture fails without it. ExpandableText exposes its whole text to
# accessibility, so the checks use headers and labels that only appear on that screen.
expected_text() {
  case "$1" in
    feed) echo "Demo feed" ;;
    expanded) echo "Demo feed" ;;
    fade) echo "Product details" ;;
    tablet-reviews) echo "Ratings and reviews" ;;
  esac
}

install_sample
set_night_mode light
if [ "$device" = tablet ]; then
  ensure_landscape
  fresh_launch --es scene tablet-reviews
  wait_for_scene tablet-reviews
  capture "tablet-reviews" "$(expected_text tablet-reviews)"
else
  for scene in feed expanded fade; do
    fresh_launch --es scene "$scene"
    wait_for_scene "$scene"
    capture "phone-$scene" "$(expected_text "$scene")"
  done
fi
